package com.example.mycloset;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import static com.example.mycloset.SubCategoryActivity.BitmapToString;
import static com.example.mycloset.SubCategoryActivity.StringToBitmap;
import static com.example.mycloset.SubCategoryActivity.unFilteredData;

//public class SubCategoryAdapter extends BaseAdapter {
//    private LayoutInflater inflater;
//    private ArrayList<SubCategoryItems>data;
//    private  int layout;
//
//    public SubCategoryAdapter(Context context, int layout, ArrayList<SubCategoryItems>data){
//        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.data = data;
//        this.layout = layout;
//    }
//
//
//    @Override
//    public int getCount() {
//        return data.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return data.get(position).getSubCategoryName();
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//       if(convertView==null){
//           convertView = inflater.inflate(layout,parent,false);
//       }
//       SubCategoryItems subCategoryItemsActivity = data.get(position);
//
//        ImageView subCategoryIcon = (ImageView) convertView.findViewById(R.id.sub_category_icon1);
//        subCategoryIcon.setImageResource(subCategoryItemsActivity.getSubCategoryIcon());
//
//        TextView subCategoryName = (TextView) convertView.findViewById(R.id.sub_category_name);
//        subCategoryName.setText(subCategoryItemsActivity.getSubCategoryName());
//
//        TextView subCategoryInfo = (TextView) convertView.findViewById(R.id.sub_category_info);
//        subCategoryInfo.setText(subCategoryItemsActivity.getSubCategoryInfo());
//
//        return convertView;
//    }
//}

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder>
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
//    private int img;

    //아이템 생성시 사진 편집 기능(카메카 촬영, 사진첩 불러오기) 관련 변수
    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private static final int PICK_FROM_ALBUM = 1;
    private String imageFilePath;
    private Uri photoUri;
    private File tempFile;

    //옷 편집 다이얼로그에서 생성되는 뷰를 위에서 정의
    //이유 - 다이얼로그 바깥에서는 다이얼로그 안의 뷰에 접근할 수 없어서 미리 객체를 정의해두고
    //수정할 수 있게 만들었음.
    View viewDialog;
    static ImageButton editImage;//todo edit tiext는 et_~~형태로 명확하게 표
    EditText editName;
    EditText editInfo;
    EditText editCategory;
    EditText editColor;
    Button buttonEditFinish;

    //추가 삭제에서 동일한 아이템 찾기를 스레드 화 시켰다.
    // 스레드 진행 중임을 알리는 프로그래스바도 구현함.
    EditHandler editHandler = new EditHandler();
    DeleteHanlder deleteHanlder = new DeleteHanlder();
    ProgressDialog mProgressDialog;

    //    public class SubCategoryViewHolder extends RecyclerView.ViewHolder  {
//        public ImageView icon;
//        public TextView name;
//        public TextView info;
//
//        public SubCategoryViewHolder(View itemView) {
//            super(itemView);
//
//
//            icon = (ImageView) itemView.findViewById(R.id.sub_category_icon1);
//            name = (TextView) itemView.findViewById(R.id.sub_category_name);
//            info = (TextView) itemView.findViewById(R.id.sub_category_info);
//            itemView.setOnCreateContextMenuListener(SubCategoryViewHolder.this);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    int position = getAdapterPosition();
//
//                    if(position != RecyclerView.NO_POSITION){
//
////                    Intent intent = new Intent(SubCategoryViewHolder.this, SubCategoryItemClickedActivity.class);
////
////                    //인텐트에 위에서 이야기한 세 개의 속성을 집어어준다.
////                intent.putExtra("subCategoryIcon", Integer.toString(SubCategoryActivity.data.get(position).getSubCategoryIcon()));
////                intent.putExtra("subCategoryName", data.get(position).getSubCategoryName());
////                intent.putExtra("subCategoryInfo", data.get(position).getSubCategoryInfoyInfo());
//                    }
//                }
//            });
//
//
//        }
//
//        @Override
//        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo contextMenuInfo) {
//
//            MenuItem Edit = menu.add(Menu.NONE.1001,1,"편집");
//            MenuItem Delete = menu.add(Menu.NONE,1002,2,"삭제");
//            Edit.setOnMenuItemClickListener(onEditMenu);
//            Delete.setOnMenuItemClickListener(onEditMenu);
//
//
//        }
//
//        // 4. 컨텍스트 메뉴에서 항목 클릭시 동작을 설정합니다.
//        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
//
//
//
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//
//                switch (item.getItemId()) {
//                    case 1001:
//                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//
//                        View view = LayoutInflater.from(mContext)
//                                .inflate(R.layout.activity_sub_category_item_edit, null, false);
//                        builder.setView(view);
//                        final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
//                        final EditText editTextID = (EditText) view.findViewById(R.id.edit_text_dialog_korean);
//                        final EditText editTextEnglish = (EditText) view.findViewById(R.id.edittext_dialog_endlish);
//                        final EditText editTextKorean = (EditText) view.findViewById(R.id.edittext_dialog_korean);
//
//                        editTextID.setText(mData.get(getAdapterPosition()).getId());
//                        editTextEnglish.setText(mData.get(getAdapterPosition()).getEnglish());
//                        editTextKorean.setText(mData.get(getAdapterPosition()).getKorean());
//
//                        final AlertDialog dialog = builder.create();
//                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
//
//                            public void onClick(View v) {
//                                String strID = editTextID.getText().toString();
//                                String strEnglish = editTextEnglish.getText().toString();
//                                String strKorean = editTextKorean.getText().toString();
//                                Dictionary dict = new Dictionary(strID, strEnglish, strKorean );
//                                mData.set(getAdapterPosition(), dict);
//                                notifyItemChanged(getAdapterPosition());
//                                dialog.dismiss();
//                            }
//                        });
//                        dialog.show();
//                        break;
//
//                    case 1002:
//                        mData.remove(getAdapterPosition());
//                        notifyItemRemoved(getAdapterPosition());
//                        notifyItemRangeChanged(getAdapterPosition(), mData.size());
//                        break;
//                }
//                return true;
//            }
//        };
//    }
    //어뎁터 생성자
    public SubCategoryAdapter(Context context, ArrayList<SubCategoryItems> data) {
        this.mContext = context;
        this.filteredList = data;
        this.unFilteredList = data;
    }

    public class SubCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public ImageView icon;
        public TextView name;
        public TextView info;

        public SubCategoryViewHolder(View itemView) {
            super(itemView);


            icon = (ImageView) itemView.findViewById(R.id.sub_category_icon);
            name = (TextView) itemView.findViewById(R.id.sub_category_name);
            info = (TextView) itemView.findViewById(R.id.sub_category_info);

            itemView.setOnCreateContextMenuListener(this);
        }

        //수정 삭제를 구현함.
        //컨텍스트 메뉴로 수정을 할지 삭제를 할 지 결정하고
        //추가는 다이얼로그를 띄어서 수정하게 함.
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem Edit = contextMenu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = contextMenu.add(Menu.NONE, 1002, 2, "삭제");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    //수정하기.
                    case 1001:
//                        for(int i =0;i<unFilteredData.size();i++){
//                            SubCategoryItems unFilteredDataItem = unFilteredData.get(i);
//                            if(clickedItem.getSubCategoryName()==unFilteredDataItem.getSubCategoryName()){
//                                position=i;
//                            }
//                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_sub_category_item_edit,null,false);
//                        builder.setView(view);
//
//                        final Button buttonEditFinish = (Button)view.findViewById(R.id.button_sub_category_item_edit_finish);
//
//                        final ImageButton editImage = (ImageButton)view.findViewById(R.id.img_iconEdit);
//                        final EditText editName = (EditText) view.findViewById(R.id.tv_nameEdit);
//                        final EditText editInfo = (EditText) view.findViewById(R.id.tv_infoEdit);
//                        final EditText editCategory = (EditText) view.findViewById(R.id.tv_categoryEdit);
//                        final EditText editColor = (EditText) view.findViewById(R.id.tv_colorEdit);

//                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SubCategoryActivity.this);
                        viewDialog = LayoutInflater.from(mContext).inflate(R.layout.activity_sub_category_item_edit, null, false);
                        builder.setView(viewDialog);

                        editImage = (ImageButton) viewDialog.findViewById(R.id.img_iconEdit);
                        editName = (EditText) viewDialog.findViewById(R.id.tv_nameEdit);
                        editInfo = (EditText) viewDialog.findViewById(R.id.tv_infoEdit);
                        editCategory = (EditText) viewDialog.findViewById(R.id.tv_categoryEdit);
                        editColor = (EditText) viewDialog.findViewById(R.id.tv_colorEdit);
                        buttonEditFinish = (Button) viewDialog.findViewById(R.id.button_sub_category_item_edit_finish);

//                        img=Integer.parseInt(unFilteredList.get(getAdapterPosition()).getSubCategoryIcon());
                        editImage.setImageBitmap(StringToBitmap(filteredList.get(getAdapterPosition()).getSubCategoryIcon()));
//                        editName.setText(filteredList.get(getAdapterPosition()).getSubCategoryName());
                        editName.setText(filteredList.get(getAdapterPosition()).getSubCategoryName());
                        editInfo.setText(filteredList.get(getAdapterPosition()).getSubCategoryInfo());
                        editCategory.setText(filteredList.get(getAdapterPosition()).getCategory());
                        editColor.setText(filteredList.get(getAdapterPosition()).getSubCategoryColor());

                        final AlertDialog dialog = builder.create();

                        //이미지 수정 클릭 버튼.
                        editImage.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                            @Override
                            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                                MenuItem Edit = contextMenu.add(Menu.NONE, 1001, 1, "사진첩에서 가져오기");
//                               MenuItem Delete = contextMenu.add(Menu.NONE,1002,2,"카메라에서 촬영하");

                                Edit.setOnMenuItemClickListener(onEditMenu);
//                                Delete.setOnMenuItemClickListener(onEditMenu);
                            }

                            private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
//                                 // 권한 체크
//                            TedPermission.with(mContext)
//                                    .setPermissionListener(permissionListener)
//                                    .setRationaleMessage("카메라 권한이 필요합니다.")
//                                    .setDeniedMessage("거부하셨습니다.")
//                                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
//                                    .check();

                                    //사진첩에서 가져오기
                                    if (menuItem.getItemId() == 1001) {
                                        goToAlbum();
                                    }
                                    //카메라에서 촬영하기
                                    else if (menuItem.getItemId() == 1002) {
                                    }
                                    return true;
                                }
                            };
                        });

                        //수정 완료 버튼.
                        buttonEditFinish.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //아직 이미지 전달하 구현 못함
//                                editImage.getResources();
//                                Integer image = Integer.getInteger(String.valueOf(editImage));
//                                Integer image = editImage.getResources();
//                                String gettedimage = editImage.getResources().toString();
//                                int image = editImage.getResources().getIdentifier(String.valueOf(editImage.getResources()),"drawable", null);
//                                int image = Integer.parseInt(gettedimage);
//                                int image = Integer.valueOf(editImage.getDrawable().toString());
//                                int image = editImage.getResources().getIdentifier(String.valueOf(editImage.getResources()),"drawable", String.valueOf(this));
                                //실패목록들

                                //에딧 텍스트에 적혀진 값을 가져와서 저장함.
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                Bitmap bitmap = ((BitmapDrawable) editImage.getDrawable()).getBitmap();
                                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
                                String name = editName.getText().toString();
                                String info = editInfo.getText().toString();
                                String category = editCategory.getText().toString();
                                String color = editColor.getText().toString();

                                SubCategoryItems items = new SubCategoryItems(BitmapToString(resizedBitmap), name, info, category, color, unFilteredList.get(editPosition).getItemUsageCount());

                                //수정할 때, 필터링된 리스트 아이템의 클릭 위치를 받아오면
                                //메인 리스트의 아이템 위치와 일치하지 않으므로
                                //클릭된 아이템의 정보와 저장된 아이템의 정보를 비교해서
                                //클릭된 아이템과 일치하는 아이템만을 수정함.
                                clickedItem = filteredList.get(getAdapterPosition());

                                for (int i = 0; i < unFilteredData.size(); i++) {
                                    SubCategoryItems unFilteredDataItem = unFilteredData.get(i);
                                    if (clickedItem.getSubCategoryName() == unFilteredDataItem.getSubCategoryName()) {
                                        deletePosition = i;
                                    }
                                }

                                //대기를 알리는 프로그래스 바
                                mProgressDialog = new ProgressDialog(itemView.getContext());
                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                mProgressDialog.show();

                                //검토&비교 분석 스레드 시작
                                findEditItem findEditItem = new findEditItem();
                                findEditItem.start();

                                for (int i = 0; i < unFilteredData.size(); i++) {
                                    SubCategoryItems unFilteredDataItem = unFilteredData.get(i);
                                    if (clickedItem.getSubCategoryName() == unFilteredDataItem.getSubCategoryName() &&
                                            clickedItem.getSubCategoryIcon() == unFilteredDataItem.getSubCategoryIcon()) {
                                        editPosition = i;
                                    }
                                }

                                unFilteredData.set(editPosition, items);
                                filteredList.set(getAdapterPosition(), items);
                                notifyItemChanged(getAdapterPosition());
//                                SubCategoryActivity.unFilteredData = mData;

                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        //원래 다른 액티비티에서 수정하려고 했으나,
                        //다른 엑티비티에서 수정하기를 수행하면, 수정된 값을 저장하기가 힘들어서 다이얼로그를 이용해 창을 띄운다.
//                        Intent intent = new Intent(mContext,SubCategoryItemEditActivity.class);
//                        intent.putExtra("subCategoryIcon", Integer.toString(unFilteredList.get(getAdapterPosition()).getSubCategoryIcon()));
//                        intent.putExtra("subCategoryName", unFilteredList.get(getAdapterPosition()).getSubCategoryName());
//                        intent.putExtra("subCategoryInfo", unFilteredList.get(getAdapterPosition()).getSubCategoryInfo());
//                        intent.putExtra("subCategory",unFilteredList.get(getAdapterPosition()).getCategory());
//                        intent.putExtra("subCategoryColor",unFilteredList.get(getAdapterPosition()).getSubCategoryColor());
//                        ((SubCategoryActivity)mContext).startActivity(intent);

                        break;

                    case 1002:

                        //수정할 때, 필터링된 리스트 아이템의 클릭 위치를 받아오면
                        //메인 리스트의 아이템 위치와 일치하지 않으므로
                        //클릭된 아이템의 정보와 저장된 아이템의 정보를 비교해서
                        //클릭된 아이템과 일치하는 아이템만을 수정함.
                        clickedItem = filteredList.get(getAdapterPosition());

                        //대기를 알리는 프로그래스 바
                        mProgressDialog = new ProgressDialog(itemView.getContext());
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressDialog.show();

                        //검토&비교 분석 스레드 시작
                        findDeleteItem findDeleteItem = new findDeleteItem();
                        findDeleteItem.start();

                        for (int i = 0; i < unFilteredData.size(); i++) {
                            SubCategoryItems unFilteredDataItem = unFilteredData.get(i);
                            if (clickedItem.getSubCategoryName() == unFilteredDataItem.getSubCategoryName() &&
                                    clickedItem.getSubCategoryIcon() == unFilteredDataItem.getSubCategoryIcon()) {
                                deletePosition = i;
                            }
                        }
//                        findDeleteItem findDeleteItem = new findDeleteItem();
//                        findDeleteItem.start();

//                        unFilteredList.remove(getAdapterPosition(
                        unFilteredData.remove(deletePosition);
                        filteredList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
//                        notifyItemRangeChanged(getAdapterPosition(),unFilteredData.size());
//                        notifyItemRangeChanged(getAdapterPosition(), filteredList.size());

//                        SubCategoryActivity.unFilteredData=mData;
                        break;
                }
                //수정된 데이터를 서브엑티비티로 넘긴다.
//                SubCategoryActivity.unFilteredData = filteredList;
                return true;
            }
        };
    }

    //저장된 배열 중에서 클릭된 아이템과 동일한 아이템을 찾아냄,
    //이유 - 보여지는 화면(필터링된 화면)과 실제 저장된 배열이 다르기 떄문에
    class findEditItem extends Thread {
        @Override
        public void run() {
            try {
//                for (int i = 0; i < unFilteredData.size(); i++) {
//                    SubCategoryItems unFilteredDataItem = unFilteredData.get(i);
//                    if (clickedItem.getSubCategoryName() == unFilteredDataItem.getSubCategoryName()&&
//                    clickedItem.getSubCategoryIcon()==unFilteredDataItem.getSubCategoryIcon()) {
//                        editPosition = i;
//                    }
//                }
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            editHandler.sendEmptyMessage(0);
        }
    }

    class findDeleteItem extends Thread {

        @Override
        public void run() {
            try {
//                for (int i = 0; i < unFilteredData.size(); i++) {
//                    SubCategoryItems unFilteredDataItem = unFilteredData.get(i);
//                    if (clickedItem.getSubCategoryName() == unFilteredDataItem.getSubCategoryName()&&
//                            clickedItem.getSubCategoryIcon()==unFilteredDataItem.getSubCategoryIcon()) {
//                        deletePosition = i;
//                    }
//                }
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            deleteHanlder.sendEmptyMessage(0);
        }
    }

    //프로그래스바를 안전하게 종료시킴
    //dismiss전에 액티비티가 finsh되면
    //프로그램이 꺼짐.
    class EditHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            mProgressDialog.dismiss();
        }
    }

    class DeleteHanlder extends Handler {

        @Override
        public void handleMessage(Message msg) {

            mProgressDialog.dismiss();
        }
    }


    @Override
    public SubCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = View.inflate(mContext, R.layout.sub_category_item, null);

        SubCategoryViewHolder viewHolder = new SubCategoryViewHolder(view);

//        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SubCategoryViewHolder viewHolder, final int position) {

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SubCategoryItemClickedActivity.class);

                //아이템의 그림, 이름, 정보를 다른 레이아웃에서 확대된 모습으로 표현함.
                intent.putExtra("subCategoryIcon", filteredList.get(position).getSubCategoryIcon());
                intent.putExtra("subCategoryName", filteredList.get(position).getSubCategoryName());
                intent.putExtra("subCategoryInfo", filteredList.get(position).getSubCategoryInfo());
                intent.putExtra("subCategory", filteredList.get(position).getCategory());
                intent.putExtra("subCategoryColor", filteredList.get(position).getSubCategoryColor());
                mContext.startActivity(intent);
            }
        });
        SubCategoryItems items = filteredList.get(position);

        viewHolder.icon.setImageBitmap(StringToBitmap(items.getSubCategoryIcon()));
        viewHolder.name.setText(items.getSubCategoryName());
        viewHolder.info.setText(items.getSubCategoryInfo());
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
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
                        if (unFilteredList.get(i).getCategory().contains(charString)) {
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

    //
    //프로필 생성에서 사진첩 불러오기, 카메라 촬영 기능 메서드
    //앨범으로 받기.
    //앨범으로 가는 메소드
    private void goToAlbum() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        ((SubCategoryActivity) mContext).startActivityForResult(intent, PICK_FROM_ALBUM);
    }
//
//    private void  takePhoto(){
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            File photoFile = null;
//            try {
//                photoFile = SubCategoryActivity.createImageFile();
//            } catch (IOException e) {
//
//            }
//
//            if (photoFile != null) {
//                photoUri = FileProvider.getUriForFile(mContext, getPackageName(), photoFile);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                ((SubCategoryActivity)mContext).startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
//    }
//
//    //갤러리에서 받아온 이미지 넣기
//    private void setImage() {
//
////        ImageButton imageButton = findViewById(R.id.img_iconEdit);
//        ImageButton imageButton = editImage;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

//        imageButton.setImageBitmap(originalBm);
//    }

//    private File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "TEST_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,
//                ".jpg",
//                storageDir
//        );
//        imageFilePath = image.getAbsolutePath();
//        return image;
//    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
//            ExifInterface exif = null;
//
//            try {
//                exif = new ExifInterface(imageFilePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            int exifOrientation;
//            int exifDegree;
//
//            if (exif != null) {
//                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//                exifDegree = exifOrientationToDegress(exifOrientation);
//            } else {
//                exifDegree = 0;
//            }
//
//            String result = "";
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.getDefault());
//            Date curDate = new Date(System.currentTimeMillis());
//            String filename = formatter.format(curDate);
//
//            String strFolderName = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + File.separator + "HONGDROID" + File.separator;
//            File file = new File(strFolderName);
//            if (!file.exists())
//                file.mkdirs();
//
//            File f = new File(strFolderName + "/" + filename + ".png");
//            result = f.getPath();
//
//            FileOutputStream fOut = null;
//            try {
//                fOut = new FileOutputStream(f);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                result = "Save Error fOut";
//            }
//
//            // 비트맵 사진 폴더 경로에 저장
//            rotate(bitmap, exifDegree).compress(Bitmap.CompressFormat.PNG, 70, fOut);
//
//            try {
//                fOut.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                fOut.close();
//                // 방금 저장된 사진을 갤러리 폴더 반영 및 최신화
////                mMediaScanner.mediaScanning(strFolderName + "/" + filename + ".png");
//            } catch (IOException e) {
//                e.printStackTrace();
//                result = "File close Error";
//            }
//            ImageButton imageButton = editImage;
//
//            // 이미지 뷰에 비트맵을 set하여 이미지 표현
//            imageButton.setImageBitmap(rotate(bitmap, exifDegree));
//
//
//        } else {
//            Uri photoUri = data.getData();
//
//            Cursor cursor = null;
//
//            try {
//
//                /*
//                 *  Uri 스키마를
//                 *  content:/// 에서 file:/// 로  변경한다.
//                 */
//                String[] proj = {MediaStore.Images.Media.DATA};
//
//                assert photoUri != null;
//                cursor = getContentResolver().query(photoUri, proj, null, null, null);
//
//                assert cursor != null;
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//
//                cursor.moveToFirst();
//
//                tempFile = new File(cursor.getString(column_index));
//
//            } finally {
//                if (cursor != null) {
//                    cursor.close();
//                }
//            }
//
//            setImage();
//        }
//    }
//
//    private int exifOrientationToDegress(int exifOrientation) {
//        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
//            return 90;
//        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
//            return 180;
//        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
//            return 270;
//        }
//        return 0;
//    }
//
//    private Bitmap rotate(Bitmap bitmap, float degree) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(degree);
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//    }

//    PermissionListener permissionListener = new PermissionListener() {
//        @Override
//        public void onPermissionGranted() {
//            Toast.makeText(getApplicationContext(), "권한이 허용됨",Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//            Toast.makeText(getApplicationContext(), "권한이 거부됨",Toast.LENGTH_SHORT).show();
//        }
//    };

}