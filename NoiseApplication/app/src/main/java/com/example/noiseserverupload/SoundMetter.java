package com.example.noiseserverupload;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class SoundMetter {
    private MediaRecorder mRecorder = null;

    public void start() {

        if (mRecorder == null) {
            Log.d("SoundMetter", "startSound: ");
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mRecorder.start();
        }

    }

    public void stop() {
        Log.d("SoundMetter", "stopSound: ");
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude() {
        if (mRecorder != null) {
            Log.d("SoundMetter", "amplitude: vaai");
            return  mRecorder.getMaxAmplitude();
        }
        else
            return 0;


    }
}
