package com.example.ledmatrix;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "LEDMatrix"; // for writing logs

    private FragmentManager mFragmentManager;
    private OutputStream mOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        bluetoothInit();
    }

    // replace main layout with fragment
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_frame_layout, fragment);
        transaction.commit();
    }

    // establish connection with Bluetooth device
    private void bluetoothInit() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // if device does not support Bluetooth
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Device does not support Bluetooth");
            return;
        }

        // if device Bluetooth is not enabled
        if (!bluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Device Bluetooth is not enabled");
            return;
        }

        // get paired devices
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices)
            Log.d(TAG, "Paired device: " + device.getName());

        // if device has no paired devices
        if (bondedDevices.size() == 0) {
            Log.e(TAG, "Device has not paired devices");
            return;
        }

        // connect with selected device
        int selection = 2; // for now, assume device 2
        Object[] devices = bondedDevices.toArray();
        assert devices != null;
        BluetoothDevice selectedDevice = (BluetoothDevice) devices[selection];
        Log.d(TAG, "Selected device: " + selectedDevice.getName());
        ParcelUuid[] uuids = selectedDevice.getUuids();
        UUID serviceRecordUuid = uuids[0].getUuid();
        try {
            BluetoothSocket socket = selectedDevice
                    .createRfcommSocketToServiceRecord(serviceRecordUuid);
            socket.connect();
            mOutputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // send data to connected device
    public void sendDataBluetooth() {

        // check if connection has been established
        if (mOutputStream == null) {
            Log.d(TAG, "Connection has not been established");
            return;
        }

        new SendTask().execute();

    }

    private class SendTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Fragment currentFragment = mFragmentManager.findFragmentById(R.id.fragment_frame_layout);
            byte[] bytes = ((PhotoFragment) currentFragment).getMessage();

            Log.d(TAG, "Sending (message): " + Arrays.toString(bytes));
            Log.d(TAG, "Sending (size, bytes): " + bytes.length);

            // send message
            try {
                mOutputStream.write('I');
                mOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}