package com.example.iberianpig.daylog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends Activity{

    private DayLogAdapter dayLogAdapter;
    private DayLog dayLog = new DayLog();
    private List<DayLog> logList = new ArrayList<DayLog>();
    private String fb_token;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_list);
        sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
        fb_token= sharedPreferences.getString("FB_TOKEN", "TOKEN IS MISSING");

        Button syncButton = (Button)findViewById(R.id.syncButton);
        Button newButton = (Button)findViewById(R.id.newButton);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sync();
            }
        });
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, EditActivity.class);
                Log.v("Check", String.valueOf(logList));
                startActivity(intent);
            }
        });
        dayLogAdapter = new DayLogAdapter(this, 0, logList);
        ListView list_view = (ListView)findViewById(R.id.list_view);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                // クリックされたアイテムを取得します
                DayLog dayLog = (DayLog) listView.getItemAtPosition(position);
                // クリックされたらURLを表示させる
                Intent intent = new Intent(ListActivity.this, ShowActivity.class);
                intent.putExtra("dayLog", dayLog);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sync();
    }

    public void sync(){
        AsyncHttpRequest task = new AsyncHttpRequest(ListActivity.this);
        task.owner = ListActivity.this;  task.execute(DayLog.apiUrl("GET"));
    }

    public class AsyncHttpRequest extends AsyncTask<String, Void, String> {
        public Activity owner;
        public AsyncHttpRequest(Activity activity) {
            owner = activity;
        }

        @Override
        protected String doInBackground(String... url) {
            try {
                HttpGet httpGet = new HttpGet(url[0]);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                httpGet.setHeader("Connection", "Keep-Alive");
                httpGet.setHeader("Authorization", fb_token);

                HttpResponse response = httpClient.execute(httpGet);
                int status = response.getStatusLine().getStatusCode();
                if (status != HttpStatus.SC_OK) {
                    throw new Exception("");
                } else {
                    JSONArray jsonArray = new JSONArray(EntityUtils.toString(response.getEntity(), "utf-8"));
                    logList.clear();
                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject json_daylog = jsonArray.getJSONObject(i);
                          dayLog = new DayLog();

                        dayLog.id = json_daylog.getInt("id");
                        dayLog.log_day = json_daylog.getString("log_day");
                        dayLog.positive_thing = json_daylog.getString("positive_thing");
                        dayLog.idea = json_daylog.getString("idea");
                        dayLog.remember = json_daylog.getString("remember");
                        dayLog.thought_again = json_daylog.getString("thought_again");
                        dayLog.motivation = json_daylog.getInt("motivation");
                        dayLog.url = json_daylog.getString("url");
//                        Log.v("log_day",dayLog.log_day);
                        logList.add(dayLog);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            ListView list_view = (ListView)findViewById(R.id.list_view);
            list_view.setAdapter(dayLogAdapter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

}
