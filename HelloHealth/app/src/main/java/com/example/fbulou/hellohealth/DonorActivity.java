package com.example.fbulou.hellohealth;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class DonorActivity extends AppCompatActivity {
    SQLiteDatabase contactsDB = null;
    Button mCreateDB, mAddContact, mDeleteContact, mGetContacts, mDeleteDB;
    EditText mNameEdittext, mContactEdittext, mIDEdittext;
    TextView mResultTextview;
    private Spinner mBloodGroupSpinner;
    String bloodGroupSelected;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mCreateDB = (Button) findViewById(R.id.mCreateDB);
        mAddContact = (Button) findViewById(R.id.mAddContact);
        mDeleteContact = (Button) findViewById(R.id.mDeleteContact);
        mGetContacts = (Button) findViewById(R.id.mGetContacts);
        mDeleteDB = (Button) findViewById(R.id.mDeleteDB);

        mIDEdittext = (EditText) findViewById(R.id.mIDEdittext);
        mNameEdittext = (EditText) findViewById(R.id.mNameEdittext);
        mContactEdittext = (EditText) findViewById(R.id.mContactEdittext);
        mResultTextview = (TextView) findViewById(R.id.mResultTextview);

        linearLayout = (LinearLayout) findViewById(R.id.mToCreateDBlayout);

        addItemstoUnitTypeSpinner();
        addListenertoUnitTypeSpinner();

        if (LoadPref() == 1) {
            mCreateDB.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }

        contactsDB = this.openOrCreateDatabase("MyContacts", MODE_PRIVATE, null);

    }

    //Shared Preferences
    public int LoadPref() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);

        return sharedPreferences.getInt("hasDatabase", 0);
    }

    public void SavePref(int id) {
        SharedPreferences.Editor spEditor = getSharedPreferences("myPref", MODE_PRIVATE).edit();      // should use getPreferences for a single value

        spEditor.putInt("hasDatabase", id);
        spEditor.apply();
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

    public void createDB(View view) {
        try {
            contactsDB = this.openOrCreateDatabase("MyContacts", MODE_PRIVATE, null);

            contactsDB.execSQL("CREATE TABLE IF NOT EXISTS contacts " +
                    "(id INTEGER PRIMARY KEY, name VARCHAR, bg VARCHAR, contact INTEGER);");

            File database = getApplicationContext().getDatabasePath("MyContacts.db");

            if (!database.exists())
                Toast.makeText(this, "Database Created", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Database Missing", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Contacts Error", "Error Creating Database");
        }

        SavePref(1); //DB Created
        mCreateDB.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    public void addContact(View view) {
        String contactName = mNameEdittext.getText().toString(),
                contactNumber = mContactEdittext.getText().toString();

        if (contactName.length() != 0 && contactNumber.length() != 0) {

            contactsDB.execSQL("INSERT INTO contacts(name,bg,contact) VALUES ('" +
                    contactName + " ',' " + bloodGroupSelected + " ',' " + contactNumber + " ');");

            mNameEdittext.setText("");
            mContactEdittext.setText("");
            Toast.makeText(this, "Contact Added", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Please enter the details", Toast.LENGTH_SHORT).show();
    }

    public void getContacts(View view) {
        Cursor cursor = contactsDB.rawQuery("SELECT * FROM contacts", null);

        int idColumn = cursor.getColumnIndex("id"),
                nameColumn = cursor.getColumnIndex("name"),
                contactColumn = cursor.getColumnIndex("contact"),
                bloodGroupColumn = cursor.getColumnIndex("bg");

        cursor.moveToFirst();

        String result = "";

        if (cursor!=null && (cursor.getCount() > 0)) {
            do {
                String id = cursor.getString(idColumn),
                        name = cursor.getString(nameColumn),
                        contact = cursor.getString(contactColumn),
                        bloodGroup = cursor.getString(bloodGroupColumn);

                result = result + id + ". " + name + " : " + contact + " " + bloodGroup + " \n";

            } while (cursor.moveToNext());

            mResultTextview.setText(result);

            cursor.close();

        } else {
            Toast.makeText(this, "No Results to show", Toast.LENGTH_SHORT).show();
            mResultTextview.setText("");
        }
    }

    public void deleteContact(View view) {

        String id = mIDEdittext.getText().toString();

        if (id.length() != 0) {
            contactsDB.execSQL("DELETE FROM contacts WHERE id = " + id + " ;");
            Toast.makeText(this, "Contact Deleted", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Enter the ID", Toast.LENGTH_SHORT).show();
    }

    public void deleteDB(View view) {
        this.deleteDatabase("MyContacts");

        mResultTextview.setText("");
        SavePref(0);
        mCreateDB.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        if (contactsDB != null)
            contactsDB.close();
        super.onDestroy();
    }
}
