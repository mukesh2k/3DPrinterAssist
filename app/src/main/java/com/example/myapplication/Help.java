package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class Help extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        TextView texts=(TextView) findViewById(R.id.data);
        String data="Paint:\n1)Click the Paint and give your sketch.\n2)Click save and enter your requirements\n" +
                "3)Click print and select bluetooth and select the 3D printer\n" +
                "4)Printing starts when the file sent successfully\n" +
                "\n" +
                "Braille:\n" +
                "1)Click Braille and enter the required text(This supports only alphabets and numbers).\n" +
                "2)Click print and select bluetooth and select the 3D printer\n" +
                "3)Printing starts when the file sent successfully" ;
        texts.setText(data);

    }
}
