package com.ascend.www.abkms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.ascend.www.abkms.NewDashboard;
import com.ascend.www.abkms.R;
import com.ascend.www.abkms.utils.UtilitySharedPreferences;

public class SettingsActivity extends AppCompatActivity {

    ImageView back_btn_toolbar;
    TextView til_text;
    CardView CardPujaVidhi,CardVarthKatha,CardChalisa,CardAarti,CardInfoOfKulshrestha,CardTerms_Condition,CardKKS;
    String LanguageSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
        til_text.setText(getResources().getString(R.string.options));
        LanguageSelected = UtilitySharedPreferences.getPrefs(getApplicationContext(),"LanguageSelected");

        CardPujaVidhi = (CardView) findViewById(R.id.CardPujaVidhi);
        CardPujaVidhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitySharedPreferences.setPrefs(getApplicationContext(),"PoojaScreen","PoojaVidhi");
                Intent intent = new Intent(getApplicationContext(), PoojaRelatedActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
            }
        });


        CardVarthKatha = (CardView) findViewById(R.id.CardVarthKatha);
        CardVarthKatha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitySharedPreferences.setPrefs(getApplicationContext(),"PoojaScreen","VarthKatha");
                Intent intent = new Intent(getApplicationContext(), PoojaRelatedActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
            }
        });

        CardChalisa = (CardView) findViewById(R.id.CardChalisa);
        CardChalisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitySharedPreferences.setPrefs(getApplicationContext(),"PoojaScreen","Chalisa");
                Intent intent = new Intent(getApplicationContext(), PoojaRelatedActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
            }
        });

        CardAarti = (CardView) findViewById(R.id.CardAarti);
        CardAarti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitySharedPreferences.setPrefs(getApplicationContext(),"PoojaScreen","Aarti");
                Intent intent = new Intent(getApplicationContext(), PoojaRelatedActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
            }
        });

        CardInfoOfKulshrestha = (CardView) findViewById(R.id.CardInfoOfKulshrestha);
        CardInfoOfKulshrestha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitySharedPreferences.setPrefs(getApplicationContext(),"PoojaScreen","KulshresthaInfo");
                Intent intent = new Intent(getApplicationContext(), PoojaRelatedActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
            }
        });





        CardTerms_Condition  = (CardView) findViewById(R.id.CardTerms_Condition);
        CardTerms_Condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getApplicationContext(), Terms_ConditionActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();*/
            }
        });




    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), NewDashboard.class);
        intent.putExtra("lang_flag", LanguageSelected);
        startActivity(intent);
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }
}
