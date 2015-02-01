package com.example.androparent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@SuppressLint("ValidFragment")
public class AddActivity extends FragmentActivity {

	EditText activityNameET, activityDateET, activityDetailsET,
			activityLocationET;
	/* TimePicker activityStartTimePicker, activityEndTimePicker; */
	EditText startTimeET, endTimeET;
	Button setStartTimeButton, setEndTimeButton, setDateButton;
	Spinner responsiblePersonSpinner;
	private int hour, minute;
	Calendar selectedStartTime = Calendar.getInstance();
	Calendar selectedEndTime = Calendar.getInstance();
	String selectedStartTimeString, selectedEndTimeString;
	ParseObject child;
	private List persons = null;
	private List<String> person_names = new ArrayList<String>();
	private Dialog progressDialog;
	ParseUser responsiblePersonUser;

	static final int START_TIME_DIALOG_ID = 999, END_TIME_DIALOG_ID = 998;

	String childId;

	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			ParseQuery query = new ParseQuery("Child");
			try {
				child = query.get(childId);
				persons = child.getList("responsible_persons");
			} catch (ParseException e) {
			} catch (com.parse.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			AddActivity.this.progressDialog = ProgressDialog.show(
					AddActivity.this, "", "Loading...", true);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void reslt) {
			// Put the list of persons into the list view
			if (!(persons == null)) {
				for (int i = 0; i < persons.size(); i++) {
					Pattern pattern = Pattern.compile(".*\\\"(.*)\\\".*");
					Matcher matcher = pattern
							.matcher(persons.get(i).toString());
					if (matcher.find()) {
						person_names.add(matcher.group(1));
					}
				}

			}
			populate_responsible_persons_spinner();
			AddActivity.this.progressDialog.dismiss();
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			childId = extras.getString("childId");
		}
		setContentView(R.layout.add_activity);

		Button addActivity = (Button) findViewById(R.id.btnSave);
		Button cancelActivity = (Button) findViewById(R.id.btnCancel);
		activityNameET = (EditText) findViewById(R.id.add_activity_name);
		activityDateET = (EditText) findViewById(R.id.activity_date);
		activityDetailsET = (EditText) findViewById(R.id.activity_details);
		activityLocationET = (EditText) findViewById(R.id.activity_location);
		
		startTimeET = (EditText) findViewById(R.id.start_time_et);
		endTimeET = (EditText) findViewById(R.id.end_time_et);
		
		responsiblePersonSpinner = (Spinner) findViewById(R.id.responsible_person_spinner);
		new RemoteDataTask().execute();

		addActivity.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("SimpleDateFormat")
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				String activityName, activityDetails, activityLocation, responsiblePerson;
				Date activityDate = new Date();
				Date today = new Date();
				activityName = activityNameET.getText().toString();
				activityDetails = activityDetailsET.getText().toString();
				activityLocation = activityLocationET.getText().toString();
				responsiblePerson = responsiblePersonSpinner.getSelectedItem()
						.toString();
				String activityDateS = activityDateET.getText().toString();

				if (!activityDateS.equals("")) {
					SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");
					try {
						activityDate = sd.parse(activityDateS);
						// The following lines are for ignoring the time from
						// date when comparing to today
						today.setHours(0);
						today.setMinutes(0);
						activityDate.setHours(1);
						activityDate.setMinutes(1);
						if (activityDate.before(today)) {
							Toast.makeText(getApplicationContext(),
									"You can not add activity to past",
									Toast.LENGTH_SHORT).show();
							return;
						}

					} catch (ParseException e) {
						e.printStackTrace();
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				try {
					if (addActivity(activityName, activityDate,
							selectedStartTimeString, selectedEndTimeString,
							activityLocation, activityDetails,
							responsiblePerson) == 0) {
						Intent i = new Intent(getApplicationContext(),
								ShowActivities.class);
						i.putExtra("childId", childId);
						startActivity(i);
					}
				} catch (com.parse.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		cancelActivity.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						ShowActivities.class);
				i.putExtra("childId", childId);
				startActivity(i);
			}
		});
		addListenerOnButton();
	}

	public void addListenerOnButton() {

		setStartTimeButton = (Button) findViewById(R.id.select_start_time_btn);
		setEndTimeButton = (Button) findViewById(R.id.select_end_time_btn);

		setStartTimeButton.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				showDialog(START_TIME_DIALOG_ID);

			}

		});

		setEndTimeButton.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				showDialog(END_TIME_DIALOG_ID);
			}
		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case START_TIME_DIALOG_ID:
			// set time picker as current time
			return new TimePickerDialog(this, startTimePickerListener, hour,
					minute, false);

		case END_TIME_DIALOG_ID:
			// set time picker as current time
			return new TimePickerDialog(this, endTimePickerListener, hour,
					minute, false);

		}
		return null;
	}

	private TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;
			selectedStartTimeString = new StringBuilder().append(pad(hour))
					.append(":").append(pad(minute)).toString();
			selectedStartTime.set(Calendar.HOUR_OF_DAY, hour);
			selectedStartTime.set(Calendar.MINUTE, minute);
			String minute_converted = "00";
			if (minute == 0)
				startTimeET.setText(String.valueOf(hour) + ":"
						+ minute_converted);
			else
				startTimeET.setText(String.valueOf(hour) + ":"
						+ String.valueOf(minute));
		}
	};

	private TimePickerDialog.OnTimeSetListener endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;
			selectedEndTimeString = new StringBuilder().append(pad(hour))
					.append(":").append(pad(minute)).toString();
			selectedEndTime.set(Calendar.HOUR_OF_DAY, hour);
			selectedEndTime.set(Calendar.MINUTE, minute);
			String minute_converted = "00";
			if (minute == 0)
				endTimeET
						.setText(String.valueOf(hour) + ":" + minute_converted);
			else
				endTimeET.setText(String.valueOf(hour) + ":"
						+ String.valueOf(minute));
		}
	};

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	@SuppressLint("NewApi")
	private int addActivity(String activityName, Date activityDate,
			String startTime, String endTime, String location, String details,
			String responsiblePerson) throws com.parse.ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (selectedEndTime.before(selectedStartTime)) {
			Log.e("",
					"startTime endtime "
							+ sdf.format(selectedEndTime.getTime()) + " "
							+ sdf.format(selectedStartTime.getTime()));
			Toast.makeText(getApplicationContext(),
					"The end time is before the start time", Toast.LENGTH_SHORT)
					.show();
			return 1;

		}

		if (activityName.equals("")) {
			Toast.makeText(getApplicationContext(),
					"You must enter activity name", Toast.LENGTH_SHORT).show();
			return 1;
		}

		if (location.equals("")) {
			Toast.makeText(getApplicationContext(),
					"You must enter activity location", Toast.LENGTH_SHORT)
					.show();
			return 1;
		}

		if (activityDate == null) {
			Toast.makeText(getApplicationContext(),
					"You must select the activity date", Toast.LENGTH_SHORT)
					.show();
			return 1;
		}

		if (startTime == null) {
			Toast.makeText(getApplicationContext(),
					"You must select the activity start time",
					Toast.LENGTH_SHORT).show();
			return 1;
		}

		if (endTime == null) {
			Toast.makeText(getApplicationContext(),
					"You must select the activity end time", Toast.LENGTH_SHORT)
					.show();
			return 1;
		}

		ParseQuery query = ParseUser.getQuery();
		query.whereEqualTo("username", responsiblePerson);
		try {
			responsiblePersonUser = (ParseUser) query.getFirst();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		ParseObject activity = new ParseObject("Activity");
		activity.put("name", activityName);
		activity.put("date", activityDate);
		activity.put("startTime", startTime);
		activity.put("endTime", endTime);
		activity.put("details", details);
		activity.put("location", location);
		activity.put("responsiblePerson", responsiblePerson);
		activity.put("childId", childId);
		ParseUser currentUser = ParseUser.getCurrentUser();
		ParseACL tutorsACL = new ParseACL();
		tutorsACL.setReadAccess(currentUser, true);
		tutorsACL.setWriteAccess(currentUser, true);
		tutorsACL.setReadAccess(responsiblePersonUser, true);
		// tutorsACL.setWriteAccess(responsiblePersonUser, true);
		activity.setACL(tutorsACL);
		activity.save();
		return 0;

	}

	public void selectDate(View view) {
		DialogFragment newFragment = new SelectDateFragment();
		newFragment.show(getSupportFragmentManager(), "DatePicker");
	}

	public void populateSetDate(int year, int month, int day) {
		activityDateET = (EditText) findViewById(R.id.activity_date);
		activityDateET.setText(month + "/" + day + "/" + year);
	}

	@SuppressLint("ValidFragment")
	public class SelectDateFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar calendar = Calendar.getInstance();
			int yy = calendar.get(Calendar.YEAR);
			int mm = calendar.get(Calendar.MONTH);
			int dd = calendar.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, yy, mm, dd);
		}

		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
			populateSetDate(yy, mm + 1, dd);
		}

	}

	void populate_responsible_persons_spinner() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item, person_names);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		responsiblePersonSpinner.setAdapter(dataAdapter);

	}
}
