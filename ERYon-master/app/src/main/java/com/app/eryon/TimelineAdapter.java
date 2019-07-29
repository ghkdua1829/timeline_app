package com.app.eryon;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    private TimelineAdapter.ViewHolder finalholder;
    private String finalnum;
    private ArrayList<Item> mData = null ;
    private Context context;
    private String a,b;
    ProgressDialog loading2;
    ProgressDialog loading3;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView_img,imageView_img2;
        private RecyclerView datlist;
        private LinearLayout hiddendat;
        private EditText datwrite;
        private TextView textView_day, textView_name, texView_text, texView_jot,texView_dat;
        private Button datregister;
        private String updatepostId;
        ViewHolder(View itemView) {
            super(itemView) ;
            imageView_img = (ImageView) itemView.findViewById(R.id.imageView_img);
            imageView_img.setBackground(new ShapeDrawable(new OvalShape()));
            imageView_img.setClipToOutline(true);

            imageView_img2 = (ImageView) itemView.findViewById(R.id.imageView_img2);

            textView_name = (TextView) itemView.findViewById(R.id.textView_name);
            textView_day = (TextView) itemView.findViewById(R.id.textView_day);
            texView_text = (TextView) itemView.findViewById(R.id.textView_text);
            texView_jot = (TextView) itemView.findViewById(R.id.textView_jot);
            texView_dat = (TextView) itemView.findViewById(R.id.textView_dat);
            datlist = (RecyclerView) itemView.findViewById(R.id.datlist);
            hiddendat=(LinearLayout)itemView.findViewById(R.id.hiddendat);
            datwrite=(EditText)itemView.findViewById(R.id.datwrite);
            datregister=(Button)itemView.findViewById(R.id.datregister);

        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    TimelineAdapter(ArrayList<Item> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public TimelineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.recyclerview_item, parent, false) ;
        TimelineAdapter.ViewHolder vh = new TimelineAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(final TimelineAdapter.ViewHolder holder, final int position) {

        String profileUrl = mData.get(position).getImg_url();
        Glide.with(holder.itemView.getContext()).load(profileUrl).into(holder.imageView_img);
        if(mData.get(position).getDetail_link().equalsIgnoreCase("null")){
            holder.imageView_img2.setVisibility(View.GONE);
        }
        holder.imageView_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a=String.valueOf(mData.get(position).getDay());
                b=whatthe.userNickname;
                new SoloFriendInfo4().execute(String.valueOf(mData.get(position).getDay()),whatthe.userNickname);
            }
        });
        String imageUrl = mData.get(position).getDetail_link();
        Glide.with(holder.itemView.getContext()).load(imageUrl).into(holder.imageView_img2);
        holder.textView_day.setText(String.valueOf(mData.get(position).getName()));
        holder.textView_name.setText(String.valueOf(mData.get(position).getDay()));
        holder.texView_text.setText(String.valueOf(mData.get(position).getText()));
        if(String.valueOf(mData.get(position).getJot()).equalsIgnoreCase("0")){
            holder.texView_jot.setText("좋아요("+mData.get(position).getGood()+")");
        }
        else{
            holder.texView_jot.setText("좋아요 취소("+mData.get(position).getGood()+")");
        }
        holder.texView_jot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalholder=holder;
                finalnum=mData.get(position).getPart();
                if(!holder.texView_jot.getText().toString().contains("취소")){
                    new InsertJot().execute(mData.get(position).getPostid(),whatthe.userNickname);
                }
                else{
                    new deleteJot().execute(mData.get(position).getPostid(),whatthe.userNickname);

                }
            }
        });
        holder.texView_dat.setText(mData.get(position).getDat());
        holder.texView_dat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalholder=holder;
                if(holder.hiddendat.getVisibility()==View.GONE) {
                    holder.texView_dat.setText("댓글 닫기");
                    new AlldATList().execute(mData.get(position).getPostid());
                }
                else {
                    holder.hiddendat.setVisibility(View.GONE);
                    holder.texView_dat.setText("댓글 열기");
                }
            }
        });

        holder.datregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalholder=holder;
                holder.updatepostId=mData.get(position).getPostid();
                if(holder.datwrite.getText().toString().equalsIgnoreCase("")){
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setMessage("댓글내용을 입력해주세요.")
                            .setNegativeButton("retry",null)
                            .create()
                            .show();
                }
                else
                insertoToDat(mData.get(position).getPostid(),whatthe.userNickname,whatthe.userprofile,holder.datwrite.getText().toString());
            }
        });
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    public class AlldATList extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            loading2 = ProgressDialog.show(context, "댓글 업데이트 중입니다.", "조금만 기다려주세요.", true, true);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String postid = (String) params[0];

                String link = "https://eryon.000webhostapp.com/on_dat.php";

                String data = URLEncoder.encode("postid", "UTF-8") + "=" + URLEncoder.encode(postid, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(final String result) {
            showList(result);
        }
    }
    protected void showList(String result){
        try{
            ArrayList<dat> list = new ArrayList<>();

            loading2.dismiss();
            //intent로 값을 가져옵니다 이때 JSONObject타입으로 가져옵니다
            JSONObject jsonObject = new JSONObject(result);


            //List.php 웹페이지에서 response라는 변수명으로 JSON 배열을 만들었음..
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;

            String datnickname,datimage,datcontent,datdate;

            //JSON 배열 길이만큼 반복문을 실행
            while(count < jsonArray.length()){
                //count는 배열의 인덱스를 의미
                JSONObject object = jsonArray.getJSONObject(count);

                datnickname = object.getString("datnickname");
                datimage = object.getString("datimage");
                datcontent = object.getString("datcontent");
                datdate = object.getString("datdate");


                dat it = new dat(datimage,datnickname,datcontent,datdate);
                list.add(it);//리스트뷰에 값을 추가해줍니다
                count++;
            }
            DatAdapter adapter = new DatAdapter(list);
            finalholder.datlist.setAdapter(adapter);
            finalholder.datlist.setLayoutManager(new LinearLayoutManager(context));
            finalholder.hiddendat.setVisibility(View.VISIBLE);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void insertoToDat(String postId, String nickname, String datimage,String comment_content) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context, "댓글 입력중입니다.", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equalsIgnoreCase("댓글 작성에 성공하셨습니다.")){
                    finalholder.datwrite.setText(null);
                    new AlldATList().execute(finalholder.updatepostId);
                }
                else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setMessage(s)
                            .setNegativeButton("retry",null)
                            .create()
                            .show();
                }
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String postId = (String) params[0];
                    String nickname = (String) params[1];
                    String datimage = (String) params[2];
                    String comment_content = (String) params[3];

                    String link = "https://eryon.000webhostapp.com/on_dat_register.php";
                    String data = URLEncoder.encode("postId", "UTF-8") + "=" + URLEncoder.encode(postId, "UTF-8");
                    data += "&" + URLEncoder.encode("nickname", "UTF-8") + "=" + URLEncoder.encode(nickname, "UTF-8");
                    data += "&" + URLEncoder.encode("datimage", "UTF-8") + "=" + URLEncoder.encode(datimage, "UTF-8");
                    data += "&" + URLEncoder.encode("comment_content", "UTF-8") + "=" + URLEncoder.encode(comment_content, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(postId, nickname, datimage,comment_content);
    }

    public class InsertJot extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            loading2 = ProgressDialog.show(context, "좋아요 업데이트 중입니다.", "조금만 기다려주세요.", true, true);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                loading2.dismiss();
                String postid = (String) params[0];
                String nickname = (String) params[1];

                String link = "https://eryon.000webhostapp.com/on_insertJot.php";

                String data = URLEncoder.encode("postid", "UTF-8") + "=" + URLEncoder.encode(postid, "UTF-8");
                data += "&" + URLEncoder.encode("nickname", "UTF-8") + "=" + URLEncoder.encode(nickname, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(final String result) {
                new AllPostList().execute(whatthe.userNickname);
                new SoloFriendPostList().execute(Solo_FriendActivity.userNickname,whatthe.userNickname);
        }
    }


    public class deleteJot extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            loading2 = ProgressDialog.show(context, "좋아요 업데이트 중입니다.", "조금만 기다려주세요.", true, true);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                loading2.dismiss();
                String postid = (String) params[0];
                String nickname = (String) params[1];

                String link = "https://eryon.000webhostapp.com/on_deleteJot.php";

                String data = URLEncoder.encode("postid", "UTF-8") + "=" + URLEncoder.encode(postid, "UTF-8");
                data += "&" + URLEncoder.encode("nickname", "UTF-8") + "=" + URLEncoder.encode(nickname, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(final String result) {
            if(Solo_FriendActivity.userNickname==null)
            new AllPostList().execute(whatthe.userNickname);
            else
            new SoloFriendPostList().execute(Solo_FriendActivity.userNickname,whatthe.userNickname);
        }
    }

    public class AllPostList extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
//            loading2 = ProgressDialog.show(context, "타임라인 업데이트 중입니다.", "조금만 기다려주세요.", true, true);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String nickname = (String) params[0];

                String link = "https://eryon.000webhostapp.com/on_timeline.php";
                String data = URLEncoder.encode("nickname", "UTF-8") + "=" + URLEncoder.encode(nickname, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(final String result) {
            showList2(result);
        }
    }
    protected void showList2(String result){
        try{
//            loading2.dismiss();
            //intent로 값을 가져옵니다 이때 JSONObject타입으로 가져옵니다
            JSONObject jsonObject = new JSONObject(result);
            ArrayList<Item> list = new ArrayList<>();


            //List.php 웹페이지에서 response라는 변수명으로 JSON 배열을 만들었음..
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;

            String content,ImgResource,username,created,userprofile,good,postid,success;

            //JSON 배열 길이만큼 반복문을 실행
            while(count < jsonArray.length()){
                //count는 배열의 인덱스를 의미
                JSONObject object = jsonArray.getJSONObject(count);

                content = object.getString("content");
                ImgResource = object.getString("ImgResource");
                username = object.getString("username");
                userprofile = object.getString("userprofile");
                created = object.getString("created");
                good = object.getString("good");
                postid = object.getString("postid");
                success = object.getString("success");


                Item it = new Item(postid,created,userprofile,ImgResource,username,content,good,success,"댓글 열기","");
                list.add(it);//리스트뷰에 값을 추가해줍니다
                count++;
            }

            TimelineAdapter adapter = new TimelineAdapter(list);
            fragment_timeline.recyclerView.setAdapter(adapter) ;
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public class SoloFriendPostList extends AsyncTask<String , Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            target = "https://eryon.000webhostapp.com/on_timeline_solofriend.php";
//            loading3 = ProgressDialog.show(context, Solo_FriendActivity.userNickname+"님의 타임라인 업데이트 중입니다.", "조금만 기다려주세요.", true, true);

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
        protected void onPostExecute(final String result) {
            showList5(result);
        }
    }
    protected void showList5(String result){
        try{
//            loading3.dismiss();
            //intent로 값을 가져옵니다 이때 JSONObject타입으로 가져옵니다.
            JSONObject jsonObject = new JSONObject(result);
            ArrayList<Item> list = new ArrayList<>();


            //List.php 웹페이지에서 response라는 변수명으로 JSON 배열을 만들었음..
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;

            String content,ImgResource,username,created,userprofile,good,postid,success;

            //JSON 배열 길이만큼 반복문을 실행
            while(count < jsonArray.length()){
                //count는 배열의 인덱스를 의미
                JSONObject object = jsonArray.getJSONObject(count);

                content = object.getString("content");
                ImgResource = object.getString("ImgResource");
                username = object.getString("username");
                created = object.getString("created");
                userprofile = object.getString("userprofile");
                good = object.getString("good");
                postid = object.getString("postid");
                success = object.getString("success");


                Item it = new Item(postid,created,userprofile,ImgResource,username,content,good,success,"댓글 열기","");
                list.add(it);//리스트뷰에 값을 추가해줍니다
                count++;
            }
            TimelineAdapter adapter = new TimelineAdapter(list);
            Solo_FriendActivity.listView.setAdapter(adapter) ;
            Solo_FriendActivity.listView.setLayoutManager(new LinearLayoutManager(context));
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public class SoloFriendInfo4 extends AsyncTask<String, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
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
                    if(a.equalsIgnoreCase(b)){
                        Intent intent = new Intent(context, Solo_FriendActivity.class);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("profile", image);
                        intent.putExtra("mainUsername", whatthe.userNickname);
                        intent.putExtra("status", "4");
                        context.startActivity(intent);
                    }
                    else if(jsonResponse.getString("Status")!=null) {
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