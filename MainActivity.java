package com.github.jacketapp.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jacketapp.app.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MainActivity extends FragmentActivity {


    // Google Map
    private SupportMapFragment fragment;
    private GoogleMap googleMap;
    private int REQUEST_ENABLE_BT = 2;
    private ReadXMLFile wf;
    private String dg;
    private Message msg;

    private TextView degreeText;
    private TextView sensorText;
    private TextView tempText;
    private SeekBar tempSlider;
    private TextView warningText;
    private Button warningButton;
    private BluetoothDevice mDevice;
    private boolean mIsBluetoothConnected;
    private BluetoothAdapter mBluetoothAdapter;

    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        sensorText = (TextView)findViewById(R.id.sensorText);
        degreeText = (TextView)findViewById(R.id.degreeText);
        tempText = (TextView)findViewById(R.id.tempText);
        tempSlider = (SeekBar)findViewById(R.id.tempSlider);

        warningText = (TextView)findViewById(R.id.warningText);
        warningButton = (Button)findViewById(R.id.warningButton);

        warningText.setVisibility(View.GONE);
        warningButton.setVisibility(View.GONE);

        /*if(strInput.contains("a")){
            warningText.setVisibility(1);
            warningButton.setVisibility(1);
        }*/



        tempText.setText("Varme: "+ 0 + "/" + tempSlider.getMax() + "°C");
        sensorText.setText("Jakke: 25°C");
        degreeText.setText("");

        /*mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
// If there are paired devices


        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mDevice = device;
            }
        }*/

       //System.out.println("BLUETOOTH:" + mDevice.getName() + "\n" + mDevice.getAddress());


        warningButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /*try {
                    mBTSocket.getOutputStream().write("b".getBytes());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/
                warningText.clearAnimation();
                warningText.setVisibility(View.GONE);
                warningButton.setVisibility(View.GONE);

            }
        });

        tempSlider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;
                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progressValue, boolean fromUser) {
                        progress = progressValue;
                        tempText.setText("Varme: "+ progressValue + "/" + seekBar.getMax() +"°C");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do something here,
                        //if you want to do anything at the start of
                        // touching the seekbar
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        //tempText.setText("Varme: "+ progress + "/" + seekBar.getMax());
                        /*try {
                            mBTSocket.getOutputStream().write(("c" + (String.valueOf(progress) + "!")).getBytes());
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }*/
                    }
                });

        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                // TODO Auto-generated method stub
                double lat =  arg0.getLatitude();
                double lng = arg0.getLongitude();

                LatLng coord = new LatLng(lat,lng);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(coord).zoom(14).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                 try {
                     dg = new ReadXMLFile().execute("http://api.yr.no/weatherapi/locationforecast/1.8/?lat=" + lat + ";lon=" + lng).get();
                 }catch(InterruptedException e){
                     e.printStackTrace();
                 }catch(ExecutionException e){
                     e.printStackTrace();
                }
                degreeText.setText("Ute: " + dg + "°C");



                try {
                    msg = new ReadJSON().execute("http://192.168.1.142:3002/index.json").get();
                    if(msg.Code == 1){
                        //try {
                           /* mBTSocket.getOutputStream().write(("a").getBytes());*/
                            warningText.setVisibility(View.VISIBLE);
                            warningButton.setVisibility(View.VISIBLE);
                            warningText.setText(msg.Content);
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(100); //You can manage the time of the blink with this parameter
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        warningText.startAnimation(anim);
                        /*} catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }*/
                    }
                }catch(InterruptedException e){
                    e.printStackTrace();
                }catch(ExecutionException e){
                    e.printStackTrace();
                }


            }
        });

    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            fragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            googleMap = fragment.getMap();
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }



    @Override
    protected void onResume() {
       /* Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
// If there are paired devices


        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mDevice = device;
            }
        }
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }*/
        super.onResume();
        initilizeMap();
    }

    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;

        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();


                while (!bStop) {
                    byte[] buffer = new byte[256];

                        inputStream.read(buffer);
                        int i = 0;
						/*
						 * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
						 */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
                        }
                        final String strInput = new String(buffer, 0, i);

						/*
						 * If checked then receive text, better design would probably be to stop thread if unchecked and free resources, but this is a quick fix
						 */


                        sensorText.post(new Runnable() {
                            @Override
                            public void run() {

                                if (strInput.startsWith("c") && strInput.endsWith("!")) {
                                    sensorText.setText("Jakke: " + strInput.substring(1, strInput.length() - 1) + "°C");

                                    //Uncomment below for testing
                                    //mTxtReceive.append("\n");
                                    //mTxtReceive.append("Chars: " + strInput.length() + " Lines: " + mTxtReceive.getLineCount() + "\n");
                                }

                            }
                        });



                    Thread.sleep(500);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }
}
/*
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554
        }

        @Override
        protected Void doInBackground(Void... devices) {
            System.out.println("Connecting");
            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    //BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
                // Unable to connect to device
                e.printStackTrace();
                mConnectSuccessful = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device. Is it a Serial device? Also check if the UUID is correct in the settings", Toast.LENGTH_LONG).show();
                finish();
            } else {
                //msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }

}*/