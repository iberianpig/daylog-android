package com.example.iberianpig.daylog;


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

    public static String apiUrl(String request){

        String url = null;

        if (request.equals("POST") || request.equals("GET") || request.equals("DELETE")) {
            url = "http://daylog-heroku.herokuapp.com/logs.json";
        }

        return url;
    }

    public static String apiUrl(String request, int id){
        String url = null;

        if (request.equals("PUT") || request.equals("GET") || request.equals("DELETE")) {
            url = "http://daylog-heroku.herokuapp.com/logs/"+id+".json";
        }
        return url;
    }

    DayLog(){

    }

//    DayLog(int id, String log_day, String positive_thing, String idea, String remember, String thought_again, int motivation, String url){
//        this.id = id;
//        this.log_day = log_day;
//        this.positive_thing = positive_thing;
//        this.idea = idea;
//        this.remember = remember;
//        this.thought_again = thought_again;
//        this.motivation = motivation;
//        this.url = url;
//    }




}
