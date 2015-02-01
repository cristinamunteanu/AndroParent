package com.example.androparent;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.Calendar;

public class ViewTreatment extends Activity {

	EditText medicationNameET, startTimeET, endTimeET, detailsET;
	EditText responsiblePersons, startDate, endDate;
	EditText repetition, reminders;

	Button saveButton, cancelButton;
	ParseObject child;
	List persons = null;
	List<String> person_names = new ArrayList<String>();
	CharSequence[] person_names_charsequence;
	List<CharSequence> selected_person_names = new ArrayList<CharSequence>();

	Dialog progressDialog;
	ParseUser responsiblePersonUser;
	ParseObject treatment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.view_treatment);
		
		medicationNameET = (EditText) findViewById(R.id.show_treatment_name);
		responsiblePersons = (EditText) findViewById(R.id.select_persons);
		startDate = (EditText) findViewById(R.id.show_startdate);
		endDate = (EditText) findViewById(R.id.show_enddate);
		detailsET = (EditText) findViewById(R.id.treatment_details);
		repetition = (EditText) findViewById(R.id.show_retetition);


		String treatmentId = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			treatmentId = extras.getString("treatmentId");
			Log.e("", "treatment id is " + treatmentId);
		}

		ParseQuery query = new ParseQuery("Treatment");

		try {
			treatment = query.get(treatmentId);
		} catch (com.parse.ParseException e1) {
			e1.printStackTrace();
			Log.e("get treatment", "The get treatment request failed.");
			return;
		}

		Long startTimeMilis = Long.valueOf(treatment.getString("startTime"));
		Long endTimeMilis = Long.valueOf(treatment.getString("endTime"));
		String dateformat = "dd/MM/yyyy hh:mm:ss";
		DateFormat dateFormatter = new SimpleDateFormat(dateformat);

		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTimeInMillis(startTimeMilis);
		startDate.setText(dateFormatter.format(startCalendar.getTime()));

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTimeInMillis(endTimeMilis);
		endDate.setText(dateFormatter.format(endCalendar.getTime()));

		medicationNameET.setText(treatment.getString("name"));
		detailsET.setText(treatment.getString("details"));
		responsiblePersons.setText(treatment.getString("responsiblePerson"));
		repetition.setText(String.valueOf(treatment.getInt("repeatInt")) + " "
				+ treatment.getString("repeatString"));
	}
}
