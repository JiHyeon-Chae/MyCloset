package com.example.mycloset;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;

import static com.example.mycloset.SubCategoryActivity.BitmapToString;
import static com.example.mycloset.SubCategoryActivity.StringToBitmap;
import static com.example.mycloset.SubCategoryActivity.unFilteredData;

public class UtilCheckWeatherActivity extends Activity {
    private Button buttonClothes, buttonCalender, buttonUtil, buttonSelectCity;
    private ImageView imageViewWeatherInfo, imageViewCloth;
    private TextView tempertureTextView,textViewWeatherInfo,textViewCityName;

    //지역 좌표값
    //기본 값은 서울로 설정함.
    String nx , ny, cityName ;


    //weatherInfo에는 총 9개의 정보를 담았다. 이중에서 활용하는 것은 0~4, 5개 이다.
    //인덱스에 따라 다른 종류의 정보가 담기도록 구현했음.
    //index : 0 - 강수확률(%), 1 - 강수형태(0: 없음, 1: 비, 2: 비/눈, 3: 눈, 4: 소나기)
    // 2 - 습도(%), 3 - 하늘상태(1: 맑음, 3: 구름 많음, 4: 흐림), 4 - 최근 3시간 기온('C)
    ArrayList<String> weatherInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_util_weather_check);
        Intent intent = getIntent();



        //설정한 위치값을 기억하도록 구현함.
        SharedPreferences sharedPreferences = getSharedPreferences("saveLocation", MODE_PRIVATE);
        nx = sharedPreferences.getString("nx", " ");
        ny = sharedPreferences.getString("ny", " ");
        cityName = sharedPreferences.getString("cityName"," ");

//        초기 설정값 - 서울로 지정해둠.
        if(ny == null||nx == null||cityName == null){
            nx = "60";
            ny = "127";
            cityName = "서울";
        }
//        nx = "60";
//        ny = "127";

        weatherInfo = getStringArrayPref(getApplicationContext());


        //화면
        imageViewWeatherInfo = findViewById(R.id.weather_info_image);
        imageViewCloth = findViewById(R.id.weather_cloth_image);
        tempertureTextView = findViewById(R.id.temperature_text_view);
        textViewWeatherInfo = findViewById(R.id.weather_info_text_view);
        textViewCityName = findViewById(R.id.city_name_text_view);


        imageViewWeatherInfo.setImageResource(R.drawable.sub_category_icon1);
        imageViewCloth.setImageResource(R.drawable.calender_outfit_icon);



        buttonClothes = findViewById(R.id.button_clothes);
        buttonCalender = findViewById(R.id.button_calendar);
        buttonUtil = findViewById(R.id.button_util);
        buttonSelectCity = findViewById(R.id.button_select_city);

        buttonClothes.setOnClickListener(buttonClothesListener);
        buttonUtil.setOnClickListener(buttonUtilListner);
        buttonCalender.setOnClickListener(buttonCalenderListener);
        buttonSelectCity.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                MenuItem seoul = contextMenu.add(Menu.NONE, 1001, 1, "서울");
                MenuItem ulsan = contextMenu.add(Menu.NONE, 1002, 2, "울산");
                MenuItem incheon = contextMenu.add(Menu.NONE, 1003, 3, "인천");
                MenuItem busan = contextMenu.add(Menu.NONE, 1004, 4, "부산");
                MenuItem daegu = contextMenu.add(Menu.NONE, 1005, 5, "대구");
                MenuItem gwangju = contextMenu.add(Menu.NONE, 1006, 6, "광주");
                MenuItem daejean = contextMenu.add(Menu.NONE, 1007, 7, "대전");

                seoul.setOnMenuItemClickListener(onEditMenu);
                ulsan.setOnMenuItemClickListener(onEditMenu);
                incheon.setOnMenuItemClickListener(onEditMenu);
                busan.setOnMenuItemClickListener(onEditMenu);
                daegu.setOnMenuItemClickListener(onEditMenu);
                gwangju.setOnMenuItemClickListener(onEditMenu);
                daejean.setOnMenuItemClickListener(onEditMenu);
            }
        });

        //기능구현
        displayWeatherInfo();
    }

    //화면 출력
    private void displayWeatherInfo() {
//        조건 문으로 조건마다 화면 출력 변경

        //최근 기온
        tempertureTextView.setText(weatherInfo.get(4) + "°C");

        //갈수확률&습도 등 설명
        textViewWeatherInfo.setText("강수 확률 : " + weatherInfo.get(0) +"%\n"
        +"습도 : "+ weatherInfo.get(2)+"%");

        textViewCityName.setText(cityName);

        //강수확률
        if (Integer.valueOf(weatherInfo.get(0)) >= 0) {

            //하늘 상태
            //맑음
            if (Integer.valueOf(weatherInfo.get(3)) == 1) {
                imageViewWeatherInfo.setImageResource(R.drawable.weather_sunny);
            }

            //흐림
            else if (Integer.valueOf(weatherInfo.get(3)) == 3) {
                imageViewWeatherInfo.setImageResource(R.drawable.weather_cloudy);
            }

            //구름 많음
            else if (Integer.valueOf(weatherInfo.get(3)) == 4) {
                imageViewWeatherInfo.setImageResource(R.drawable.weather_more_cloudy);
            }

        } else if (Integer.valueOf(weatherInfo.get(0)) <= 0) {

            //강수 형태
            //안 옴
            if (Integer.valueOf(weatherInfo.get(1)) != 0) {

                //비옴
                if (Integer.valueOf(weatherInfo.get(1)) == 1 || Integer.valueOf(weatherInfo.get(1)) == 4 || Integer.valueOf(weatherInfo.get(1)) == 5) {
                    imageViewWeatherInfo.setImageResource(R.drawable.weather_rain);
                }

                //눈 옴
                else if(Integer.valueOf(weatherInfo.get(1))==2||Integer.valueOf(weatherInfo.get(1))==3||Integer.valueOf(weatherInfo.get(1))==6||Integer.valueOf(weatherInfo.get(1))==7){
                    imageViewWeatherInfo.setImageResource(R.drawable.weather_snow);
                }
            } else {

                //하늘 상태
                //맑음
                if (Integer.valueOf(weatherInfo.get(3)) == 1) {
                    imageViewWeatherInfo.setImageResource(R.drawable.weather_sunny);
                }

                //흐림
                else if (Integer.valueOf(weatherInfo.get(3)) == 3) {
                    imageViewWeatherInfo.setImageResource(R.drawable.weather_cloudy);
                }

                //구름 많음
                else if (Integer.valueOf(weatherInfo.get(3)) == 4) {
                    imageViewWeatherInfo.setImageResource(R.drawable.weather_more_cloudy);
                }
            }

        }

        //입을 옷 추천
        if(Integer.valueOf(weatherInfo.get(4))>=21){
            imageViewCloth.setImageResource(R.drawable.cloth_from_21_to_hot);
        }else if(Integer.valueOf(weatherInfo.get(4))<=20&&Integer.valueOf(weatherInfo.get(4))>=17){
            imageViewCloth.setImageResource(R.drawable.cloth_from_17_to_20);
        }else if(Integer.valueOf(weatherInfo.get(4))<=16&&Integer.valueOf(weatherInfo.get(4))>=9){
            imageViewCloth.setImageResource(R.drawable.cloth_from_9_to_16);
        }else if(Integer.valueOf(weatherInfo.get(4))<=15){
            imageViewCloth.setImageResource(R.drawable.cloth_cold);
        }

    }

    //API에서 정보 받아오기
    private ArrayList getStringArrayPref(Context context) {
        ArrayList<String> urls = new ArrayList();

        StrictMode.enableDefaults();
//        TextView status1 = (TextView)findViewById(R.id.result); //파싱된 결과확인!
//        boolean initem = false, inAddr = false, inChargeTp = false,
//        boolean inCpStat = false, inCpTp = false, inCsId = false, inCsNm = false, inLat=false;
//        boolean inLongi = false, inStatUpdateDatetime = false;

//        String addr = null, chargeTp = null,
//        String lat = null, longi = null, statUpdateDatetime = null;
        boolean inCategory = false, inFcstValue = false, initem = false;
        String category = null, fcstValue = null;

        try {
            URL url = new URL("http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst" +
                    "?serviceKey=LemoeUz5Bvy%2BwdZVZFgXA8urKfM5ZD%2BlJiOOKOuGgjsyuHdY4x7bcHMWTk6aScZMQzlu6%2FuJQ31cCkX1qGfxlQ%3D%3D" +
                    "&pageNo=1" +
                    "&numOfRows=9" +
                    "&base_date=20201014" +
                    "&base_time=1100" +
                    "&nx=" + nx +
                    "&ny=" + ny
            ); //검색 URL부분

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();
            System.out.println("파싱시작");

            int i = 0;
            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                        if (parser.getName().equals("category")) { //mapx 만나면 내용을 받을수 있게 하자
                            inCategory = true;
                        }
                        if (parser.getName().equals("fcstValue")) { //mapy 만나면 내용을 받을수 있게 하자
                            inFcstValue = true;
                        }
                        break;

                    case XmlPullParser.TEXT://parser가 내용에 접근했을때
                        if (inCategory) { //isMapx이 true일 때 태그의 내용을 저장.
                            category = parser.getText();
                            inCategory = false;
                        }
                        if (inFcstValue) { //isMapy이 true일 때 태그의 내용을 저장.
                            fcstValue = parser.getText();
                            inFcstValue = false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item")) {
//                            status1.setText(status1.getText() + category +" : " + fcstValue + "\n");
                            initem = false;
                        }
                        break;
                }
                if (fcstValue != null) {
                    if (urls.size() == 0) {
                        urls.add(fcstValue);
                        i++;
                    } else {
                        if (urls.get(i - 1) != fcstValue) {
                            urls.add(fcstValue);
                            i++;
                        }
                    }
                }
                parserEvent = parser.next();
            }
        } catch (Exception e) {
        }
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        String json = prefs.getString(key, null);
//
//
//        if (json != null) {
//            try {
//                JSONArray changedArray = new JSONArray(json);
//
//                //리스트의 크기만큼 반복하면서
//                // json에 닮긴 문자열 값들을 객체로 변환하고
//                // 아이템 단위로 저장함.
//                for (int i = 0; i < changedArray.length(); i++) {
//                    JSONObject jsonObject = changedArray.getJSONObject(i);
//
//                    SubCategoryItems subCategoryItems = new SubCategoryItems("", "", "", "", "", 0);
//                    subCategoryItems.setSubCategoryIcon(jsonObject.getString("subCategoryIcon"));
//                    subCategoryItems.setSubCategoryName(jsonObject.getString("subCategoryName"));
//                    subCategoryItems.setSubCategoryInfo(jsonObject.getString("subCategoryInfo"));
//                    subCategoryItems.setSubCategory(jsonObject.getString("subCategory"));
//                    subCategoryItems.setSubCategoryColor(jsonObject.getString("subCategoryColor"));
//                    subCategoryItems.setItemUsageCount(jsonObject.getInt("itemUsageCount"));
//
//                    urls.add(subCategoryItems);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        return urls;
    }

    View.OnClickListener buttonClothesListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(UtilCheckWeatherActivity.this, MainCategoryActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener buttonCalenderListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(UtilCheckWeatherActivity.this, CalenderActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener buttonUtilListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(UtilCheckWeatherActivity.this, UtilActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        //액티비티가 종료되기 전에 저장, 셰어드 프리퍼런스를 savedFile이름, 기본 모드로 저장.
        SharedPreferences sharedPreferences = getSharedPreferences("saveLocation", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //하단에 적고 있던 글자와 체크해놨던 위치를 액티비티가 종료되기 전에 저장함.

        editor.putString("nx", nx);
        editor.putString("ny", ny);
        editor.putString("cityName",cityName);

        editor.commit();
    }

    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {

                //수정하기.
                case 1001:
//
                    nx = "60";
                    ny = "127";
                    cityName = "서울";
                    break;

                case 1002:
                    nx = "102";
                    ny = "84";
                    cityName = "울산";

                    break;

                case 1003:
                    nx = "55";
                    ny = "126";
                    cityName = "인천";
                    break;

                case 1004:
                    nx = "98";
                    ny = "76";
                    cityName = "부산";
                    break;

                case 1005:
                    nx = "89";
                    ny = "90";
                    cityName = "대구";
                    break;

                case 1006:
                    nx = "58";
                    ny = "74";
                    cityName = "광주";
                    break;

                case 1007:
                    nx = "67";
                    ny = "100";
                    cityName = "대전";
                    break;
            }
            Toast.makeText(getApplicationContext(),"수정이 완료되었습니다.",Toast.LENGTH_SHORT).show();
            return true;
        }
    };
}
