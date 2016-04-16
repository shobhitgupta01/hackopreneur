package com.example.fbulou.hellohealth;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RecipientActivity extends AppCompatActivity {

    SQLiteDatabase contactsDB;
    private Spinner mBloodGroupSpinner;
    private String bloodGroupSelected;
    private EditText mNameEdittext;
    private EditText mContactEdittext;
    private EditText mAddressEdittext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNameEdittext = (EditText) findViewById(R.id.mNameEdittext);
        mContactEdittext = (EditText) findViewById(R.id.mContactEdittext);
        mAddressEdittext = (EditText) findViewById(R.id.mAddressEdittext);

        addItemstoUnitTypeSpinner();
        addListenertoUnitTypeSpinner();

        contactsDB = this.openOrCreateDatabase("MyContacts", MODE_PRIVATE, null);

    }

    public void addItemstoUnitTypeSpinner() {
        mBloodGroupSpinner = (Spinner) findViewById(R.id.mBloodGroupSpinner);
        ArrayAdapter<CharSequence> unitTypeSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.blood_groups, android.R.layout.simple_spinner_item);
        unitTypeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBloodGroupSpinner.setAdapter(unitTypeSpinnerAdapter);
    }

    public void addListenertoUnitTypeSpinner() {
        mBloodGroupSpinner = (Spinner) findViewById(R.id.mBloodGroupSpinner);

        mBloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodGroupSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO maybe add something here later
            }
        });
    }


    public void SendSMSButton(View view) {

        String contactName = mNameEdittext.getText().toString(),
                contactNumber = mContactEdittext.getText().toString(),
                contactAddress = mAddressEdittext.getText().toString();

        if (contactName.length() != 0 && contactNumber.length() != 0 && contactAddress.length() != 0) {

            Cursor cursor = null;

            // if(bloodGroupSelected.equals("O+"))
            cursor = contactsDB.rawQuery("SELECT * FROM contacts", null);


            int nameColumn = cursor.getColumnIndex("name"),
                    contactColumn = cursor.getColumnIndex("contact"),
                    bloodGroupColumn = cursor.getColumnIndex("bg");

            cursor.moveToFirst();

            String result = "";

            if (cursor != null && (cursor.getCount() > 0)) {
                do {
                    String name = cursor.getString(nameColumn),
                            contact = cursor.getString(contactColumn);

                    String bg = cursor.getString(bloodGroupColumn);
                    //result = result + name + " - " + contact + " " + bg + " \n";

                    result = result + name + " - " + contact + " " + " \n";
                    sendMessage(contact);

                } while (cursor.moveToNext());

                Log.e("TAG", "SMS sent to \n" + result);
                Toast.makeText(RecipientActivity.this, "SMS sent to \n" + result, Toast.LENGTH_SHORT).show();

                cursor.close();

                /*<data android:scheme="sms" />
                <data android:scheme="smsto" />
                <data android:scheme="mms" />
                <data android:scheme="mmsto" />*/
            }

        } else
            Toast.makeText(this, "Please enter the details", Toast.LENGTH_SHORT).show();
    }

    private void sendMessage(String phoneNum) {
        try {
            SmsManager smsManager = SmsManager.getDefault();

            String message = "A blood recipient is in need. To help, contact : \n" + mNameEdittext + "\nPhone Number : " + mContactEdittext + "\n Address : " + mAddressEdittext;
            smsManager.sendTextMessage(phoneNum, null, message, null, null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

}

