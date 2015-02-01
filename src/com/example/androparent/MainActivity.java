package com.example.androparent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;


public class MainActivity extends Activity implements View.OnClickListener {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        if (isNetworkAvailable()) {
            setContentView(R.layout.activity_main);
            //Parse.enableLocalDatastore(this);
            Parse.initialize(this, "JU4o2qxZChI2ZAs4LLJj0l0Nyp0w59yziOlsOGsN",
                    "BzXsRSrcYMh64ku8KoO4vg4bNVKzUQRNlp9yBuPt");
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                ParseACL parentsACL = new ParseACL();
                parentsACL.setReadAccess(currentUser, true);
                parentsACL.setWriteAccess(currentUser, true);
                ParseACL.setDefaultACL(parentsACL, true);
                Intent i = new Intent(getApplicationContext(),
                        ShowKidsListActivity.class);
                startActivity(i);
            }
        } else
            Toast.makeText(
                    getApplicationContext(),
                    "Your device is not connected to the internet. The application will run offline",
                    Toast.LENGTH_LONG).show();

    }

	private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		/* have an account */
		case R.id.button1:
			Intent logInIntent = new Intent(this, LogIn.class);
			startActivity(logInIntent);
			break;
		/* create an account */
		case R.id.button2:
			Intent signUpIntent = new Intent(this, SignUp.class);
			startActivity(signUpIntent);
			break;
		/* log-in as anonymous */
		case R.id.button3:
			ParseUser.enableAutomaticUser();
			ParseUser.getCurrentUser().increment("RunCount");
			ParseUser.getCurrentUser().saveInBackground();
			ParseAnonymousUtils.logIn(new LogInCallback() {
				@Override
				public void done(ParseUser user, ParseException e) {
					if (e != null) {
						Log.d("AndroParent", "Anonymous login failed.");
					} else {
						Log.d("AndroParent", "Anonymous user logged in.");
						Intent i = new Intent(getApplicationContext(),
								ShowKidsListActivity.class);
						startActivity(i);
					}
				}
			});
			break;
		/* forgot password */
		default:
			Intent resetPasswordIntent = new Intent(this, ResetPassword.class);
			startActivity(resetPasswordIntent);
			break;

		}
	}

}
