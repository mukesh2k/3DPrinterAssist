package com.example.myapplication;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.slider.RangeSlider;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.OutputStream;


public class MainActivity extends Activity {

    // creating the object of type DrawView
    // in order to get the reference of the View
    private DrawView paint;

    // creating objects of type button
    private Button save, color, stroke, undo;

    // creating a RangeSlider object, which will
    // help in selecting the width of the Stroke
    private RangeSlider rangeSlider;
    private static final String TAG = "Chilkat";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // getting the reference of the views from their ids
        paint = (DrawView) findViewById(R.id.draw_view);
        rangeSlider = (RangeSlider) findViewById(R.id.rangebar);
        undo = (Button) findViewById(R.id.btn_undo);
        save = (Button) findViewById(R.id.btn_save);
        stroke = (Button) findViewById(R.id.btn_stroke);

        // creating a OnClickListener for each button,
        // to perform certain actions

        // the undo button will remove the most
        // recent stroke from the canvas
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paint.undo();
            }
        });

        // the save button will save the current
        // canvas which is actually a bitmap
        // in form of PNG, in the storage


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.utility_size, null);
                Button saver= (Button)mView.findViewById(R.id.saver);
                EditText width=(EditText)mView.findViewById(R.id.width);
                EditText thick=(EditText)mView.findViewById(R.id.thickness);
                Handler hand=new Handler();
                saver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ww=width.getEditableText().toString(),hh=thick.getEditableText().toString();
                        float w=Float.parseFloat(ww),h=Float.parseFloat(hh);
                        if (w>150 || w<1)
                        {
                            Toast.makeText(getApplicationContext(),"width is too high",Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (h>10 || h<1)
                        {
                            Toast.makeText(getApplicationContext(),"Thickness is too high",Toast.LENGTH_LONG).show();
                            return;
                        }

                    // getting the bitmap from DrawView class

                    Bitmap bmp = paint.save();

                    // opening a OutputStream to write into the file
                    final OutputStream[] imageOutStream = {null};

                    ContentValues cv = new ContentValues();
                    // name of the file
                        String name=System.currentTimeMillis() / 1000 +"_"+ww+"_"+hh+".png";
                    cv.put(MediaStore.Images.Media.DISPLAY_NAME, name);

                    // type of the file
                    cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

                    // location of the file to be saved
                    cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                    // get the Uri of the file which is to be created in the storage
                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

                    try {
                        // open the output stream with the above uri
                        imageOutStream[0] = getContentResolver().openOutputStream(uri);

                        // this method writes the files in storage
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream[0]);

                        // close the output stream after use
                        imageOutStream[0].close();
                        String remotePath = "/home/pi/Desktop/3Dprinter/"+name;
                        String localPath = "/storage/emulated/0/"+Environment.DIRECTORY_PICTURES+"/"+name;
                        runThread rune=new runThread(localPath,remotePath,hand);
                        new Thread(rune).start();


                        // Disconnect
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"SCP problemz",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    System.out.println("done");

                    }

                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();



            }
        });
        // the color button will allow the user
        // to select the color of his brusr
        // the button will toggle the visibility of the RangeBar/RangeSlider
        stroke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rangeSlider.getVisibility() == View.VISIBLE)
                    rangeSlider.setVisibility(View.GONE);
                else
                    rangeSlider.setVisibility(View.VISIBLE);
            }
        });

        // set the range of the RangeSlider
        rangeSlider.setValueFrom(0.0f);
        rangeSlider.setValueTo(100.0f);

        // adding a OnChangeListener which will
        // change the stroke width
        // as soon as the user slides the slider
        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                paint.setStrokeWidth((int) value);
            }
        });

        // pass the height and width of the custom view
        // to the init method of the DrawView object
        ViewTreeObserver vto = paint.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                paint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = paint.getMeasuredWidth();
                int height = paint.getMeasuredHeight();
                paint.init(height, width);
            }
        });
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
