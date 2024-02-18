package com.example.locked_in;

import androidx.annotation.NonNull;
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
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SelectBluetoothDevice extends AppCompatActivity {

    public static int BLUETOOTH_CONNECT_REQUEST_CODE = 1;

    private BluetoothAdapter bluetoothAdapter;
    private List<DeviceInfoModel> deviceList;
    private DeviceListAdapter deviceListAdapter;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bluetooth_device);

        final Button refresh_devices = findViewById(R.id.refresh_device);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Try reconnecting
            return;
        }

        refresh_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchForDevices();
                requestBluetoothPermission();
            }
        });

        searchForDevices();
        requestBluetoothPermission();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestBluetoothPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH}, BLUETOOTH_CONNECT_REQUEST_CODE);
        } else {
            retrievePairedDevices();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void retrievePairedDevices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    BLUETOOTH_CONNECT_REQUEST_CODE);
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        List<DeviceInfoModel> deviceList = new ArrayList<>();

        for (BluetoothDevice device : pairedDevices) {
            String deviceName = device.getName();
            String deviceHardwareAddress = device.getAddress();
            deviceList.add(new DeviceInfoModel(deviceName, deviceHardwareAddress));
        }

        setupRecyclerView(deviceList);
    }

    private void setupRecyclerView(List<DeviceInfoModel> deviceList) {
        if (deviceList.isEmpty()) {
            // No paired devices available
            // display appropriate message to user
            Toast.makeText(this, "NO PAIRED DEVICES", Toast.LENGTH_SHORT).show();
            return;
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerViewDevice);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceListAdapter = new DeviceListAdapter(this, deviceList);
        recyclerView.setAdapter(deviceListAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void searchForDevices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    BLUETOOTH_CONNECT_REQUEST_CODE);
            return;
        }
        if (!bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BLUETOOTH_CONNECT_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                retrievePairedDevices();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "COULD NOT CONNECT", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to handle the click event of the pairing button
    @RequiresApi(api = Build.VERSION_CODES.S)
    public void onPairingButtonClick(View view) {
        // Perform Bluetooth device discovery
        searchForDevices();
    }
}