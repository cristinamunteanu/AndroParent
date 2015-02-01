package com.example.androparent;

import java.io.ByteArrayOutputStream;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ShowKidsListActivity extends ListActivity {

	private List<ParseObject> kids;
	private ParseObject kid;
	private Dialog progressDialog;
	ChildAdapter adapter;
	private static final int DIALOG_DELETE_CHILD = 1;
	public static boolean deleted = false;
	public Child child_data[];
	private String TAG = "ShowKids";

	public class RemoteDataTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			ParseQuery query = new ParseQuery("Child");
			try {
				kids = query.find();
			} catch (ParseException e) {

			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			ShowKidsListActivity.this.progressDialog = ProgressDialog.show(
					ShowKidsListActivity.this, "", "Loading...", true);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			if (!(kids == null)) {
				Log.e("Cristina", "---kids is not null");
				child_data = new Child[kids.size()];
				int i = 0;
				for (ParseObject child : kids) {
					ParseFile photo = (ParseFile) child.get("photo");
					if (photo == null) {
						Drawable childImage = getResources().getDrawable(
								R.drawable.devil_boy);
						Bitmap bitmap = ((BitmapDrawable) childImage).getBitmap();
						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
						byte[] childImageByte = stream.toByteArray();
						photo = new ParseFile("child.jpeg", childImageByte);
						try {
							photo.save();
						} catch (com.parse.ParseException e1) {
							e1.printStackTrace();
						}
					}
					child_data[i] = new Child(photo, (String) child.get("name"));
					i++;
				}
			}

			if (child_data != null) {
				adapter = new ChildAdapter(ShowKidsListActivity.this, R.layout.row,
						child_data);
				setListAdapter(adapter);
				Log.e("Cristina", "------list data is not null");
				

			}
			ShowKidsListActivity.this.progressDialog.dismiss();
			TextView empty = (TextView) findViewById(android.R.id.empty);
			empty.setVisibility(View.VISIBLE);
		} 
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		TextView empty = (TextView) findViewById(android.R.id.empty);
		empty.setVisibility(View.INVISIBLE);
		new RemoteDataTask().execute();
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.show_kids_om, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem signUp = menu.findItem(R.id.signup);
		MenuItem logIn = menu.findItem(R.id.login);

		MenuItem logOut = menu.findItem(R.id.logout);
		if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {

			signUp.setVisible(true);
			logIn.setVisible(true);

		} else {
			logOut.setVisible(true);

		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addChild:
			Intent addChildIntent = new Intent(this, AddChild.class);
			startActivity(addChildIntent);
			return true;
		case R.id.login:
			Intent logInIntent = new Intent(this, LogIn.class);
			startActivity(logInIntent);
			return true;
		case R.id.signup:
			Intent signUpIntent = new Intent(this, SignUp.class);
			startActivity(signUpIntent);
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
							deleteChild(kid);
							
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

	public void deleteChild(final ParseObject child) {
		new RemoteDataTask() {
			protected Void doInBackground(Void... params) {
				try {
					child.delete();
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
		inflater.inflate(R.menu.show_kids_cm, menu);
	}

	@SuppressWarnings("deprecation")
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		kid = kids.get(info.position);
		switch (item.getItemId()) {
		case R.id.edit_child:
			if (kid.getACL().getWriteAccess(ParseUser.getCurrentUser()) == false) {
				Toast.makeText(getApplicationContext(),
						"You don't have the right to perform this action!",
						Toast.LENGTH_SHORT).show();
				return false;
			} else {
				String objectId = kids.get(info.position).getObjectId();
				Intent i = new Intent(getApplicationContext(), EditChild.class);
				i.putExtra("childId", objectId);
				startActivity(i);
				return true;
			}
		case R.id.delete_child:
			if (kid.getACL().getWriteAccess(ParseUser.getCurrentUser()) == false) {
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
		Intent i = new Intent(this, ViewChild.class);
		i.putExtra("childId", kids.get(position).getObjectId());
		i.putExtra("childName", kids.get(position).getString("name"));
		startActivity(i);
	}

	@Override
	public void onBackPressed() {
		// do nothing
	}

}
