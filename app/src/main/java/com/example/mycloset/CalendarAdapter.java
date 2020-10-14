package com.example.mycloset;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CalendarAdapter extends BaseAdapter {
    private ArrayList<SubCategoryItems> subCategoryItemsList = new ArrayList<>();

    public CalendarAdapter(ArrayList<SubCategoryItems> getList) {
        this.subCategoryItemsList = getList;
    }

    @Override
    public int getCount() {
        return subCategoryItemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return subCategoryItemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.sub_category_item, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.sub_category_icon);
        TextView textViewName = (TextView) convertView.findViewById(R.id.sub_category_name);
        TextView textViewInfo = (TextView) convertView.findViewById(R.id.sub_category_info);

        SubCategoryItems subCategoryItems = subCategoryItemsList.get(position);

        imageView.setImageBitmap(StringToBitmap(subCategoryItems.getSubCategoryIcon()));
        textViewName.setText(subCategoryItems.getSubCategoryName());
        textViewInfo.setText(subCategoryItems.getSubCategoryInfo());

        return convertView;
    }

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

    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }
}
