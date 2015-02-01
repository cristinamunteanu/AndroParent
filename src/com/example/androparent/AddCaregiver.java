package com.example.androparent;

import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class AddCaregiver extends Activity {

	EditText personNameET;
	CheckBox writeAccessCB;
	ParseUser responsiblePerson;
	ParseObject child;
	String personName;
	String childId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_responsible_person);
		Parse.initialize(this, "JU4o2qxZChI2ZAs4LLJj0l0Nyp0w59yziOlsOGsN",
				"BzXsRSrcYMh64ku8KoO4vg4bNVKzUQRNlp9yBuPt");
		Button add = (Button) findViewById(R.id.btnAddPerson);
		Button cancel = (Button) findViewById(R.id.btnCancelPerson);
		personNameET = (EditText) findViewById(R.id.add_responsible_person);
		writeAccessCB = (CheckBox) findViewById(R.id.access_cb);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			childId = extras.getString("childId");
		}
		add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				personName = personNameET.getText().toString();
				Log.e("", "the personName retreived from textbox is"
						+ personName);
				try {
					if (addPerson(personName) == 1) {
						Intent i = new Intent(getApplicationContext(),
								ShowCaregivers.class);
						i.putExtra("childId", childId);
						startActivity(i);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private int addPerson(String personName) throws ParseException {
		ParseQuery queryForChild = new ParseQuery("Child");
		try {
			child = queryForChild.get(childId);
		} catch (com.parse.ParseException e1) {
			e1.printStackTrace();
			return 0;
		}
		
		if (personName.equals("")) {
			Toast.makeText(getApplicationContext(),
					"You must enter the user name", Toast.LENGTH_SHORT).show();
			return 0;
		}

		ParseQuery query = ParseUser.getQuery();
		query.whereEqualTo("username", personName);
		try {
			responsiblePerson = (ParseUser) query.getFirst();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (responsiblePerson == null) {
			Toast.makeText(getApplicationContext(),
					"The username you entered is not correct",
					Toast.LENGTH_SHORT).show();
			return 0;
		}
		
		child.addUnique("responsible_persons", Arrays.asList(responsiblePerson.getUsername())); //put array, because we might have multiple persons
		ParseACL childACL=child.getACL();
		childACL.setReadAccess(responsiblePerson, true);
		if (writeAccessCB.isChecked())
			childACL.setWriteAccess(responsiblePerson, true);
		child.setACL(childACL);
		child.saveEventually();
		return 1;
	}

}
