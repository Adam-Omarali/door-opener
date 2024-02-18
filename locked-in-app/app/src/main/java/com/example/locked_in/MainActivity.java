package com.example.locked_in;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    // MESSAGE CODES
    private final static int CONNECTING_STATUS = 1;
    private final static int MESSAGE_READ = 2;

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

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter, deviceAddress);
            createConnectThread.start();
        }

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
        @SuppressLint("MissingPermission")
        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tempSocket = null;

            @SuppressLint("MissingPermission") UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                tempSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e("SOCKET ERROR", "createSocket() failed", e);
            }

            bluetoothSocket = tempSocket;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();

            try {
                bluetoothSocket.connect();
                Log.e("STATUS", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException e) {
                try {
                    bluetoothSocket.close();
                    Log.e("STATUS", "CANNOT CONNECT TO DEVICE");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e("CLOSE", "CANNOT CLOSE SOCKET", closeException);
                }
                return;
            }

            connectedThread = new ConnectedThread(bluetoothSocket);
            connectedThread.run();
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

            while (true) {
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
                    e.printStackTrace();
                    break;
                }
            }
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