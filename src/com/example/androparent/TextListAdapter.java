package com.example.androparent;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TextListAdapter extends ArrayAdapter<TextListItem> {

	Context context;
	int layoutResourceId;
	TextListItem data[] = null;

	public TextListAdapter(Context context, int layoutResourceId,
			TextListItem[] data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	/**
	 * Gets a custom view for an item in the list.
	 * 
	 * This method is responsible for creating or recycling a view for a list item
	 * and populating it with data from the TextListItem at the specified position.
	 * It uses the ViewHolder pattern for efficient scrolling performance.
	 * 
	 * @param position The position of the item within the adapter's data set
	 * @param convertView The old view to reuse, if possible
	 * @param parent The parent that this view will eventually be attached to
	 * @return A View corresponding to the data at the specified position
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		TextListHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new TextListHolder();
			holder.text1 = (TextView) row.findViewById(R.id.text1_row);
			holder.text2 = (TextView) row.findViewById(R.id.text2_row);
			row.setTag(holder);
		} else {
			holder = (TextListHolder) row.getTag();
		}

		TextListItem TextList = data[position];
		holder.text1.setText(TextList.upText);
		holder.text2.setText(TextList.downText);
		return row;

	}

	static class TextListHolder {
		TextView text1;
		TextView text2;
	}

}
