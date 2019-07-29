package com.app.eryon;


import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.app.eryon.LoginActivity.CLIENT_ID;
import static com.app.eryon.LoginActivity.CLIENT_NAME;
import static com.app.eryon.LoginActivity.CLIENT_SECRET;


public class whatthe extends AppCompatActivity {
    private static OAuthLogin naverLoginInstance;
    public static Context mContext;

    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageButton logout,search;
    public static String userNickname,API,userprofile;
    private ImageView mainuserprofile;
    private TextView mainusername;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);

        Intent intent=getIntent();
        userNickname=intent.getStringExtra("userName");
        API=intent.getStringExtra("Api");
        userprofile=intent.getStringExtra("userprofile");


        mainuserprofile=(ImageView)findViewById(R.id.mainuserprofile);
        mainusername=(TextView)findViewById(R.id.mainusername);
        mainuserprofile.setBackground(new ShapeDrawable(new OvalShape()));
        mainuserprofile.setClipToOutline(true);
        Glide.with(getApplicationContext()).load(userprofile).into(mainuserprofile);
        mainusername.setText(userNickname);
        mainuserprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SoloFriendInfo().execute(userNickname,"");
            }
        });

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 툴바 이름 지우기

        logout=(ImageButton)findViewById(R.id.logoutBtn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(whatthe.this);

                builder.setTitle("안내");
                builder.setMessage("정말로그아웃하시겠습니까?");
                builder.setIcon(android.R.drawable.ic_dialog_alert);

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(API==null){
                            SharedPreferences.Editor editor=LoginActivity.pref.edit();
                            editor.remove("nickname");
                            editor.commit();
                            Intent intent = new Intent(whatthe.this, LoginActivity.class);
                            Toast.makeText(getApplicationContext(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                        else if(API.equalsIgnoreCase("")){
                            SharedPreferences.Editor editor=LoginActivity.pref.edit();
                            editor.remove("nickname");
                            editor.commit();

                            Intent intent = new Intent(whatthe.this, LoginActivity.class);
                            Toast.makeText(getApplicationContext(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();


                        }
                        else if(API.equalsIgnoreCase("KAKAO")){

                            SharedPreferences.Editor editor=LoginActivity.pref.edit();
                            editor.remove("nickname");
                            editor.commit();
                            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                                @Override
                                public void onCompleteLogout() {
                                    Toast.makeText(getApplicationContext(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(whatthe.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });
                        }
                        else if(API.equalsIgnoreCase("NAVER")){
                            SharedPreferences.Editor editor=LoginActivity.pref.edit();
                            editor.remove("nickname");
                            editor.commit();

                            Intent intent = new Intent(whatthe.this, LoginActivity.class);
                            Toast.makeText(getApplicationContext(), "정상적으로 로그아웃되었습니다.", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }

                    }
                });

                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Snackbar.make(toolbar, "로그아웃을 취소하셨습니다.", Snackbar.LENGTH_LONG).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("메인"));
        tabLayout.addTab(tabLayout.newTab().setText("친구"));
        tabLayout.addTab(tabLayout.newTab().setText("글쓰기"));
        tabLayout.addTab(tabLayout.newTab().setText("알림"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //  Initializing ViewPager
        viewPager = (ViewPager) findViewById(R.id.pager);
        search=findViewById(R.id.help_menu_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(whatthe.this, SearchActivity.class);
                intent.putExtra("nickname",userNickname);
                startActivity(intent);
            }
        });
        // Creating TabPagerAdapter adapter
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {viewPager.setCurrentItem(tab.getPosition());}
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }


    public class SoloFriendInfo extends AsyncTask<String, Void, String> {
        String target;
        ProgressDialog friendloading;

        @Override
        protected void onPreExecute() {
            friendloading = ProgressDialog.show(whatthe.this, userNickname+"님 개인 타임라인 입장중 입니다.", "조금만 기다려주세요.", true, true);

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
                    friendloading.dismiss();

                    String nickname = jsonResponse.getString("NickName");
                    String image = jsonResponse.getString("image");

                    if(jsonResponse.getString("Status")!=null) {

                        Intent intent = new Intent(getApplicationContext(), Solo_FriendActivity.class);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("profile", image);
                        intent.putExtra("mainUsername", whatthe.userNickname);
                        intent.putExtra("status", "4");
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(), Solo_FriendActivity.class);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("profile", image);
                        intent.putExtra("mainUsername", whatthe.userNickname);
                        intent.putExtra("status", "4");
                        startActivity(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
