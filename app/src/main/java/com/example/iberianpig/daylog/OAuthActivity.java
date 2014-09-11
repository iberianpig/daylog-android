package com.example.iberianpig.daylog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;


public class OAuthActivity extends Activity {

    private Button facebookAuthButton;
    private String fb_token;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        sharedPreferences = getSharedPreferences("INFO", MODE_PRIVATE);
        facebookAuthButton = (Button)findViewById(R.id.action_start_oauth_facebook);
        facebookAuthButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // start Facebook Login
                Session.openActiveSession(OAuthActivity.this, true, new Session.StatusCallback() {

                    // callback when session changes state
                    @Override
                    public void call(Session session, SessionState state, Exception exception) {
                        if (session.isOpened()) {

                            // make request to the /me API
                            Request.newMeRequest(session, new Request.GraphUserCallback() {

                                // callback after Graph API response with user object
                                @Override
                                public void onCompleted(GraphUser user, Response response) {
                                    if (user != null) {
                                        Toast.makeText(OAuthActivity.this, "My name is "+ user.getName()+".", Toast.LENGTH_LONG ).show();
                                        Session session = Session.getActiveSession();
                                        if (session != null && session.getState().isOpened()){
                                            fb_token=session.getAccessToken();
                                            Log.i("sessionToken", fb_token);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("FB_TOKEN", fb_token);
                                            editor.commit();
                                            Intent intent = new Intent(OAuthActivity.this, ListActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            }).executeAsync();
                        }
                    }
                });

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.oauth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
