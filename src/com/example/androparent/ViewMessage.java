package com.example.androparent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class ViewMessage extends Activity {
	EditText fromET, subjectET, bodyET;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.view_message);
	    String sender="", subject="", body="";
	    fromET = (EditText) findViewById(R.id.from_et);
	    subjectET = (EditText) findViewById(R.id.subject_et);
	    bodyET = (EditText) findViewById(R.id.body_et);
	    Bundle extras = getIntent().getExtras();
		if (extras != null) {
			sender = extras.getString("sender");
			subject=extras.getString("subject");
			body=extras.getString("body");
		}
		if (sender.compareTo("")!=0)
			fromET.setText(sender);
		if(subject.compareTo("")!=0)
			subjectET.setText(subject);
		if(body.compareTo("")!=0)
			bodyET.setText(body);
	}

}
