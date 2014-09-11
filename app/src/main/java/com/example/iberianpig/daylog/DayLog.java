package com.example.iberianpig.daylog;


import android.content.SharedPreferences;

import java.io.Serializable;

public class DayLog implements Serializable{
    public int id;
    public String log_day;
    public String positive_thing;
    public String idea;
    public String remember;
    public String thought_again;
    public int motivation;
    public String url;
//    static final String api_base_url = "http://192.168.100.4:3000/logs";
    static final String api_base_url = "http://daylog-heroku.herokuapp.com/logs";

    public static String apiUrl(String request){

        String url = null;

        if (request.equals("POST") || request.equals("GET") || request.equals("DELETE")) {
            url = api_base_url + ".json";
        }

        return url;
    }

    public static String apiUrl(String request, int id){
        String url = null;

        if (request.equals("PUT") || request.equals("GET") || request.equals("DELETE")) {
            url = api_base_url+"/"+id+".json";
        }
        return url;
    }

    DayLog(){

    }

}
