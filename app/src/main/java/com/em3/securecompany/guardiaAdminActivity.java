package com.em3.securecompany;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class guardiaAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardia_admin);
    }
    @Override
    public void onBackPressed() {
        //Toast.makeText(context,"Thanks for using application!!",Toast.LENGTH_LONG).show()l
        finish();
        return;
    }
}
