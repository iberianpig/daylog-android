package com.example.iberianpig.daylog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;


public class ShowActivity extends Activity {

    private TextView tvLogDay;
    private TextView tvPositiveThing;
    private TextView tvIdea;
    private TextView tvRemember;
    private TextView tvThoughtAgain;
    private RatingBar rbMotivation;
    private Button editButton;
    private DayLog dayLog;

    public ShowActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show);

        tvLogDay = (TextView)findViewById(R.id.log_day);

        tvPositiveThing = (TextView)findViewById(R.id.positive_thing);

        tvIdea = (TextView)findViewById(R.id.idea);

        tvRemember = (TextView)findViewById(R.id.remember);

        tvThoughtAgain = (TextView)findViewById(R.id.thought_again);

        rbMotivation = (RatingBar)findViewById(R.id.motivation);
        rbMotivation.setMax(5);
        rbMotivation.setIsIndicator(true);

        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowActivity.this, EditActivity.class);
                intent.putExtra("dayLog", dayLog);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //listView, editViewからのシリアライズされたDayLogオブジェクトをもらう
        Intent intent = getIntent();
        dayLog = (DayLog)intent.getSerializableExtra("dayLog");

        //各Viewにセット
        tvLogDay.setText(dayLog.log_day);

        tvPositiveThing.setText(dayLog.positive_thing);

        tvIdea.setText(dayLog.idea);

        tvRemember.setText(dayLog.remember);

        tvThoughtAgain.setText(dayLog.thought_again);

        rbMotivation.setRating(dayLog.motivation);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show, menu);
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
