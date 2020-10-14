package com.example.mycloset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UtilActivity extends AppCompatActivity {
    private Button buttonClothes, buttonCalender, buttonUtil, buttonWeather;

    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_util);
        Intent intent = getIntent();

//        titleText = findViewById(R.id.titleUtilWeatherCategory);
//        titleText.setText("오늘의 날씨");

        buttonClothes = findViewById(R.id.button_clothes);
        buttonCalender = findViewById(R.id.button_calendar);
        buttonUtil = findViewById(R.id.button_util);
        buttonWeather = findViewById(R.id.button_check_whether);

        buttonClothes.setOnClickListener(buttonClothesListener);
        buttonUtil.setOnClickListener(buttonUtilListner);
        buttonCalender.setOnClickListener(buttonCalenderListener);

        //기능구현
        buttonWeather.setOnClickListener(buttonWeatherListener);

    }


    View.OnClickListener buttonClothesListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(UtilActivity.this, MainCategoryActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener buttonCalenderListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(UtilActivity.this, CalenderActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener buttonUtilListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(UtilActivity.this, UtilActivity.class);
            startActivity(intent);
        }
    };

    //날씨 확인 리스너
    View.OnClickListener buttonWeatherListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(UtilActivity.this, UtilCheckWeatherActivity.class);
            startActivity(intent);
        }
    };


}
