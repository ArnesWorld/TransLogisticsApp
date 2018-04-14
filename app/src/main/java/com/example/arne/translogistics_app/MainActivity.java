package com.example.arne.translogistics_app;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arne.translogistics_app.DAL.AppDataBase;
import com.example.arne.translogistics_app.Model.DataRecording;
import com.example.arne.translogistics_app.Model.DataSegment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_BLUETOOTH = 100;
    private static final int DISCOVERY_REQUEST = 300;
    private static final int LOCATION_REQUEST_CODE = 50;
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;

    private HashMap<String, BluetoothDevice> bluetoothDeviceHashMap;
    private Handler mHandler;
    public Set<BluetoothDevice> pairedDevices;

    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;

    private Button btnDiscover;
    private ListView listViewDevices;
    private ImageButton btnRefresh;
    private ConnectedThread comThread;
    private DataRecording dataRecording;
    Gson gson;

    private TextView txtConnedtedDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gson = new GsonBuilder()
                .setDateFormat("MMM dd, yyyy HH:mm:ss").create();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case MessageConstants.MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a DataRecording object from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        dataRecording = gson.fromJson(readMessage, DataRecording.class);
                         //txtConnedtedDevice.setText(readMessage);
                         AcceptRecordingDialog(dataRecording);
                        break;
                    case MessageConstants.MESSAGE_WRITE:
                        break;
                    case MessageConstants.CONNECTION_ACCEPTED:
                        String deviceName = (String)msg.obj;
                        txtConnedtedDevice.setText(deviceName);

                    default:
                        Toast.makeText(getApplicationContext(), "default", Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permissin granted", Toast.LENGTH_SHORT).show();

            if(bluetoothAdapter == null){

            }
            if(!bluetoothAdapter.isEnabled()){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_BLUETOOTH);
            }
            AcceptThread acceptThread = new AcceptThread();
            acceptThread.start();

        }else { // Else we ask for for it
            String[] permissionRequest = {Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissionRequest, LOCATION_REQUEST_CODE);
            Toast.makeText(getApplicationContext(), "Permissin denied", Toast.LENGTH_SHORT).show();

        }



        txtConnedtedDevice = findViewById(R.id.txtConnDevice);
        if(bluetoothAdapter.getBondedDevices().size() != 0) {
            boolean matchFound = false;
            for (BluetoothDevice bd: bluetoothAdapter.getBondedDevices()) {
               String uuidString = bd.getUuids()[0].getUuid().toString();
               String myUuidString = uuidString.toString();
                if(uuidString.substring(uuidString.length() -12,uuidString.length()).equals(myUuidString.substring(myUuidString.length()-12, myUuidString.length()))){
                    BluetoothDevice device = (BluetoothDevice) bluetoothAdapter.getBondedDevices().toArray()[0];
                    txtConnedtedDevice.setText(device.getName());
                }
              Object o = bd.getUuids();
                int x = 1;
            }


        }
        setDiscoverability();
        btnDiscover = findViewById(R.id.btnDiscover);
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDiscoverability();
            }
        });

    }

    private void setDiscoverability(){
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoverableIntent, DISCOVERY_REQUEST);
    }

    private void AcceptRecordingDialog(final DataRecording dataRecording){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to receive this object: " + dataRecording.getId())
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SaveDataRecordingToDb task = new SaveDataRecordingToDb();
                        task.execute(dataRecording);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class SaveDataRecordingToDb extends AsyncTask<DataRecording, Void, Void> {

        @Override
        protected Void doInBackground(DataRecording... dataRecordings) {
            DataRecording dr = dataRecordings[0];
            AppDataBase dataBase = AppDataBase.getInstance(getApplicationContext());
            dataBase.dataRecordingModel().insertDataRecording(dr);
            dataBase.packageModel().insertPackage(dr.pack);
            for (DataSegment ds: dr.dataSegments  ) {
                dataBase.dataSegmentModel().insertDataSegment(ds);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(getApplicationContext(),DisplayRecordingsActivity.class );
            startActivity(intent);
        }
    }

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
        public static final int CONNECTION_ACCEPTED = 3;

        // ... (Add other message types here as needed.)
    }

//***********************************************************ACCEPT THREAD**************************************************
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("ArneIsAwsome!", uuid);
            } catch (IOException e) {
                Log.e("MAInActivity", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                    comThread = new ConnectedThread(socket);
                    comThread.start();
                } catch (IOException e) {
                    Log.e("MainActivity", "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    // manageMyConnectedSocket(socket);
                    String deviceName = socket.getRemoteDevice().getName();
                    Message connectionAcceptedMsg =
                            mHandler.obtainMessage(MessageConstants.CONNECTION_ACCEPTED, deviceName);
                    connectionAcceptedMsg.sendToTarget();

                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("MainActivity", "Could not close the connect socket", e);
            }
        }
    }
//**************************************************CONNECTED THREAD*************************************************************
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                //     ObjectInputStream in = new ObjectInputStream(mmInStream);
               //      dataRecording = (DataRecording)in.readObject();
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = mHandler.obtainMessage(
                            MessageConstants.MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }

            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }

        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
