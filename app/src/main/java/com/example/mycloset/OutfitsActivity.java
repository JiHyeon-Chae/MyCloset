package com.example.mycloset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class OutfitsActivity extends AppCompatActivity {
    private Button buttonClothes, buttonOutfits, buttonCalender, buttonAnalysis;
    private List<String> outfitsData;
    private ArrayAdapter<String> outfitsAdapter;
    private ListView outfitsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outfits);
        Intent intent = getIntent();

        buttonClothes = findViewById(R.id.button_clothes);
        buttonOutfits = findViewById(R.id.button_outfits);
        buttonCalender = findViewById(R.id.button_calendar);
        buttonAnalysis = findViewById(R.id.button_util);

        buttonClothes.setOnClickListener(buttonClothesListener);
        buttonOutfits.setOnClickListener(buttonOutfitsListener);
        buttonAnalysis.setOnClickListener(buttonAnalysisListener);
        buttonCalender.setOnClickListener(buttonCalenderListener);
        initData();
        initAdapter();
        initListView();

    }

    //리스트뷰
    private void initData() {
        outfitsData = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            outfitsData.add("item" + i);
        }
    }

    private void initAdapter() {
        outfitsAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, outfitsData);
    }

    private void initListView() {
        ListView listView = (ListView) findViewById(R.id.outfitsListView);
        listView.setAdapter((outfitsAdapter));
    }

    View.OnClickListener buttonClothesListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(OutfitsActivity.this, MainCategoryActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener buttonOutfitsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(OutfitsActivity.this, OutfitsActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener buttonCalenderListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(OutfitsActivity.this, CalenderActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener buttonAnalysisListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(OutfitsActivity.this, UtilActivity.class);
            startActivity(intent);
        }
    };

}
