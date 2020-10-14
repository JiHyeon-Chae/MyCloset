package com.example.mycloset;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainCategoryActivity extends Activity {
    private Button buttonClothes, buttonCalender, buttonAnalysis, buttonViewAllItem, buttonMainCategoryEdit;

    //    private ExpandableListView listView;
    private ListView listView;
    ArrayList<String> mainList = new ArrayList<>();
    ArrayList<String> getEdittedList;

    private static final String SETTINGS_PLAYER_JSON = "setting_main_item_jason";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_category);
        Intent intent = getIntent();

        //intent로 전달 받은, 수정된 리스트를 변수화한다.
        //밑에서 수전된 리스트가 널이 아닐 시 메인카테고리리스트에 그 값을 그대로 넘기기 위해서.
        getEdittedList = (ArrayList<String>) intent.getSerializableExtra("editedMainCategory");

        //상단바 버튼
//        buttonViewAllItem = findViewById(R.id.button_view_all_item);
        buttonMainCategoryEdit = findViewById(R.id.button_main_category_item_edit);

        //하단바 버튼
        buttonClothes = findViewById(R.id.button_clothes);
        buttonCalender = findViewById(R.id.button_calendar);
        buttonAnalysis = findViewById(R.id.button_util);

        //리스트뷰
        listView = (ListView) findViewById(R.id.main_category_list);

        mainList = getStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON);

        //       mainlist를 수정하기 전(값을 MainCategoryEditActivity에서 넘겨받기 전==getEdittedList가 null일 떄)에,
        //       nullPointException이 떠서, 방지하고자 만들었음.
        if (getEdittedList != null) {
            mainList = getEdittedList;
        }

        ArrayAdapter<String> mainAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mainList);
        listView.setAdapter(mainAdapter);


        //서브카테고리로 이동할 떄, 클릭한 메인 카테고리에 해당되는 SubCategoryItem만 보일 수 았도록
        // 클릭한 아이템의 이름(문자열)을 SubCategory로 넘겨서 그것을 바탕으로 검사하게 함.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), SubCategoryActivity.class);
                intent.putExtra("clickedCategoryItem", mainList.get(position));
                startActivity(intent);
            }
        });

        //메인 카테고리 - 익스텐더블리스트
//        final ArrayList <MainCategoryGroup> DataList = new ArrayList<MainCategoryGroup>();
//        listView = (ExpandableListView)findViewById(R.id.main_category_list);
//        MainCategoryGroup mainCategoryGroup = new MainCategoryGroup("상의");
//        mainCategoryGroup.child.add("셔츠");
//        mainCategoryGroup.child.add("반팔티");
//        DataList.add(mainCategoryGroup);
//
//        mainCategoryGroup = new MainCategoryGroup("하의");
//        mainCategoryGroup.child.add("청바지");
//        mainCategoryGroup.child.add("면바지");
//        DataList.add(mainCategoryGroup);
//
//        mainCategoryGroup = new MainCategoryGroup("신발");
//        mainCategoryGroup.child.add("구두");
//        mainCategoryGroup.child.add("운동화");
//        DataList.add(mainCategoryGroup);
//
//        MainCategoryAdapter adapter = new MainCategoryAdapter(getApplicationContext(),
//                R.layout.main_category_group_row, R.layout.main_category_child_row, DataList) ;
//        listView.setAdapter(adapter);
//        listView.setGroupIndicator(null);
//
//        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
//                Intent intent = new Intent(getApplicationContext(), SubCategoryActivity.class);
//                intent.putExtra("clickedCategoryName",DataList.get(childPosition).groupName);
//                startActivity(intent);
//                return true;
//
//            }
//        });
//        모든 아이템 보기 기능 - 문자열"all"을 전달함.

        //메인카테고리는 다른 엑티비티로 리스트를 통째로 보내서 수정하고 다시 받아오게 함.
        buttonMainCategoryEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainCategoryEditActivity.class);
                intent.putExtra("mainCategory", mainList);
                startActivity(intent);
            }
        });

        //하단바 클릭 리스너
        buttonClothes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainCategoryActivity.class);
                startActivity(intent);
            }
        });
        buttonAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UtilActivity.class);
                startActivity(intent);
            }
        });
        buttonCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CalenderActivity.class);
                startActivity(intent);
            }
        });

        //상단바 클릭 리스너
//        buttonViewAllItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), SubCategoryActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    //값을 저장하고 불러오기 위해서 setStringArrayPref, getStringArrayPre를 구현함.
    private void setStringArrayPref(Context context, String key, ArrayList<String> values) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();

        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }

        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }

        editor.apply();
    }

    private ArrayList getStringArrayPref(Context context, String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList urls = new ArrayList();

        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);

                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    //종료되기 전에 메인 리스트 저장.
    @Override
    protected void onPause() {
        super.onPause();

        setStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON, mainList);
        Log.d(TAG, "Put json");
    }
}