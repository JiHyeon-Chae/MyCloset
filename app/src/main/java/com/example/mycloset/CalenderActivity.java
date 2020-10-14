package com.example.mycloset;

import android.Manifest;
import android.app.AlertDialog;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import static com.example.mycloset.SubCategoryActivity.unFilteredData;



public class CalenderActivity extends AppCompatActivity {
    private Button buttonClothes, buttonCalender, buttonAnalysis, buttonOutfitEdit, buttonOutFitDelete;

    private ImageView outfitImageView;

    private ListView outfitListView;

    private CalendarView calenderView;

    CalendarAdapter adapter;

    private static final String SETTINGS_PLAYER_JSON_CALENDAR = "setting_calendar_item_jason_1";

    //그날의 입은 옷을 SubCategoryItems형태로 보관하고 불러오기 위해서 사용
    static ArrayList<SubCategoryItems> CalendarOutfitClothes;

    //날짜마다의 코디를 사진+입은 날짜+입은 옷 의 정보를 담아서 저장함.
    ArrayList<CalendarItems> calendarViewList = new ArrayList<>();


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

    View viewOutfit;
    static ImageButton editOutfitImage;
    EditText findName;
    Button buttonOutfitEditFinish;
    RecyclerView dialogRecyclerView;
    int itemYear;
    int itemMonth;
    int itemDay;
    CheckBox checkBox;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        Intent intent = getIntent();

        buttonClothes = findViewById(R.id.button_clothes);
        buttonCalender = findViewById(R.id.button_calendar);
        buttonAnalysis = findViewById(R.id.button_util);

        buttonClothes.setOnClickListener(buttonClothesListener);
        buttonAnalysis.setOnClickListener(buttonAnalysisListener);
        buttonCalender.setOnClickListener(buttonCalenderListener);

        //상단바 버튼 구현
        buttonOutfitEdit = findViewById(R.id.button_calendar_outfit_edit);
        buttonOutFitDelete = findViewById(R.id.button_calender_outfit_delete);

        outfitImageView = findViewById(R.id.calender_outfit_image_view);
        outfitListView = findViewById(R.id.calender_outfit_item_list);

        calendarViewList = getStringArrayPref(getApplicationContext(), SETTINGS_PLAYER_JSON_CALENDAR);


        calenderView = (CalendarView) findViewById(R.id.calendarView);
        calenderView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                itemDay = dayOfMonth;
                itemMonth = month + 1;
                itemYear = year;

                //클릭 리스너 이용해서
                for (int i = 0; i < calendarViewList.size(); i++) {
                    if (calendarViewList.get(i).getCalendarOutfitDateYear() == itemYear
                            && calendarViewList.get(i).getCalendarOutfitDateMonth() == itemMonth
                            && calendarViewList.get(i).getCalendarOutfitDateDay() == itemDay) {
                        CalendarItems clickedDateItem = new CalendarItems(itemYear, itemMonth, itemDay, null, null);
                        clickedDateItem = calendarViewList.get(i);
                        outfitImageView.setImageBitmap(StringToBitmap(clickedDateItem.getCalendarOutfit()));
                        adapter = new CalendarAdapter(clickedDateItem.getCalendarClothes());
                        outfitListView.setAdapter(adapter);

                    } else {
                        outfitImageView.setImageResource(R.drawable.calender_outfit_icon);
                        ArrayList<SubCategoryItems> nullList = new ArrayList<>();
                        adapter = new CalendarAdapter(nullList);
                        outfitListView.setAdapter(adapter);
                    }
                }
            }
        });

        //추가 버튼
        buttonOutfitEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CalenderActivity.this);

                viewOutfit = LayoutInflater.from(getApplicationContext()).inflate(R.layout.calender_outfit_edit, null, false);
                builder.setView(viewOutfit);

                //체크박스에서 클릭된 아이템들을 저장해서 그날 어떤 옷을 입었는지 표시하기 위함.
                CalendarOutfitClothes = new ArrayList<>();

                editOutfitImage = (ImageButton) viewOutfit.findViewById(R.id.img_oufitEdit);
                findName = (EditText) viewOutfit.findViewById(R.id.calendar_outfit_item_search);
                dialogRecyclerView = (RecyclerView) viewOutfit.findViewById(R.id.calender_outfit_item_list_edit);


                final CalendarEditAdapter calendarEditAdapter = new CalendarEditAdapter(CalenderActivity.this, unFilteredData);
                dialogRecyclerView.setAdapter(calendarEditAdapter);
                dialogRecyclerView.setLayoutManager(new LinearLayoutManager(CalenderActivity.this, LinearLayoutManager.VERTICAL, false));

                buttonOutfitEditFinish = (Button) viewOutfit.findViewById(R.id.button_calender_item_edit_finish);
                editOutfitImage.setImageResource(R.drawable.calender_outfit_icon);


                //아이템 검색 기능
                findName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String text = findName.getText().toString().toLowerCase(Locale.getDefault());
                        calendarEditAdapter.getFilter().filter(text);
                    }
                });


                final AlertDialog dialog = builder.create();


                //이미지 수정 클릭 버튼.
                editOutfitImage.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
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
//
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
                            return true;
                        }
                    };
                });


                //수정 완료 버튼.
                buttonOutfitEditFinish.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

//                        //에딧 텍스트에 적혀진 값을 가져와서 저장함.
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;
                        Bitmap bitmap = ((BitmapDrawable) editOutfitImage.getDrawable()).getBitmap();
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);

                        CalendarItems calendarItems = new CalendarItems(itemYear, itemMonth, itemDay, BitmapToString(resizedBitmap), CalendarOutfitClothes);

                        calendarViewList.add(calendarItems);

                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

    }


    View.OnClickListener buttonClothesListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(CalenderActivity.this, MainCategoryActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener buttonCalenderListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(CalenderActivity.this, CalenderActivity.class);
            startActivity(intent);
        }
    };
    View.OnClickListener buttonAnalysisListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(CalenderActivity.this, UtilActivity.class);
            startActivity(intent);
        }
    };

    public void setStringArrayPref() {
        CalenderActivity.SetStringArrayPref setStringArrayPref = new CalenderActivity.SetStringArrayPref();
        setStringArrayPref.execute();
    }

    private class SetStringArrayPref extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(CalenderActivity.this);
            progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
            progressDialog.setMessage("저장 중입니다.");

            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //값을 영구저장하기 위한 셰어드 프리퍼런스.
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CalenderActivity.this);
                SharedPreferences.Editor editor = prefs.edit();


                //제이슨으로 변환된 리스트값들을 저장하기위한 제이슨리스트.
                JSONArray changedArray = new JSONArray();

                //리스트의 크기만큼 반복하면서
                // 객체를 json에 닮긴 문자열 값들로 변환하고
                // 아이템 단위로 셰어드프리퍼런스에 저장함.
                for (int j = 0; j < calendarViewList.size(); j++) {
                    JSONObject jsonEdittingObject = new JSONObject();

                    //tear
                    jsonEdittingObject.put("calendarOutfitYear", calendarViewList.get(j).getCalendarOutfitDateYear());
                    //month
                    jsonEdittingObject.put("calendarOutfitMonth", calendarViewList.get(j).getCalendarOutfitDateMonth());
                    //day
                    jsonEdittingObject.put("calendarOutfitDay", calendarViewList.get(j).getCalendarOutfitDateDay());
                    //outfit
                    jsonEdittingObject.put("calendarOutfit", calendarViewList.get(j).getCalendarOutfit());
                    //subcategory icon
                    ArrayList <SubCategoryItems> puttiunClothes = calendarViewList.get(j).getCalendarClothes();

                    jsonEdittingObject.put("subCategorySize",puttiunClothes.size());
                    for (int i = 0; i < puttiunClothes.size(); i++) {
//            unChangedArray.put("subCategoryIcon",unFilteredData.get(i).)
                        String count = String.valueOf(i);
                        jsonEdittingObject.put("subCategoryIcon"+count, puttiunClothes.get(i).getSubCategoryIcon());
                        jsonEdittingObject.put("subCategoryName"+count, puttiunClothes.get(i).getSubCategoryName());
                        jsonEdittingObject.put("subCategoryInfo"+count, puttiunClothes.get(i).getSubCategoryInfo());
                        jsonEdittingObject.put("subCategory"+count, puttiunClothes.get(i).getCategory());
                        jsonEdittingObject.put("subCategoryColor"+count, puttiunClothes.get(i).getSubCategoryColor());
                        jsonEdittingObject.put("itemUsageCount"+count, puttiunClothes.get(i).getItemUsageCount());
                    }
                    changedArray.put(jsonEdittingObject);
                }
//            jsonObject.put("Clothes",changedArray);

                if (!calendarViewList.isEmpty()) {
                    editor.putString(SETTINGS_PLAYER_JSON_CALENDAR, changedArray.toString());
                } else {
                    editor.putString(SETTINGS_PLAYER_JSON_CALENDAR, null);
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

                    CalendarItems calendarItems = new CalendarItems(2020,9,12,"",null);
                    calendarItems.setCalendarOutfitDateYear(jsonObject.getInt("calendarOutfitYear"));
                    calendarItems.setCalendarOutfitDateMonth(jsonObject.getInt("calendarOutfitMonth"));
                    calendarItems.setCalendarOutfitDateDay(jsonObject.getInt("calendarOutfitDay"));
                    calendarItems.setCalendarOutfit(jsonObject.getString("calendarOutfit"));

                    ArrayList<SubCategoryItems> edittongList = new ArrayList<>();

                    for (int j = 0; j < jsonObject.getInt("subCategorySize");j++) {
                        String count = String.valueOf(j);
                        SubCategoryItems subCategoryItems = new SubCategoryItems("", "", "", "", "", 0);
                        subCategoryItems.setSubCategoryIcon(jsonObject.getString("subCategoryIcon"+count));
                        subCategoryItems.setSubCategoryName(jsonObject.getString("subCategoryName"+count));
                        subCategoryItems.setSubCategoryInfo(jsonObject.getString("subCategoryInfo"+count));
                        subCategoryItems.setSubCategory(jsonObject.getString("subCategory"+count));
                        subCategoryItems.setSubCategoryColor(jsonObject.getString("subCategoryColor"+count));
                        subCategoryItems.setItemUsageCount(jsonObject.getInt("itemUsageCount"+count));

                        edittongList.add(subCategoryItems);
                    }

                    calendarItems.setCalendarClothes(edittongList);

                    urls.add(calendarItems);
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
        ImageButton imageButton = editOutfitImage;
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
            ImageButton imageButton = editOutfitImage;

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

}
