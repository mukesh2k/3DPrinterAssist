package com.example.myapplication;
import com.chilkatsoft.chilkat;
import com.chilkatsoft.*;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class Help extends Activity {
    private static final String TAG = "Chilkat";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        TextView texts = (TextView) findViewById(R.id.data);
        String data = "Setup:\n" +

                "1)Turn on the raspberrypi.\n" +
                "2)In your android phone, connect to wifi 'printer' using the credentials given at the printer.\n" +
                "4)Open 3DPrintAssist app and click connect.\n" +
                "5)Wait till it shows connected and then go with your required option.\n\n" +
                "Paint:\n1)Click the Paint and give your sketch.\n2)Click save and enter your requirements.\n" +
                "3)Click print.\n" +
                "4)Printing starts when the file sent successfully\n" +
                "\n\n" +
                "Braille:\n" +
                "1)Click Braille and enter the required text(This supports only alphabets and numbers).\n" +
                "2)Click print and then it will take a navigator to save the file. DO NOT CHANGE THE FILE LOCATION OR NAME OF THE FILE\n" +
                "3)Click print, printing starts when the file sent successfully";
        texts.setText(data);

    }

}

