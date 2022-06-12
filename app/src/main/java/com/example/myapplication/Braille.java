package com.example.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Braille extends Activity {
    String input="",name;
    Handler h;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.braille);
        EditText text=(EditText) findViewById(R.id.lines);
        Button send=(Button) findViewById(R.id.button10);
        h=new Handler();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                name= System.currentTimeMillis() / 1000 +"braille.txt";
                intent.putExtra(Intent.EXTRA_TITLE,name);
                intent.setType("text/plain");
                input=text.getText().toString();
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1)
        {
            if (resultCode==RESULT_OK)
            {
                Uri uri=data.getData();
                try
                {
                    OutputStream os=getContentResolver().openOutputStream(uri);
                    os.write(input.getBytes());
                    os.close();
                    System.out.println(uri.getPath());
                    String remotePath = "/home/pi/Desktop/3Dprinter/"+name;
                    String localPath = "/storage/emulated/0/Download/"+name;
                    runThread rune=new runThread(localPath,remotePath,h);
                    new Thread(rune).start();
                } catch (IOException e) {
                        e.printStackTrace();
                    }


            }
        }
    }
    public class runThread extends Thread
    {
        String localPath,remotePath;
        Handler hand;
        boolean success= false;
        public runThread(String l, String r, Handler h)
        {
            localPath=l;
            remotePath=r;
            hand=h;
        }


        public void run()
        {
            super.run();
            try {
                Paint.channelSftp.put(localPath, remotePath);
                success=true;
            } catch (SftpException e) {
                e.printStackTrace();
            }
            hand.post(()->{
                if (success)
                    Toast.makeText(getApplicationContext(),"File loaded to the printer successfully",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(),"SFTP problem",Toast.LENGTH_LONG).show();
            });

        }
    }
}
