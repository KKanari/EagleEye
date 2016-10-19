package kms2.EagleEye;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingAdapter extends ArrayAdapter<SettingStatus> {
	private ArrayList<SettingStatus> items;   
	private LayoutInflater inflater;

	public SettingAdapter(Context context, int textViewResourceId,   
			ArrayList<SettingStatus> items) {   
		super(context, textViewResourceId, items);   
		this.items = items;   
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}   

	@Override  
	public View getView(int position, View convertView, ViewGroup parent) {   
		View view = convertView;   
		if (view == null) {   
			view = inflater.inflate(R.layout.list_setting, null);   
		}   
		SettingStatus item = (SettingStatus)items.get(position);
		
		if (item != null) {
			/** instance */
			ImageView checked = (ImageView)view.findViewById(R.id.SettingCheckBox);   
			TextView text = (TextView)view.findViewById(R.id.SettingText);   

			/** チェックボックス背景設定 */
			if(item.getCheacked() == MainForm.FLG_ON){
				checked.setBackgroundResource(R.drawable.btn_check_on);
				checked.setVisibility(View.VISIBLE);
			}else if(item.getCheacked() == MainForm.FLG_OFF){
				checked.setBackgroundResource(R.drawable.btn_check_off);
				checked.setVisibility(View.VISIBLE);
			}else{
				checked.setVisibility(View.INVISIBLE);
			}
			text.setText(item.getText());
		}   
		return view;   
	}   
}   
