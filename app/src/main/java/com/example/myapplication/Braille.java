package com.example.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.chilkatsoft.CkScp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Braille extends Activity {
    String input="",name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.braille);
        EditText text=(EditText) findViewById(R.id.lines);
        Button send=(Button) findViewById(R.id.button10);

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
                        CkScp scp = new CkScp();
                        boolean success = scp.UseSsh(Paint.ssh);
                        if (success != true) {
                            Toast.makeText(getApplicationContext(),"SCP failed",Toast.LENGTH_LONG).show();
                            Log.i(Paint.TAG, scp.lastErrorText());
                            return;
                        }

                        String remotePath = "~/Desktop/3Dprinter/"+name;
                        String localPath = "/storage/emulated/0/Download/"+name;
                        success = scp.UploadFile(localPath,remotePath);
                        if (success != true) {
                            Log.i(Paint.TAG, scp.lastErrorText());
                            Toast.makeText(getApplicationContext(),"Give file and storage permission to this app",Toast.LENGTH_LONG).show();
                            return;
                        }

                        Log.i(Paint.TAG, "SCP upload file success.");
                        Toast.makeText(getApplicationContext(),"File loaded to the printer successfully",Toast.LENGTH_LONG).show();

                }
                catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }
    static {
        System.loadLibrary("chilkat");

        // Note: If the incorrect library name is passed to System.loadLibrary,
        // then you will see the following error message at application startup:
        //"The application <your-application-name> has stopped unexpectedly. Please try again."
    }
}
