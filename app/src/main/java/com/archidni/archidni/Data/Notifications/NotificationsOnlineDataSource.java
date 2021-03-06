package com.archidni.archidni.Data.Notifications;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.archidni.archidni.App;
import com.archidni.archidni.AppSingleton;
import com.archidni.archidni.Data.SharedPrefsUtils;
import com.archidni.archidni.Model.Notifications.Notification;
import com.archidni.archidni.Model.Transport.Line;
import com.archidni.archidni.Model.Transport.LineSkeleton;
import com.archidni.archidni.Model.TransportMean;
import com.archidni.archidni.OauthStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class NotificationsOnlineDataSource implements NotificationsDataSource {


    private static final String GET_NOTIFICATIONS_URL = "/api/v1/CompanyNotifications";

    @Override
    public void getNotifications(final NotificationsRepository.OnNotificationsFound onNotificationsFound) {
        String url = SharedPrefsUtils.getServerUrl(App.getAppContext())+GET_NOTIFICATIONS_URL;
        OauthStringRequest stringRequest = new OauthStringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<Notification> notifications = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("title");
                        int transportModeId = jsonObject.getInt("transport_mode_id")-1;
                        String description = jsonObject.getString("description");
                        int type = jsonObject.getInt("type");
                        JSONArray linesJsonArray = jsonObject.getJSONArray("lines");
                        ArrayList<LineSkeleton> lines = new ArrayList<>();
                        for (int j=0;j<linesJsonArray.length();j++)
                        {
                            JSONObject lineJson = linesJsonArray.getJSONObject(j);
                            int id = lineJson.getInt("id");
                            String name = lineJson.getString("name");
                            int transportMeanId = lineJson.getInt("transport_mode_id")-1;
                            LineSkeleton line = new LineSkeleton(id,name, TransportMean.allTransportMeans.get(transportMeanId));
                            lines.add(line);
                        }
                        Notification notification = new Notification(title,
                                TransportMean.allTransportMeans.get(transportModeId),lines,description,type);
                        notifications.add(notification);
                    }
                    onNotificationsFound.onNotificationsFound(notifications);
                } catch (JSONException e) {
                    e.printStackTrace();
                    onNotificationsFound.onError();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onNotificationsFound.onError();
            }
        });
        stringRequest.performRequest("TAG_NOTIFICATIONS");
    }
}
