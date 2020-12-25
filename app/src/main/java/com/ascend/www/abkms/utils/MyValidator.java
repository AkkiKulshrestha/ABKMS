package com.ascend.www.abkms.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by SIR.WilliamRamsay on 03-Dec-15.
 */
public class MyValidator {
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "\\d{3}-\\d{7}";
    private static final String REQUIRED_MSG = "Field required";
    Bitmap bitmap=null;



    public static boolean isValidPhone(CharSequence phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }


    // validating email id
    public static boolean isValidEmail(EditText editText) {
        String email = editText.getText().toString().trim();
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            editText.setError("Enter valid Email");
            return false;
        }else if (email.length() == 0) {
            editText.setError("Enter valid Email");
            return false;
        }
        editText.setError(null);
        return true;
    }

    // validating password
    public static boolean isValidPassword(EditText editText) {

        String pass = editText.getText().toString().trim();
        if (pass != null && pass.length()<5) {
            editText.setError(REQUIRED_MSG);
            return false;
        }else {
            //$specialCharacters = "-@%\\\\[\\\\}+'!/#$^?:;,\\\\(\\\"\\\\)~`.*=&\\\\{>\\\\]<_";
           /* Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@%!/#$^?*=&])(?=\\S+$).{6,20}$");

            Matcher matcher = pattern.matcher(pass);

            if (!matcher.matches()) {
                editText.setError("Please Enter Strong Password.");
                return false;
            }else{
                editText.setError(null);
                return  true;
            }*/
            editText.setError(null);
            return  true;

        }



    }


    public static boolean isValidImageString(String imageStr) {

        if (imageStr != null && (imageStr.length()!=0 || !imageStr.equalsIgnoreCase(""))) {

            return true;
        }

        return false;
    }




    public static boolean isValidField(EditText editText) {
        String txtValue = editText.getText().toString().trim();

        if (txtValue.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }
        editText.setError(null);
        return true;
    }


    public static boolean isValidName(EditText editText) {
        String txtValue = editText.getText().toString().trim();

        if (txtValue.length() <= 3) {
            editText.setError(REQUIRED_MSG);
            return false;
        }
        editText.setError(null);
        return true;
    }

    public static boolean isValidAge(EditText editText) {
       Integer retValue=0;

        String txtValue = editText.getText().toString().trim();
        if(txtValue.length() == 0){
            editText.setError(REQUIRED_MSG);
            return  false;
        }

        if(txtValue.length() > 0 && txtValue.length()>2)
        {
            editText.setError("Age cannot be greater than 100 years");
            return  false;
        }

        if(txtValue.length() > 0 && Integer.valueOf(txtValue)==0){
            editText.setError("Age cannot be 0 year");
            return  false;
        }


        editText.setError(null);
        return true;
    }

    public static boolean isValidSpinner(Spinner spinner) {
        View view = spinner.getSelectedView();
        TextView textView = (TextView) view;
        if (spinner.getSelectedItemPosition() == 0) {
            textView.setError("None selected");
            return false;
        }
        textView.setError(null);
        return true;
    }


    public static boolean isValidSearchableSpinner(SearchableSpinner spinner) {
        View view = spinner.getSelectedView();
        //TextView textView = (TextView) view;
        if (spinner.getSelectedItemPosition() == 0) {
            //textView.setError("None selected");
            return false;
        }

        return true;
    }

    public static boolean isValidMobile(EditText editText) {
        String mob = editText.getText().toString().trim();
        if (mob != null && mob.length()!=10) {
            editText.setError(REQUIRED_MSG + " Enter 10 digits");
            return false;
        }else {
            Pattern pattern = Pattern.compile("(([6-9]{1})([0-9]{9}))");

            Matcher matcher = pattern .matcher(mob);

            if (!matcher.matches()) {
                editText.setError("Invalid Mobile No. Enter Valid Mobile Number");
                return false;
            }else{
                editText.setError(null);
                return  true;
            }
        }


    }

    public static boolean isValidMobileString(Context context, String mob) {

        if (mob != null && mob.length()!=10) {
            CommonMethods.DisplayToastWarning(context,"Invalid Mobile No. Enter Valid Mobile Number");
            //editText.setError(REQUIRED_MSG + " Enter 10 digits");
            return false;
        }else {
            Pattern pattern = Pattern.compile("(([6-9]{1})([0-9]{9}))");

            Matcher matcher = pattern .matcher(mob);

            if (!matcher.matches()) {
                CommonMethods.DisplayToastWarning(context,"Invalid Mobile No. Enter Valid Mobile Number");
                return false;
            }else{
                //editText.setError(null);
                return  true;
            }
        }
    }

    public static boolean isValidPan(EditText editText){

        String pan = editText.getText().toString().trim();
        if (pan != null && pan.length()!=10) {
            editText.setError("Enter Valid PAN Number");
            return false;
        }else if (pan != null && pan.length()>1) {
            Pattern pattern = Pattern.compile("(([A-Z]{5})([0-9]{4})([a-zA-Z]))");

            Matcher matcher = pattern .matcher(pan);

            if (!matcher.matches()) {
                editText.setError("Invalid PAN. Enter Valid PAN Number");
                return false;

            }else {
                //      ([CPHFATBLJG])
                    String char_4 = pan.substring(3,4);
                    Log.d("Char 4",char_4);
                    Pattern pat = Pattern.compile("[CPHFATBLJG]");
                    Matcher mat = pat.matcher(char_4);
                    if(!mat.matches()){
                        editText.setError("Invalid PAN. Enter Valid PAN Number");
                        return false;

                    }else {
                        editText.setError(null);
                        return true;
                    }
            }

        }else {
            editText.setError(null);
            return true;
        }

    }

    public static boolean isValidAadhaar(EditText editText) {
        String adhaar = editText.getText().toString().trim();
        if (adhaar != null && adhaar.length() == 12) {
            editText.setError(null);
            return true;
        }
        editText.setError(REQUIRED_MSG + " Enter 12 digits");
        return false;
    }

    public static boolean isValidImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

    public static boolean CheckDates(String StartDT,String EndDT,EditText edt_SIPEndDate)    {
        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy-MM-dd");
        boolean b = false;
        try {
            if(dfDate.parse(StartDT).before(dfDate.parse(EndDT)))
            {

                b = true;//If start date is before end date
                edt_SIPEndDate.setError("End Date Should Be Greater Than Start Date");
            }
            else if(dfDate.parse(StartDT).equals(dfDate.parse(EndDT)))
            {
                b = false;//If two dates are equal
                edt_SIPEndDate.setError("End Date Should Be Greater Than Start Date");
            }
            else
            {
                b = false; //If start date is after the end date
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }



}
