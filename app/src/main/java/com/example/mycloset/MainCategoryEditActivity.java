package com.example.mycloset;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainCategoryEditActivity extends Activity {
    ArrayList<String> edittingList;
    private EditText editTextMainCategory;
    private int count, checked;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_category_edit);
        Intent intent = getIntent();

        //MainCategory에서 만들어놓은 배열을 가져와서 그것을 여기서 수정하여
        // 다시 내보내는 형식으로 리스트 수정을 구현함.
        edittingList = (ArrayList<String>) intent.getSerializableExtra("mainCategory");

        final ArrayAdapter adapter = new ArrayAdapter
                (this, android.R.layout.simple_list_item_single_choice, edittingList);

        listView = (ListView) findViewById(R.id.main_category_list_edit);
        listView.setAdapter(adapter);

        editTextMainCategory = (EditText) findViewById(R.id.edit_text_main_category);

        //수정도중 나갔을 경우 그 값이 유지되어 다시 들어오더라도
        //계속해서 작업을 이어나갈 수 있게 구현함. 작성 중이던 문자열, 체크된 아이템이 기억됨.
        SharedPreferences sharedPreferences = getSharedPreferences("savedFile", MODE_PRIVATE);
        String text = sharedPreferences.getString("text", " ");
        checked = sharedPreferences.getInt("checked", 0);
        editTextMainCategory.setText(text);
        listView.setItemChecked(checked, true);

        //아이템 추가 리스너
        Button addButton = (Button) findViewById(R.id.button_add_main_category);
        addButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Text = editTextMainCategory.getText().toString();

                count = adapter.getCount();

                edittingList.add(Text);
                adapter.notifyDataSetChanged();
                editTextMainCategory.setText("");
            }
        });

        //아이템 수정 리스너
        Button modifyButton = (Button) findViewById(R.id.button_modify_main_category);
        modifyButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                String Text = editTextMainCategory.getText().toString();

                count = adapter.getCount();

                if (count > 0) {

                    checked = listView.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {

                        edittingList.set(checked, Text);
                        adapter.notifyDataSetChanged();
                        editTextMainCategory.setText("");

                    }
                }
            }
        });

        //아이템 삭제 리스너
        Button deleteButton = (Button) findViewById(R.id.button_delete_main_category);
        deleteButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                count = adapter.getCount();

                if (count > 0) {
                    checked = listView.getCheckedItemPosition();
                    if (checked > -1 && checked < count) {

                        edittingList.remove(checked);
                        listView.clearChoices();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        //완료 버튼을 누르면 수정된 값이 저장되고, MainCategory로 이동하며 수정된 리스트를 전달한다.
        Button finishButton = (Button) findViewById(R.id.button_sub_category_item_edit_finish);
        finishButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainCategoryActivity.class);
                intent.putExtra("editedMainCategory", edittingList);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();

        //액티비티가 종료되기 전에 저장, 셰어드 프리퍼런스를 savedFile이름, 기본 모드로 저장.
        SharedPreferences sharedPreferences = getSharedPreferences("savedFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //하단에 적고 있던 글자와 체크해놨던 위치를 액티비티가 종료되기 전에 저장함.
        String text = editTextMainCategory.getText().toString();
        checked = listView.getCheckedItemPosition();

        editor.putString("text", text);
        editor.putInt("checked", checked);

        editor.commit();
    }

}


