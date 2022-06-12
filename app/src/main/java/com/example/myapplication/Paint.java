package com.example.myapplication;

import static com.jcraft.jsch.JSch.setConfig;
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

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Paint extends Activity {
    Handler h;
    TextView connect;
    ProgressBar pc;
    static ChannelSftp channelSftp;
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

            JSch jsch = new JSch();
            jsch.setConfig("StrictHostKeyChecking", "no");
            String username="pi",remoteHost="192.168.4.1",password="admin";
            Session jschSession = null;
            boolean success=false;


            h.post(()->{
                pc.setVisibility(View.VISIBLE);
                connect.setText("Processing... Wait...");
                connect.setTextColor(Color.rgb(255,255,0));
            });
            int c=3;
            while(c>0)
            {
                try {
                    jschSession = jsch.getSession(username, remoteHost);
                    jschSession.setPassword(password);
                    jschSession.setTimeout(3000);
                    jschSession.connect();
                    channelSftp =(ChannelSftp) jschSession.openChannel("sftp");
                    channelSftp.connect();
                    success=true;
                    break;
                } catch (JSchException e){
                    e.printStackTrace();
                }
                c--;
            }




            if (success != true) {
                h.post(()->{
                    pc.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),"Wifi is not connected with the actual printer wifi.",Toast.LENGTH_LONG).show();
                    connect.setText("Not connected");
                    connect.setTextColor(Color.rgb(255,0,0));
                });
                return;
            }

            if (!success) {

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

}
