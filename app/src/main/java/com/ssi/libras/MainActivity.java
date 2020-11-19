package com.ssi.libras;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button btnRecord, btnStopRecord, btnTranslate;
    private TextView txtLibras;
    private TextView txtIbm;

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    Ibm ibm = new Ibm();
    Vlibras libras = new Vlibras();
    String pathSave = "";
    String recordfile = "";
    String ibmText;
    String vlibrasText;

    final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtIbm = (TextView) findViewById(R.id.txtIbm);
        txtLibras = (TextView) findViewById(R.id.txtLibras);
        btnRecord = (Button)findViewById(R.id.btnStartRecord);
        btnStopRecord = (Button)findViewById(R.id.btnStopRecord);
        btnTranslate = (Button)findViewById(R.id.btnTranslate);

        if(!checkPermissionFromDevice()) requestPermission();


        //record button rules
        btnRecord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            btnStopRecord.setEnabled(true);
            if(checkPermissionFromDevice()){}

            setUpMediaRecorder();
                try{
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                }catch (IOException e){
                    e.printStackTrace();
                }

                //turning play and stop audios off while recording
                btnTranslate.setEnabled(false);

                Toast.makeText(MainActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
            }
        });

        btnStopRecord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                btnStopRecord.setEnabled(false);
                btnTranslate.setEnabled(true);
                btnRecord.setEnabled(true);
            }
        });

        btnTranslate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                btnStopRecord.setEnabled(false);
                btnRecord.setEnabled(false);
                btnTranslate.setEnabled(true);
                mediaPlayer = new MediaPlayer();

                try{
                    mediaPlayer.setDataSource(pathSave + "/" + recordfile);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //mediaPlayer.start();

                Toast.makeText(MainActivity.this, "Enviando para Tradução...", Toast.LENGTH_SHORT).show();
                ibmText = ibm.setUpIbmService(pathSave, recordfile);
                txtIbm.setText(ibmText);
                System.out.println(ibmText);

                String data =
                        "{"+"\"text\"" + ":" + " " + "\"" + ibmText + "\" "+"}";
                libras.getContext(getApplicationContext());
                vlibrasText = libras.post(data);
                txtLibras.setText(vlibrasText);

                btnStopRecord.setEnabled(true);
                btnRecord.setEnabled(true);
            }
        });

    }


    //checking if already exist permission.
    private boolean checkPermissionFromDevice(){
        //permission to save the content
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //permission to record content
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    //requesting device permissions
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO

        }, REQUEST_PERMISSION_CODE);
    }

    //setting up the recorder
    private void setUpMediaRecorder(){
        pathSave = getApplicationContext().getExternalFilesDir("/").getAbsolutePath();
        Log.d("My Path->", pathSave);
        recordfile = "filename.ogg";
        Log.d("Path With File->", pathSave + "/" + recordfile);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.OGG);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.VORBIS);
        mediaRecorder.setOutputFile(pathSave + "/" + recordfile);
    }
}
