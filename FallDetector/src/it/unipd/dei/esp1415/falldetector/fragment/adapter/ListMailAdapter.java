
package it.unipd.dei.esp1415.falldetector.fragment.adapter;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.utility.MailAddress;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListMailAdapter extends ArrayAdapter<MailAddress>{
	
	private ArrayList<MailAddress> mArray;
	private ViewHolder mViewHolder;

	private LayoutInflater mInflater;

	/**[c]
	 * @param context
	 * @param objects
	 */
	public ListMailAdapter(Context context, ArrayList<MailAddress> objects) {
		super(context, R.layout.mail_list_row, objects);
		
		mArray = objects;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		MailAddress toView = mArray.get(position);
		mViewHolder = new ViewHolder();
		
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.mail_list_row, parent, false);
		
			mViewHolder.nameSurname = (TextView) convertView.findViewById(R.id.name_surname);
			mViewHolder.address = (TextView) convertView.findViewById(R.id.mail_address);
		}
		
		String nameSurname = toView.getName();
		nameSurname = (nameSurname != null && nameSurname.length() > 0) ? 
				nameSurname + " " + toView.getSurname() : nameSurname + toView.getSurname();
				
		mViewHolder.nameSurname.setText(nameSurname);
		mViewHolder.address.setText(toView.getAddress());
		
		return convertView;
	}
	
	private class ViewHolder{
		private TextView nameSurname;
		private TextView address;
	}
}
