package com.example.rcmatrix;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ConnectActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName(); // for writing logs

    // extras names
    private static final String EXTRA_ADDRESS = "com.example.rcmatrix.address";

    // request codes
    private final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        // set toolbar as app bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Log.e(TAG, "Device does not support Bluetooth");
            // TODO show dialog if device does not support Bluetooth
            finish();
            return;
        }

        // request user permission to enable Bluetooth if not enabled
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // TODO check if Bluetooth was enabled

        Set<BluetoothDevice> bondedSet = bluetoothAdapter.getBondedDevices();
        List<BluetoothDevice> bondedList = new ArrayList<>(bondedSet);

        // show dialog with paired devices
        if (bondedSet.size() == 0) {
            Log.d(TAG, "No paired devices");
            // TODO show message if there are no paired devices
        }

        // set up RecyclerView
        RecyclerView pairedRecyclerView = findViewById(R.id.devices_recycler_view);
        pairedRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        pairedRecyclerView.setLayoutManager(layoutManager);
        DeviceAdapter mAdapter = new DeviceAdapter(bondedList);
        pairedRecyclerView.setAdapter(mAdapter);

        // finish and return address of selected device
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent data = new Intent();
            // TODO return different result if no selection
            BluetoothDevice device = mAdapter.getSelection();
            data.putExtra(EXTRA_ADDRESS, device);
            setResult(RESULT_OK, data);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) {

            // enabling Bluetooth failed
            if (resultCode != RESULT_OK)
                Log.e(TAG, "Enabling Bluetooth failed");

        }
    }

    // called by parent activity to retrieve address extra
    public static BluetoothDevice getExtraAddress(Intent result) {
        return Objects.requireNonNull(result.getExtras()).getParcelable(EXTRA_ADDRESS);
    }

}
