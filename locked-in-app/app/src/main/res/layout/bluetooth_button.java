package com.droiduino.bluetoothconn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import static android.content.ContentValues.TAG;

public class bluetooth_button extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the bluetooth_pair button by its ID
        Button pairToDeviceButton = findViewById(R.id.bluetooth_pair);

        // Set an onClickListener to handle button clicks
        pairToDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the application context
                Context context = bluetooth_button.this;

                // Create AlertDialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Connection Status");

                // Set a custom layout for the dialog
                View dialogLayout = getLayoutInflater().inflate(R.layout.loading_dialog, null);
                builder.setView(dialogLayout);

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();

                // Simulate a delay using a Handler to dismiss the dialog after a certain time
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Dismiss the dialog after 3 seconds
                        dialog.dismiss();
                    }
                }, 3000);
            }
        });
    }
}