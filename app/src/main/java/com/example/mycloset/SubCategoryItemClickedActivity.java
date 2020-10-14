package com.example.mycloset;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Dictionary;

public class SubCategoryItemClickedActivity extends Activity {

    private Button buttonClothes, buttonCalender, buttonAnalysis, buttonSubCategoryItemEdit;

    private int img;

    private String getName, getInfo, getColor, getCategory, getimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category_item_clicked);
        Intent intent = getIntent();

        //수정버튼 클릭시 인텐트로 넘길 자료들을 변수화해둠.
        getName = intent.getStringExtra("subCategoryName");
        getInfo = intent.getStringExtra("subCategoryInfo");
        getCategory = intent.getStringExtra("subCategory");
        getColor = intent.getStringExtra("subCategoryColor");

        // 버튼 구현.
        buttonSubCategoryItemEdit = findViewById(R.id.button_sub_category_item_edit);
//        buttonClothes = findViewById(R.id.button_clothes);
////        buttonOutfits = findViewById(R.id.button_outfits);
//        buttonCalender = findViewById(R.id.button_calender);
//        buttonAnalysis = findViewById(R.id.button_analysis);
//        buttonClothes.setOnClickListener(buttonClothesListener);
//        buttonOutfits.setOnClickListener(buttonOutfitsListener);
//        buttonAnalysis.setOnClickListener(buttonAnalysisListener);
//        buttonCalender.setOnClickListener(buttonCalenderListener);
//        buttonSubCategoryItemEdit.setOnClickListener(buttonSubCategoryItemEditListener);

        ImageView subCategoryIcon = (ImageView) findViewById(R.id.img_icon);
        TextView subCategoryName = (TextView) findViewById(R.id.tv_name);
        TextView subCategoryInfo = (TextView) findViewById(R.id.tv_info);
        TextView subCategory = (TextView) findViewById(R.id.tv_category);
        TextView subCategoryColor = (TextView) findViewById(R.id.tv_color);

        //인텐트로 받은 이미지, 이름, 설명을 레이아웃으로 채운다.
        subCategoryIcon.setImageBitmap(SubCategoryActivity.StringToBitmap(intent.getStringExtra("subCategoryIcon")));
        subCategoryName.setText(intent.getStringExtra("subCategoryName"));
        subCategoryInfo.setText(intent.getStringExtra("subCategoryInfo"));
        subCategory.setText(intent.getStringExtra("subCategory"));
        subCategoryColor.setText(intent.getStringExtra("subCategoryColor"));

        //수정버튼
//        buttonSubCategoryItemEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), SubCategoryAdapter.SubCategoryViewHolder.class);
////                intent.putExtra("subCategoryIcon", img);
////                intent.putExtra("subCategoryName", getName);
////                intent.putExtra("subCategoryInfo", getInfo);
////                intent.putExtra("subCategory", getCategory);
////                intent.putExtra("subCategoryColor",getColor);
//                startActivity(intent);
//            }
//        });
    }
//    //상단바 클릭 리스너
//    View.OnClickListener buttonSubCategoryItemEditListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(getApplicationContext(), SubCategoryItemEditActivity.class);
//            intent.putExtra("subCategoryIcon", img);
//            intent.putExtra("subCategoryName", getName);
//            intent.putExtra("subCategoryInfo", getInfo);
//            intent.putExtra("subCategory", getCategory);
//            intent.putExtra("subCategoryColor",getColor);
//            startActivity(intent);
//        }
//    };
//    //하단바 클릭 리스너
//    View.OnClickListener buttonClothesListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(SubCategoryItemClickedActivity.this, MainCategoryActivity.class);
//            startActivity(intent);
//        }
//    };
//    View.OnClickListener buttonCalenderListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(SubCategoryItemClickedActivity.this, CalenderActivity.class);
//            startActivity(intent);
//        }
//    };
//    View.OnClickListener buttonAnalysisListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent intent = new Intent(SubCategoryItemClickedActivity.this, AnalysisActivity.class);
//            startActivity(intent);
//        }
//    };

//    @Override
//    public void onClick (View view){
//
//    }


}
