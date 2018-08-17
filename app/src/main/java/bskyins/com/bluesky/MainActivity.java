package bskyins.com.bluesky;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String mypreference = "mypref";
    public static final String CompanyName = "insuredCompanyNameKey";
    public static final String ContactName = "insuredContactNameKey";
    public static final String Email = "insuredEmailKey";
    public static final String AgencyEmail = "agencyEmailKey";

    EditText companyName;
    EditText contactName;
    EditText email;
    EditText agencyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set up shared preferences to check if this method is being run during the first launch
        SharedPreferences settings = getSharedPreferences(mypreference, 0);
        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        if(hasLoggedIn)
        {
            //If the user has already logged in once, set the content view to the Main Activity
            setContentView(R.layout.activity_main);

            //Action Bar Setup
            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);
            ActionBar ab = getSupportActionBar();
            ab.setDisplayShowTitleEnabled(false);
        }
        else {
            SharedPreferences.Editor editor = settings.edit();

            //Set "hasLoggedIn" to true
            editor.putBoolean("hasLoggedIn", true);

            // Commit the edits!
            editor.commit();

            //Set content view to the entry version of the Setup Screen
            setContentView(R.layout.setup_entry);

            //Action Bar Setup
            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);
            ActionBar ab = getSupportActionBar();
            ab.setDisplayShowTitleEnabled(false);
        }
    }

    //sends user to call screen
    public void goToCall(View view) {
        Intent intent = new Intent(this, Call.class);
        startActivity(intent);
    }

    //sends user to Auto ID Request Screen
    public void goToAutoIDRequest(View view) {
        Intent intent = new Intent(this, AutoIDRequest.class);
        startActivity(intent);
    }

    //sends user to Cert Request Screen
    public void goToCertRequest(View view) {
        Intent intent = new Intent(this, CertRequest.class);
        startActivity(intent);
    }

    //sends user to Setup Screen
    public void goToSetup(View view) {
        Intent intent = new Intent(this, Setup.class);
        startActivity(intent);
    }

    //saves information on the Entry version of the setup page, then sends the user to the Main Activity
    public void saveOnButtonClickEntry(View view) {
        //Set all EditText objects to their respective Edit Text xml fields
        companyName = (EditText) findViewById(R.id.setup_entry_insured_company_name);
        contactName = (EditText) findViewById(R.id.setup_entry_insured_contact_name);
        email = (EditText) findViewById(R.id.setup_entry_insured_email);
        agencyEmail = (EditText) findViewById(R.id.setup_agency_email);

        //Set up sharedPreferences again with name "settings"
        SharedPreferences settings = getSharedPreferences(mypreference, 0);
        //save user information
        String cpn = companyName.getText().toString();
        String ctn = contactName.getText().toString();
        String e = email.getText().toString();
        String ae = agencyEmail.getText().toString();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(CompanyName, cpn);
        editor.putString(ContactName, ctn);
        editor.putString(Email, e);
        editor.putString(AgencyEmail, ae);
        editor.commit();

        //Toast for user to be sure information is saved
        Context context = getApplicationContext();
        CharSequence text = "Your information has been saved!";
        int duration = Toast.LENGTH_SHORT;

        Toast autoToast = Toast.makeText(context, text, duration);
        autoToast.show();

        //sends user to Main Activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        //Disallow user from using back button to re-enter screen
        finish();

    }
}
