package com.app.eryon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.app.eryon.Request.LoginRequest;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;

public class SearchActivity extends AppCompatActivity {
    private ArrayList<Friend> list;
    private ListView listView;          // 검색을 보여줄 리스트변수
    private EditText editSearch;        // 검색어를 입력할 Input 창
    private SearchAdapter adapter;      // 리스트뷰에 연결할 아답터
    public static ArrayList<Friend> arraylist;
    private String mainUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent=getIntent();
        mainUsername= intent.getStringExtra("nickname");
        editSearch = (EditText) findViewById(R.id.search_keyword);
        listView = (ListView) findViewById(R.id.searchfriendlist);
        list = new ArrayList<>();

        new friend_list().execute(mainUsername);
        // 리스트를 생성한다.

        // 검색에 사용할 데이터을 미리 저장한다.

        // 리스트의 모든 데이터를 arraylist에 복사한다.// list 복사본을 만든다.


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new SoloFriendInfo().execute(String.valueOf(list.get(i).getFriendname()),mainUsername);

            }
        });

        // input창에 검색어를 입력시 "addTextChangedListener" 이벤트 리스너를 정의한다.
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text);
            }
        });


    }

    // 검색을 수행하는 메소드
    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        list.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            list.addAll(arraylist);
        }
        // 문자 입력을 할때..
        else
        {
            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < arraylist.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (arraylist.get(i).getFriendname().toLowerCase().contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    list.add(arraylist.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }

    // 검색에 사용될 데이터를 리스트에 추가한다.
    class friend_list extends AsyncTask<String, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            target = "https://eryon.000webhostapp.com/on_allfriend.php";
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
        protected void onPostExecute(final String result) {
            showfriendList(result);
        }
    }

    protected void showfriendList(String result){
        try{
            //intent로 값을 가져옵니다 이때 JSONObject타입으로 가져옵니다
            JSONObject jsonObject = new JSONObject(result);

            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;

            String Friend_name,friend_profile;

            //JSON 배열 길이만큼 반복문을 실행
            while(count < jsonArray.length()){
                //count는 배열의 인덱스를 의미

                JSONObject object = jsonArray.getJSONObject(count);

                Friend_name = object.getString("nickname");
                friend_profile = object.getString("friend_profile");

                Friend it = new Friend(Friend_name,friend_profile);
                list.add(it);//리스트뷰에 값을 추가해줍니다


                count++;
            }

            arraylist = new ArrayList<>();
            arraylist.addAll(list);
            // 리스트에 연동될 아답터를 생성한다.


            adapter = new SearchAdapter(list,SearchActivity.this);

            // 리스트뷰에 아답터를 연결한다.
            listView.setAdapter(adapter);




        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public class SoloFriendInfo extends AsyncTask<String, Void, String> {
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

                    if(jsonResponse.getString("Status")!=null) {
                        Intent intent = new Intent(SearchActivity.this, Solo_FriendActivity.class);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("profile", image);
                        intent.putExtra("mainUsername", mainUsername);
                        intent.putExtra("status", jsonResponse.getString("Status"));
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(SearchActivity.this, Solo_FriendActivity.class);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("profile", image);
                        intent.putExtra("mainUsername", mainUsername);
                        intent.putExtra("status", "");
                        startActivity(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}