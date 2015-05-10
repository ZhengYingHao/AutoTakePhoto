package com.example.zyh.autotakephoto.dao.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.zyh.autotakephoto.R;
import com.example.zyh.autotakephoto.dao.DateDataDao;
import com.example.zyh.autotakephoto.model.DateData;
import com.example.zyh.autotakephoto.model.UserInfo;
import com.ta.util.http.AsyncHttpClient;
import com.ta.util.http.AsyncHttpResponseHandler;
import com.ta.util.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DateDataDaoImpl implements DateDataDao {

    private List<DateData> dates = null;
    private Context context;

    public DateDataDaoImpl(Context context) { this.context = context; }

    /**
     * 从网站得到JSON数据，解析并存入dates
     */
    @Override
    public void downloadDateData(final OnFinishGetDateData listener) {
        RequestParams params = new RequestParams();
        params.put("userId", UserInfo.getUserInfo().getUserId());
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(context, context.getString(R.string.getDataUrl), params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String content) {
                        super.onSuccess(content);

                        if (TextUtils.isEmpty(content)) return ;
                        try {
                            JSONArray jsonArray = new JSONArray(content);
                            dates = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); ++i) {
                                JSONObject row = jsonArray.getJSONObject(i);

                                DateData d = new DateData();
                                d.setDateId(row.getInt(DateData.DATE_ID));
                                d.setYear(row.getInt(DateData.DATE_YEAR));
                                d.setMonth(row.getInt(DateData.DATE_MONTH));
                                d.setDate(row.getString(DateData.DATE));
                                dates.add(d);

                            }
                            listener.onFinish();
                        } catch (JSONException e) {
                            Log.i("DateDataDaoImpl", "json exception.");
                            dates = null;
                        }
                    }
                });
    }

    @Override
    public void deleteDateData(String deleteType, int dateId) {
        if (!DELETE_ALL.equals(deleteType) && !DELETE_SINGLE.equals(deleteType)) return ;


        RequestParams params = new RequestParams();
        if (DELETE_ALL.equals(deleteType)) {
            params.put("type", DELETE_ALL);
            params.put("userId", UserInfo.getUserInfo().getUserId());
            Log.i("DateData", "id : " + UserInfo.getUserInfo().getUserId());
        } else if (DELETE_SINGLE.equals(deleteType)) {
            params.put("type", DELETE_SINGLE);
            params.put("dateId", "" + dateId);
            Log.i("DateData", "dateId: " + dateId);
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(context, context.getString(R.string.deleteDataUrl), params, new AsyncHttpResponseHandler());
    }

    @Override
    public List<DateData> getDateData() {
        return dates;
    }

    public interface OnFinishGetDateData {
        void onFinish();
    }

}
