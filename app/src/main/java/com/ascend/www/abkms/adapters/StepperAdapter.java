package com.ascend.www.abkms.adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.ascend.www.abkms.fragments.AddressDetailsFragment;
import com.ascend.www.abkms.fragments.BasicDetailsFragment;
import com.ascend.www.abkms.fragments.Education_CareerInfoFragment;
import com.ascend.www.abkms.fragments.FamilyInfoFragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;


public class StepperAdapter extends AbstractFragmentStepAdapter {

    private static final String CURRENT_STEP_POSITION_KEY = "messageResourceId";
    public StepperAdapter(FragmentManager fm, Context context) {
        super(fm, context);
    }
    @Override
    public Step createStep(int position) {
        switch (position){

            case 0:
                final BasicDetailsFragment step0 = new BasicDetailsFragment();
                Bundle b0 = new Bundle();
                b0.putInt(CURRENT_STEP_POSITION_KEY, position);
                step0.setArguments(b0);
                return step0;

            case 1:
                final AddressDetailsFragment step1 = new AddressDetailsFragment();
                Bundle b1 = new Bundle();
                b1.putInt(CURRENT_STEP_POSITION_KEY, position);
                step1.setArguments(b1);
                return step1;


            case 2:
                final Education_CareerInfoFragment step2 = new Education_CareerInfoFragment();
                Bundle b2 = new Bundle();
                b2.putInt(CURRENT_STEP_POSITION_KEY, position);
                step2.setArguments(b2);
                return step2;

            case 3:
                final FamilyInfoFragment step3 = new FamilyInfoFragment();
                Bundle b3 = new Bundle();
                b3.putInt(CURRENT_STEP_POSITION_KEY, position);
                step3.setArguments(b3);
                return step3;


            /*case 4:
                final UploadDocumentActivity step4 = new UploadDocumentActivity();
                Bundle b4 = new Bundle();
                b4.putInt(CURRENT_STEP_POSITION_KEY, position);
                step4.setArguments(b4);
                return step4;*/






        }
        return null;
    }
    @Override
    public int getCount() {
        return 4;
    }
    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        switch (position){
            case 0:
                return new StepViewModel.Builder(context)
                        .setTitle("Basic Details")
                        .setEndButtonLabel("Address Details")
                        .create();
            case 1:
                return new StepViewModel.Builder(context)
                        .setTitle("Address Details")
                        .setBackButtonLabel("Basic Details")
                        .setEndButtonLabel("Career Details")
                        .create();

            case 2:
                return new StepViewModel.Builder(context)
                        .setTitle("Career Details")
                        .setBackButtonLabel("Address Details")
                        .setEndButtonLabel("Family Details")
                        .create();


            case 3:
                return new StepViewModel.Builder(context)
                        .setTitle("Family Details")
                        .setBackButtonLabel("Career Details")
                        .setEndButtonLabel("Submit")
                        .create();

           /* case 4:
                return new StepViewModel.Builder(context)
                        .setTitle("IDENTIFICATION DETAILS")
                        .setBackButtonLabel("BANK DETAILS")
                        .setEndButtonLabel("SUBMIT")
                        .create();
*/



        }
        return null;
    }
}
