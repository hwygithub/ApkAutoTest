package com.tencent.apk_auto_test.data;

import com.tencent.apk_auto_test.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class RunPartAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;

	public RunPartAdapter(Context context) {
		mContext = context;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return StaticData.runList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		RunViewHolder holder = null;
		if (convertView == null) {
			holder = new RunViewHolder();

			convertView = mInflater.inflate(R.layout.run_list_item, null);
			holder.text = (TextView) convertView.findViewById(R.id.list_text);
			holder.button = (Button) convertView.findViewById(R.id.list_button);
			holder.add = (Button) convertView.findViewById(R.id.btn_add);
			holder.reduce = (Button) convertView.findViewById(R.id.btn_reduce);
			holder.runText = (TextView) convertView
					.findViewById(R.id.run_text_time);
			convertView.setTag(holder);

		} else {
			holder = (RunViewHolder) convertView.getTag();
		}

		holder.runText.setText(StaticData.runList.get(position).runNumber + "");
		holder.text.setText((position + 1) + "-"
				+ StaticData.runList.get(position).runCase);

		holder.button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) { 
				StaticData.runList.remove(position);
				StaticData.runAdapter.notifyDataSetChanged();
			}
		});
		holder.add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StaticData.runList.get(position).runNumber++;
				StaticData.runAdapter.notifyDataSetChanged();
			}
		});
		holder.reduce.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (StaticData.runList.get(position).runNumber == 1) {
					return;
				}
				StaticData.runList.get(position).runNumber--;
				StaticData.runAdapter.notifyDataSetChanged();

			}
		});

		return convertView;
	}
}
