package tunertest.com.eric.tran.tunertest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.appcompat.*;
//import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.note_text) TextView noteText;

    @BindView(R.id.pitch_text) TextView pitchText;


    //Requesting run-time permissions

    //Create placeholder for user's consent to record_audio permission.
    //This will be used in handling callback
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
//            recordAudio();
        }
    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
//                    recordAudio();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //check for audio permission
        requestAudioPermissions();


        AudioDispatcher dispatcher =
                AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);


        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e) {
                final float pitchInHz = res.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processPitch(pitchInHz);
                    }
                });
            }
        };

        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);

        dispatcher.addAudioProcessor(pitchProcessor);

        Thread audioThread = new Thread(dispatcher, "Audio Thread");
        audioThread.start();


    }

    public void processPitch(float pitchInHz) {
        pitchText.setText("" + pitchInHz);
        String note = getNoteFromPitch(pitchInHz);
        noteText.setText(note);

        Log.v("ERIC", "pitch: " + pitchInHz);

//        if (pitchInHz >= 110 && pitchInHz < 123.47) {
//            //A
//            noteText.setText("A");
//        } else if (pitchInHz >= 123.47 && pitchInHz < 130.81) {
//            //B
//            noteText.setText("B");
//        } else if (pitchInHz >= 130.81 && pitchInHz < 146.83) {
//            //C
//            noteText.setText("C");
//        } else if (pitchInHz >= 146.83 && pitchInHz < 164.81) {
//            //D
//            noteText.setText("D");
//        } else if (pitchInHz >= 164.81 && pitchInHz <= 174.61) {
//            //E
//            noteText.setText("E");
//        } else if (pitchInHz >= 174.61 && pitchInHz < 185) {
//            //F
//            noteText.setText("F");
//        } else if (pitchInHz >= 185 && pitchInHz < 196) {
//            //G
//            noteText.setText("G");
//        }


    }

    public String getNoteFromPitch(float pitch){
        String returnChar = "Q";
        if (
                pitch < 16.5 || //C0
                pitch >=31.5 && pitch <34 || //C1
                pitch >= 63.5 && pitch < 67 || //C2
                pitch >=127.5 && pitch < 135 || //C3
                pitch >=254.5 && pitch < 270 || //C4
                pitch >=510 && pitch < 538.5 || //C5
                pitch >=1017.5 && pitch < 1078 || //C6
                pitch >=2034.5 && pitch < 2155.5 || //C7
                pitch >=4068.5 && pitch < 4310.5 ){ //C8
            returnChar = "C";
        } else if(
                pitch >=16.5 && pitch < 17.5 || //C#0
                pitch >=34 && pitch <36 || //C#1
                pitch >= 67 && pitch < 71 || //C#2
                pitch >=135 && pitch < 143 || //C#3
                pitch >=270 && pitch < 286 || //C#4
                pitch >=538.5 && pitch < 570.5 || //C#5
                pitch >=1078 && pitch < 1142 || //C#6
                pitch >=2155.5 && pitch < 2283.5 || //C#7
                pitch >=4310.5 && pitch < 4567 ){ //C#8
            returnChar = "C#";
        } else if (
                pitch >= 17.5 && pitch < 19 || //D0
                pitch >= 36 && pitch <38 || //D1
                pitch >= 71 && pitch < 75.5 || //D2
                pitch >= 143 && pitch < 151.5 || //D3
                pitch >= 286 && pitch < 302.5 || //D4
                pitch >= 570.5 && pitch < 604.5 || //D5
                pitch >= 1142 && pitch < 1210 || //D6
                pitch >= 2283.5 && pitch < 2419 || //D7
                pitch >= 4567 && pitch < 4838.5 ) { //C#8
            returnChar = "D";
        }
        return returnChar;
    }



}
