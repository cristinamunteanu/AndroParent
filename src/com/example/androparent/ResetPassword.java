package com.example.androparent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ResetPassword extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reset_password);

		Button resetPassButton = (Button) findViewById(R.id.btnResetPass);
		final EditText resetEditText = (EditText) findViewById(R.id.resetPass);

		resetPassButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String resetEmail = resetEditText.getText().toString();
				if (validMail(resetEmail)) {

					ParseUser.requestPasswordResetInBackground(resetEmail,
							new RequestPasswordResetCallback() {
								public void done(ParseException e) {
									if (e == null) {
										Toast.makeText(
												getApplicationContext(),
												"An email was successfully sent with reset instructions.",
												Toast.LENGTH_SHORT).show();
										resetEditText.setText("");
										Intent i = new Intent(
												getApplicationContext(),
												MainActivity.class);
										startActivity(i);
									} else {
										AlertDialog.Builder builder = new AlertDialog.Builder(
												getApplicationContext());
										builder.setMessage("Oups! Something went wrong!\n"
												+ e.toString());
										AlertDialog alert = builder.create();
										alert.show();
									}
								}
							});

					Toast.makeText(
							getApplicationContext(),
							"An email was successfully sent with reset instructions.",
							Toast.LENGTH_SHORT).show();
				} else {

					Toast.makeText(getApplicationContext(),
							"Invalid email address", Toast.LENGTH_SHORT).show();
					resetEditText.setText("");

				}

			}
		});
	}

	private boolean validMail(String email) {
		Pattern pattern;
		Matcher matcher;
		String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
