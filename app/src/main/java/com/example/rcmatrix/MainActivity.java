package com.example.rcmatrix;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName(); // for writing logs

    // request codes
    private final int REQUEST_CONNECT = 1;

    private FragmentManager mFragmentManager;
    private ConnectedThread mConnectedThread;
    private Handler mMessageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMessageHandler = new Handler(new MessageHandler());

        mFragmentManager = getSupportFragmentManager();

        // set toolbar as app bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // open fragment on navigation item selection
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_photo:
                    openFragment(new PhotoFragment());
                    return true;
                case R.id.navigation_draw:
                    openFragment(new DrawFragment());
                    return true;
                case R.id.navigation_text:
                    openFragment(new TextFragment());
                    return true;
            }
            return false;
        });

        // send data via Bluetooth on send FAB click
        FloatingActionButton sendFAB = findViewById(R.id.send_FAB);
        sendFAB.setOnClickListener(v -> sendDataBluetooth());

        // open first fragment by default
        openFragment(new PhotoFragment());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // fragments should handle result first, if needed
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CONNECT) {

            // attempt connection with given address
            if (resultCode == RESULT_OK && data != null) {
                BluetoothDevice device = ConnectActivity.getExtraAddress(data);
                ConnectThread t = new ConnectThread(device);
                t.start();
            }

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem connectItem = menu.findItem(R.id.connect_item);

        connectItem.setOnMenuItemClickListener(v -> {

            // start connect intent
            Intent intent = new Intent(this, ConnectActivity.class);
            startActivityForResult(intent, REQUEST_CONNECT);
            return true;

        });

        return true;
    }

    // replace main layout with fragment
    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_frame_layout, fragment);
        transaction.commit();
    }

    // send data to connected device
    private void sendDataBluetooth() {

        // display error if connected thread hasn't started
        if (mConnectedThread == null) {
            String toastText = getResources().getString(R.string.no_connection);
            Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
            return;
        }

        Fragment currentFragment = mFragmentManager.findFragmentById(R.id.fragment_frame_layout);
        assert currentFragment != null;
        byte[] bytes = ((PhotoFragment) currentFragment).getMessage();
        mConnectedThread.write("I".getBytes());
        mConnectedThread.write(bytes);

    }

    // constants used when transmitting messages between service and UI
    private interface MessageConstants {
        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
        int MESSAGE_ERROR = 2;
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;

        ConnectThread(BluetoothDevice device) {

            // use temporary object since mmSocket is final
            BluetoothSocket tmp = null;

            // attempt to create socket
            try {
                UUID id = device.getUuids()[0].getUuid();
                tmp = device.createRfcommSocketToServiceRecord(id);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }

            // assign socket if successful
            mmSocket = tmp;
        }

        public void run() {

            // attempt to connect to device through socket, otherwise close socket and return
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close client socket", e);
                }
                return;
            }

            // perform work in separate thread if successful
            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
        }
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer;

        ConnectedThread(BluetoothSocket socket) {

            // use temporary object since input and output streams are final
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // attempt to create input and output streams
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            // assign input and output streams if successful
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes;

            // keep listening to input stream until an exception occurs
            while (true) {
                try {

                    // read from input stream
                    numBytes = mmInStream.read(mmBuffer);

                    // send obtained bytes back to activity (unhandled for now)
                    Message readMessage = mMessageHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1, mmBuffer);
                    readMessage.sendToTarget();

                } catch (IOException e) {
                    Log.e(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // called from main activity to send data
        void write(byte[] bytes) {
            try {

                // write to output stream
                mmOutStream.write(bytes);

                // send sent message back to activity
                Message writtenMsg = mMessageHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();

            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // send failure message back to activity
                Message writeErrorMsg =
                        mMessageHandler.obtainMessage(MessageConstants.MESSAGE_ERROR);
                mMessageHandler.sendMessage(writeErrorMsg);
            }
        }
    }

    private class MessageHandler implements Handler.Callback {
        public boolean handleMessage(Message msg) {
            String toastText;
            switch(msg.what) {
                case MessageConstants.MESSAGE_WRITE:
                    toastText = getResources().getString(R.string.send_success);
                    Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    break;
                case MessageConstants.MESSAGE_ERROR:
                    toastText = getResources().getString(R.string.send_fail);
                    Toast.makeText(MainActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    }
}