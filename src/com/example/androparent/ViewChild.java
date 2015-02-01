package com.example.androparent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewChild extends Activity {
	
	String childId, childName;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			childId = extras.getString("childId");
			childName = extras.getString("childName");
			
		}
		if(childName!=null)
			getActionBar().setTitle(childName);
         
        /**
         * Creating all buttons instances
         * */
        // Dashboard Activities button
        Button btn_activities = (Button) findViewById(R.id.btn_activities);
         
        // Dashboard Caregivers button
        Button btn_caregivers = (Button) findViewById(R.id.btn_caregivers);
         
        // Dashboard Messages button
        Button btn_messages = (Button) findViewById(R.id.btn_messages);
         
        // Dashboard Treatments button
        Button btn_treatments = (Button) findViewById(R.id.btn_treatments);
         
        /**
         * Handling all button click events
         * */
         
        // Listening to News Feed button click
        btn_activities.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), ShowActivities.class);
                i.putExtra("childId", childId);
                startActivity(i);
            }
        });
         
       // Listening Friends button click
        btn_caregivers.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), ShowCaregivers.class);
                i.putExtra("childId", childId);
                startActivity(i);
            }
        });
         
        // Listening Messages button click
        btn_messages.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), ShowMessages.class);
                //i.putExtra("childId", childId);
                startActivity(i);
            }
        });
         
        // Listening to Places button click
        btn_treatments.setOnClickListener(new View.OnClickListener() {
             
            @Override
            public void onClick(View view) {
                // Launching News Feed Screen
                Intent i = new Intent(getApplicationContext(), ShowTreatments.class);
                i.putExtra("childId", childId);
                startActivity(i);
            }
        });
         
    }
}
