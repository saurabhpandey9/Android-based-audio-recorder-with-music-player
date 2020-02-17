package com.pandey.saurabh.recorder;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.util.IndianCalendar;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {

    private static final int PERMISSION_CODE =22 ;
    private NavController navController;
    private ImageView lstbtn;
    private  ImageView record_btn;
    private boolean isRecording=false;
    private String recordpermission=Manifest.permission.RECORD_AUDIO;
    private MediaRecorder mediaRecorder;
    private String recordingfilename;
    private TextView filename;

    private Chronometer chronometer;// also called timer


    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController= Navigation.findNavController(view);
        lstbtn= (ImageView)view.findViewById(R.id.record_lst_btn);
        record_btn=(ImageView) view.findViewById(R.id.record_btn);
        chronometer=view.findViewById(R.id.record_timer);
        filename=(TextView)view.findViewById(R.id.record_filename);

        lstbtn.setOnClickListener(this);
        record_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.record_lst_btn:
                navController.navigate(R.id.action_recordFragment_to_audioList);
                break;

            case R.id.record_btn:
                if(isRecording){
                    //stop recording
                    stoprecording();
                    record_btn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped,null));
                    isRecording=false;
                }
                else {
                    //start recording
                    startrecording();
                    
                    if(checkrecording()){
                        record_btn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording,null));
                        isRecording=true;
                    }

                }
                break;


        }

    }

    private void startrecording() {

        chronometer.setBase(SystemClock.elapsedRealtime());//to reset the timer when new recording start.
        chronometer.start();

        String recordingfilepath=getActivity().getExternalFilesDir("/").getAbsolutePath();

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYY_MM_DD_HH_MM_SS", Locale.ENGLISH);
        Date date=new Date();

        recordingfilename="Recording_"+simpleDateFormat.format(date)+".mp3";
        filename.setText("Recording Started\n"+recordingfilename); //recording file name display
        filename.setTextColor(Color.RED);

        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordingfilepath+"/"+recordingfilename);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }

    private void stoprecording() {
        chronometer.stop(); //timer stop

        filename.setText("Recording Stopped \n"+recordingfilename);
        filename.setTextColor(Color.BLUE);//change file name color to blue when recording stop


        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder=null;
    }

    private boolean checkrecording() {
        if(ActivityCompat.checkSelfPermission(getContext(), recordpermission)== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            ActivityCompat.requestPermissions(getActivity(),new String[]{recordpermission},PERMISSION_CODE);
            return false;
        }
    }
}
