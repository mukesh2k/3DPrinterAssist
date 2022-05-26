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
        CkSsh ssh = new CkSsh();

        // Connect to an SSH server:
        String hostname;
        int port;

        // Hostname may be an IP address or hostname:
        hostname = "raspberrypi";
        port = 22;

        boolean success = ssh.Connect(hostname,port);
        if (success != true) {
            Log.i(TAG, ssh.lastErrorText());
            return;
        }

        // Wait a max of 5 seconds when reading responses..
        ssh.put_IdleTimeoutMs(5000);

        // Authenticate using login/password:
        success = ssh.AuthenticatePw("pi","admin");
        if (success != true) {
            Log.i(TAG, ssh.lastErrorText());
            return;
        }

        // Once the SSH object is connected and authenticated, we use it
        // as the underlying transport in our SCP object.
        CkScp scp = new CkScp();

        success = scp.UseSsh(ssh);
        if (success != true) {
            Log.i(TAG, scp.lastErrorText());
            return;
        }

        String remotePath = "~/Desktop/test.txt";
        String localPath = "/home/bob/test.txt";
        success = scp.UploadFile(localPath,remotePath);
        if (success != true) {
            Log.i(TAG, scp.lastErrorText());
            return;
        }

        Log.i(TAG, "SCP upload file success.");

        // Disconnect
        ssh.Disconnect();

    }

    static {
        System.loadLibrary("chilkat");

        // Note: If the incorrect library name is passed to System.loadLibrary,
        // then you will see the following error message at application startup:
        //"The application <your-application-name> has stopped unexpectedly. Please try again."
    }

}

