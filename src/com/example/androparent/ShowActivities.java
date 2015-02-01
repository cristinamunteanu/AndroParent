package com.example.androparent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ShowActivities extends ListActivity {

	String childId;
	ParseObject child;
	private List<ParseObject> activities;
	ParseObject activity;
	private Dialog progressDialog;
	// ArrayAdapter<String> adapter;
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
	private static final int DIALOG_DELETE_CHILD = 1;
	boolean deleted = false;
	TextListAdapter adapter;
	public TextListItem text_item_data[];
	public TextListItem text_item_data_today[];
	public TextListItem text_item_data_week[];
	public TextListItem text_item_data_month[];

	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			ParseQuery query = new ParseQuery("Activity");
			query.whereEqualTo("childId", childId);
			try {
				activities = query.find();
			} catch (ParseException e) {
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			ShowActivities.this.progressDialog = ProgressDialog.show(
					ShowActivities.this, "", "Loading...", true);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Void reslt) {
			Date activity_date;
			String activity_time;
			Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);

			if (!(activities == null)) {
				text_item_data = new TextListItem[activities.size()];
				for (int i = 0; i < activities.size(); i++) {
					activity_date = (Date) activities.get(i).get("date");
					activity_time = (String) activities.get(i).get("endTime");
					activity_date.setHours(Integer.parseInt(activity_time
							.substring(0, 2)));
					activity_date.setMinutes(Integer.parseInt(activity_time
							.substring(3, 5)));
					if (activity_date.after(today))
						text_item_data[i] = new TextListItem("Location: "+
								activities.get(i).get("location")+"  "+" Date "
										+ " " + sdf.format(activities.get(i).get("date")),
								"Name: "+activities.get(i).get("name").toString());
					else
						try {
							activities.get(i).delete();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}

			if (text_item_data != null) {
				adapter = new TextListAdapter(ShowActivities.this,
						R.layout.row_2_tv, text_item_data);
				setListAdapter(adapter);
				ShowActivities.this.progressDialog.dismiss();
			}
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
		new RemoteDataTask().execute();
		registerForContextMenu(getListView());
		ParseQuery query = new ParseQuery("Child");

		try {
			child = query.get(childId);
		} catch (com.parse.ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.e("get child", "The get child request failed.");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.show_activities_om, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_activity:

			if (child.getACL().getWriteAccess(ParseUser.getCurrentUser()) == false) {
				Toast.makeText(getApplicationContext(),
						"You don't have the right to perform this action!",
						Toast.LENGTH_SHORT).show();
				return false;
			} else {
				Intent addActivityIntent = new Intent(this, AddActivity.class);
				addActivityIntent.putExtra("childId", childId);
				startActivity(addActivityIntent);
				return true;
			}
		case R.id.logout:
			ParseUser.logOut();
			Toast.makeText(getApplicationContext(), "Good bye!",
					Toast.LENGTH_SHORT).show();
			Intent mainIntent = new Intent(this, MainActivity.class);
			startActivity(mainIntent);
			return true;
		case R.id.today:
			Date activity_date;
			Date today = new Date();
			List<ParseObject> activities_today = new ArrayList<ParseObject>(
					activities.size());
			for (int i = 0; i < activities.size(); i++) {
				activity_date = (Date) activities.get(i).get("date");
				if (sdf.format(activity_date).equals(sdf.format(today))) {
					activities_today.add(activities.get(i));
				}
			}
			text_item_data_today = new TextListItem[activities_today.size()];
			for (int i = 0; i < activities_today.size(); i++)
				text_item_data_today[i] = new TextListItem(
						"Location: "+
								activities.get(i).get("location")+"  "+" Date "
										+ " " + sdf.format(activities.get(i).get("date")),
								"Name: "+activities.get(i).get("name").toString());
			adapter = new TextListAdapter(ShowActivities.this,
					R.layout.row_2_tv, text_item_data_today);
			setListAdapter(adapter);
			return true;
		case R.id.week:
			Calendar week_end = Calendar.getInstance();
			week_end.setFirstDayOfWeek(Calendar.MONDAY);
			week_end.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			List<ParseObject> activities_week = new ArrayList<ParseObject>(
					activities.size());
			for (int i = 0; i < activities.size(); i++) {
				activity_date = (Date) activities.get(i).get("date");
				Calendar activity_cal = Calendar.getInstance();
				activity_cal.setTime(activity_date);
				if (activity_cal.compareTo(week_end) <= 0)
					activities_week.add(activities.get(i));
			}
			text_item_data_week = new TextListItem[activities_week.size()];
			for (int i = 0; i < activities_week.size(); i++)
				text_item_data_week[i] = new TextListItem("Location: "+
						activities.get(i).get("location")+"  "+" Date "
						+ " " + sdf.format(activities.get(i).get("date")),
				"Name: "+activities.get(i).get("name").toString());
			adapter = new TextListAdapter(ShowActivities.this,
					R.layout.row_2_tv, text_item_data_week);
			setListAdapter(adapter);
			return true;
		case R.id.month:
			Date month_begining,
			month_end;
			Calendar calendar = getCalendarForNow();
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			setTimeToBeginningOfDay(calendar);
			month_begining = calendar.getTime();
			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			setTimeToEndofDay(calendar);
			month_end = calendar.getTime();
			List<ParseObject> activities_month = new ArrayList<ParseObject>(
					activities.size());
			for (int i = 0; i < activities.size(); i++) {
				activity_date = (Date) activities.get(i).get("date");
				if (activity_date.before(month_end))
					activities_month.add(activities.get(i));
			}
			text_item_data_month = new TextListItem[activities_month.size()];
			for (int i = 0; i < activities_month.size(); i++)
				text_item_data_month[i] = new TextListItem(
						"Location: "+
								activities.get(i).get("location")+"  "+" Date "
										+ " " + sdf.format(activities.get(i).get("date")),
								"Name: "+activities.get(i).get("name").toString());
			adapter = new TextListAdapter(ShowActivities.this,
					R.layout.row_2_tv, text_item_data_month);
			setListAdapter(adapter);
			return true;
		case R.id.all:
			new RemoteDataTask().execute();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private static Calendar getCalendarForNow() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date());
		return calendar;
	}

	private static void setTimeToBeginningOfDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	private static void setTimeToEndofDay(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DELETE_CHILD:
			return new AlertDialog.Builder(this)
					.setTitle(getString(R.string.confirm_delete))
					.setIcon(R.drawable.delete)
					.setPositiveButton(R.string.confirm, new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							deleteActivity(activity);
							deleted = true;
						}
					})
					.setNegativeButton(R.string.cancel, new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}

					}).create();
		}
		return super.onCreateDialog(id);
	}

	public void deleteActivity(final ParseObject activity) {
		new RemoteDataTask() {
			protected Void doInBackground(Void... params) {
				try {
					activity.delete();
				} catch (ParseException e) {
				}
				super.doInBackground();
				return null;
			}
		}.execute();

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.show_activities_cm, menu);
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		activity = activities.get(info.position);
		switch (item.getItemId()) {
		case R.id.edit:
			if (activity.getACL().getWriteAccess(ParseUser.getCurrentUser()) == false) {
				Toast.makeText(getApplicationContext(),
						"You don't have the right to perform this action!",
						Toast.LENGTH_SHORT).show();
				return false;
			} else {
				String objectId = activities.get(info.position).getObjectId();
				Intent i = new Intent(getApplicationContext(),
						EditActivity.class);
				i.putExtra("childId", childId);
				i.putExtra("activityId", objectId);
				startActivity(i);
				return true;
			}
		case R.id.delete:
			if (activity.getACL().getWriteAccess(ParseUser.getCurrentUser()) == false) {
				Toast.makeText(getApplicationContext(),
						"You don't have the rights to perform this action!",
						Toast.LENGTH_SHORT).show();
				return false;
			} else {
				showDialog(1);
			}
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ViewActivity.class);
		i.putExtra("activityId", activities.get(position).getObjectId());
		i.putExtra("childId", childId);
		startActivity(i);
	}
}
