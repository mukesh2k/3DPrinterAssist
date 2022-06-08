package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chilkatsoft.CkSsh;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Paint extends Activity {
    static String ipaddress="192.168.100.100";
    static CkSsh ssh;
    static final String TAG = "Chilkat";    Handler h;
    TextView connect;
    ProgressBar pc;
    static Boolean good=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint);
        connect=(TextView)findViewById(R.id.con);
        Button help,brail,paint,ipsave;
        h=new Handler();
        pc=(ProgressBar)findViewById(R.id.prog);
        help=(Button) findViewById(R.id.help);
        brail=(Button) findViewById(R.id.Braille);
        paint=(Button) findViewById(R.id.Paint);
        ipsave=(Button) findViewById(R.id.ipsave);
        paint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(good==false)
                {
                    Toast.makeText(getApplicationContext(),"Not connected",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        ipsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runThread async=new runThread();
                new Thread(async).start();
                return;
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Help.class);
                startActivity(intent);
            }
        });
        brail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(good==false)
                {
                    Toast.makeText(getApplicationContext(),"Not connected",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(),Braille.class);
                startActivity(intent);
            }
        });

    }

    public class runThread extends Thread
    {
        @Override
        public void run() {
            super.run();
            String cur="192.168.4.1";
            h.post(()->{
                pc.setVisibility(View.VISIBLE);
                connect.setText("Processing... Wait...");
                connect.setTextColor(Color.rgb(255,255,0));
            });

            ipaddress=cur;
            ssh = new CkSsh();

            // Connect to an SSH server:
            String hostname;
            int port;

            // Hostname may be an IP address or hostname:
            hostname =ipaddress;
            port = 22;

            boolean success = ssh.Connect(hostname,port);
            if (success != true) {
                Log.i(TAG, ssh.lastErrorText());
                h.post(()->{
                    pc.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),"Wifi is not connected with the actual printer wifi.",Toast.LENGTH_LONG).show();
                    connect.setText("Not connected");
                    connect.setTextColor(Color.rgb(255,0,0));
                });
                return;
            }

            // Wait a max of 5 seconds when reading responses..
            ssh.put_IdleTimeoutMs(1000);

            // Authenticate using login/password:
            success = ssh.AuthenticatePw("pi","admin");
            if (!success) {
                Log.i(TAG, ssh.lastErrorText());
                h.post(()->{
                    pc.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),"LoginID or password is wrong, contact developer",Toast.LENGTH_LONG).show();
                    connect.setText("Not connected");
                    connect.setTextColor(Color.rgb(255,0,0));
                });

                return;
            }
            h.post(()->{
                pc.setVisibility(View.INVISIBLE);
                connect.setText("Connected");
                connect.setTextColor(Color.rgb(0,255,0));
                Toast.makeText(getApplicationContext(),"Connection Established",Toast.LENGTH_LONG).show();
                good=true;
            });
        }
    }
    static {
        System.loadLibrary("chilkat");

        // Note: If the incorrect library name is passed to System.loadLibrary,
        // then you will see the following error message at application startup:
        //"The application <your-application-name> has stopped unexpectedly. Please try again."
    }
}
