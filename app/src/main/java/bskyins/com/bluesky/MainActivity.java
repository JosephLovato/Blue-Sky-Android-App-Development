package bskyins.com.bluesky;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import bskyins.com.bluesky.Setup;

public class MainActivity extends AppCompatActivity {
    //First run shared preferences setup
    SharedPreferences settings = null;

    public static final String mypreference = "mypref";
    public static final String CompanyName = "insuredCompanyNameKey";
    public static final String ContactName = "insuredContactNameKey";
    public static final String Email = "insuredEmailKey";

    EditText companyName;
    EditText contactName;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences settings = getSharedPreferences("mypref", 0);
        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        if(hasLoggedIn)
        {
            setContentView(R.layout.activity_main);
        }
        else {
            SharedPreferences.Editor editor = settings.edit();

            //Set "hasLoggedIn" to true
            editor.putBoolean("hasLoggedIn", true);

            // Commit the edits!
            editor.commit();

            setContentView(R.layout.setup_entry);
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

    public void goToSetup(View view) {
        Intent intent = new Intent(this, Setup.class);
        startActivity(intent);
    }

    public void saveOnButtonClickEntry(View view) {
        //Set all EditText objects to their respective Edit Text xml fields
        companyName = (EditText) findViewById(R.id.setup_entry_insured_company_name);
        contactName = (EditText) findViewById(R.id.setup_entry_insured_contact_name);
        email = (EditText) findViewById(R.id.setup_entry_insured_email);

        //Set up sharedPreferences again with name "settings"
        SharedPreferences settings = getSharedPreferences("mypref", 0);
        //save user information
        String cpn = companyName.getText().toString();
        String ctn = contactName.getText().toString();
        String e = email.getText().toString();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(CompanyName, cpn);
        editor.putString(ContactName, ctn);
        editor.putString(Email, e);
        editor.commit();

        //Toast for user to be sure information is saved
        Context context = getApplicationContext();
        CharSequence text = "Your information has been saved!";
        int duration = Toast.LENGTH_SHORT;

        Toast autoToast = Toast.makeText(context, text, duration);
        autoToast.show();

        //sends user to Main Screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        //Disallow user from using back button to re-enter screen
        finish();

    }
}
