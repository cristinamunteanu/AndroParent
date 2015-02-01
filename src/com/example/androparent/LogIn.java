package com.example.androparent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LogIn extends Activity {
	EditText usernameEditText;
	EditText passwordEditText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setting default screen to login.xml
		setContentView(R.layout.activity_log_in);

		TextView registerScreen = (TextView) findViewById(R.id.link_to_register);

		Button loginButton = (Button) findViewById(R.id.btnLogin);
		usernameEditText = (EditText) findViewById(R.id.usernameLogin);
		passwordEditText = (EditText) findViewById(R.id.passwordLogin);

		// Listening to register new account link
		registerScreen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Switching to Register screen
				Intent i = new Intent(getApplicationContext(), SignUp.class);
				startActivity(i);
			}
		});

		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String emailLogin = usernameEditText.getText().toString();
				String passwordLogin = passwordEditText.getText().toString();
				loginUser(emailLogin, passwordLogin);

			}
		});
	}

	@SuppressLint("NewApi")
	public void loginUser(String emailP, String passwordP) {
		if (emailP.isEmpty() || passwordP.isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("You must enter both username and password");
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			ParseUser.logInInBackground(emailP, passwordP, new LogInCallback() {
				public void done(ParseUser user, ParseException e) {
					if (user != null) {
						// switch to ShowKids screen
						Intent i = new Intent(getApplicationContext(),
								ShowKidsListActivity.class);
						startActivity(i);
					} else {
						Toast.makeText(getApplicationContext(),
								"Invalid login credentials",
								Toast.LENGTH_SHORT).show();
						usernameEditText.setText("");
						passwordEditText.setText("");
					}
				}
			});
		}
	}
}