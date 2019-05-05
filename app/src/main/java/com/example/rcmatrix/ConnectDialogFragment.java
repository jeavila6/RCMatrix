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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

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

        // build device radio group
        RadioGroup deviceRadioGroup = dialogView.findViewById(R.id.device_radio_group);
        for (BluetoothDevice device : bondedList) {
            RadioButton button = new RadioButton(getContext());
            button.setText(device.getName());
            deviceRadioGroup.addView(button);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        // set constructed layout for dialog
        builder.setView(dialogView);

        // set positive button for dialog, send positive button event back to host activity
        builder.setPositiveButton(R.string.connect_dialog_positive_button, (dialog, id) -> {
            int checkedRadioButtonId = deviceRadioGroup.getCheckedRadioButtonId();
            View checkedRadioButton = deviceRadioGroup.findViewById(checkedRadioButtonId);
            int checkedRadioButtonIndex = deviceRadioGroup.indexOfChild(checkedRadioButton);
            BluetoothDevice selectedDevice = bondedList.get(checkedRadioButtonIndex);
            mListener.onDialogPositiveClick(selectedDevice);
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