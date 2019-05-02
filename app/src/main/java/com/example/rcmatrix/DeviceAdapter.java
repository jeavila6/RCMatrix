package com.example.rcmatrix;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<BluetoothDevice> mDeviceList; // data set
    private int mSelection; // index of selected device

    class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView nameTextView;
        private TextView addressTextView;

        DeviceViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            addressTextView = itemView.findViewById(R.id.address_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            int previousSelection = mSelection;
            mSelection = getLayoutPosition();
            notifyItemChanged(previousSelection);
            notifyItemChanged(mSelection);
        }
    }

    DeviceAdapter(List<BluetoothDevice> deviceList) {
        mDeviceList = deviceList;
        mSelection = -1;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_view, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        BluetoothDevice device = mDeviceList.get(position);

        // set text for device name, decorate if selected
        String name = device.getName();
        if (position == mSelection)
            name = ">" + name + "<";
        holder.nameTextView.setText(name);

        // set text for device address
        String address = device.getAddress();
        holder.addressTextView.setText(address);

    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    BluetoothDevice getSelection() {
        return mDeviceList.get(mSelection);
    }

}
