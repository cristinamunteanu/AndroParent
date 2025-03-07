package com.example.androparent;

import java.util.ArrayList;
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
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ShowMessages extends ListActivity {

	ParseUser currentUser = ParseUser.getCurrentUser();
	String messageId;
	private List<ParseObject> messages;
	ParseObject message;
	private Dialog progressDialog;
	TextListAdapter adapter;
	private static final int DIALOG_DELETE_MESSAGE = 1;
	public TextListItem text_item_data[];
	public TextListItem text_item_data_sent[];
	public TextListItem text_item_data_received[];

	private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		/**
		 * Performs a background query to retrieve messages from the "Message1" Parse class.
		 * 
		 * @param params Varargs parameter of type Void (not used in this method)
		 * @return null This method always returns null
		 * @throws ParseException If there's an error while querying the Parse database
		 */
		protected Void doInBackground(Void... params) {
			ParseQuery query = new ParseQuery("Message1");
			try {
				messages = query.find();
			} catch (ParseException e) {
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			ShowMessages.this.progressDialog = ProgressDialog.show(
					ShowMessages.this, "", "Loading...", true);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		/**
		 * Processes the result of an asynchronous task that retrieves messages.
		 * This method populates a list adapter with message data and updates the UI.
		 * 
		 * @param result The result of the asynchronous task (not used in this method)
		 * @return void This method doesn't return a value
		 */
		@Override
		protected void onPostExecute(Void result) {
			ParseUser sender=null;
			String sender_name;
			String subject;
			
			if (!(messages == null)) {
				
				text_item_data = new TextListItem[messages.size()];
				
				for (int i = 0; i < messages.size(); i++) {
					sender_name = messages.get(i).get("sender").toString();
					if (sender==null){
						Log.e("", "Parseuser for this message is null");
					}
					//sender_name = sender.get("email").toString();
					subject = (String) messages.get(i).get("subject");
					text_item_data[i]= new TextListItem("From: "+sender_name, "Subject: "+subject);
				}
			}
			if (text_item_data != null) {
				adapter = new TextListAdapter(ShowMessages.this, R.layout.row_2_tv,
						text_item_data);
			setListAdapter(adapter);
			ShowMessages.this.progressDialog.dismiss();
		}
	}
	}

	/**
	 * Initializes the activity, sets up the remote data task, and registers for context menu.
	 * 
	 * This method is called when the activity is starting. It is where most initialization
	 * should go: calling setContentView(int) to inflate the activity's UI, using findViewById(int)
	 * to programmatically interact with widgets in the UI, calling managedQuery(android.net.Uri, String[], String, String[], String)
	 * to retrieve cursors for data being displayed, etc.
	 *
	 * @param savedInstanceState If the activity is being re-initialized after previously being shut down
	 *                           then this Bundle contains the data it most recently supplied in
	 *                           onSaveInstanceState(Bundle). Note: Otherwise it is null.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new RemoteDataTask().execute();
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.show_messages_om, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	/**
	 * Handles the selection of options in the options menu.
	 * 
	 * This method is responsible for processing the user's selection from the options menu.
	 * It handles three main actions:
	 * 1. Compose: Launches the ComposeMessage activity.
	 * 2. Sent: Filters and displays sent messages.
	 * 3. Received: Filters and displays received messages.
	 * 
	 * @param item The menu item that was selected.
	 * @return boolean Returns true if the menu item selection was handled successfully,
	 *         false otherwise.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String sender_name, receiver_name;
		String subject;
		switch (item.getItemId()) {
		case R.id.compose:
			Intent composeMessageIntent = new Intent(this, ComposeMessage.class);
			startActivity(composeMessageIntent);
			return true;
		case R.id.sent:
			List<ParseObject> messages_sent = new ArrayList<ParseObject>(
					messages.size());
			for (int i = 0; i < messages.size(); i++) {
				sender_name = messages.get(i).get("sender").toString();

				if (sender_name.compareTo(currentUser.getUsername().toString()) == 0)
					messages_sent.add(messages.get(i));
			}
			
			text_item_data_sent = new TextListItem[messages_sent.size()];
			for (int i = 0; i < messages_sent.size(); i++) {
				receiver_name = messages_sent.get(i).get("receiver").toString();
				subject = (String) messages_sent.get(i).get("subject");
				text_item_data_sent[i]= new TextListItem("To: "+receiver_name, "Subject: "+subject);
			}
			adapter = new TextListAdapter(ShowMessages.this, R.layout.row_2_tv,
					text_item_data_sent);
			setListAdapter(adapter);
			return true;
		case R.id.received:
			List<ParseObject> messages_received = new ArrayList<ParseObject>(
					messages.size());
			for (int i = 0; i < messages.size(); i++) {
				receiver_name = messages.get(i).get("receiver").toString();

				if (receiver_name.compareTo(currentUser.getUsername().toString()) == 0)
					messages_received.add(messages.get(i));
			}
			text_item_data_received = new TextListItem[messages_received.size()];

			for (int i = 0; i < messages_received.size(); i++) {
				sender_name = messages_received.get(i).get("sender").toString();
				subject = (String) messages_received.get(i).get("subject");
				text_item_data_received[i]= new TextListItem("From: "+sender_name, "Subject: "+subject);
			}
			adapter = new TextListAdapter(ShowMessages.this, R.layout.row_2_tv,
					text_item_data_received);
			setListAdapter(adapter);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Creates and returns a dialog based on the given dialog ID.
	 * 
	 * This method is responsible for creating an AlertDialog for deleting a message
	 * when the DIALOG_DELETE_MESSAGE ID is provided. For other dialog IDs, it
	 * delegates to the superclass implementation.
	 * 
	 * @param id The ID of the dialog to create
	 * @return A Dialog object corresponding to the given ID, or null if the ID is not recognized
	 * @deprecated This method uses deprecated API calls and is marked with @SuppressWarnings("deprecation")
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DELETE_MESSAGE:
			return new AlertDialog.Builder(this)
					.setTitle(getString(R.string.confirm_delete))
					.setIcon(R.drawable.delete)
					.setPositiveButton(R.string.confirm, new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							deleteMessage(message);
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
	 * Deletes a message asynchronously using a background task.
	 * 
	 * This method creates a new RemoteDataTask to delete the specified message
	 * object in the background, preventing UI thread blocking. If an exception
	 * occurs during deletion, it is silently caught and ignored.
	 * 
	 * @param message The ParseObject representing the message to be deleted
	 * @return void This method does not return a value
	 */
	public void deleteMessage(final ParseObject message) {
		new RemoteDataTask() {
			protected Void doInBackground(Void... params) {
				try {
					message.delete();
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
		inflater.inflate(R.menu.show_messages_cm, menu);
	}

	/**
	 * Handles the selection of a context menu item for a message.
	 * 
	 * This method is called when a context menu item is selected. It checks the user's
	 * permissions to delete a message and shows a confirmation dialog if allowed.
	 * 
	 * @param item The selected menu item
	 * @return true if the menu item was handled, false otherwise
	 */
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		message = messages.get(info.position);
		switch (item.getItemId()) {
		case R.id.delete_message:
			if (message.getACL().getWriteAccess(ParseUser.getCurrentUser()) == false) {
				Toast.makeText(getApplicationContext(),
						"You don't have the rights to perform this action!",
						Toast.LENGTH_SHORT).show();
				return false;
			} else {
				showDialog(1);
				return true;
				}
		default:
			return super.onContextItemSelected(item);
		}
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ViewMessage.class);
		i.putExtra("messageId", messages.get(position).getObjectId());
		i.putExtra("subject", messages.get(position).get("subject").toString());
		i.putExtra("body", messages.get(position).get("body").toString());
		i.putExtra("sender", messages.get(position).get("sender").toString());
		Log.e("", "messageId "+messages.get(position).getObjectId().toString());
		Log.e("", "subject "+messages.get(position).get("subject").toString());
		Log.e("", "body "+messages.get(position).get("body").toString());
		Log.e("", "sender "+messages.get(position).get("sender").toString());
		startActivity(i);
	}
}