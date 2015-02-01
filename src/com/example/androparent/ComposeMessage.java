package com.example.androparent;

import android.app.Activity;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ComposeMessage extends Activity {
	
	EditText toET, subjectET, bodyET;
	Button sendButton;
	ParseUser currentUser = ParseUser.getCurrentUser();
	ParseUser receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.compose_message);
	    toET = (EditText) findViewById(R.id.to_et);
	    subjectET = (EditText) findViewById(R.id.subject_et);
	    bodyET = (EditText) findViewById(R.id.body_et);
	    sendButton=(Button) findViewById(R.id.btnSend);
	    
		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String to, subject, body;
				to = toET.getText().toString();
				subject = subjectET.getText().toString();
				body = bodyET.getText().toString();
				if (to.equals(""))
				{
					Toast.makeText(getApplicationContext(),
							"Please enter the receiver name",
							Toast.LENGTH_SHORT).show();
					return;
				}
				ParseQuery query = ParseUser.getQuery();
				query.whereEqualTo("username", to);
				try {
					receiver = (ParseUser) query.getFirst();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (com.parse.ParseException e) {
					e.printStackTrace();
				}
				
				if (receiver == null) {
					Toast.makeText(getApplicationContext(),
							"The username you entered is not correct",
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				try {
					if (addMessage(to, currentUser.getUsername(), subject, body) == 0) {
						Intent i = new Intent(getApplicationContext(),
								ShowMessages.class);
						startActivity(i);
					}
				} catch (com.parse.ParseException e) {
					e.printStackTrace();
				}

			}
		});
		
	}
	
	private int addMessage(String receiverName, String senderName,
			String subject, String body) throws com.parse.ParseException {

		if (subject.equals("")) {
			Toast.makeText(getApplicationContext(),
					"You must enter a subject", Toast.LENGTH_SHORT).show();
			return 1;
		}

		if (body.equals("")) {
			Toast.makeText(getApplicationContext(),
					"Please complete the body of the message", Toast.LENGTH_SHORT)
					.show();
			return 1;
		}

		//We create two message instances- one for sender and one for receiver
		ParseObject message_sender = new ParseObject("Message1");
		message_sender.put("receiver", receiverName);
		message_sender.put("sender", senderName);
		message_sender.put("subject", subject);
		message_sender.put("body", body);
		ParseACL messageSenderACL = new ParseACL();
		messageSenderACL.setReadAccess(currentUser, true);
		messageSenderACL.setWriteAccess(currentUser, true);
		message_sender.setACL(messageSenderACL);
		message_sender.save();
		
		ParseObject message_receiver = new ParseObject("Message1");
		message_receiver.put("receiver", receiverName);
		message_receiver.put("sender", senderName);
		message_receiver.put("subject", subject);
		message_receiver.put("body", body);
		ParseACL messageReceiverACL = new ParseACL();
		messageReceiverACL.setReadAccess(receiver, true);
		messageReceiverACL.setWriteAccess(receiver, true);
		message_receiver.setACL(messageReceiverACL);
		message_receiver.save();
		return 0;

	}

}
