package com.example.androparent;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;

public class ChildAdapter extends ArrayAdapter<Child>{

    Context context;
    int layoutResourceId;   
    Child data[] = null;
   
    public ChildAdapter(Context context, int layoutResourceId, Child[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int width=390, height=260;
    	View row = convertView;
        ChildHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new ChildHolder();
            holder.imgPhoto = (ImageView)row.findViewById(R.id.child_picture_row);
            holder.txtName = (TextView)row.findViewById(R.id.child_name_row);
           
            row.setTag(holder);
        }
        else
        {
            holder = (ChildHolder)row.getTag();
        }
       
        Child Child = data[position];
        holder.txtName.setText(Child.name);
        ParseFile childPhoto = Child.photo;
		
        try {
			byte[] childPhotoByteArray = childPhoto.getData();
			Bitmap bmp = BitmapFactory.decodeByteArray(childPhotoByteArray,
					0, childPhotoByteArray.length);
			bmp = Bitmap.createScaledBitmap(bmp, width, height, true);
			holder.imgPhoto.setImageBitmap(bmp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return row;
    }
   
    static class ChildHolder
    {
        ImageView imgPhoto;
        TextView txtName;
    }
}