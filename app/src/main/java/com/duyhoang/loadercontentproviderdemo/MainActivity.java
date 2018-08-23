package com.duyhoang.loadercontentproviderdemo;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static int MY_PERMISSION_READ_CONTACT_CODE = 20;

    Button btnLoadData;
    TextView txtShowing;
    String[] mColumnProjection = {
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.CONTACT_STATUS
    };
    String mSelectionClause = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " = ?";
    String[] mSelectionArguments = {"van nhan"};
    boolean firstLoadedFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLoadData = (Button)findViewById(R.id.button_load_data);
        btnLoadData.setOnClickListener(this);
        txtShowing = (TextView)findViewById(R.id.text_showing_text);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_load_data:
                if(firstLoadedFlag)
                {
                    getLoaderManager().initLoader(1, null, this);
                    firstLoadedFlag = true;
                }
                else
                    getLoaderManager().restartLoader(1, null, this);

                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(i == 1){
            return new CursorLoader(this, ContactsContract.Contacts.CONTENT_URI,
                    mColumnProjection,
                    mSelectionClause,
                    mSelectionArguments,
                    null
            );
        }
        return  null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            StringBuilder strBuilder = new StringBuilder();
            if (cursor != null && cursor.getCount() > 0){
                while(cursor.moveToNext()){
                    strBuilder.append(cursor.getString(0) + " , " + cursor.getString(1) + " , " + cursor.getString(2) + "\n");
                }
                txtShowing.setText(strBuilder.toString());
            }
            else
                txtShowing.setText("NO contact");

        }
        else if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)){
            Snackbar.make(findViewById(android.R.id.content), "Requesting permission read contact", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Enable", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSION_READ_CONTACT_CODE);
                        }
                    })
                    .show();
        }
        else
            ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSION_READ_CONTACT_CODE);


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
