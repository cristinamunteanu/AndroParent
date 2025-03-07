package com.example.androparent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ViewActivity extends Activity {

	String activityId, childId;
	ParseObject activity, child;

	EditText activityNameET, activityDateET, activityStartTimeET,
			activityEndTimeET, activityResponsiblePersonET, activityDetailsET,
			activityLocationET;
	SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");

	/**
	 * Initializes the activity, sets up the UI components, and populates the form fields with existing activity data.
	 * This method is called when the activity is first created. It inflates the layout, initializes UI elements,
	 * retrieves activity data from the Parse database, and sets up a click listener for the back button.
	 * 
	 * @param savedInstanceState Bundle containing the activity's previously saved state, or null if there was none
	 * @return void
	 * 
	 * @throws com.parse.ParseException if there is an error retrieving the activity data from Parse
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_activity);
		Button backActivity = (Button) findViewById(R.id.btnBack);
		activityNameET = (EditText) findViewById(R.id.add_activity_name);
		activityDateET = (EditText) findViewById(R.id.activity_date);
		activityDetailsET = (EditText) findViewById(R.id.activity_details);
		activityLocationET = (EditText) findViewById(R.id.activity_location);
		activityStartTimeET = (EditText) findViewById(R.id.activity_start_time);
		activityEndTimeET = (EditText) findViewById(R.id.activity_end_time);
		activityResponsiblePersonET = (EditText) findViewById(R.id.activity_responsible_person);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			activityId = extras.getString("activityId");
			childId = extras.getString("childId");
		}
		ParseQuery query = new ParseQuery("Activity");

		try {
			activity = query.get(activityId);
		} catch (com.parse.ParseException e1) {
			e1.printStackTrace();
		}
		activityNameET.setText(activity.getString("name"));
		activityDateET.setText(sd.format(activity.get("date")));
		activityDetailsET.setText(activity.getString("details"));
		activityLocationET.setText(activity.getString("location"));
		activityStartTimeET.setText(activity.getString("startTime"));
		activityEndTimeET.setText(activity.getString("endTime"));
		activityResponsiblePersonET.setText(activity
				.getString("responsiblePerson"));

		backActivity.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						ShowCaregivers.class);
				i.putExtra("childId", childId);
				startActivity(i);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_activity_om, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	/**
	 * Handles the selection of options menu items.
	 * 
	 * This method is called when an item in the options menu is selected. It handles
	 * two specific cases: editing an activity and logging out. For editing, it checks
	 * if the user has write access before allowing the action. For logout, it logs out
	 * the current user and navigates to the main activity.
	 * 
	 * @param item The menu item that was selected.
	 * @return boolean Returns true if the menu item selection was handled, false otherwise.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.edit:

			if (activity.getACL().getWriteAccess(ParseUser.getCurrentUser()) == false) {
				Toast.makeText(getApplicationContext(),
						"You don't have the right to perform this action!",
						Toast.LENGTH_SHORT).show();
				return false;
			} else {
				Intent editActivityIntent = new Intent(this, EditActivity.class);
				editActivityIntent.putExtra("childId", childId);
				editActivityIntent.putExtra("activityId", activityId);
				startActivity(editActivityIntent);
				return true;
			}
		case R.id.logout:
			ParseUser.logOut();
			Toast.makeText(getApplicationContext(), "Good bye!",
					Toast.LENGTH_SHORT).show();
			Intent mainIntent = new Intent(this, MainActivity.class);
			startActivity(mainIntent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
