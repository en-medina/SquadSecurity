package com.em3.securecompany;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class MainLoginActivity extends AppCompatActivity {
    ServerConnection serverConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serverConnection = new ServerConnection(getApplicationContext(), this);
        if(serverConnection.isUserLogIn())
            setPrivilegeActivity();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        setContentView(R.layout.activity_main_login);
    }
    private void setPrivilegeActivity()
    {
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e != null)
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                else{
                    String rank = object.getString("rank");
                    if(rank.equals("supervisor")) serverConnection.changeActivity(SupervisorActivity.class);
                    else if(rank.equals("administrador")) serverConnection.changeActivity(SupervisorActivity.class);
                    else serverConnection.changeActivity(SupervisorActivity.class);
                    finish();
                }
            }
        });
    }

    public void loginOrSignUp(View view)
    {
        view.setEnabled(false);
        String username = ((EditText)findViewById(R.id.mainLoginUsername)).getText().toString();
        String password = ((EditText)findViewById(R.id.mainLoginPassword)).getText().toString();
        if(username.equals("") || password.equals(""))
            Toast.makeText(this, "An username and password are required.", Toast.LENGTH_SHORT).show();
        else {
            if(serverConnection.checkCredentials(username, password)) {
                setPrivilegeActivity();

            }
        }
        view.setEnabled(true);

    }

}
