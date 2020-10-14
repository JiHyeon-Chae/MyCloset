package com.example.mycloset;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_PICTURES;
import static com.example.mycloset.CalenderActivity.CalendarOutfitClothes;
import static com.example.mycloset.SubCategoryActivity.BitmapToString;
import static com.example.mycloset.SubCategoryActivity.StringToBitmap;
import static com.example.mycloset.SubCategoryActivity.unFilteredData;

public class CalendarEditAdapter extends RecyclerView.Adapter<CalendarEditAdapter.CalendarEditViewHolder>
        implements Filterable {
    //필터링된 결과 데이터를 저장하기 위한 공간.
    private ArrayList<SubCategoryItems> filteredList;

    //모든 데이터가 저장된 곳
    private ArrayList<SubCategoryItems> unFilteredList;
    //    Filter listFilter;
    String mClickedCategory;

    SubCategoryItems clickedItem;

    //화면에서 클릭한 아이템과 실제로 배열에 담긴 아이템이 일치하지 않으므로
    //둘을 일치시켜서 수정, 삭제를 해야 한다. 그러려면 삭제되는, 추가되는 아이템의 원래 포지션을 찾아서
    //각각의 위치를 지정해줘야 함. 그 떄 각자 위치를 저장하기 위한 변수들을 선언 함.
    int editPosition;
    int deletePosition;

    private Context mContext;

    //아이템 생성시 사진 편집 기능(카메카 촬영, 사진첩 불러오기) 관련 변수
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private static final int PICK_FROM_ALBUM = 1;
    private String imageFilePath;
    private Uri photoUri;
    private File tempFile;

    //옷 편집 다이얼로그에서 생성되는 뷰를 위에서 정의
    //이유 - 다이얼로그 바깥에서는 다이얼로그 안의 뷰에 접근할 수 없어서 미리 객체를 정의해두고
    //수정할 수 있게 만들었음.
    View view1;
    static ImageButton editImage;
    EditText editName;
    EditText editInfo;
    EditText editCategory;
    EditText editColor;
    Button buttonEditFinish;


    //어뎁터 생성자
    public CalendarEditAdapter(Context context, ArrayList<SubCategoryItems> data) {
        this.mContext = context;
        this.filteredList = data;
        this.unFilteredList = data;

    }

    public class CalendarEditViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView info;
        public CheckBox checkBox;

        public CalendarEditViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.sub_category_icon);
            name = (TextView) itemView.findViewById(R.id.sub_category_name);
            info = (TextView) itemView.findViewById(R.id.sub_category_info);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_box);

        }

        ;
    }

    @Override
    public CalendarEditViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = View.inflate(mContext, R.layout.sub_category_item_check_box, null);

        CalendarEditViewHolder viewHolder = new CalendarEditViewHolder(view);

//        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CalendarEditViewHolder viewHolder, final int position) {

//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(mContext, SubCategoryItemClickedActivity.class);
//
//                //아이템의 그림, 이름, 정보를 다른 레이아웃에서 확대된 모습으로 표현함.
//                intent.putExtra("subCategoryIcon", filteredList.get(position).getSubCategoryIcon());
//                intent.putExtra("subCategoryName", filteredList.get(position).getSubCategoryName());
//                intent.putExtra("subCategoryInfo", filteredList.get(position).getSubCategoryInfo());
//                intent.putExtra("subCategory", filteredList.get(position).getCategory());
//                intent.putExtra("subCategoryColor", filteredList.get(position).getSubCategoryColor());
//                mContext.startActivity(intent);
//            }
//        });
        SubCategoryItems items = filteredList.get(position);

        viewHolder.icon.setImageBitmap(StringToBitmap(items.getSubCategoryIcon()));
        viewHolder.name.setText(items.getSubCategoryName());
        viewHolder.info.setText(items.getSubCategoryInfo());
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubCategoryItems clickedItem = filteredList.get(position);

//                for (int i = 0; i < CalendarOutfitClothes.size(); i++) {
                if (CalendarOutfitClothes.contains(clickedItem)) {
                    CalendarOutfitClothes.remove(clickedItem);
                } else {
                    CalendarOutfitClothes.add(clickedItem);
                }
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
//        if(filteredList==null){
//            return 0;
//        }else {
        return filteredList.size();
//        }
    }

    //모든 아이템 데이터 중에서 워하는 것만 필터링:
    //모든 아이템은 일단 unFilteredData에 추가 된다.
    //다만 카테고리 선택에 따라 표시되는 아이템이 달라져야 하므로
    //unFilteredData 중에서 아래의 구별검사를 통과한 아이템을 따로 filteredList에 담아서
    //화면에 표기하도록 구현함.
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                //오류있음.
                if (charString == null) {
                    ArrayList<SubCategoryItems> filteringList = new ArrayList<>();
                    for (int i = 0; i < unFilteredList.size(); i++) {
                        filteringList.add(unFilteredList.get(i));
                    }
                    filteredList = filteringList;
                } else {
                    ArrayList<SubCategoryItems> filteringList = new ArrayList<>();
                    for (int i = 0; i < unFilteredList.size(); i++) {
                        if (unFilteredList.get(i).getSubCategoryName().contains(charString)) {
                            filteringList.add(unFilteredList.get(i));
                        } else {
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, final FilterResults filterResults) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (filteredList != null) {
                            filteredList = (ArrayList<SubCategoryItems>) filterResults.values;
                        }
                        notifyDataSetChanged();
                    }
                }, 100);
            }
        };
    }
}