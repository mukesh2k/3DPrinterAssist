package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
    static final String TAG = "Chilkat";
    static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    static final Pattern IPv4_PATTERN = Pattern.compile(IPV4_REGEX);
    static Boolean good=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint);
        TextView connect=(TextView)findViewById(R.id.con);
        EditText ip=(EditText)findViewById(R.id.editip);
        Button help,brail,paint,ipsave;
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
                String cur=ip.getText().toString();
                if(validaddress(cur)==true)
                {
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
                        Toast.makeText(getApplicationContext(),"IP Address is wrong",Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Wait a max of 5 seconds when reading responses..
                    ssh.put_IdleTimeoutMs(5000);

                    // Authenticate using login/password:
                    success = ssh.AuthenticatePw("pi","admin");
                    if (success != true) {
                        Log.i(TAG, ssh.lastErrorText());
                        Toast.makeText(getApplicationContext(),"LoginID or password is wrong, contact developer",Toast.LENGTH_LONG).show();
                        return;
                    }
                    connect.setText("Connected");
                    connect.setTextColor(Color.rgb(0,255,0));
                    Toast.makeText(getApplicationContext(),"Connection Established",Toast.LENGTH_LONG).show();
                    good=true;

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Enter a valid IpV4 address",Toast.LENGTH_LONG).show();
                    return;
                }
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
    public static boolean validaddress(String ip)
    {
        if (ip == null) {
            return false;
        }

        Matcher matcher = IPv4_PATTERN.matcher(ip);
        return matcher.matches();
    }
    static {
        System.loadLibrary("chilkat");

        // Note: If the incorrect library name is passed to System.loadLibrary,
        // then you will see the following error message at application startup:
        //"The application <your-application-name> has stopped unexpectedly. Please try again."
    }
}
