package com.example.rcmatrix;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

        mMessageHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }

            public void obtainMessage(int code, int i, int j, byte[] data) {
                return;
            }
        };

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
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_frame_layout, fragment);
        transaction.commit();
    }

    // send data to connected device
    public void sendDataBluetooth() {

        Fragment currentFragment = mFragmentManager.findFragmentById(R.id.fragment_frame_layout);
        byte[] bytes = ((PhotoFragment) currentFragment).getMessage();

        // TODO check if ConnectedThread has been started
        mConnectedThread.write("I".getBytes());
        mConnectedThread.write(bytes);

    }

    // constants used when transmitting messages between service and UI
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer;

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // get the input and output streams; using temp objects because streams are final
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

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes;

            // keep listening to the InputStream until an exception occurs
            while (true) {
                try {

                    // read from the InputStream
                    numBytes = mmInStream.read(mmBuffer);

                    // send the obtained bytes to the UI activity
                    Message readMsg = mMessageHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1, mmBuffer);
                    readMsg.sendToTarget();

                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // called from main activity to send data to the remote device.
        void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // share sent message with UI activity
                Message writtenMsg = mMessageHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // send a failure message back to activity
                Message writeErrorMsg =
                        mMessageHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mMessageHandler.sendMessage(writeErrorMsg);
            }
        }

        // called from main activity to shut down connection
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        ConnectThread(BluetoothDevice device) {

            // use temp object to later assign to mmSocket
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {

                // get device UUID
                ParcelUuid[] uuids = mmDevice.getUuids();
                UUID serviceRecordUuid = uuids[0].getUuid();

                tmp = device.createRfcommSocketToServiceRecord(serviceRecordUuid);
            } catch (IOException e) {
                Log.e(TAG, "socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {

            // cancel discovery
            // bluetoothAdapter.cancelDiscovery();

            try {

                // connect to remote device through socket
                // this call blocks until it succeeds or throws an exception
                mmSocket.connect();

            } catch (IOException connectException) {
                // unable to connect; close the socket and return
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "could not close the client socket", closeException);
                }
                return;
            }

            // connection attempt succeeded, perform work in a separate thread
            Log.d(TAG, "Connection attempt succeeded");
            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();

        }

        // close client socket and causes the thread to finish
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }
}