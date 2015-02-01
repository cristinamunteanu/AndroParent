package com.example.androparent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ViewCaregiver extends Activity {
	String childId, personName;
	ParseObject child;
	ParseUser person;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_responsible_person);
		Parse.initialize(this, "JU4o2qxZChI2ZAs4LLJj0l0Nyp0w59yziOlsOGsN",
				"BzXsRSrcYMh64ku8KoO4vg4bNVKzUQRNlp9yBuPt");
		final Button savePerson = (Button) findViewById(R.id.btnSavePerson);
		EditText personNameET = (EditText) findViewById(R.id.view_responsible_person_et);
		final CheckBox writeAccessCB = (CheckBox) findViewById(R.id.access_cb);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			childId = extras.getString("childId");
			personName = extras.getString("personName");
		} else
			return;
		personNameET.setText(personName);
		ParseQuery query = new ParseQuery("Child");

		try {
			child = query.get(childId);
		} catch (com.parse.ParseException e1) {
			e1.printStackTrace();
			return;
		}
		
		Log.e ("", "Got childid " +child.getObjectId());
		ParseQuery queryForUser = ParseUser.getQuery();
		queryForUser.whereEqualTo("username", personName);
		try {
			person = (ParseUser) queryForUser.getFirst();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (person == null) {
			return;
		}
		
		
		if (child.getACL().getWriteAccess(person) == true)
			writeAccessCB.setChecked(true);
		writeAccessCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				savePerson.setEnabled(true);
				if (isChecked) {
					child.getACL().setWriteAccess(person, true);
				} else {
					child.getACL().setWriteAccess(person, false);
				}
			}
		});

		savePerson.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					child.save();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent showResponsiblePersonsIntent = new Intent(getApplicationContext(), ShowCaregivers.class);
				showResponsiblePersonsIntent.putExtra("childId", childId);
				startActivity(showResponsiblePersonsIntent);
			}
		});

	}
	
}
