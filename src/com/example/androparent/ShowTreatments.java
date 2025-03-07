package com.example.androparent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
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

public class ShowTreatments extends ListActivity {

	String childId;
	ParseObject child;
	private List<ParseObject> treatments;
	private ParseObject treatment;
	ParseObject activity;
	private Dialog progressDialog;
	ArrayAdapter<String> adapter;
	boolean deleted = false;
	private static final int DIALOG_DELETE_TREATMENT = 1;

	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		/**
		 * Performs a background query to retrieve treatments for a specific child.
		 * 
		 * This method executes a Parse query to fetch Treatment objects associated with
		 * a given childId. The query results are stored in the treatments field.
		 * 
		 * @param params Varargs parameter of type Void, not used in this method.
		 * @return null This method always returns null as per the AsyncTask contract.
		 * @throws ParseException If there's an error during the query execution.
		 *         Note: The exception is caught but not handled in the method.
		 */
		protected Void doInBackground(Void... params) {
			ParseQuery query = new ParseQuery("Treatment");
			query.whereEqualTo("childId", childId);
			try {
				treatments = query.find();
			} catch (ParseException e) {
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			ShowTreatments.this.progressDialog = ProgressDialog.show(
					ShowTreatments.this, "", "Loading...", true);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		/**
		 * Updates the list adapter with treatment names after background processing.
		 * 
		 * This method is called when the AsyncTask completes. It populates an ArrayAdapter
		 * with treatment names from the 'treatments' list and sets it as the list adapter.
		 * Finally, it dismisses the progress dialog.
		 * 
		 * @param reslt The result of the background computation, which is not used in this method.
		 */
		@Override
		protected void onPostExecute(Void reslt) {
			adapter = new ArrayAdapter<String>(ShowTreatments.this,
					R.layout.row_simple);
			if (!(treatments == null)) {
				for (int i = 0; i < treatments.size(); i++) {
					adapter.add(treatments.get(i).get("name").toString());
				}
			}

			setListAdapter(adapter);
			ShowTreatments.this.progressDialog.dismiss();
		}
	}

	/**
	 * Initializes the activity, sets up the user interface, and retrieves child data.
	 * 
	 * This method is called when the activity is first created. It performs the following tasks:
	 * 1. Calls the superclass onCreate method
	 * 2. Retrieves the childId from the intent extras
	 * 3. Executes a RemoteDataTask asynchronously
	 * 4. Registers the activity for context menu
	 * 5. Retrieves child data from Parse database
	 *
	 * @param savedInstanceState Bundle containing the activity's previously saved state, or null if there was none
	 * @throws com.parse.ParseException if there is an error retrieving child data from Parse
	 */
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
			e1.printStackTrace();
			Log.e("get child", "The get child request failed.");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.show_treatments_om, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	/**
	 * Handles the selection of options menu items in the activity.
	 * 
	 * This method is called when a user selects an item from the options menu.
	 * It handles two specific cases:
	 * 1. Adding a treatment: Checks user permissions and starts the AddTreatment activity if allowed.
	 * 2. Logging out: Logs out the current user and returns to the MainActivity.
	 * 
	 * @param item The menu item that was selected.
	 * @return boolean Returns true if the menu item selection was handled, false otherwise.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_treatment:
			if (child.getACL().getWriteAccess(ParseUser.getCurrentUser()) == false) {
				Toast.makeText(getApplicationContext(),
						"You don't have the right to perform this action!",
						Toast.LENGTH_SHORT).show();
				return false;
			} else {
				Intent addTreatmentIntent = new Intent(this, AddTreatment.class);
				addTreatmentIntent.putExtra("childId", childId);
				startActivity(addTreatmentIntent);
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

	/**
	 * Creates and returns a Dialog for the specified dialog ID.
	 * 
	 * This method is responsible for creating dialogs used in the application.
	 * Currently, it handles the creation of a confirmation dialog for deleting a treatment.
	 * 
	 * @param id The ID of the dialog to create
	 * @return A Dialog object corresponding to the specified ID, or the result of
	 *         the superclass method if the ID is not recognized
	 * @deprecated This method uses deprecated functionality and is marked with @SuppressWarnings("deprecation")
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DELETE_TREATMENT:
			return new AlertDialog.Builder(this)
					.setTitle(getString(R.string.confirm_delete))
					.setIcon(R.drawable.delete)
					.setPositiveButton(R.string.confirm, new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							deleteTreatment(treatment);
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

	/**
	 * Deletes a treatment object asynchronously.
	 * 
	 * This method creates a new RemoteDataTask to delete the given treatment
	 * object in the background. It handles any ParseException that may occur
	 * during the deletion process.
	 * 
	 * @param treatment The ParseObject representing the treatment to be deleted
	 * @return void This method does not return a value
	 */
	public void deleteTreatment(final ParseObject treatment) {
		new RemoteDataTask() {
			protected Void doInBackground(Void... params) {
				try {
					treatment.delete();
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
		inflater.inflate(R.menu.show_treatments_cm, menu);
	}

	/**
	 * Handles the selection of context menu items for a treatment.
	 * 
	 * This method processes the user's selection from the context menu, performing
	 * actions such as adding the treatment to the calendar or deleting the treatment.
	 * It handles different menu item selections and executes the corresponding logic.
	 * 
	 * @param item The selected menu item
	 * @return true if the menu item selection was handled, false otherwise
	 */
	@SuppressWarnings("deprecation")
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		treatment = treatments.get(info.position);
		switch (item.getItemId()) {
		case R.id.add_to_calendar:
			String treatFreq,
			calFreq = null;
			treatFreq = treatment.getString("repeatString");

			if (treatFreq.compareTo("none") == 0)
				calFreq = "NONE";
			else if (treatFreq.compareTo("hours") == 0)
				calFreq = "HOURLY";
			else if (treatFreq.compareTo("days") == 0)
				calFreq = "DAILY";
			else if (treatFreq.compareTo("weeks") == 0)
				calFreq = "WEEKLY";
			else if (treatFreq.compareTo("months") == 0)
				calFreq = "MONTHLY";

			Intent intent = new Intent(Intent.ACTION_INSERT)
					.setData(Events.CONTENT_URI)
					.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
							treatment.getLong("startTime"))
					.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
							treatment.getLong("endTime"))
					.putExtra(Events.TITLE, treatment.getString("name"))
					.putExtra(Events.DESCRIPTION,
							treatment.getString("details"))

					.putExtra(
							Events.RRULE,
							"FREQ=" + calFreq + ";INTERVAL="
									+ treatment.getInt("repeatInt"))
					.putExtra(ALARM_SERVICE, true);

			startActivity(intent);

			return true;

		case R.id.delete:
			if (treatment.getACL().getWriteAccess(ParseUser.getCurrentUser()) == false) {
				Toast.makeText(getApplicationContext(),
						"You don't have the rights to perform this action!",
						Toast.LENGTH_SHORT).show();
				return false;
			} else {
				showDialog(1);
				// refresh after delete
				if (deleted) {
					treatments.remove(treatment);
					adapter.clear();
					setListAdapter(adapter);
					for (ParseObject treatment : treatments) {
						adapter.add((String) treatment.get("name"));
					}
					setListAdapter(adapter);
					return true;
				}
			}

		default:
			return super.onContextItemSelected(item);
		}
	}

	/**
	 * Handles the click event on a list item in the treatments list.
	 * 
	 * This method is called when a user clicks on a treatment in the list view.
	 * It creates an intent to start the ViewTreatment activity, passing the
	 * selected treatment's ID as an extra.
	 * 
	 * @param l The ListView where the click happened
	 * @param v The view that was clicked within the ListView
	 * @param position The position of the view in the list
	 * @param id The row id of the item that was clicked
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ViewTreatment.class);
		Log.e(" ", "try to pass treatmentID "
				+ treatments.get(position).getObjectId().toString());
		i.putExtra("treatmentId", treatments.get(position).getObjectId()
				.toString());
		startActivity(i);
	}

}
