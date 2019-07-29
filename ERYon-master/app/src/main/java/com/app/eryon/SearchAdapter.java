package com.app.eryon;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Administrator on 2017-08-07.
 */

public class SearchAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Friend> list;
    private LayoutInflater inflate;
    private ViewHolder viewHolder;

    public SearchAdapter(ArrayList<Friend> list, Context context){
        this.list = list;
        this.context = context;
        this.inflate = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null){
            convertView = inflate.inflate(R.layout.recyclerview_friend,null);

            viewHolder = new ViewHolder();
            viewHolder.label = (TextView) convertView.findViewById(R.id.friendname);
            viewHolder.profile=convertView.findViewById(R.id.friendface);
            viewHolder.profile.setBackground(new ShapeDrawable(new OvalShape()));
            viewHolder.profile.setClipToOutline(true);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        // 리스트에 있는 데이터를 리스트뷰 셀에 뿌린다.
        viewHolder.label.setText(list.get(position).getFriendname());
        String profileUrl = list.get(position).getFriendprofile();
        Glide.with(context).load(profileUrl).into(viewHolder.profile);
        return convertView;
    }

    class ViewHolder{
        public TextView label;
        public ImageView profile;
    }

}