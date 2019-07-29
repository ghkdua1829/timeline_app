package com.app.eryon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    ProgressDialog friendloading;
    private Context context;
    private ArrayList<Friend> mData = null ;
    public String selectfriend;
    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_friendname;
        private ImageView friendprofile;
        private LinearLayout friendlayout;

        ViewHolder(View itemView) {
            super(itemView) ;
            textView_friendname = (TextView) itemView.findViewById(R.id.friendname);
            friendlayout = (LinearLayout) itemView.findViewById(R.id.friendlayout);
            friendprofile = (ImageView) itemView.findViewById(R.id.friendface);
            friendprofile.setBackground(new ShapeDrawable(new OvalShape()));
            friendprofile.setClipToOutline(true);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    FriendAdapter(ArrayList<Friend> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recyclerview_friend, parent, false) ;
        FriendAdapter.ViewHolder vh = new FriendAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(final FriendAdapter.ViewHolder holder, final int position) {

        holder.textView_friendname.setText(String.valueOf(mData.get(position).getFriendname()));
        String profileUrl = mData.get(position).getFriendprofile();
        Glide.with(holder.itemView.getContext()).load(profileUrl).into(holder.friendprofile);
        holder.friendlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectfriend=String.valueOf(mData.get(position).getFriendname());
                new SoloFriendInfo().execute(String.valueOf(mData.get(position).getFriendname()),whatthe.userNickname);
            }
        });
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    public class SoloFriendInfo extends AsyncTask<String, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            friendloading = ProgressDialog.show(context, selectfriend+"님 개인 타임라인 입장중 입니다.", "조금만 기다려주세요.", true, true);

            target = "https://eryon.000webhostapp.com/on_friend_solo.php";
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String nickname = (String) params[0];
                String mainname = (String) params[1];

                URL url = new URL(target);//URL 객체 생성

                String data = URLEncoder.encode("nickname", "UTF-8") + "=" + URLEncoder.encode(nickname, "UTF-8");
                data += "&" + URLEncoder.encode("mainname", "UTF-8") + "=" + URLEncoder.encode(mainname, "UTF-8");

                //URL을 이용해서 웹페이지에 연결하는 부분
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());

                wr.write(data);
                wr.flush();

                //바이트단위 입력스트림 생성 소스는 httpURLConnection
                InputStream inputStream = httpURLConnection.getInputStream();

                //웹페이지 출력물을 버퍼로 받음 버퍼로 하면 속도가 더 빨라짐
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;

                //문자열 처리를 더 빠르게 하기 위해 StringBuilder클래스를 사용함
                StringBuilder stringBuilder = new StringBuilder();


                //한줄씩 읽어서 stringBuilder에 저장함
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");//stringBuilder에 넣어줌
                }

                //사용했던 것도 다 닫아줌
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();//trim은 앞뒤의 공백을 제거함

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            try {

                JSONObject jsonResponse = new JSONObject(result);

                boolean success = jsonResponse.getBoolean("success");


                //서버에서 보내준 값이 true이면?
                if (success) {


                    String nickname = jsonResponse.getString("NickName");
                    String image = jsonResponse.getString("image");

                    if(jsonResponse.getString("Status")!=null) {
                        friendloading.dismiss();

                        Intent intent = new Intent(context, Solo_FriendActivity.class);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("profile", image);
                        intent.putExtra("mainUsername", whatthe.userNickname);
                        intent.putExtra("status", jsonResponse.getString("Status"));
                        context.startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(context, Solo_FriendActivity.class);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("profile", image);
                        intent.putExtra("mainUsername", whatthe.userNickname);
                        intent.putExtra("status", "");
                        context.startActivity(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}