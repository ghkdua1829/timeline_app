package com.app.eryon;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.bumptech.glide.Glide;

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

public class Solo_FriendActivity extends AppCompatActivity {
    public static RecyclerView listView;          // 검색을 보여줄 리스트변수
    public static ArrayList<Item> arraylist;
    public static String userNickname;
    private String profilesrc,mainUsername,status;
    private TextView nickname;
    private ImageView profile;
    private Button relationship;
    PullRefreshLayout loading;
    ProgressDialog loading3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_solo);
        Intent intent=getIntent();
        userNickname=intent.getStringExtra("nickname");
        profilesrc=intent.getStringExtra("profile");
        mainUsername=intent.getStringExtra("mainUsername");
        status=intent.getStringExtra("status");

        listView = (RecyclerView) findViewById(R.id.friendsolo);
        nickname =(TextView) findViewById(R.id.nickname);
        profile =(ImageView)findViewById(R.id.profile);
        profile.setBackground(new ShapeDrawable(new OvalShape()));
        profile.setClipToOutline(true);
        relationship=(Button)findViewById(R.id.relationship);

        nickname.setText(userNickname);
        Glide.with(getApplicationContext()).load(profilesrc).into(profile);
        new SoloFriendPostList().execute(userNickname,mainUsername);

        loading= (PullRefreshLayout)findViewById(R.id.pull);

        loading.setRefreshStyle(PullRefreshLayout.STYLE_WATER_DROP);

        loading.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//Thread - 0.5초 후 로딩 종료
                new SoloFriendPostList().execute(userNickname,mainUsername);
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading.setRefreshing(false);
                    }
                },1000);
            }
        });

        switch (status){
            case "" :
                relationship.setText("친구신청하기");
                break;
            case "0" :
                relationship.setText("친구신청상태");
                relationship.setEnabled(false);
                break;
            case "1":
                relationship.setText("친구");
                relationship.setEnabled(false);
                break;
            case "2":
                relationship.setText("차단");
                relationship.setEnabled(false);
                break;
            case "3":
                relationship.setText("요청들어옴 알림으로 가세요.");
                break;
            case "4":
                relationship.setText("나");
                relationship.setEnabled(false);
                break;
        }
        relationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertrelationship(mainUsername,userNickname,mainUsername+"님이 친구요청을 보내셨습니다.요청에 응답하려면 클릭하세요.");
            }
        });

    }
    // 검색에 사용될 데이터를 리스트에 추가한다.
    public class SoloFriendPostList extends AsyncTask<String , Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            target = "https://eryon.000webhostapp.com/on_timeline_solofriend.php";
            loading3 = ProgressDialog.show(Solo_FriendActivity.this, userNickname+"님의 타임라인 업데이트 중입니다.", "조금만 기다려주세요.", true, true);

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
            showList(result);
        }
    }

    protected void showList(String result){
        try{
            loading3.dismiss();
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


                Item it = new Item(postid,created,userprofile,ImgResource,username,content,good,success,"댓글 열기","solo");
                list.add(it);//리스트뷰에 값을 추가해줍니다
                count++;
            }
            TimelineAdapter adapter = new TimelineAdapter(list);
            listView.setAdapter(adapter) ;
            listView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    private void insertrelationship(String one_name, String two_name,String alarm_Post) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Solo_FriendActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equalsIgnoreCase("친구신청을 요청하였습니다")){
                    Intent intent = new Intent(Solo_FriendActivity.this, whatthe.class);
                    intent.putExtra("userName", mainUsername);
                    intent.putExtra("userprofile", whatthe.userprofile);
                    intent.putExtra("Api", whatthe.API);

                    startActivity(intent);
                    finish();
                }
                else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(Solo_FriendActivity.this);
                    builder.setMessage(s)
                            .setNegativeButton("retry",null)
                            .create()
                            .show();
                }
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String one_name = (String) params[0];
                    String two_name = (String) params[1];
                    String alarm_Post = (String) params[2];

                    String link = "https://eryon.000webhostapp.com/friend_apply.php";
                    String data = URLEncoder.encode("one_name", "UTF-8") + "=" + URLEncoder.encode(one_name, "UTF-8");
                    data += "&" + URLEncoder.encode("two_name", "UTF-8") + "=" + URLEncoder.encode(two_name, "UTF-8");
                    data += "&" + URLEncoder.encode("alarm_Post", "UTF-8") + "=" + URLEncoder.encode(alarm_Post, "UTF-8");

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
        task.execute(one_name, two_name,alarm_Post);
    }

}