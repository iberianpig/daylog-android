package com.example.iberianpig.daylog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class DayLogAdapter extends ArrayAdapter<DayLog>{
    LayoutInflater layoutInflater_;

    public DayLogAdapter(Context context, int textViewResourceId, List<DayLog> objects) {
        super(context, textViewResourceId, objects);
        layoutInflater_ = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 特定の行(position)のデータを得る
        DayLog dayLog = getItem(position);

        // convertViewは使い回しされている可能性があるのでnullの時だけ新しく作る
        if (null == convertView) {
            convertView = layoutInflater_.inflate(R.layout.daylog_layout, null);
        }

        TextView tvLogDay;
        tvLogDay = (TextView)convertView.findViewById(R.id.log_day);
        tvLogDay.setText(dayLog.log_day);

// RememberとThoughtAgainのみ表示させるようにする
//        TextView tvPositiveThing;
//        tvPositiveThing = (TextView)convertView.findViewById(R.id.positive_thing);
//        tvPositiveThing.setText(dayLog.positive_thing);
//
//        TextView tvIdea;
//        tvIdea = (TextView)convertView.findViewById(R.id.idea);
//        tvIdea.setText(dayLog.idea);

        TextView tvThoughtAgain;
        tvThoughtAgain = (TextView)convertView.findViewById(R.id.thought_again);
        tvThoughtAgain.setText(dayLog.thought_again);

        TextView tvRemember;
        tvRemember = (TextView)convertView.findViewById(R.id.remember);
        tvRemember.setText(dayLog.remember);

        RatingBar rbMotivation;
        rbMotivation = (RatingBar)convertView.findViewById(R.id.motivation);
        rbMotivation.setMax(5);
        rbMotivation.setRating(dayLog.motivation);
        rbMotivation.setIsIndicator(true);

        return convertView;
    }
}
