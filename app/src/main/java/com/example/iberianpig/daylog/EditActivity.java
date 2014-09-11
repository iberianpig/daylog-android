package com.example.iberianpig.daylog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditActivity extends Activity {

    private DayLog dayLog;
    private DatePicker dpLogDay;
    private EditText etPositiveThing;
    private EditText etIdea;
    private EditText etThoughtAgain;
    private EditText etRemember;
    private RatingBar rbMotivation;
    private Button submitButton;
    private Button deleteButton;
    private String strStateOfActivity = null;
    private String fb_token;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit);

        sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
        fb_token= sharedPreferences.getString("FB_TOKEN", "TOKEN IS MISSING");
        submitButton = (Button)findViewById(R.id.submitButton);
        dpLogDay = (DatePicker)findViewById(R.id.log_day);
        etPositiveThing = (EditText)findViewById(R.id.positive_thing);
        etIdea = (EditText)findViewById(R.id.idea);
        etThoughtAgain = (EditText)findViewById(R.id.thought_again);
        etRemember = (EditText)findViewById(R.id.remember);
        rbMotivation = (RatingBar)findViewById(R.id.motivation);
        rbMotivation.setMax(5);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(EditActivity.this);
                alert.setTitle("Delete log");
                alert.setMessage("Are you sure you want to permanently delete this log?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Yesボタンが押された時の処理
                        Toast.makeText(EditActivity.this, "Delete is executed!", Toast.LENGTH_LONG).show();
                        strStateOfActivity = "delete";
                        try {
                            execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            strStateOfActivity = "Edit";
                        }
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        //Noボタンが押された時の処理
                        Toast.makeText(EditActivity.this, "Canceled!", Toast.LENGTH_LONG).show();
                    }});
                alert.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        dayLog = (DayLog)intent.getSerializableExtra("dayLog");
        if ( dayLog == null ){
            strStateOfActivity = "new";
            deleteButton.setVisibility(View.GONE);
            dayLog = new DayLog();
            return;
        }
        submitButton.setText("Update");
        strStateOfActivity = "edit";

        //showから受け取ったlog_dayはString型
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date_log_day;
        try {
            date_log_day = simpleDateFormat.parse(dayLog.log_day);
            Calendar ca = Calendar.getInstance();
            ca.setTime(date_log_day);
            dpLogDay.updateDate( ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        etPositiveThing.setText(dayLog.positive_thing);

        etIdea.setText(dayLog.idea);

        etRemember.setText(dayLog.remember);

        etThoughtAgain.setText(dayLog.thought_again);

        rbMotivation.setRating(dayLog.motivation);
    }

    public void execute() throws JSONException {
        AsyncHttpRequest task = new AsyncHttpRequest(EditActivity.this);
        task.owner = EditActivity.this;
        task.execute();
    }

    public class AsyncHttpRequest extends AsyncTask<String, Void, String> {
        public Activity owner;
        public AsyncHttpRequest(Activity activity) {
            owner = activity;
        }

        @Override
        protected String doInBackground(String... str) {
            try {
                HttpRequestBase httpRequest ;
                if (strStateOfActivity.equals("new")){
                    httpRequest = new HttpPost(DayLog.apiUrl("POST"));
                }else if(strStateOfActivity.equals("edit")){
                    httpRequest = new HttpPut(DayLog.apiUrl("PUT", dayLog.id));
                }else if(strStateOfActivity.equals("delete")){
                    httpRequest = new HttpDelete(DayLog.apiUrl("DELETE", dayLog.id));
                }else{
                    return "strStateOfActivity is Invalid";
                }
                DefaultHttpClient httpClient = new DefaultHttpClient();
                httpRequest.setHeader("Connection", "Keep-Alive");
                httpRequest.setHeader("Authorization", fb_token);
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                if (strStateOfActivity.equals("edit") || strStateOfActivity.equals("delete")) {
                    params.add(new BasicNameValuePair("[log][id]", String.valueOf(dayLog.id)));
                }else{
                    params.add(new BasicNameValuePair("[log][id]", null));
                }
                params.add(new BasicNameValuePair("[log][log_day(1i)]", String.valueOf(dpLogDay.getYear())));
                params.add(new BasicNameValuePair("[log][log_day(2i)]", String.valueOf(dpLogDay.getMonth()+1)));
                params.add(new BasicNameValuePair("[log][log_day(3i)]", String.valueOf(dpLogDay.getDayOfMonth())));
                params.add(new BasicNameValuePair("[log][positive_thing]", String.valueOf(etPositiveThing.getText())));
                params.add(new BasicNameValuePair("[log][idea]", String.valueOf(etIdea.getText())));
                params.add(new BasicNameValuePair("[log][remember]", String.valueOf(etRemember.getText())));
                params.add(new BasicNameValuePair("[log][thought_again]", String.valueOf(etThoughtAgain.getText())));
                int intRate= (int)rbMotivation.getRating();
                params.add(new BasicNameValuePair("[log]motivation", String.valueOf(intRate)));

                //httpリクエスト実行
                HttpResponse response;

                if (strStateOfActivity.equals("edit") || strStateOfActivity.equals("new")) {
                    HttpEntityEnclosingRequestBase httpRequestWithBody = (HttpEntityEnclosingRequestBase) httpRequest;
                    httpRequestWithBody.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
                    httpRequest =  httpRequestWithBody;
                }
                response = httpClient.execute(httpRequest);
//                Log.v("EntityUtils", EntityUtils.toString(response.getEntity()));
                int status = response.getStatusLine().getStatusCode();
                if (status == HttpStatus.SC_CREATED) {
                    //when create is success

                    //EntityUtils.toString(response.getEntity()) is readable only once
                    JSONObject json_daylog=new JSONObject(EntityUtils.toString(response.getEntity()));
                    dayLog.id = json_daylog.getInt("id");
                    dayLog.log_day = json_daylog.getString("log_day");
                    dayLog.positive_thing = json_daylog.getString("positive_thing");
                    dayLog.idea = json_daylog.getString("idea");
                    dayLog.remember = json_daylog.getString("remember");
                    dayLog.thought_again = json_daylog.getString("thought_again");
                    dayLog.motivation = json_daylog.getInt("motivation");

                    return "Success";
                } else if (status == HttpStatus.SC_NO_CONTENT) {
                    //when update is Success
                    dayLog.log_day = params.get(1).getValue() + "-" + params.get(2).getValue() + "-" + params.get(3).getValue();
                    dayLog.positive_thing = params.get(4).getValue();
                    dayLog.idea = params.get(5).getValue();
                    dayLog.remember = params.get(6).getValue();
                    dayLog.thought_again = params.get(7).getValue();
                    dayLog.motivation = Integer.parseInt(params.get(8).getValue());
                    return "Success";

                }else{

                    String message;
                    if (status==HttpStatus.SC_UNPROCESSABLE_ENTITY){
                        message = "必須項目を記入していますか？";
                    }
                    throw new Exception("Error! status code is:" + status);

                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed:"+ e;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(EditActivity.this, result, Toast.LENGTH_LONG).show();
            if(result.equals("Success")) {
                Intent intent;
                if(strStateOfActivity.equals("delete")){
                    intent = new Intent(EditActivity.this, ListActivity.class);
                }else {
                    intent = new Intent(EditActivity.this, ShowActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("dayLog", dayLog);
                startActivity(intent);
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
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
