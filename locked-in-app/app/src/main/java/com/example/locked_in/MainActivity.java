package com.example.locked_in;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public Context mainActivityContext;
    private String deviceName = null;
    private String deviceAddress = null;

    public static Handler handler;
    public static BluetoothSocket bluetoothSocket;

    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;
    private static final int REQUEST_ENABLE_BT = 0;

    public static final int REQUEST_DISCOVER_BT = 1;

    private BluetoothAdapter bluetoothAdapter;

    // MESSAGE CODES
    private final static int CONNECTING_STATUS = 1;
    private final static int MESSAGE_READ = 2;
    private final static int BLUETOOTH_CONNECT_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button buttonConnect = findViewById(R.id.bluetooth_pair);

        final TextView textViewinfo = findViewById(R.id.headingText);

        deviceName = getIntent().getStringExtra("deviceName");

        if (deviceName != null) {
            deviceAddress = getIntent().getStringExtra("deviceAddress");

            buttonConnect.setEnabled(false);

            // Connect to device
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter, deviceAddress, this, MainActivity.this);
            createConnectThread.run();
        }

        // discover bluetooth button click
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] {Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_CONNECT_CODE);
                    return;
                }
                if (!bluetoothAdapter.isDiscovering()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                }
            }
        });

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CONNECTING_STATUS:
                        switch (msg.arg1) {
                            case 1:
                                buttonConnect.setEnabled(true);
                                break;
                            case -1:
                                buttonConnect.setEnabled(true);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString();

                        switch (arduinoMsg.toLowerCase()) {
                            default:
                                break;
                        }
                }

            }
        };

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[] {Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_CODE);
                    }
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }

                Intent intent = new Intent(MainActivity.this, SelectBluetoothDevice.class);
                startActivity(intent);
            }
        });

        // Bluetooth Process
        // (credits to https://medium.com/swlh/create-custom-android-app-to-control-arduino-board-using-bluetooth-ff878e998aa8)

        // Initialize Default Bluetooth Device on Android Phone
        // Get MAC Address of HC05 Bluetooth Module
        // Create thread to initiate connection
        // Connection is successful -> thread does call backs for data exchange
        // Thread reads data transmission

    }

    public static class CreateConnectThread extends Thread {
        private final Context context;
        private final Activity activity;

        private final BluetoothSocket bluetoothSocket;

        @RequiresApi(api = Build.VERSION_CODES.S)
        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address, Context context, Activity activity) {
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tempSocket = null;

            this.context = context;
            this.activity = activity;

            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        BLUETOOTH_CONNECT_CODE);
            }
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                tempSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e("SOCKET ERROR", "createSocket() failed", e);
            }

            bluetoothSocket = tempSocket;
            Toast.makeText(context, "Created SOCKET", Toast.LENGTH_SHORT).show();
        }

        @RequiresApi(api = Build.VERSION_CODES.S)
        public void run() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_CONNECT_CODE);
            }
            bluetoothAdapter.cancelDiscovery();

            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                            BLUETOOTH_CONNECT_CODE);
                    return;
                }

                // Attempt insecure bluetooth connections.
                turnBluetoothOn();
                bluetoothSocket.connect();

                Log.e("STATUS", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException e) {
                try {
                    bluetoothSocket.close();
                    Log.e("STATUS", "CANNOT CONNECT TO DEVICE");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();

                    // NOTE: application crashes here
                } catch (IOException closeException) {
                    Log.e("CLOSE", "CANNOT CLOSE SOCKET", closeException);
                }
                return;
            }

            connectedThread = new ConnectedThread(bluetoothSocket);
            Log.e("CONNECTED", "CONNECTION AFTER CONNECTED THREAD");
            connectedThread.run();
        }

        @RequiresApi(api = Build.VERSION_CODES.S)
        private void turnBluetoothOn() {
            try {
                BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

                if (!bluetooth.isEnabled()) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_CODE);
                        return;
                    }
                    bluetooth.enable();
                }
            } catch (Exception e) {
                Log.e("ENABLE FAILED", "TURN BLUETOOTH ON");
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e("SOCKET", "COULD NOT CLOSE CLIENT SOCKET", e);
            }
        }
    }

    public static class ConnectedThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket) {
            this.bluetoothSocket = socket;
            InputStream tempInput = null;
            OutputStream tempOutput = null;

            try {
                tempInput = socket.getInputStream();
                tempOutput = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("SOCKET", "INPUT OUTPUT ERROR", e);
            }

            this.inputStream = tempInput;
            this.outputStream = tempOutput;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes = 0;

            Log.e("CONNECTED THREAD", "ARDUINO PHASE");

            BluetoothGattCallback bluetoothGattCallback =
                    new BluetoothGattCallback() {
                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            if (newState == BluetoothProfile.STATE_CONNECTED) {
                                Log.e("BLUETOOTH PROFILE STATUS", "CONNECTED");
                            }

                    }


            /*
            while (true) {
                Log.e("ARDUINO PHASE", "ENTERED");
                try {
                    // Read inputstream from Arduino until termination.
                    buffer[bytes] = (byte) inputStream.read();
                    String readMessage;

                    if (buffer[bytes] == '\n') {
                        readMessage = new String(buffer, 0, bytes);
                        Log.e("Arduino Message", readMessage);
                        handler.obtainMessage(MESSAGE_READ, readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    Log.e("ARDUINO PHASE", "CRASHED");
                    break;
                }
                */

            };

        }

        public void write(String input) {
            byte[] bytes = input.getBytes();

            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error", "Unable to send message", e);
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e("Socket Error", "Unable to close socket", e);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (createConnectThread != null) {
            createConnectThread.cancel();
        }

        Intent main = new Intent(Intent.ACTION_MAIN);
        main.addCategory(Intent.CATEGORY_HOME);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(main);
    }
}