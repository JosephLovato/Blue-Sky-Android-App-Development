package bskyins.com.bluesky;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class AutoIDRequest extends AppCompatActivity {
    SharedPreferences sharedpreferences;

    public static final String mypreference = "mypref";
    public static final String CompanyName = "insuredCompanyNameKey";
    public static final String ContactName = "insuredContactNameKey";
    public static final String Email = "insuredEmailKey";
    public static final String AgencyEmail = "agencyEmailKey";

    public String prefBody = "";

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_id_request);

        //Action Bar Setup
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        //Initially set the ID-Card Check box to be invisible to the user
        CheckBox IDCardCheckBox = (CheckBox) findViewById(R.id.ID_card_check_box);
        IDCardCheckBox.setVisibility(View.INVISIBLE);

    }

    //makes the ID-Check Box visible
    public void checkBoxVisible(View view) {
        CheckBox IDCardCheckBox = (CheckBox) findViewById(R.id.ID_card_check_box);
        IDCardCheckBox.setVisibility(View.VISIBLE);
    }

    //makes the ID-Check Box invisible again
    public void checkBoxInvisible(View view) {
        boolean f = false;
        CheckBox IDCardCheckBox = (CheckBox) findViewById(R.id.ID_card_check_box);
        IDCardCheckBox.setVisibility(View.INVISIBLE);
        IDCardCheckBox.setChecked(false);
    }

    //sends the the Auto ID Request Email using the JavaMail Api and the
    public void sendAutoIDRequestEmail(View arg0) {

        //create all four editText objects
        EditText autoYear = (EditText) findViewById(R.id.auto_year_input);
        EditText autoMake = (EditText) findViewById(R.id.auto_make_input);
        EditText autoModel = (EditText) findViewById(R.id.auto_model_input);
        EditText autoVin = (EditText) findViewById(R.id.auto_vin_input);

        //create four radio button objects and one check box objects
        RadioButton liability = (RadioButton) findViewById(R.id.liability_radio_button);
        RadioButton fullCoverage = (RadioButton) findViewById(R.id.full_coverage_radio_button);
        RadioButton add = (RadioButton) findViewById(R.id.add_radio_button);
        RadioButton delete = (RadioButton) findViewById(R.id.delete_radio_button);
        CheckBox autoID = (CheckBox) findViewById(R.id.ID_card_check_box);

        //
        // REQUIRED (Checks if every required field is not empty.
        // If any field is empty, highlight field in red
        //

        boolean autoYearEmpty = TextUtils.isEmpty(autoYear.getText());
        boolean autoMakeEmpty = TextUtils.isEmpty(autoMake.getText());
        boolean autoModelEmpty = TextUtils.isEmpty(autoModel.getText());
        boolean autoVinEmpty = TextUtils.isEmpty(autoVin.getText());
        boolean liabilityFullCoverageEmpty = !liability.isChecked() && !fullCoverage.isChecked();
        boolean addDeleteEmpty = !add.isChecked() && !delete.isChecked();

        if(autoYearEmpty || autoMakeEmpty || autoModelEmpty || autoVinEmpty || liabilityFullCoverageEmpty || addDeleteEmpty) {
            if(autoYearEmpty) {
                autoYear.setBackgroundColor(getResources().getColor(R.color.required));
                autoYear.setHintTextColor(getResources().getColor(R.color.requiredText));
            }
            if(autoMakeEmpty) {
                autoMake.setBackgroundColor(getResources().getColor(R.color.required));
                autoMake.setHintTextColor(getResources().getColor(R.color.requiredText));
            }
            if(autoModelEmpty) {
                autoModel.setBackgroundColor(getResources().getColor(R.color.required));
                autoModel.setHintTextColor(getResources().getColor(R.color.requiredText));
            }
            if(autoVinEmpty) {
                autoVin.setBackgroundColor(getResources().getColor(R.color.required));
                autoVin.setHintTextColor(getResources().getColor(R.color.requiredText));
            }
            if(liabilityFullCoverageEmpty) {
                liability.setTextColor(getResources().getColor(R.color.required));
                fullCoverage.setTextColor(getResources().getColor(R.color.required));
            }
            if(addDeleteEmpty) {
                add.setTextColor(getResources().getColor(R.color.required));
                delete.setTextColor(getResources().getColor(R.color.required));
            }
        }

        //
        // ELSE, proceed with sending email:
        //
        else{
            //Create sharedPreferences object
            sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

            //get all insured (user) variables
            String insuredCompanyName = sharedpreferences.getString(CompanyName, "");
            String insuredContactName = sharedpreferences.getString(ContactName, "");
            String insuredEmail = sharedpreferences.getString(Email, "");
            String agencyEmail = sharedpreferences.getString(AgencyEmail, "");

            //retrieve inputted text from editText objects
            String autoYearText = autoYear.getText().toString();
            String autoMakeText = autoMake.getText().toString();
            String autoModelText = autoModel.getText().toString();
            String autoVinText = autoVin.getText().toString();

            //retrieve boolean values if respective radio buttons were selected
            boolean liabilityBool = liability.isChecked();
            boolean fullCoverageBool = fullCoverage.isChecked();
            boolean addBool = add.isChecked();
            boolean deleteBool = delete.isChecked();
            boolean autoIDBool = autoID.isChecked();


            //Fill in "prefBody" with all preferences for liability OR full coverage, add OR delete, and if auto ID is needed (string instantiated above)
            if (liabilityBool) {
                prefBody += ("LIABILITY\n");
            }

            if (fullCoverageBool) {
                prefBody += ("FULL COVERAGE\n");
            }

            if (deleteBool) {
                prefBody += ("DELETE\n");
            }

            if (addBool) {
                prefBody += ("ADD\n");
                if (autoIDBool) {
                    prefBody += ("Needs Auto ID Card\n");
                }
            }


            //retrieve the agency's primary email
            String agencyPrimaryEmail = getString(R.string.agency_primary_email);

            //Send email:
            //Getting content for email
            String email = agencyEmail;
            String emailCC = insuredEmail;
            String subject = "***Auto ID Request from: " + insuredCompanyName + "***";
            String message = "*************AUTO ID REQUEST***********\n" +
                    insuredContactName + " from " + insuredCompanyName + " has requested:\n" +
                    autoYearText + "\n" + autoMakeText + "\n" + autoModelText + "\n" + autoVinText + "\n" +
                    prefBody;


            //Creating SendMail object
            SendMail sm = new SendMail(this, email, emailCC, subject, message);

            //Executing SendMail to send email
            sm.execute();

            //Send back to main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }

}


