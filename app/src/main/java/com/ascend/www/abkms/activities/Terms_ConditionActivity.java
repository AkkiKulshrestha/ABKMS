package com.ascend.www.abkms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ascend.www.abkms.R;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;

public class Terms_ConditionActivity extends AppCompatActivity {
    ImageView back_btn_toolbar;
    TextView til_text;
    Button BtnAcceptTnC;
    String LanguageSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tnc);

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
        til_text.setText(getResources().getString(R.string.t_n_c));
        LanguageSelected = UtilitySharedPreferences.getPrefs(getApplicationContext(),"LanguageSelected");

        BtnAcceptTnC  = (Button) findViewById(R.id.BtnAcceptTnC);
        BtnAcceptTnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });




    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }
}

