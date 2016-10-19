package kms2.EagleEye;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PositionDataAdapter extends ArrayAdapter<PositionDataStatus> {
	private ArrayList<PositionDataStatus> items;   
	private LayoutInflater inflater;

	public PositionDataAdapter(Context context, int textViewResourceId,   
			ArrayList<PositionDataStatus> items) {   
		super(context, textViewResourceId, items);   
		this.items = items;   
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}   

	@Override  
	public View getView(int position, View convertView, ViewGroup parent) {   
		View view = convertView;   
		if (view == null) {   
			view = inflater.inflate(R.layout.list_position_data, null);   
		}   
		PositionDataStatus item = (PositionDataStatus)items.get(position);
		
		if (item != null) {
			/** instance */
			TextView row1 = (TextView)view.findViewById(R.id.PositionDataRow1);   
			TextView row2 = (TextView)view.findViewById(R.id.PositionDataRow2);   
			TextView row3 = (TextView)view.findViewById(R.id.PositionDataRow3);   

			/** 文字セット */
			row1.setText(item.getRow1());
			row2.setText(item.getRow2());
			row3.setText(item.getRow3());
			
			/** 文字色変更 */
			if(item.getColorFlg() != MainForm.FLG_DEFAULT){
				row1.setTextColor(item.getColor());
				row2.setTextColor(item.getColor());
			}
		}   
		return view;   
	}   
}   
