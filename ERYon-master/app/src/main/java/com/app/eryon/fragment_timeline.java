package com.app.eryon;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class fragment_timeline extends Fragment {
    public static RecyclerView recyclerView;
    PullRefreshLayout loading;
    ProgressDialog loading2;
    static Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())) ;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup View1 = (ViewGroup) inflater.inflate(R.layout.fragment_time_line, container, false);
        recyclerView = (RecyclerView) View1.findViewById(R.id.recycler1);
        new AllPostList().execute(whatthe.userNickname);

        loading= (PullRefreshLayout)View1.findViewById(R.id.swipeRefreshLayout);

        loading.setRefreshStyle(PullRefreshLayout.STYLE_WATER_DROP);

        loading.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//Thread - 0.5초 후 로딩 종료
                new AllPostList().execute(whatthe.userNickname);
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading.setRefreshing(false);
                    }
                },1000);
            }
        });

        return View1;
    }

    public class AllPostList extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            loading2 = ProgressDialog.show(mContext, "타임라인 업데이트 중입니다.", "조금만 기다려주세요.", true, true);
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
            showList(result);
        }
    }
    protected void showList(String result){
        try{
            loading2.dismiss();
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


                Item it = new Item(postid,created,userprofile,ImgResource,username,content,good,success,"댓글 열기","every");
                list.add(it);//리스트뷰에 값을 추가해줍니다
                count++;
            }

            TimelineAdapter adapter = new TimelineAdapter(list);
            recyclerView.setAdapter(adapter) ;
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
