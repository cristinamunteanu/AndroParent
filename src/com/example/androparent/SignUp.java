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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUp extends Activity {
	EditText usernameEditText;
	EditText emailEditText;
	EditText passwordEditText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set View to register.xml
		setContentView(R.layout.register);
		
		TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
		Button registerNewAccount = (Button) findViewById(R.id.btnRegister);
		usernameEditText = (EditText) findViewById(R.id.reg_fullname);
		emailEditText = (EditText) findViewById(R.id.reg_email);
		passwordEditText = (EditText) findViewById(R.id.reg_password);

				
		// Listening to Login Screen link
		loginScreen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// Closing registration screen
				// Switching to Login Screen/closing register screen
				finish();
			}
		});

		registerNewAccount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = usernameEditText.getText().toString();
				String email = emailEditText.getText().toString();
				String password = passwordEditText.getText().toString();
				registerUser(username, email, password);
			}
		});
	}

	@SuppressLint("NewApi")
	public void registerUser(String usernameP, String emailP, String passwordP) {
		if (usernameP.isEmpty() || emailP.isEmpty() || passwordP.isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("You must enter username, password and email");
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			ParseUser user = new ParseUser();
			user.setUsername(usernameP);
			user.setPassword(passwordP);
			user.setEmail(emailP);
			user.signUpInBackground(new SignUpCallback() {
				public void done(ParseException e) {
					if (e == null) {
						// switch to ShowKids screen
						Intent i = new Intent(getApplicationContext(),
								ShowKidsListActivity.class);
						startActivity(i);
					} else {
						Toast.makeText(getApplicationContext(), e.toString(),
								Toast.LENGTH_SHORT).show();

					}
				}
			});
		}

	}
}
