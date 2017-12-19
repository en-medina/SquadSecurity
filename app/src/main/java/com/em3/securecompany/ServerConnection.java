/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.em3.securecompany;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseSession;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ServerConnection extends Application {

    private Context context;
    private Activity activity;
    private SharedPreferences credentials;
    private static boolean localDataStoreEnable = false;
    private static ParseUser curUser, anyGuardian;
    public int getSupervisorRank(){
        if(curUser == null)
            return -1;
        return curUser.getInt("rank");
    }

    public ServerConnection(Context param, Activity param1)
    {
        context = param;
        activity = param1;
        credentials = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);

        // Enable Local Datastore.
        if(!localDataStoreEnable)
            Parse.enableLocalDatastore(this);
        localDataStoreEnable = true;
        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId(DBCredentials.applicationId)
                .clientKey(DBCredentials.clientKey)
                .server(DBCredentials.server)
                .build()
        );
        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
    public boolean isUserLogIn()
    {
        ParseUser user = ParseUser.getCurrentUser();
        String username = credentials.getString("username", null);
        if(user == null)
            return false;

        if(username.equals(user.getUsername()))
            return true;
        if(!username.equals(null))
            return false;
        return checkCredentials(username, credentials.getString("password", null), true);
    }
    public boolean checkCredentials(String username, String password, boolean isMainLogin)
    {
        ParseUser user = new ParseUser();
        boolean ans;

        try {

            if(isMainLogin) {
                curUser = user.logIn(username, password);
                credentials.edit().putString("username", username).putString("password", password).apply();
            }
            else anyGuardian = user.logIn(username, password);
            ans = true;
        } catch (ParseException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            ans = false;
        }
        return ans;
    }

    public void fillData(final View view)
    {
        if(curUser == null) {
            Toast.makeText(context, "There is an unusual error, please log out", Toast.LENGTH_SHORT).show();
            return;
        }
        ((TextView)view.findViewById(R.id.profileFullName)).setText(curUser.getString("name")+" "+curUser.getString("lastname"));
        ((TextView)view.findViewById(R.id.profileIdCard)).setText(curUser.getString("idCard"));
        ((TextView)view.findViewById(R.id.profileType)).setText(curUser.getString("rank"));

        Calendar birth = Calendar.getInstance();
        birth.setTime(curUser.getDate("birth"));
        int age = Calendar.getInstance().get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        ((TextView)view.findViewById(R.id.profileAge)).setText(Integer.toString(age));
        ((TextView)view.findViewById(R.id.profileTelephone)).setText(curUser.getString("telNumber"));
        ((TextView)view.findViewById(R.id.profileCelullar)).setText(curUser.getString("celNumber"));
    }
    public void logout(int rank)
    {
        switch (rank){
            case RankClass.Administrator:
            case RankClass.Supervisor:
                curUser.logOut();
                break;
            case RankClass.Guardian:
                anyGuardian.logOut();
                break;
            default:
                break;
         }
/*
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null)
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        */
    }
    public void changeActivity(final Class nextActivity)
    {
        Intent intent = new Intent(context, nextActivity);
        activity.startActivity(intent);
    }

}
