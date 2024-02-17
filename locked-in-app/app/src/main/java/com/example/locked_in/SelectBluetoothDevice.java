package com.example.locked_in;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pManager.DeviceInfoListener;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SelectBluetoothDevice extends AppCompatActivity {

    public static int BLUETOOTH_CONNECT_REQUEST_CODE = 0;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bluetooth_device);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.BLUETOOTH_CONNECT};

            ActivityCompat.requestPermissions(this, permissions, BLUETOOTH_CONNECT_REQUEST_CODE);
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        List<DeviceInfoModel> deviceList = new ArrayList<DeviceInfoModel>();

        if (pairedDevices.size() > 0) {
             for (BluetoothDevice device : pairedDevices) {
                 String deviceName = device.getName();
                 String deviceHardwareAddress = device.getAddress();

                 DeviceInfoModel deviceInfoModel = new DeviceInfoModel(deviceName, deviceHardwareAddress);
                 deviceList.add(deviceInfoModel);
             }

             RecyclerView recyclerView = findViewById(R.id.recyclerViewDevice);
             recyclerView.setLayoutManager(new LinearLayoutManager(this));

             DeviceListAdapter deviceListAdapter = new DeviceListAdapter(this, deviceList);
             recyclerView.setAdapter(deviceListAdapter);
             recyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            View view = findViewById(R.id.recyclerViewDevice);
            Snackbar snackbar = Snackbar.make(view, "Pair Bluetooth Device", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            snackbar.show();
        }
    }
}