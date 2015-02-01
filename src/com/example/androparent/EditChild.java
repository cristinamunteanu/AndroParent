package com.example.androparent;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class EditChild extends FragmentActivity {

	EditText childNameET, childBirthdateET, childNicknameET, childDetailsET;
	ImageView imageViewChild;
	String childId;
	ParseObject child;
	private static int RESULT_LOAD_IMAGE = 1;
	SimpleDateFormat sd = new SimpleDateFormat("MM-dd-yyyy");
	ParseFile childPhoto;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		int width = 400, height = 300;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_child);

		Button saveChild = (Button) findViewById(R.id.btnSave);
		Button cancelEditChild = (Button) findViewById(R.id.btnCancel);
		childNameET = (EditText) findViewById(R.id.edit_child_name);
		childBirthdateET = (EditText) findViewById(R.id.edit_child_birthdate);
		childNicknameET = (EditText) findViewById(R.id.edit_child_nickname);
		childDetailsET = (EditText) findViewById(R.id.edit_child_details);
		imageViewChild = (ImageView) findViewById(R.id.edit_child_picture_iv);
		Button buttonLoadImage = (Button) findViewById(R.id.edit_pictureButton1);
		buttonLoadImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			childId = extras.getString("childId");
		}
		ParseQuery query = new ParseQuery("Child");

		try {
			child = query.get(childId);
		} catch (com.parse.ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Log.e("get child", "The get child request failed.");
			return;
		}
		childNameET.setText(child.getString("name"));
		if (!(child.getDate("birthdate") == null))
			childBirthdateET.setText(child.getDate("birthdate").toString());
		if (!(child.getString("nickname") == null))
			childNicknameET.setText(child.getString("nickname"));
		if (!(child.getString("details") == null))
			childDetailsET.setText(child.getString("details"));
		if (!(child.getParseFile("photo") == null)) {
			childPhoto = child.getParseFile("photo");
			try {
				byte[] childPhotoByteArray = childPhoto.getData();
				Bitmap bmp = BitmapFactory.decodeByteArray(childPhotoByteArray,
						0, childPhotoByteArray.length);
				bmp = Bitmap.createScaledBitmap(bmp, width, height, true);
				imageViewChild.setImageBitmap(bmp);
			} catch (com.parse.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		saveChild.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Date birthdate = null;
				String childName = childNameET.getText().toString();
				String birthdateS = childBirthdateET.getText().toString();
				if (!birthdateS.equals("")) {
					SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");
					try {
						try {
							birthdate = sd.parse(birthdateS);
						} catch (java.text.ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				// get photo
				Drawable childImage = imageViewChild.getDrawable();
				Bitmap bitmap = ((BitmapDrawable) childImage).getBitmap();
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				byte[] childImageByte = stream.toByteArray();
				ParseFile childPhoto = new ParseFile("child.jpeg",
						childImageByte);
				try {
					childPhoto.save();
				} catch (com.parse.ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String nicknameS = childNicknameET.getText().toString();
				String details = childDetailsET.getText().toString();
				try {
					saveChild(childName, birthdate, childPhoto, nicknameS,
							details);
				} catch (com.parse.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// TODO: verify fields
				Intent i = new Intent(getApplicationContext(),
						ShowKidsListActivity.class);
				startActivity(i);
			}

		});

		cancelEditChild.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private void saveChild(String childName, Date birthdate, ParseFile photo,
			String nickname, String details) throws com.parse.ParseException {
		if (childName.equals("")) {
			Toast.makeText(getApplicationContext(),
					"You must enter child name", Toast.LENGTH_SHORT).show();
			return;
		}

		String currentUserName = ParseUser.getCurrentUser().getUsername()
				.toString();
		ParseUser currentUser = ParseUser.getCurrentUser();

		child.put("name", childName);
		if (!(birthdate == null))
			child.put("birthdate", birthdate);
		if (!(photo == null))
			child.put("photo", photo);
		if (!nickname.equals(""))
			child.put("nickname", nickname);
		if (!details.equals(""))
			child.put("details", details);
		child.put("parent", currentUserName);
		ParseACL parentsACL = new ParseACL();
		parentsACL.setReadAccess(currentUser, true);
		parentsACL.setWriteAccess(currentUser, true);
		child.setACL(parentsACL);
		child.save();

	}

	public void selectDate(View view) {
		DialogFragment newFragment = new SelectDateFragment();
		newFragment.show(getSupportFragmentManager(), "DatePicker");
	}

	public void populateSetDate(int year, int month, int day) {
		childBirthdateET = (EditText) findViewById(R.id.edit_child_birthdate);
		childBirthdateET.setText(month + "/" + day + "/" + year);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_child_om, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.activities:
			Intent showActivitiesIntent = new Intent(this, ShowActivities.class);
			showActivitiesIntent.putExtra("childId", childId);
			startActivity(showActivitiesIntent);
			return true;
		case R.id.treatments:
			Intent showTreatmentsIntent = new Intent(this, ShowTreatments.class);
			showTreatmentsIntent.putExtra("childId", childId);
			startActivity(showTreatmentsIntent);
			return true;
		case R.id.responsible_persons:
			Intent showResponsiblePersonsIntent = new Intent(this,
					ShowCaregivers.class);
			showResponsiblePersonsIntent.putExtra("childId", childId);
			startActivity(showResponsiblePersonsIntent);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ImageView imageView = (ImageView) findViewById(R.id.edit_child_picture_iv);
		super.onActivityResult(requestCode, resultCode, data);
		if (imageView.getDrawable() != null)
			imageView.setImageResource(0);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

		}

	}

}
