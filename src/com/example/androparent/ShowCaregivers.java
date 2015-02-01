package com.example.androparent;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ShowCaregivers extends ListActivity {

	String childId;
	ParseObject child;
	private List persons=null;
	private Dialog progressDialog;
	ArrayAdapter<String> adapter;

	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			ParseQuery query = new ParseQuery("Child");
			try {
				child = query.get(childId);
				persons = child.getList("responsible_persons");
			} catch (ParseException e) {
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			ShowCaregivers.this.progressDialog = ProgressDialog.show(
					ShowCaregivers.this, "", "Loading...", true);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void reslt) {
			// Put the list of persons into the list view
			adapter = new ArrayAdapter<String>(ShowCaregivers.this,
					R.layout.row_simple);
			if (!(persons==null)) {
				for (int i = 0; i < persons.size(); i++) {
					Pattern pattern = Pattern.compile(".*\\\"(.*)\\\".*");
					Matcher matcher = pattern
							.matcher(persons.get(i).toString());
					if (matcher.find()) {
						adapter.add(matcher.group(1));
					}
				}
			}

			setListAdapter(adapter);
			ShowCaregivers.this.progressDialog.dismiss();
			TextView empty = (TextView) findViewById(android.R.id.empty);
			empty.setVisibility(View.VISIBLE);
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_caregivers);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			childId = extras.getString("childId");
		}
		new RemoteDataTask().execute();
		registerForContextMenu(getListView());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.show_responsible_persons_om, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem add = menu.findItem(R.id.add);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			Intent addResponsiblePerson = new Intent(this,
					AddCaregiver.class);
			addResponsiblePerson.putExtra("childId", childId);
			startActivity(addResponsiblePerson);
			return true;
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.show_responsible_persons_cm, menu);
	}

	public boolean onContextItemSelected(MenuItem item) {
		String personName;
		ParseUser person = null;
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.remove:
			if (child.getACL().getWriteAccess(ParseUser.getCurrentUser())==false){
				Toast.makeText(getApplicationContext(), "You don't have the right to perform this action!",
						Toast.LENGTH_SHORT).show();
			return false;	
			}
			else{
			// TODO: put are you sure question
			child.removeAll("responsible_persons",
					Arrays.asList(persons.get(info.position)));
			Pattern pattern = Pattern.compile(".*\\\"(.*)\\\".*");
			Matcher matcher = pattern.matcher(persons.get(info.position)
					.toString());
			if (matcher.find()) {
				personName = matcher.group(1);
			} else
				return false;

			ParseQuery queryForUser = ParseUser.getQuery();

			queryForUser.whereEqualTo("username", personName);
			try {
				person = (ParseUser) queryForUser.getFirst();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (person == null) {
				return false;
			}
			if (child.getACL().getReadAccess(person) == true) {
				Log.e("",
						person.getUsername()
								+ "has read access "
								+ String.valueOf(child.getACL().getReadAccess(
										person)));
				child.getACL().setReadAccess(person, false);
				Log.e("",
						"After change "
								+ person.getUsername()
								+ "has read access "
								+ String.valueOf(child.getACL().getReadAccess(
										person)));
			}
			if (child.getACL().getWriteAccess(person) == true) {
				Log.e("",
						person.getUsername()
								+ "has write access "
								+ String.valueOf(child.getACL().getReadAccess(
										person)));
				child.getACL().setWriteAccess(person, false);
				Log.e("",
						"After change "
								+ person.getUsername()
								+ "has write access "
								+ String.valueOf(child.getACL().getReadAccess(
										person)));
			}
			try {
				child.save();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			persons.remove(persons.get(info.position));
			adapter.clear();
			setListAdapter(adapter);
			for (int i = 0; i < persons.size(); i++) {
				matcher = pattern.matcher(persons.get(i).toString());
				if (matcher.find()) {
					adapter.add(matcher.group(1));
				}
			}
			setListAdapter(adapter);
			return true;
			}

		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String personName;
		super.onListItemClick(l, v, position, id);
		Pattern pattern = Pattern.compile(".*\\\"(.*)\\\".*");
		Matcher matcher = pattern.matcher(persons.get(position).toString());
		if (matcher.find()) {
			personName = matcher.group(1);
		} else
			return;
		Intent i = new Intent(this, ViewCaregiver.class);
		i.putExtra("childId", childId);
		i.putExtra("personName", personName);
		startActivity(i);
	}
}
