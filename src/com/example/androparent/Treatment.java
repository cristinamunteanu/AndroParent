package com.example.androparent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.ParseException;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class Treatment extends Activity {
	private String medicationName, responsiblePerson, treatmentDetails;
	private int repeatInt, repetition;
	private String repeatString, reminders;
	int start_hour = 0;
	Calendar startDateCalendar = Calendar.getInstance();
	Calendar endDateCalendar = Calendar.getInstance();
	String childId;
	

	ParseUser responsiblePersonUser;

	@SuppressLint("InlinedApi")
	public Treatment(String medicationNameC, String responsiblePersonC,
			String treatmentDetailsC, Calendar startDateCalendarC,
			Calendar endDateCalendarC, int repeatIntC, String repeatStringC,
			String childIdC, int repetitionC) throws com.parse.ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		medicationName = medicationNameC;
		responsiblePerson = responsiblePersonC.substring(0,
				responsiblePersonC.length() - 1);
		treatmentDetails = treatmentDetailsC;
		startDateCalendar = startDateCalendarC;
		endDateCalendar = endDateCalendarC;
		repeatInt = repeatIntC;
		repeatString = repeatStringC;
		childId = childIdC;
		repetition=repetitionC;

		ParseQuery query = ParseUser.getQuery();
		query.whereEqualTo("username", responsiblePerson);
		try {
			responsiblePersonUser = (ParseUser) query.getFirst();
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e ("", "Could not find the responsible person in the system");
		}

		Log.e("", "medicationName " + medicationName);
		Log.e("", "responsiblePerson " + responsiblePerson);
		Log.e("", "treatmentDetails " + treatmentDetails);
		Log.e("", "startDate " + startDateCalendar.toString());
		Log.e("", "endDate " + endDateCalendar.toString());
		Log.e("", "repeatInt " + String.valueOf(repeatInt));
		Log.e("", "repeatString " + repeatString);
		Log.e("", "reminders " + reminders);
		Log.e("", "childId " + String.valueOf(childId));

		ParseObject treatment = new ParseObject("Treatment");
		treatment.put("name", medicationName);
		treatment.put("responsiblePerson", responsiblePerson);
		treatment.put("details", treatmentDetails);
		treatment.put("startTime", String.valueOf(startDateCalendar.getTimeInMillis())); 
		treatment.put("endTime", String.valueOf(endDateCalendar.getTimeInMillis()));
		treatment.put("repeatInt", repeatInt);
		treatment.put("repeatString", repeatString);
		treatment.put("childId", childId);
		treatment.put("repetition", repetition);
		ParseUser currentUser = ParseUser.getCurrentUser();
		ParseACL tutorsACL = new ParseACL();
		tutorsACL.setReadAccess(currentUser, true);
		tutorsACL.setWriteAccess(currentUser, true);
		tutorsACL.setReadAccess(responsiblePersonUser, true);

		// tutorsACL.setWriteAccess(responsiblePersonUser, true);
		treatment.setACL(tutorsACL);
		treatment.save();

	
	}
}
