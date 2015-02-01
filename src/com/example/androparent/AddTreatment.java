package com.example.androparent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class AddTreatment extends FragmentActivity {

	EditText medicationNameET, startTimeET, endTimeET, detailsET;

	TimePicker treatmentStartTimePicker, treatmentEndTimePicker;

	Button responsiblePersonsButton, startDateButton, endDateButton,
			startTimeButton, endTimeButton, addButton, cancelButton;
	Spinner repetitionNumberSpinner, repetitionStringSpinner;
	CheckBox repeatCb;
	Calendar startTimeCalendar = Calendar.getInstance();
	Calendar endTimeCalendar = Calendar.getInstance();
	String selectedStartTimeString, selectedEndTimeString;
	ParseObject child;
	private List persons = null;
	private List<String> person_names = new ArrayList<String>();
	protected CharSequence[] person_names_charsequence;
	private List<CharSequence> selected_person_names = new ArrayList<CharSequence>();

	private Dialog progressDialog;
	ParseUser responsiblePersonUser;
	Treatment treatment;

	int startHour, startMinute, endHour, endMinute, repetition = 0;

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
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			AddTreatment.this.progressDialog = ProgressDialog.show(
					AddTreatment.this, "", "Loading...", true);
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
						// person_names[i]=matcher.group(1);
						person_names.add(matcher.group(1));
					}
				}

			}
			AddTreatment.this.progressDialog.dismiss();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			childId = extras.getString("childId");
		}
		setContentView(R.layout.add_treatment);

		addButton = (Button) findViewById(R.id.btnAdd);
		cancelButton = (Button) findViewById(R.id.btnCancel);
		medicationNameET = (EditText) findViewById(R.id.add_treatment_name);
		startTimeET = (EditText) findViewById(R.id.at_start_time_text);
		endTimeET = (EditText) findViewById(R.id.at_end_time_text);
		detailsET = (EditText) findViewById(R.id.treatment_details);
		addListenerOnRepeatCb();

		/*
		 * to be seen how we program that treatmentStartTimePicker
		 * treatmentEndTimePicker
		 */

		responsiblePersonsButton = (Button) findViewById(R.id.select_persons);
		startDateButton = (Button) findViewById(R.id.select_start_date_btn);
		endDateButton = (Button) findViewById(R.id.select_end_date_btn);
		startTimeButton = (Button) findViewById(R.id.select_start_time_btn);
		endTimeButton = (Button) findViewById(R.id.select_end_time_btn);
		repetitionNumberSpinner = (Spinner) findViewById(R.id.repetition_number_spinner);
		repetitionStringSpinner = (Spinner) findViewById(R.id.repetition_string_spinner);

		new RemoteDataTask().execute(); // this populates the person_names list

		responsiblePersonsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.select_persons:
					showSelectedPersonNamesDialog();
					break;

				default:
					break;
				}
			}

		});

		/* display current time on time pickers */

		final Calendar c = Calendar.getInstance();
		startHour = endHour = c.get(Calendar.HOUR_OF_DAY);
		startMinute = endMinute = c.get(Calendar.MINUTE);
		updateTime(startHour, startMinute, "start");
		updateTime(endHour, endMinute, "end");
		startTimeButtonClickListener();
		endTimeButtonClickListener();

		addButton.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("SimpleDateFormat")
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				String medicationName, responsiblePerson, treatmentStartDateS, treatmentEndDateS, treatmentDetails;
				int repeatInt = 0;
				String repeatString = "", reminders = "";

				Date treatmentStartDate = new Date();
				Date treatmentEndDate = new Date();

				medicationName = medicationNameET.getText().toString();
				if (medicationName.equals("")) {
					Toast.makeText(getApplicationContext(),
							"Please complete the medication name",
							Toast.LENGTH_SHORT).show();
					return;
				}
				responsiblePerson = responsiblePersonsButton.getText()
						.toString();
				if (responsiblePerson.equals("- None Selected -")) {
					Toast.makeText(getApplicationContext(),
							"You must select the responsible persons name",
							Toast.LENGTH_SHORT).show();
					return;
				}
				treatmentDetails = detailsET.getText().toString();
				treatmentStartDateS = startDateButton.getText().toString();
				treatmentEndDateS = endDateButton.getText().toString();
				if (repetition == 0) {
					repeatInt = 0;
					repeatString = "0";
				} else {
					repeatInt = Integer
							.parseInt((String) repetitionNumberSpinner
									.getSelectedItem());

					repeatString = repetitionStringSpinner.getSelectedItem()
							.toString();

				}


				if (!treatmentStartDateS.equals("Start Date")) {
					SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");
					try {
						treatmentStartDate = sd.parse(treatmentStartDateS);
						// The following lines are for ignoring the time from
						// date when comparing to today
						startTimeCalendar.setTime(treatmentStartDate);
						Calendar today = Calendar.getInstance();
						if (startTimeCalendar.before(today)) {
							Toast.makeText(
									getApplicationContext(),
									"You can not add treatment start date to past",
									Toast.LENGTH_SHORT).show();
							return;
						}
						startTimeCalendar.set(Calendar.HOUR, startHour);
						startTimeCalendar.set(Calendar.MINUTE, startMinute);

					} catch (ParseException e) {
						e.printStackTrace();
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				if (!treatmentEndDateS.equals("End Date")) {
					SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");
					try {
						treatmentEndDate = sd.parse(treatmentEndDateS);

						if (treatmentEndDate.before(treatmentStartDate)) {
							Toast.makeText(
									getApplicationContext(),
									"You can not add treatment end date to past",
									Toast.LENGTH_SHORT).show();
							return;
						}
						endTimeCalendar.set(Calendar.HOUR, endHour);
						endTimeCalendar.set(Calendar.MINUTE, endMinute);

					} catch (ParseException e) {
						e.printStackTrace();
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				try {
					treatment = new Treatment(medicationName,
							responsiblePerson, treatmentDetails,
							startTimeCalendar, endTimeCalendar, repeatInt,
							repeatString, childId, repetition);
				} catch (com.parse.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Intent i = new Intent(getApplicationContext(),
						ShowTreatments.class);
				i.putExtra("childId", childId);
				startActivity(i);

			}

		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						ShowTreatments.class);
				i.putExtra("childId", childId);
				startActivity(i);
			}
		});

	}

	public void addListenerOnRepeatCb() {

		repeatCb = (CheckBox) findViewById(R.id.repeat_cb);

		repeatCb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (((CheckBox) v).isChecked()) {
					repetitionNumberSpinner.setEnabled(true);
					repetitionStringSpinner.setEnabled(true);
					repetition = 1;
				} else {
					repetitionNumberSpinner.setEnabled(false);
					repetitionStringSpinner.setEnabled(false);
					repetition = 0;
				}

			}
		});

	}

	protected void showSelectedPersonNamesDialog() {
		boolean[] checked_person_names = new boolean[person_names.size()];
		int count = person_names.size();

		for (int i = 0; i < count; i++)
			checked_person_names[i] = selected_person_names
					.contains(person_names.get(i));

		DialogInterface.OnMultiChoiceClickListener personNamesDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which,
					boolean isChecked) {
				if (isChecked)
					selected_person_names.add(person_names.get(which));
				else
					selected_person_names.remove(person_names.get(which));

				onChangeSelectedPersonNames();
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select responsible persons");
		person_names_charsequence = person_names
				.toArray(new CharSequence[person_names.size()]);
		builder.setMultiChoiceItems(person_names_charsequence,
				checked_person_names, personNamesDialogListener);
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();

	}

	protected void onChangeSelectedPersonNames() {
		StringBuilder stringBuilder = new StringBuilder();

		for (CharSequence person : selected_person_names)
			stringBuilder.append(person + ",");

		responsiblePersonsButton.setText(stringBuilder.toString());
	}

	public void startTimeButtonClickListener() {
		startTimeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog(START_TIME_DIALOG_ID);

			}

		});

	}

	public void endTimeButtonClickListener() {
		endTimeButton.setOnClickListener(new OnClickListener() {

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
			return new TimePickerDialog(this, startTimePickerListener,
					startHour, startMinute, false);
		case END_TIME_DIALOG_ID:
			return new TimePickerDialog(this, endTimePickerListener, endHour,
					endMinute, false);
		}
		return null;
	}

	private TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
			startHour = hourOfDay;
			startMinute = minutes;
			updateTime(startHour, startMinute, "start");

		}
	};

	private TimePickerDialog.OnTimeSetListener endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
			endHour = hourOfDay;
			endMinute = minutes;
			updateTime(endHour, endMinute, "end");
		}
	};

	private static String utilTime(int value) {
		if (value < 10)
			return "0" + String.valueOf(value);
		else
			return String.valueOf(value);
	}

	private void updateTime(int hours, int mins, String flag) {

		String timeSet = "";
		if (hours > 12) {
			hours -= 12;
			timeSet = "PM";
		} else if (hours == 0) {
			hours += 12;
			timeSet = "AM";
		} else if (hours == 12)
			timeSet = "PM";
		else
			timeSet = "AM";
		String minutes = "";
		if (mins < 10)
			minutes = "0" + mins;
		else
			minutes = String.valueOf(mins);
		String aTime = new StringBuilder().append(hours).append(':')
				.append(minutes).append(" ").append(timeSet).toString();
		if (flag.compareTo("start") == 0)
			startTimeET.setText(aTime);
		else if (flag.compareTo("end") == 0)
			endTimeET.setText(aTime);
		else {
			Toast.makeText(getApplicationContext(),
					"start/end time is not selected", Toast.LENGTH_SHORT)
					.show();
			return;
		}
	}

	public void selectStartDate(View view) {
		DialogFragment newFragment = new SelectStartDateFragment();
		newFragment.show(getSupportFragmentManager(), "DatePicker");
	}

	public void selectEndDate(View view) {
		DialogFragment newFragment = new SelectEndDateFragment();
		newFragment.show(getSupportFragmentManager(), "DatePicker");
	}

	public void populateSetStartDate(int year, int month, int day) {
		startDateButton.setText(month + "/" + day + "/" + year);
	}

	public void populateSetEndDate(int year, int month, int day) {
		endDateButton.setText(month + "/" + day + "/" + year);
	}

	@SuppressLint("ValidFragment")
	public class SelectStartDateFragment extends DialogFragment implements
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
			populateSetStartDate(yy, mm + 1, dd);
		}

	}

	@SuppressLint("ValidFragment")
	public class SelectEndDateFragment extends DialogFragment implements
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
			populateSetEndDate(yy, mm + 1, dd);
		}

	}

}
