package com.example.administrator.biodiversityapplication;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class ListAdapterOfRecords extends BaseAdapter {

    Context context;
    //private final String [] species_names;
    //private final String [] common_names;
    //private final int [] images;
    private final ArrayList<Record> selectedRecords;
    //private final ArrayList<Record> images;

    public ListAdapterOfRecords(Context context,ArrayList<Record> records){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.selectedRecords = records;
    }

    @Override
    public int getCount() {
        return selectedRecords.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.custom_row_list_view, parent, false);
            viewHolder.txtDateTime = (TextView) convertView.findViewById(R.id.record_date_time);
            viewHolder.txtSpeciesName = (TextView) convertView.findViewById(R.id.record_species_name);
            viewHolder.txtCommonName = (TextView) convertView.findViewById(R.id.record_common_name);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.record_image);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        try {
            Record oneRecord = selectedRecords.get(position);
            byte[] recordImage = oneRecord.get_imageData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage, 0, recordImage.length);

            viewHolder.txtSpeciesName.setText(oneRecord.get_speciesName());
            viewHolder.txtCommonName.setText(oneRecord.get_commonName());
            viewHolder.txtDateTime.setText(oneRecord.get_datetime());
            viewHolder.icon.setImageBitmap(bitmap);
        } catch (Exception e) {
            viewHolder.txtDateTime.setText("No Saved Records");
            //e.printStackTrace();
        }
        //viewHolder.icon.setImageResource(images[position]);

        return convertView;
    }

    private static class ViewHolder {

        TextView txtSpeciesName;
        TextView txtCommonName;
        TextView txtDateTime;
        ImageView icon;

    }


    public boolean areAllItemsEnabled(){
        return false;
    }

}