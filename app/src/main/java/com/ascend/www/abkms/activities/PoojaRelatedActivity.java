package com.ascend.www.abkms.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ascend.www.abkms.R;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;

public class  PoojaRelatedActivity extends AppCompatActivity implements View.OnTouchListener {
    ImageView back_btn_toolbar;
    TextView tv_pooja_txt,tv_history_txt;
    Button BtnSubmit;
    final static float move = 200;
    float mRatio = 1.0f;
    int mBaseDist;
    float mBaseRatio;
    float fontsize = 13;
    String LanguageSelected="Hindi",StrPoojaScreen;
    RadioGroup RGLang;
    RadioButton RbHindi,RbEnglish;
    ExpandableListView expandable_view;
    LinearLayout LayoutPlayer;
    ImageView iv_play,iv_pause,iv_stop;
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pooja_realted_screen);

        findViewByIds();
    }

    private void findViewByIds() {

        back_btn_toolbar = (ImageView)findViewById(R.id.back_btn_toolbar);
        back_btn_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView til_text = (TextView)findViewById(R.id.til_text);

        StrPoojaScreen = UtilitySharedPreferences.getPrefs(getApplicationContext(),"PoojaScreen");

        RGLang = (RadioGroup)findViewById(R.id.RGLang);
        RbHindi = (RadioButton)findViewById(R.id.RbHindi);
        RbEnglish = (RadioButton)findViewById(R.id.RbEnglish);
        LanguageSelected = "Hindi";

        LayoutPlayer = (LinearLayout)findViewById(R.id.LayoutPlayer);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_pause = (ImageView)findViewById(R.id.iv_pause);
        iv_stop = (ImageView)findViewById(R.id.iv_stop);

        RGLang.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    LanguageSelected = rb.getText().toString();
                    SetValueToTextView();
                }

            }
        });




        //LanguageSelected = UtilitySharedPreferences.getPrefs(getApplicationContext(),"LanguageSelected");


        if(StrPoojaScreen!=null && !StrPoojaScreen.equalsIgnoreCase("") && !StrPoojaScreen.equalsIgnoreCase("null")){
            if(StrPoojaScreen.equalsIgnoreCase("PoojaVidhi")){
                LayoutPlayer.setVisibility(View.GONE);
                if(LanguageSelected.equalsIgnoreCase("Hindi")) {
                    til_text.setText(getResources().getString(R.string.pooja_process_heading_hin));
                }else {
                    til_text.setText(getResources().getString(R.string.pooja_process_heading));
                }
            }else if(StrPoojaScreen.equalsIgnoreCase("VarthKatha")){
                LayoutPlayer.setVisibility(View.VISIBLE);
                if(LanguageSelected.equalsIgnoreCase("Hindi")) {
                    til_text.setText(getResources().getString(R.string.story_heading_hin));
                }else {
                    til_text.setText(getResources().getString(R.string.story_heading));
                }
            }else if(StrPoojaScreen.equalsIgnoreCase("Chalisa")){
                LayoutPlayer.setVisibility(View.GONE);
                if(LanguageSelected.equalsIgnoreCase("Hindi")) {
                    til_text.setText(getResources().getString(R.string.chalisa_heading_hin));
                }else {
                    til_text.setText(getResources().getString(R.string.chalisa_heading));
                }
            }else if(StrPoojaScreen.equalsIgnoreCase("Aarti")){
                LayoutPlayer.setVisibility(View.VISIBLE);
                if(LanguageSelected.equalsIgnoreCase("Hindi")) {
                    til_text.setText(getResources().getString(R.string.arti_heading_hin));
                }else {
                    til_text.setText(getResources().getString(R.string.arti_heading));
                }
            }else if(StrPoojaScreen.equalsIgnoreCase("KulshresthaInfo")){
                LayoutPlayer.setVisibility(View.GONE);
                if(LanguageSelected.equalsIgnoreCase("Hindi")) {
                    til_text.setText(getResources().getString(R.string.about_heading_hin));
                }else {
                    til_text.setText(getResources().getString(R.string.about_heading));
                }
            }

        }

        tv_pooja_txt = (TextView)findViewById(R.id.tv_pooja_txt);
        tv_pooja_txt.setTextSize(mRatio + 13);

        tv_history_txt  = (TextView)findViewById(R.id.tv_history_txt);
        tv_history_txt.setVisibility(View.GONE);
       SetValueToTextView();
        tv_history_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String History_pdf_url = "http://royalsparsh.in/kulshrestha_app_api/asset/Historyofkulshrestha.pdf";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(History_pdf_url));
                startActivity(browserIntent);
            }
        });

        tv_pooja_txt.setOnTouchListener(this);


        BtnSubmit  = (Button) findViewById(R.id.BtnSubmit);
        BtnSubmit.setText(getResources().getString(R.string.go_back));
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });




    }

    private void SetValueToTextView() {
        if(StrPoojaScreen!=null && !StrPoojaScreen.equalsIgnoreCase("") && !StrPoojaScreen.equalsIgnoreCase("null")){
            if(StrPoojaScreen.equalsIgnoreCase("PoojaVidhi")){
                LayoutPlayer.setVisibility(View.GONE);
                if(LanguageSelected.equalsIgnoreCase("Hindi")) {
                    tv_pooja_txt.setText(getResources().getString(R.string.pooja_vidhi_hin));
                }else {
                    tv_pooja_txt.setText(getResources().getString(R.string.pooja_vidhi));
                }
                tv_history_txt.setVisibility(View.GONE);
            }else if(StrPoojaScreen.equalsIgnoreCase("VarthKatha")){
                LayoutPlayer.setVisibility(View.VISIBLE);
                if(LanguageSelected.equalsIgnoreCase("Hindi")) {
                    tv_pooja_txt.setText(getResources().getString(R.string.varth_katha_hin));
                }else {
                    tv_pooja_txt.setText(getResources().getString(R.string.varth_katha));
                }
                tv_history_txt.setVisibility(View.GONE);
            }else if(StrPoojaScreen.equalsIgnoreCase("Chalisa")){
                LayoutPlayer.setVisibility(View.GONE);
                if(LanguageSelected.equalsIgnoreCase("Hindi")) {
                    tv_pooja_txt.setText(getResources().getString(R.string.full_chalisa_hin));
                }else {
                    tv_pooja_txt.setText(getResources().getString(R.string.full_chalisa));
                }
                tv_history_txt.setVisibility(View.GONE);
            }else if(StrPoojaScreen.equalsIgnoreCase("Aarti")){
                LayoutPlayer.setVisibility(View.VISIBLE);
                if(LanguageSelected.equalsIgnoreCase("Hindi")) {
                    tv_pooja_txt.setText(getResources().getString(R.string.full_aarti_hin));
                }else {
                    tv_pooja_txt.setText(getResources().getString(R.string.full_aarti));
                }
                tv_history_txt.setVisibility(View.GONE);
            }else if(StrPoojaScreen.equalsIgnoreCase("KulshresthaInfo")){
                LayoutPlayer.setVisibility(View.GONE);
                if(LanguageSelected.equalsIgnoreCase("Hindi")) {
                    tv_pooja_txt.setText(getResources().getString(R.string.origin_of_kulshrestha_hin));
                    tv_history_txt.setText(getResources().getString(R.string.click_to_view_history_of_kulshrestha_hin));

                }else {
                    tv_pooja_txt.setText(getResources().getString(R.string.origin_of_kulshrestha));
                    tv_history_txt.setText(getResources().getString(R.string.click_to_view_history_of_kulshrestha));

                }
                tv_history_txt.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            int action = event.getAction();
            int mainaction = action&MotionEvent.ACTION_MASK;

            if (mainaction == MotionEvent.ACTION_POINTER_DOWN) {

                mBaseDist = getDistanceFromEvent(event);
                mBaseRatio = mRatio;

            } else {

                float scale = (getDistanceFromEvent(event)-mBaseDist)/move;
                float factor = (float) Math.pow(2,scale);
                mRatio = Math.min(1024.0f,Math.max(0.1f,mBaseRatio*factor));
                tv_pooja_txt.setTextSize(mRatio+15);


            }

        }
        return true;
    }

    // good function to get the distance between the multiple touch
    int getDistanceFromEvent(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }

    public void OnPlay(View view) {
        if(mediaPlayer == null){

            if(StrPoojaScreen!=null && !StrPoojaScreen.equalsIgnoreCase("") && !StrPoojaScreen.equalsIgnoreCase("null")){
               if(StrPoojaScreen.equalsIgnoreCase("VarthKatha")){
                   mediaPlayer = MediaPlayer.create(this,R.raw.chitraguptji_ki_katha);
                }else if(StrPoojaScreen.equalsIgnoreCase("Aarti")){
                   mediaPlayer = MediaPlayer.create(this,R.raw.chitraguptji_ki_aarti);
                }




            }

            if(mediaPlayer!=null) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        StopPlayer();
                    }
                });

            }


        }
    }

    public void OnPause(View view) {
        if(mediaPlayer!=null) {
            mediaPlayer.pause();
        }

    }

    public void OnStop(View view) {
        StopPlayer();
    }

    private void StopPlayer(){
        if(mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        StopPlayer();
    }
}
