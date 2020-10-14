package com.example.mycloset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class SubCategoryItemEditActivity extends AppCompatActivity {
    private Button buttonEditFinish;
    private EditText editName, editInfo, editCategory, editColor;
    private ImageButton editImage;
    private int img;
    private String getCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category_item_edit);
        Intent intent = getIntent();

        getCategory = intent.getStringExtra("subCategory");

        buttonEditFinish = findViewById(R.id.button_sub_category_item_edit_finish);

        editImage = findViewById(R.id.img_iconEdit);
        editName = findViewById(R.id.tv_nameEdit);
        editInfo = findViewById(R.id.tv_infoEdit);
        editCategory = findViewById(R.id.tv_categoryEdit);
        editColor = findViewById(R.id.tv_colorEdit);

//        NumberFormatException이 계속 일어남 대책 강구.
//        - SubCategoryActivity에서 넘어올 떄는 발생 안 하고, SubActivityItemClicked에서 넘어갈 떄만 발생함.
//        img=Integer.parseInt(intent.getStringExtra("subCategoryIcon"));
//        editImage.setImageResource(img);
        editName.setText(intent.getStringExtra("subCategoryName"));
        editInfo.setText(intent.getStringExtra("subCategoryInfo"));
        editCategory.setText(intent.getStringExtra("subCategory"));
        editColor.setText(intent.getStringExtra("subCategoryColor"));

        buttonEditFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SubCategoryActivity.class);
                intent.putExtra("clickedCategoryItem", getCategory);

                startActivity(intent);
            }
        });
    }
}
