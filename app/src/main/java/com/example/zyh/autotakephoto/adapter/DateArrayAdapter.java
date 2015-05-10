package com.example.zyh.autotakephoto.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.zyh.autotakephoto.R;
import com.example.zyh.autotakephoto.dao.impl.DateDataDaoImpl;
import com.example.zyh.autotakephoto.model.DateData;

import java.util.List;

public class DateArrayAdapter extends ArrayAdapter<DateData> {

    private Context context;
    private int resource;
    private List<DateData> dates;

    public DateArrayAdapter(Context context, int resource, List<DateData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        dates = objects;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (dates == null) return null;
        View view = convertView;
        DateViewHolder holder;
        if (view == null) {
            view = View.inflate(context, resource, null);
            holder = new DateViewHolder();
            holder.tv = (TextView)view.findViewById(R.id.tv_date);
            holder.btn = (Button)view.findViewById(R.id.btn_delete);
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DateData d = dates.get(position);
                    DateDataDaoImpl impl = new DateDataDaoImpl(context);
                    impl.deleteDateData(DateDataDaoImpl.DELETE_SINGLE, d.getDateId());
                    DateArrayAdapter.this.remove(d);
                }
            });
            view.setTag(holder);
        } else {
            holder = (DateViewHolder)view.getTag();
        }
        holder.tv.setText(dates.get(position).getDate());
        return view;
    }

    class DateViewHolder {
        public TextView tv;
        public Button btn;
    }
}
