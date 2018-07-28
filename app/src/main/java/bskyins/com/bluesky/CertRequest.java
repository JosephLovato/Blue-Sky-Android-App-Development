package bskyins.com.bluesky;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CertRequest extends AppCompatActivity {

    SharedPreferences sharedpreferences;

    public static final String mypreference = "mypref";
    public static final String CompanyName = "insuredCompanyNameKey";
    public static final String ContactName = "insuredContactNameKey";
    public static final String Email = "insuredEmailKey";

    public String prefBody = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cert_request);

        Spinner spinner = (Spinner) findViewById(R.id.certholder_state_address_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.state_codes, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void sendCertRequestEmail(View arg0) {
        //Create sharedpreferences object
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        //get all insured (user) variables
        String insuredCompanyName = sharedpreferences.getString(CompanyName, "");
        String insuredContactName = sharedpreferences.getString(ContactName, "");
        String insuredEmail = sharedpreferences.getString(Email, "");

        //create all five editText objects and retrieve inputted text
        EditText certholderCompanyName = (EditText) findViewById(R.id.certholder_company_name_input);
        EditText certholderEmail = (EditText) findViewById(R.id.certholder_email_input);
        EditText certholderStreetAddress = (EditText) findViewById(R.id.certholder_street_address_input);
        EditText certholderCityAddress = (EditText) findViewById(R.id.certholder_city_address_input);
        EditText certholderZipAddress = (EditText) findViewById(R.id.certholder_zip_address_input);

        String certholderCompanyNameText = certholderCompanyName.getText().toString();
        String certholderEmailText = certholderEmail.getText().toString();
        String certholderStreetAddressText = certholderStreetAddress.getText().toString();
        String certholderCityAddressText = certholderCityAddress.getText().toString();
        String certholderZipAddressText = certholderZipAddress.getText().toString();

        //get selected state of spinner
        Spinner mySpinner=(Spinner) findViewById(R.id.certholder_state_address_spinner);
        String certholderStateAdrdress = mySpinner.getSelectedItem().toString();

        //get boolean values for both check boxes
        CheckBox additionalInsured = (CheckBox) findViewById(R.id.additional_insured_check_box);
        CheckBox waiver = (CheckBox) findViewById(R.id.waiver_check_box);

        boolean additionalInsuredBool = additionalInsured.isChecked();
        boolean waiverBool = waiver.isChecked();

        if(additionalInsuredBool) {
            prefBody += "ADDITIONAL INSURED\n";
        }

        if(waiverBool) {
            prefBody += "WAIVER\n";
        }

        //retrieve the agency's primary email
        String agencyPrimaryEmail = getString(R.string.agency_primary_email);

        //send email
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("*/*");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, agencyPrimaryEmail);
        emailIntent.putExtra(Intent.EXTRA_CC, insuredEmail);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Cert Request from " + insuredCompanyName);
        emailIntent.putExtra(Intent.EXTRA_TEXT, certholderCompanyNameText + "\n" + certholderEmailText + "\n" +
                certholderStreetAddressText + "\n" + certholderCityAddressText + "\n" + certholderZipAddressText + "\n" +
                certholderStateAdrdress + "\n" +
                prefBody + "From, " + insuredContactName);
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }

        //send email
        //Getting content for email
        String email = agencyPrimaryEmail;
        String emailCC = insuredEmail;
        String subject = "***Cert Request from: " + insuredCompanyName + "***";
        String message = "*************CERT REQUEST***********\n" +
                insuredContactName + "from " + insuredCompanyName + " has requested a cert for:\n" +
                certholderCompanyNameText + "\n" + certholderEmailText + "\n" +
                certholderStreetAddressText + "\n" + certholderCityAddressText + "\n" + certholderZipAddressText + "\n" +
                certholderStateAdrdress + "\n" +
                prefBody;


        //Creating SendMail object
        SendMail sm = new SendMail(this, email, emailCC, subject, message);

        //Executing sendmail to send email
        sm.execute();

        //Send back to main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }


}
