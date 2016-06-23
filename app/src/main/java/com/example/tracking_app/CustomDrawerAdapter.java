package com.example.tracking_app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomDrawerAdapter extends ArrayAdapter {

    Context context;
    List<DrawerItem> drawerItemList;
    String[] colorlist=new String[]{"#ffffd700","#ffff0000","#ffffa500","#ff008000","#fff45d6b"};


    public CustomDrawerAdapter(Context context,
                               List<DrawerItem> listItems ) {
        super(context, R.layout.drawer_list_item, listItems);
        this.context = context;
        this.drawerItemList = listItems;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        DrawerItemHolder drawerHolder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            drawerHolder = new DrawerItemHolder();

            view = inflater.inflate(R.layout.drawer_list_item, parent, false);
            drawerHolder.ItemName = (TextView) view
                    .findViewById(R.id.title);
            drawerHolder.icon = (ImageView) view.findViewById(R.id.icon);

            view.setTag(drawerHolder);


        } else {
            drawerHolder = (DrawerItemHolder) view.getTag();

        }

        DrawerItem dItem = (DrawerItem) this.drawerItemList.get(position);

        drawerHolder.icon.setImageDrawable(view.getResources().getDrawable(
                dItem.getImgResID()));
        drawerHolder.ItemName.setText(dItem.getItemName());

        view.setBackgroundColor(Color.parseColor(colorlist[position]));

        return view;
    }

    private static class DrawerItemHolder {
        TextView ItemName;
        ImageView icon;

    }

}