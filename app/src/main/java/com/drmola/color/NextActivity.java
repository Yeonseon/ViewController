package com.drmola.color;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.io.InputStream;
import java.net.Socket;

public class NextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_next);

        Intent intent = getIntent();
        String IP_Addr = intent.getStringExtra("IP");

        try {
            if(android.provider.Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) ==1) {
                android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Settings.System.putInt(getContentResolver(), "screen_brightness", 127);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = (float)0.5;
        getWindow().setAttributes(params);
        LinearLayout back = findViewById(R.id.background);
        back.setBackgroundColor(Color.rgb(255, 255, 255));

        Networking NetworkTread = new Networking(IP_Addr);
        NetworkTread.execute();

        ConnectTread tread = new ConnectTread(IP_Addr);
        tread.start();

    }

    class ConnectTread extends Thread {
        String hostname;

        public ConnectTread(String addr) {
            hostname = addr;
        }

        public void run() {
            try {
                int port = 80;
                Socket sock = new Socket(hostname, port);
                float[] bound = {(float)1.00, (float)0.00};
                while (true) {
                    /* DataInputStream rawdata = new DataInputStream(sock.getInputStream());
                    ObjectInputStream data1 = new ObjectInputStream(rawdata);
                    char[] data = (char[])data1.readObject(); */
                    byte[] data = new byte[10];
                    InputStream in = sock.getInputStream();
                    in.read(data);
                    if(data[0] == 0) {
                        bound = brightnessAdjust(data[1], bound[0], bound[1]);
                    } else if(data[0] == 1) {
                        if(data[4] == 1) {
                            LinearLayout back = findViewById(R.id.background);
                            back.setBackgroundColor(Color.rgb(data[1], data[2], data[3]));
                        }
                        else {
                            return;
                        }
                    } else if(data[0] == 2) {
                        //Settings.System.putInt(getContentResolver(), "screen_brightness", 127);
                        WindowManager.LayoutParams params = getWindow().getAttributes();
                        params.screenBrightness = (float)0.5;
                        getWindow().setAttributes(params);
                        LinearLayout back = findViewById(R.id.background);
                        back.setBackgroundColor(Color.rgb(255, 255, 255));
                    } else {
                        break;
                    }
                }
                sock.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class Networking extends AsyncTask <void, void, void> {

        String IP_addr;
        int portNum = 80;

        Networking(String addr) {
            IP_addr = addr;
        }

        @Override
        protected void doInBackground(byte[] data) {

        }

        @Override
        protected void doInBackground(void... voids) {

        }

        protected void onPreExecute() {
            try {
                Socket sock = new Socket(IP_addr, portNum);
                InputStream input = sock.getInputStream();
                float[] bound = {(float)1.00, (float)0.00};
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void doInBackground(Object... voids) {
            try {
                Socket sock = new Socket(IP_addr, portNum);
                InputStream input = sock.getInputStream();
                float[] bound = {(float)1.00, (float)0.00};
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        protected void onPostExecute() {
            sock
        }
    }

    public float[] brightnessAdjust(byte upordown, float upperbound, float lowerbound) {
        float nowBright;
        try {
            nowBright = android.provider.Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
            float[] error = {(float)0, (float)0};
            return error;
        }

        if(upordown == 0) {
            upperbound = nowBright;
        } else {
            lowerbound = nowBright;
        }

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = (upperbound + lowerbound) / 2;
        getWindow().setAttributes(params);
        float[] newBound = {upperbound, lowerbound};
        return newBound;
    }

}
