package com.tencent.apk_auto_test.data;

import com.tencent.apk_auto_test.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class ChoosePartAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;

    private String[] chooseListText;

    public ChoosePartAdapter(Context context, String[] listText) {
        mContext = context;
        chooseListText = listText;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return chooseListText.length;
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
        ChooseViewHolder holder = null;
        if (convertView == null) {

            holder = new ChooseViewHolder();

            convertView = mInflater.inflate(R.layout.choose_list_item, null);
            holder.text = (TextView) convertView.findViewById(R.id.list_text);
            holder.button = (Button) convertView.findViewById(R.id.btn_reduce);
            convertView.setTag(holder);

        } else {

            holder = (ChooseViewHolder) convertView.getTag();
        }

        holder.text.setText(chooseListText[position]);
        holder.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RunPara mPara = new RunPara();
                mPara.runNumber = 1;
                mPara.runCase = chooseListText[position];

                mPara.runCaseNumber = position;

                StaticData.runList.add(mPara);
                StaticData.runAdapter.notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
