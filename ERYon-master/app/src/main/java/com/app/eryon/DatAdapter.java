package com.app.eryon;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DatAdapter extends RecyclerView.Adapter<DatAdapter.ViewHolder> {

    private ArrayList<dat> mData = null ;



    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView dat_image;
        private TextView datid, dattext, created;
        private Button btndatpost;

        ViewHolder(View itemView) {
            super(itemView) ;
            dat_image = (ImageView) itemView.findViewById(R.id.datimage);
            dat_image.setBackground(new ShapeDrawable(new OvalShape()));
            dat_image.setClipToOutline(true);
            datid = (TextView) itemView.findViewById(R.id.datid);
            dattext = (TextView) itemView.findViewById(R.id.dattext);
            created = (TextView) itemView.findViewById(R.id.created);

        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    DatAdapter(ArrayList<dat> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public DatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.activity_dat, parent, false) ;
        DatAdapter.ViewHolder vh = new DatAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(final DatAdapter.ViewHolder holder, int position) {

        String profileUrl = mData.get(position).getDatimg();
        if(mData.get(position).getDatimg().equalsIgnoreCase("null")){
            holder.dat_image.setVisibility(View.GONE);
        }
        else {
            Glide.with(holder.itemView.getContext()).load(profileUrl).into(holder.dat_image);
        }
        holder.datid.setText(String.valueOf(mData.get(position).getDatid()));
        holder.dattext.setText(String.valueOf(mData.get(position).getDattext()));
        holder.created.setText(String.valueOf(mData.get(position).getDatcreated()));

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }
}
