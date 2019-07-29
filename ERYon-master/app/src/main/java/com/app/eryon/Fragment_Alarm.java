package com.app.eryon;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Fragment_Alarm extends Fragment {
    private RecyclerView recyclerView;
    private static String alarm;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())) ;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup View1 = (ViewGroup) inflater.inflate(R.layout.fragment_alarm, container, false);
        recyclerView = (RecyclerView) View1.findViewById(R.id.recycleralarm);

        new AlarmPost().execute(whatthe.userNickname);


        return View1;
    }

    class AlarmPost extends AsyncTask<String, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            target = "https://eryon.000webhostapp.com/on_alarm.php";
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String nickname = (String) params[0];

                URL url = new URL(target);//URL 객체 생성

                String data = URLEncoder.encode("nickname", "UTF-8") + "=" + URLEncoder.encode(nickname, "UTF-8");
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

            showalarmList(result);
        }
    }
    protected void showalarmList(String result){
        try{
            //intent로 값을 가져옵니다 이때 JSONObject타입으로 가져옵니다
            JSONObject jsonObject = new JSONObject(result);
            ArrayList<alarm> list = new ArrayList<>();

            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;

            String alarm_content,sendname,username;

            //JSON 배열 길이만큼 반복문을 실행
            while(count < jsonArray.length()){
                //count는 배열의 인덱스를 의미

                JSONObject object = jsonArray.getJSONObject(count);

                alarm_content = object.getString("content");
                sendname = object.getString("sendname");
                username = object.getString("username");

                alarm it = new alarm(alarm_content,sendname,username);
                list.add(it);//리스트뷰에 값을 추가해줍니다


                count++;
            }

            alarmAdapter adapter = new alarmAdapter(list) ;
            recyclerView.setAdapter(adapter) ;

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}