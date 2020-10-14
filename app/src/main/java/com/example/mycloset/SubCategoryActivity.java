package com.example.mycloset;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_PICTURES;

public class SubCategoryActivity extends AppCompatActivity {

    private Button buttonClothes, buttonCalender, buttonAnalysis;
    String clickedCategory;

    RecyclerView recyclerView;
    SubCategoryAdapter adapter;

    //옷 정보가, 필터링 되지 않고 모두 저장되어있는 공간, 옷을 수정하려면 여기 내용을 수정 해야 함.
    static ArrayList<SubCategoryItems> unFilteredData = new ArrayList<>();
    TextView titleTextView;

    //옷에 대한 정보가 영구적으로 저장되는 공간
    private static final String SETTINGS_PLAYER_JSON = "setting_sub_category_item_jason";

    //아이템 생성시 사진 편집 기능(카메카 촬영, 사진첩 불러오기) 관련 변수
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private static final int PICK_FROM_ALBUM = 1;
    private String imageFilePath;
    private Uri photoUri;
    private File tempFile;

    //어뎁터에서 정의된 '아이템 수정 다이얼로그'에서 액티비티에 있는 onActivityResult로
    //이미지 변경 요청을 보내는 상황이 있다.
    //이때, 이미지 변경 요청이 액티비티에서 정의된 '아이템 생성 다이얼로그' 보낸 것이 아니
    //아니라 어댑터라의 '아이템 수정 다이얼로그'에서 보낸 것임을 알리기 위함.
    //이것이 없을 경우, 갤러리에서 선택한 사진을 이미지버튼에 적용(SET)시킬 때
    //어떤 이미지뷰에 적용 시킬지(1.액티비티 다이얼로그의 이미지 뷰, 2. 어댑터 다이얼로그의 이미지뷰) 정해지지 않아
    //null예외가 남.
    private boolean isStartedInNotActivityButAdapter = true;

    //옷 편집 다이얼로그에서 생성되는 뷰를 위에서 정의
    //이유 - 다이얼로그 바깥에서는 다이얼로그 안의 뷰에 접근할 수 없어서 미리 객체를 정의해두고
    //수정할 수 있게 만들었음.
    View view1;

    ImageButton editImage;
    EditText editName;
    EditText editInfo;
    EditText editCategory;
    EditText editColor;
    Button buttonEditFinish;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);
        Intent intent = getIntent();

        //인텐트를 통해 메인 엑티비티에서 받은 카테고리의 이름을 스트링화 함.
        //이것을 통해서, 전체 리스트 아이템 중 카테고리 이름에 해당하는 아이템만을 화면에 표시함.
        clickedCategory = intent.getStringExtra("clickedCategoryItem");

        //하단 바 버튼
        buttonClothes = findViewById(R.id.button_clothes);
        buttonCalender = findViewById(R.id.button_calendar);
        buttonAnalysis = findViewById(R.id.button_util);
        //히딘비 버튼 리스너
        buttonClothes.setOnClickListener(buttonClothesListener);
        buttonAnalysis.setOnClickListener(buttonAnalysisListener);
        buttonCalender.setOnClickListener(buttonCalenderListener);

        unFilteredData = getStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON);

        //이 리스트뷰는 sub_category_recycler_view에서 출력됨.
        recyclerView = (RecyclerView) findViewById(R.id.sub_category_recycler_view);

        //리스트뷰 아이템 추가.

//        사진을 불러오는 방식ㅇㄹ 더 공부하기. 다른 방식으로 구현할 수 있다.***
//        SubCategoryItems subCategoryItem2 =
//                new SubCategoryItems
//                        (R.drawable.sub_category_icon1, "긴팔 a", "사이즈 작음.", "상의","파란색");
//        SubCategoryItems subCategoryItem3 =
//                new SubCategoryItems
//                        (R.drawable.sub_category_icon1, "리바이스 청남방", ".", "상의","파란색");
//        SubCategoryItems subCategoryItem4 =
//                new SubCategoryItems
//                        (R.drawable.sub_category_icon1, "리바리스 501", "엉덩이 수선함.", "하의","빨강색");
//        SubCategoryItems subCategoryItem5 =
//                new SubCategoryItems
//                        (R.drawable.sub_category_icon1, "브룩스 브라더스 카키 팬츠", "벨트고리 망가짐.", "하의","연보라색");
//        unFilteredData.add(subCategoryItem2);
//        unFilteredData.add(subCategoryItem3);
//        unFilteredData.add(subCategoryItem4);
//        unFilteredData.add(subCategoryItem5);

        //상단 타이틀 변경
        titleTextView = (TextView) findViewById(R.id.titleMainCategory);

        titleTextView.setText(clickedCategory);

        //목적 : 메인카테고리에 서브카테고리로 넘어갈 떄, 카테고리에 담긴 정보만을 보여주고자 함.
        //모두 보기 버튼을 클릭하면, 저장되어진 모든 데이터가 표시되도록 구형함.

        //메인에서 클릭한 아이템 이름과 서브카테고리 아이템의 'subCategory'의 문자열값을
        //for문에서 검사한 뒤 일치하는 아이템만 data(실질적으로 화면에 보여지는 리스트)에 추가함.
        //만약 일치하는 아이템이 하나도 없을 경우

//        if(clickedCategory!=null) {
//            for (int i = 0; i < unFilteredData.size(); i++) {
//                if (unFilteredData.get(i).getCategory().contains(clickedCategory)) {
//                    data.add(unFilteredData.get(i));
//                }
//                if (data.size() == 0) {
//                    data.isEmpty();
//                }
//            }
//        }else {
//            data = unFilteredData;
//        }

        //뷰와 클래스를 연결해주기 위해서 어댑터를 사용한다. 보내는 위치(클래스)와 받는 위치(리스트뷰) 보내는 데이터의 속성을 지정한다.

//        jsonParsing(getJson());

        final SubCategoryAdapter adapter = new SubCategoryAdapter(this, unFilteredData);
        recyclerView.setAdapter(adapter);

        //어뎁터클래스의 필터 메소드로 (인텐트에서)전달받은 메인카테고리 이름을 넘김.

        adapter.getFilter().filter(clickedCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //아이템 추가 기능 구현.
        //다이얼로그를 띄어서 입력된 값을 받아 아이템이 생성되게 함.
        Button buttonItemAdd = (Button) findViewById(R.id.button_sub_category_item_add);
        buttonItemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(SubCategoryActivity.this);
                view1 = LayoutInflater.from(SubCategoryActivity.this).inflate(R.layout.activity_sub_category_item_edit, null, false);
                builder.setView(view1);

                editImage = (ImageButton) view1.findViewById(R.id.img_iconEdit);
                editName = (EditText) view1.findViewById(R.id.tv_nameEdit);
                editInfo = (EditText) view1.findViewById(R.id.tv_infoEdit);
                editCategory = (EditText) view1.findViewById(R.id.tv_categoryEdit);
                editColor = (EditText) view1.findViewById(R.id.tv_colorEdit);
                buttonEditFinish = (Button) view1.findViewById(R.id.button_sub_category_item_edit_finish);

                final AlertDialog dialog = builder.create();

                editCategory.setText(clickedCategory);

                editImage.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                        MenuItem Edit = contextMenu.add(Menu.NONE, 1001, 1, "사진첩에서 가져오기");
                        MenuItem Delete = contextMenu.add(Menu.NONE, 1002, 2, "카메라에서 촬영하기");

                        Edit.setOnMenuItemClickListener(onEditMenu);
                        Delete.setOnMenuItemClickListener(onEditMenu);
                    }

                    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            // 권한 체크
                            TedPermission.with(getApplicationContext())
                                    .setPermissionListener(permissionListener)
                                    .setRationaleMessage("카메라 권한이 필요합니다.")
                                    .setDeniedMessage("거부하셨습니다.")
                                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                                    .check();

                            //사진첩에서 가져오기
                            if (menuItem.getItemId() == 1001) {
                                isStartedInNotActivityButAdapter = false;
                                goToAlbum();
                            }
                            //카메라에서 촬영하기
                            else if (menuItem.getItemId() == 1002) {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException e) {

                                    }

                                    if (photoFile != null) {
//                                        isStartedInNotActivityButAdapter =false;
                                        photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                                    }
                                }
                            }
                            //수정된 데이터를 서브엑티비티로 넘긴다.
//                SubCategoryActivity.unFilteredData = filteredList;
                            return true;
                        }
                    };
                });


                //생성 완료 버튼
                buttonEditFinish.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;
                        Bitmap bitmap = ((BitmapDrawable) editImage.getDrawable()).getBitmap();
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
                        String name = editName.getText().toString();
                        String info = editInfo.getText().toString();
                        String category = editCategory.getText().toString();
                        String color = editColor.getText().toString();

                        SubCategoryItems subCategoryItems = new SubCategoryItems(BitmapToString(resizedBitmap), name, info, category, color, 0);

                        unFilteredData.add(subCategoryItems);
                        adapter.getFilter().filter(clickedCategory);
//                        adapter.notifyItemInserted(adapter.getItemCount()+1);
//                        adapter.notifyDataSetChanged();
                        adapter.notifyItemInserted(adapter.getItemCount());

                        //스레드로 저장.
//                        setStringArrayPref();
//
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        //리스트 제이슨화
//        jsonReParsing();
    }

    //하단바 클릭 리스너
    View.OnClickListener buttonClothesListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SubCategoryActivity.this, MainCategoryActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener buttonCalenderListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SubCategoryActivity.this, CalenderActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener buttonAnalysisListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SubCategoryActivity.this, UtilActivity.class);
            startActivity(intent);
        }
    };

//    private String getJson(){
//        String json = "";
//
//        try{
//            InputStream inputStream = getAssets().open("Clothes.json");
//            int filSize = inputStream.available();
//
//            byte[]buffer = new byte[filSize];
//            inputStream.read(buffer);
//            inputStream.close();
//
//            json = new String(buffer,"UTF-8");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return json;
//    }
//    private void jsonParsing(String json){
//        try{
//            JSONObject jsonObject = new JSONObject(json);
//            JSONArray clothArray = jsonObject.getJSONArray("Clothes");
//
//            for (int i =0; i<clothArray.length();i++){
//                JSONObject clotheObject = clothArray.getJSONObject(i);
//
//                SubCategoryItems subCategoryItems = new SubCategoryItems(R.drawable.sub_category_icon1,"","","","");
//                subCategoryItems.setSubCategoryName(clotheObject.getString("subCategoryName"));
//                subCategoryItems.setSubCategoryInfo(clotheObject.getString("subCategoryInfo"));
//                subCategoryItems.setSubCategory(clotheObject.getString("subCategory"));
//                subCategoryItems.setSubCategoryColor(clotheObject.getString("subCategoryColor"));
//
//                unFilteredData.add(subCategoryItems);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//    private void jsonReParsing(){
//        try {
//            JSONObject jsonObject = new JSONObject();
//            JSONArray unChangedArray = jsonObject.getJSONArray("Clothes");
////            JSONObject jsonObject = new JSONObject();
////            JSONArray changedArray = jsonObject
//
//            for(int i =0;i<unFilteredData.size();i++){
//                JSONObject jsonEdittingObject = new JSONObject();
//
////                unChangedArray.put("subCategoryIcon",unFilteredData.get(i).)
//                jsonEdittingObject.put("subCategoryName", unFilteredData.get(i).getSubCategoryName());
//                jsonEdittingObject.put("subCategoryInfo",unFilteredData.get(i).getSubCategoryInfo());
//                jsonEdittingObject.put("subCategory",unFilteredData.get(i).getCategory());
//                jsonEdittingObject.put("subCategoryColor",unFilteredData.get(i).getSubCategoryColor());
//                unChangedArray.put(jsonEdittingObject);
//            }
//            jsonObject.put("Clothes",unChangedArray);
//
////            File file = new File()
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    //아이템을 저장하고 불러오기 위한 메소드들.
    //리스트를 json으로 문자열화 시켜서 셰어드 프리퍼런스에 저장함.
    //불러올때는 문자열화된 json을 다시 객체화, 리스트화 함.

    public void setStringArrayPref() {
        SetStringArrayPref setStringArrayPref = new SetStringArrayPref();
        setStringArrayPref.execute();
    }

    private class SetStringArrayPref extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SubCategoryActivity.this);
            progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
            progressDialog.setMessage("저장 중입니다.");

            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //값을 영구저장하기 위한 셰어드 프리퍼런스.
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SubCategoryActivity.this);
                SharedPreferences.Editor editor = prefs.edit();


//            JSONObject jsonObject = new JSONObject();

                //제이슨으로 변환된 리스트값들을 저장하기위한 제이슨리스트.
                JSONArray changedArray = new JSONArray();

                //리스트의 크기만큼 반복하면서
                // 객체를 json에 닮긴 문자열 값들로 변환하고
                // 아이템 단위로 셰어드프리퍼런스에 저장함.
                for (int i = 0; i < unFilteredData.size(); i++) {
                    JSONObject jsonEdittingObject = new JSONObject();
//            unChangedArray.put("subCategoryIcon",unFilteredData.get(i).)
                    jsonEdittingObject.put("subCategoryIcon", unFilteredData.get(i).getSubCategoryIcon());
                    jsonEdittingObject.put("subCategoryName", unFilteredData.get(i).getSubCategoryName());
                    jsonEdittingObject.put("subCategoryInfo", unFilteredData.get(i).getSubCategoryInfo());
                    jsonEdittingObject.put("subCategory", unFilteredData.get(i).getCategory());
                    jsonEdittingObject.put("subCategoryColor", unFilteredData.get(i).getSubCategoryColor());
                    jsonEdittingObject.put("itemUsageCount", unFilteredData.get(i).getItemUsageCount());
                    changedArray.put(jsonEdittingObject);

                }
//            jsonObject.put("Clothes",changedArray);

                if (!unFilteredData.isEmpty()) {
                    editor.putString(SETTINGS_PLAYER_JSON, changedArray.toString());
                } else {
                    editor.putString(SETTINGS_PLAYER_JSON, null);
                }
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
        }
    }

//    private void setStringArrayPref (Context context, String key, ArrayList<SubCategoryItems> values) {
//        try {
//            //값을 영구저장하기 위한 셰어드 프리퍼런스.
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//            SharedPreferences.Editor editor = prefs.edit();
//
//
////            JSONObject jsonObject = new JSONObject();
//
//            //제이슨으로 변환된 리스트값들을 저장하기위한 제이슨리스트.
//            JSONArray changedArray = new JSONArray();
//
//            //리스트의 크기만큼 반복하면서
//            // 객체를 json에 닮긴 문자열 값들로 변환하고
//            // 아이템 단위로 셰어드프리퍼런스에 저장함.
//            for (int i = 0; i < values.size(); i++) {
//                JSONObject jsonEdittingObject = new JSONObject();
////            unChangedArray.put("subCategoryIcon",unFilteredData.get(i).)
//                jsonEdittingObject.put("subCategoryIcon", unFilteredData.get(i).getSubCategoryIcon());
//                jsonEdittingObject.put("subCategoryName", unFilteredData.get(i).getSubCategoryName());
//                jsonEdittingObject.put("subCategoryInfo", unFilteredData.get(i).getSubCategoryInfo());
//                jsonEdittingObject.put("subCategory", unFilteredData.get(i).getCategory());
//                jsonEdittingObject.put("subCategoryColor", unFilteredData.get(i).getSubCategoryColor());
//                jsonEdittingObject.put("itemUsageCount",unFilteredData.get(i).getItemUsageCount());
//                changedArray.put(jsonEdittingObject);
//            }
////            jsonObject.put("Clothes",changedArray);
//
//            if (!values.isEmpty()) {
//                editor.putString(key, changedArray.toString());
//            } else {
//                editor.putString(key, null);
//            }
//            editor.apply();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    //json에 저장된 리스트를 불러오기 위해서 사용함.
    //json에 저장된 문자열을 객체화 시켜서 셰어드 urls리스트에 저장함.
    //urls는 리턴을 통해서 서브아이템 리스트에게 값을 넘김.
    private ArrayList getStringArrayPref(Context context, String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList urls = new ArrayList();


        if (json != null) {
            try {
                JSONArray changedArray = new JSONArray(json);

                //리스트의 크기만큼 반복하면서
                // json에 닮긴 문자열 값들을 객체로 변환하고
                // 아이템 단위로 저장함.
                for (int i = 0; i < changedArray.length(); i++) {
                    JSONObject jsonObject = changedArray.getJSONObject(i);

                    SubCategoryItems subCategoryItems = new SubCategoryItems("", "", "", "", "", 0);
                    subCategoryItems.setSubCategoryIcon(jsonObject.getString("subCategoryIcon"));
                    subCategoryItems.setSubCategoryName(jsonObject.getString("subCategoryName"));
                    subCategoryItems.setSubCategoryInfo(jsonObject.getString("subCategoryInfo"));
                    subCategoryItems.setSubCategory(jsonObject.getString("subCategory"));
                    subCategoryItems.setSubCategoryColor(jsonObject.getString("subCategoryColor"));
                    subCategoryItems.setItemUsageCount(jsonObject.getInt("itemUsageCount"));

                    urls.add(subCategoryItems);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStringArrayPref();
    }

    //프로필 생성에서 사진첩 불러오기, 카메라 촬영 기능 메서드
    //앨범으로 받기.
    //앨범으로 가는 메소드
    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    //갤러리에서 받아온 이미지 넣기
    private void setImage() {

//        ImageButton imageButton = findViewById(R.id.img_iconEdit);
        ImageButton imageButton = editImage;
        ImageButton mImageButton = SubCategoryAdapter.editImage;
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);


        if (isStartedInNotActivityButAdapter) {
            //어뎁터에서 정의된 다이얼로그의 이미지버튼일 때
            mImageButton.setImageBitmap(originalBm);
        } else {
            //액티비티에서 정의된 다이얼로그의이미지버튼일 때
            imageButton.setImageBitmap(originalBm);
            isStartedInNotActivityButAdapter = true;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegress(exifOrientation);
            } else {
                exifDegree = 0;
            }

            String result = "";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.getDefault());
            Date curDate = new Date(System.currentTimeMillis());
            String filename = formatter.format(curDate);

            String strFolderName = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + File.separator + "HONGDROID" + File.separator;
            File file = new File(strFolderName);
            if (!file.exists())
                file.mkdirs();

            File f = new File(strFolderName + "/" + filename + ".png");
            result = f.getPath();

            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                result = "Save Error fOut";
            }

            // 비트맵 사진 폴더 경로에 저장
            rotate(bitmap, exifDegree).compress(Bitmap.CompressFormat.PNG, 70, fOut);

            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
                // 방금 저장된 사진을 갤러리 폴더 반영 및 최신화
//                mMediaScanner.mediaScanning(strFolderName + "/" + filename + ".png");
            } catch (IOException e) {
                e.printStackTrace();
                result = "File close Error";
            }
            ImageButton imageButton = editImage;

            // 이미지 뷰에 비트맵을 set하여 이미지 표현
            imageButton.setImageBitmap(rotate(bitmap, exifDegree));


        } else {
            Uri photoUri = data.getData();

            Cursor cursor = null;

            try {

                /*
                 *  Uri 스키마를
                 *  content:/// 에서 file:/// 로  변경한다.
                 */
                String[] proj = {MediaStore.Images.Media.DATA};

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            setImage();
        }
    }

    private int exifOrientationToDegress(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거부됨", Toast.LENGTH_SHORT).show();
        }
    };

    /*
     * String형을 BitMap으로 변환시켜주는 함수
     * */
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    /*
     * Bitmap을 String형으로 변환
     * */
    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }

    //다이얼로그를 안전하게 종료시키기 위한 방법.
    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }

}
