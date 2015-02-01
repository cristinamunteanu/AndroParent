package com.example.androparent;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class AddChild extends FragmentActivity {

	EditText childNameET, childBirthdateET, childNicknameET, childDetailsET;
	ImageView imageViewChild;
	private static int RESULT_LOAD_IMAGE = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_child);
		Parse.initialize(this, "JU4o2qxZChI2ZAs4LLJj0l0Nyp0w59yziOlsOGsN",
				"BzXsRSrcYMh64ku8KoO4vg4bNVKzUQRNlp9yBuPt");
		Button addChild = (Button) findViewById(R.id.btnAdd);
		childNameET = (EditText) findViewById(R.id.child_name);
		childBirthdateET = (EditText) findViewById(R.id.child_birthdate);
		childNicknameET = (EditText) findViewById(R.id.child_nickname);
		childDetailsET = (EditText) findViewById(R.id.child_details);
		imageViewChild = (ImageView) findViewById(R.id.child_picture_iv);
		Button buttonLoadImage = (Button) findViewById(R.id.pictureButton1);
		buttonLoadImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		addChild.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Date birthdate = null;
				String childName = childNameET.getText().toString();
				String birthdateS = childBirthdateET.getText().toString();
				String nickName = childNicknameET.getText().toString();
				Drawable childImage = imageViewChild.getDrawable();
				if (childImage == null)
					childImage = getResources().getDrawable(
							R.drawable.devil_boy);
				Bitmap bitmap = ((BitmapDrawable) childImage).getBitmap();
				bitmap = Bitmap.createScaledBitmap(bitmap, 400, 300, true);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
				byte[] childImageByte = stream.toByteArray();
				ParseFile childPhoto = new ParseFile("child.jpeg",
						childImageByte);
				try {
					childPhoto.save();
				} catch (com.parse.ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				if (!birthdateS.equals("")) {
					SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy");
					try {
						birthdate = sd.parse(birthdateS);

					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				String details = childDetailsET.getText().toString();
				try {
					addChild(childName, birthdate, childPhoto, nickName,
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

	}

	private void addChild(String childName, Date birthdate, ParseFile photo,
			String nickname, String details) throws com.parse.ParseException {
		if (childName.equals("")) {
			Toast.makeText(getApplicationContext(),
					"You must enter child name", Toast.LENGTH_SHORT).show();
			return;
		}

		ParseUser currentUser = ParseUser.getCurrentUser();

		ParseObject child = new ParseObject("Child");
		child.put("name", childName);
		if (!(birthdate == null))
			child.put("birthdate", birthdate);
		if (!(photo == null))
			child.put("photo", photo);
		if (!nickname.equals(""))
			child.put("nickname", nickname);
		if (!details.equals(""))
			child.put("details", details);
		child.addUnique("responsible_persons",
				Arrays.asList(currentUser.getUsername()));
		ParseACL childACL = new ParseACL();
		childACL.setReadAccess(currentUser, true);
		childACL.setWriteAccess(currentUser, true);
		child.setACL(childACL);
		child.save();
	}

	public void selectDate(View view) {
		DialogFragment newFragment = new SelectDateFragment();
		newFragment.show(getSupportFragmentManager(), "DatePicker");
	}

	public void populateSetDate(int year, int month, int day) {
		childBirthdateET = (EditText) findViewById(R.id.child_birthdate);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ImageView imageView = (ImageView) findViewById(R.id.child_picture_iv);
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
