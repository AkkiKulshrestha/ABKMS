package com.ascend.www.abkms.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ascend.www.abkms.NewDashboard;
import com.ascend.www.abkms.R;
import com.ascend.www.abkms.adapters.StepperAdapter;
import com.stepstone.stepper.StepperLayout;

public class CompleteDetailedProfileInfo extends AppCompatActivity {

    StepperLayout mStepperLayout;
    String StrEmployeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile_page);
        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        mStepperLayout.setAdapter(new StepperAdapter(getSupportFragmentManager(), this));


    }



    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.go_back_text))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(), NewDashboard.class);
                        startActivity(intent);
                        overridePendingTransition(R.animator.left_right,R.animator.right_left);
                        finish();

                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), null)
                .show();

    }
}