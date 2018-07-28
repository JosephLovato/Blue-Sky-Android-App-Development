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
import android.support.v7.app.AppCompatActivity;
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

    public String prefBody = "";

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_id_request);
        CheckBox IDCardCheckBox = (CheckBox) findViewById(R.id.ID_card_check_box);
        IDCardCheckBox.setVisibility(View.INVISIBLE);

        //Check Camera Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2);

        } else {
            // Permission has already been granted
        }

    }


    public void checkBoxVisible(View view) {
        CheckBox IDCardCheckBox = (CheckBox) findViewById(R.id.ID_card_check_box);
        IDCardCheckBox.setVisibility(View.VISIBLE);
    }

    public void checkBoxInvisible(View view) {
        boolean f = false;
        CheckBox IDCardCheckBox = (CheckBox) findViewById(R.id.ID_card_check_box);
        IDCardCheckBox.setVisibility(View.INVISIBLE);
        IDCardCheckBox.setChecked(false);
    }

    public void sendAutoIDRequestEmail(View arg0) {

        //Create sharedpreferences object
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        //get all insured (user) variables
        String insuredCompanyName = sharedpreferences.getString(CompanyName, "");
        String insuredContactName = sharedpreferences.getString(ContactName, "");
        String insuredEmail = sharedpreferences.getString(Email, "");

        //create all four editText objects and retrieve inputted text
        EditText autoYear = (EditText) findViewById(R.id.auto_year_input);
        EditText autoMake = (EditText) findViewById(R.id.auto_make_input);
        EditText autoModel = (EditText) findViewById(R.id.auto_model_input);
        EditText autoVin = (EditText) findViewById(R.id.auto_vin_input);

        String autoYearText = autoYear.getText().toString();
        String autoMakeText = autoMake.getText().toString();
        String autoModelText = autoModel.getText().toString();
        String autoVinText = autoVin.getText().toString();

        //create four radio button objects and one check box objects and retrieve boolean values for if they were selected
        RadioButton liability = (RadioButton) findViewById(R.id.liability_radio_button);
        RadioButton fullCoverage = (RadioButton) findViewById(R.id.full_coverage_radio_button);
        RadioButton add = (RadioButton) findViewById(R.id.add_radio_button);
        RadioButton delete = (RadioButton) findViewById(R.id.delete_radio_button);
        CheckBox autoID = (CheckBox) findViewById(R.id.ID_card_check_box);

        boolean liabilityBool = liability.isChecked();
        boolean fullCoverageBool = fullCoverage.isChecked();
        boolean addBool = add.isChecked();
        boolean deleteBool = delete.isChecked();
        boolean autoIDBool = autoID.isChecked();


        //Fill in string with all preferences for liability OR full coverage, add OR delete, and if auto ID is needed (string instiated above)

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

        //send email
        //Getting content for email
        String email = agencyPrimaryEmail;
        String emailCC = insuredEmail;
        String subject = "***Auto ID Request from: " + insuredCompanyName + "***";
        String message = "*************AUTO ID REQUEST***********\n" +
                insuredContactName + " from " + insuredCompanyName + " has requested:\n" +
                autoYearText + "\n" + autoMakeText + "\n" + autoModelText + "\n" + autoVinText + "\n" +
                prefBody;


        //Creating SendMail object
        SendMail sm = new SendMail(this, email, emailCC, subject, message);

        //Executing sendmail to send email
        sm.execute();

        //Send back to main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void takePicutre(View arg0) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



}


