package com.bsreeinf.jobapp.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bsreeinf.jobapp.R;

import java.util.List;

public class SimpleContainerAdapter extends ArrayAdapter<SimpleContainer> {
    private final List<SimpleContainer.SimpleBlock> listItems;
    private LayoutInflater inflater;
    private SimpleContainer.SimpleBlock currBlock;

    public SimpleContainerAdapter(Context context, List<SimpleContainer.SimpleBlock> listItems) {
        super(context, R.layout.simple_row_layout);
        this.listItems = listItems;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null || convertView.getTag() == null) {
            convertView = inflater.inflate(R.layout.simple_row_layout,
                    parent, false);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        currBlock = listItems.get(position);
        holder.txtTitle.setText(currBlock.getTitle());

        int pos = -1;
//        pos = DialogModifyRoster.getmSelectedItem();
        
        if (pos == position) {
            convertView.setBackgroundColor(Color.LTGRAY);
            holder.txtTitle.setTextColor(Color.WHITE);
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
            holder.txtTitle.setTextColor(Color.DKGRAY);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    private class ViewHolder {
        public TextView txtTitle;
    }
}
