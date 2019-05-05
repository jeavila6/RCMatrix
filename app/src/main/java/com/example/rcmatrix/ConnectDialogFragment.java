package com.example.rcmatrix;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ConnectDialogFragment extends DialogFragment {

    // hosting activity must implement this interface to receive event callbacks
    public interface NoticeDialogListener {
        void onDialogPositiveClick(BluetoothDevice device);
    }

    private NoticeDialogListener mListener; // for delivering action events

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // inflate layout for dialog, passing null as parent view since its used in dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView =
                inflater.inflate(R.layout.fragment_dialog_connect, null);

        // get paired devices
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        List<BluetoothDevice> bondedList = new ArrayList<>(bluetoothAdapter.getBondedDevices());

        // TODO show message if no paired devices, bondedList.isEmpty()

        // set up paired devices RecyclerView
        RecyclerView pairedRecyclerView = dialogView.findViewById(R.id.devices_recycler_view);
        pairedRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        pairedRecyclerView.setLayoutManager(layoutManager);
        DeviceAdapter mAdapter = new DeviceAdapter(bondedList);
        pairedRecyclerView.setAdapter(mAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        // set constructed layout for dialog
        builder.setView(dialogView);

        // set positive button for dialog, send positive button event back to host activity
        builder.setPositiveButton(R.string.connect_dialog_positive_button, (dialog, id) -> {
            BluetoothDevice device = mAdapter.getSelection();
            mListener.onDialogPositiveClick(device);
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // attempt to instantiate NoticeDialogListener to send events back to host
        try {
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }
}