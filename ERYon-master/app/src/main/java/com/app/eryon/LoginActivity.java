package com.app.eryon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ApiErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.jibble.simpleftp.SimpleFTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.app.eryon.Request.*;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

public class LoginActivity extends AppCompatActivity {

    private OAuthLoginButton naverLogInButton;
    private static OAuthLogin naverLoginInstance;
    static final String CLIENT_ID = "e7D7H42GHwwJKcUazSE6";
    static final String CLIENT_SECRET = "2IFjqoiFej";
    static final String CLIENT_NAME = "네이버 Api 로그인";
    static  String email="";
    public static int idnum;
    static Context context;

    Button BtnSignUp;
    String finalresult;
    public static Context mContext;
    public static EditText idText;
    public static EditText passwordText;
    public static SharedPreferences pref;
    private SessionCallback sessionCallback;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        sessionCallback = new SessionCallback(); //SessionCallback 초기화
        Session.getCurrentSession().addCallback(sessionCallback); //현재 세션에 콜백 붙임
//        Session.getCurrentSession().checkAndImplicitOpen(); //자동 로그인

        //강의에서 final을 추가시켜줌
        idText = (EditText) findViewById(R.id.idText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        final Button loginbtn = (Button) findViewById(R.id.loginbtn);
        BtnSignUp = (Button) findViewById(R.id.btn_signup);


        pref = getSharedPreferences("nickname", MODE_PRIVATE);





//        ftp 연결

        if(pref.getString("nickname","")!=""){
            Response.Listener<String> responseListener = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject jsonResponse = new JSONObject(response);

                        boolean success = jsonResponse.getBoolean("success");


                        //서버에서 보내준 값이 true이면?
                        if (success) {

                            Toast.makeText(getApplicationContext(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();


                            String id = jsonResponse.getString("Id");
                            String nickname = jsonResponse.getString("NickName");
                            String userprofile = jsonResponse.getString("image");
                            //로그인에 성공했으므로 MenuPage로 넘어감
                            Intent intent = new Intent(LoginActivity.this, whatthe.class);
                            intent.putExtra("userID", id);
                            intent.putExtra("userName", nickname);
                            intent.putExtra("userprofile", userprofile);

                            LoginActivity.this.startActivity(intent);
                            finish();

                        } else {//로그인 실패시

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage("로그인에 실패하셨습니다.")
                                    .setNegativeButton("retry", null)
                                    .create()
                                    .show();


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            LoginRequest_AUTO LoginRequest_AUTO = new LoginRequest_AUTO(pref.getString("nickname",""), responseListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(LoginRequest_AUTO);
        }


        init();
        init_View();

        BtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, SignupPage.class);
                LoginActivity.this.startActivity(registerIntent);
                finish();
            }
        });


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UserOfTeam().execute();
            }
        });

    }


    public class UserOfTeam extends AsyncTask<Void, Void, String> {
        String target;
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            loading = ProgressDialog.show(LoginActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected String doInBackground(Void... params) {

            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(final String result) {

            Response.Listener<String> responseListener = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject jsonResponse = new JSONObject(response);

                        boolean success = jsonResponse.getBoolean("success");


                        //서버에서 보내준 값이 true이면?
                        if (success) {
                            loading.cancel();

                            Toast.makeText(getApplicationContext(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();


                            String id = jsonResponse.getString("Id");
                            String nickname = jsonResponse.getString("NickName");
                            String userprofile = jsonResponse.getString("image");

                            SharedPreferences.Editor editor = pref.edit();
                            editor.remove("nickname");
                            editor.commit();

                            editor.putString("nickname",nickname);
                            editor.commit();

                            Push(nickname);
                            //로그인에 성공했으므로 MenuPage로 넘어감
                            Intent intent = new Intent(LoginActivity.this, whatthe.class);
                            intent.putExtra("userID", id);
                            intent.putExtra("userName", nickname);
                            intent.putExtra("userprofile", userprofile);
                            intent.putExtra("Api", "");

                            LoginActivity.this.startActivity(intent);
                            finish();

                        } else {//로그인 실패시

                            loading.cancel();
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage("로그인에 실패하셨습니다.")
                                    .setNegativeButton("retry", null)
                                    .create()
                                    .show();


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            LoginRequest loginRequest = new LoginRequest(idText.getText().toString(), passwordText.getText().toString(), responseListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(loginRequest);
        }


    }

    //로그인할때 푸시알림 주기
    private void Push(String Name) {
        class pushmessage extends AsyncTask<String, Void, String> {
            String target;

            @Override
            protected void onPreExecute() {
                target = "https://eryon.000webhostapp.com/on_loginAlarm.php";
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String Name = (String) params[0];

                    String data = URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8");
                    URL url = new URL(target);//URL 객체 생성

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
            }


        }
        pushmessage task = new pushmessage();
        task.execute(Name);
    }
    ////////////////*************카카오로그인

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();

                    if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"로그인 도중 오류가 발생했습니다: "+errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(),"세션이 닫혔습니다. 다시 시도해 주세요: "+errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(final MeV2Response result) {

                    Response.Listener<String> responseListener2 = new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject jsonResponse2 = new JSONObject(response);

                                boolean success = jsonResponse2.getBoolean("success");


                                //서버에서 보내준 값이 true이면?
                                if (success) {

                                    Toast.makeText(getApplicationContext(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                                    String nickname = jsonResponse2.getString("NickName");
                                    String userprofile = jsonResponse2.getString("image");

                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("nickname",nickname);
                                    editor.commit();

                                    Intent intent = new Intent(LoginActivity.this, whatthe.class);
                                    intent.putExtra("userID", result.getId());
                                    intent.putExtra("userName", nickname);
                                    intent.putExtra("Api", "KAKAO");
                                    intent.putExtra("userprofile", userprofile);
                                    intent.putExtra("userEmail", result.getKakaoAccount().getEmail());

                                    LoginActivity.this.startActivity(intent);
                                    finish();

                                } else {//로그인 실패시 회원가입을 실시한다.

                                    Intent intent = new Intent(LoginActivity.this, SignupPage_API.class);
                                    intent.putExtra("userID", String.valueOf(result.getId()));
                                    intent.putExtra("Api", "KAKAO");

                                    LoginActivity.this.startActivity(intent);
                                    finish();


                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    LoginRequest_API LoginRequest_KAKAO = new LoginRequest_API(String.valueOf(result.getId()), "KAKAO", responseListener2);
                    RequestQueue queue2 = Volley.newRequestQueue(LoginActivity.this);
                    queue2.add(LoginRequest_KAKAO);

                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
////////////////*************카카오로그인

/////////////******************네이버로그인

    private void init(){
        context = this;
        naverLoginInstance = OAuthLogin.getInstance();
        naverLoginInstance.init(this,CLIENT_ID,CLIENT_SECRET,CLIENT_NAME);
    }
    //변수 붙이기
    private void init_View(){
        naverLogInButton = (OAuthLoginButton)findViewById(R.id.btn_naver_login);

        //로그인 핸들러
        OAuthLoginHandler naverLoginHandler  = new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (success) {//로그인 성공
                    new RequestApiTask().execute();//static이 아니므로 클래스 만들어서 시행.
                } else {//로그인 실패
                    String errorCode = naverLoginInstance.getLastErrorCode(context).getCode();
                    String errorDesc = naverLoginInstance.getLastErrorDesc(context);
                    Toast.makeText(context, "errorCode:" + errorCode + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
                }
            }

        };

        naverLogInButton.setOAuthLoginHandler(naverLoginHandler);

    }

    private class RequestApiTask extends AsyncTask<Void, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {//작업이 실행되기 전에 먼저 실행.

        }

        @Override
        protected String doInBackground(Void... params) {//네트워크에 연결하는 과정이 있으므로 다른 스레드에서 실행되어야 한다.
            String url = "https://openapi.naver.com/v1/nid/me";
            String at = naverLoginInstance.getAccessToken(context);
            return naverLoginInstance.requestApi(context, at, url);//url, 토큰을 넘겨서 값을 받아온다.json 타입으로 받아진다.
        }
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(final String content) {//doInBackground 에서 리턴된 값이 여기로 들어온다.

            try{
                JSONObject jsonObject = new JSONObject(content);
                JSONObject naverresponse = jsonObject.getJSONObject("response");
                email = naverresponse.getString("email");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Response.Listener<String> responseListener3 = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {


                        JSONObject jsonResponse2 = new JSONObject(response);

                        boolean success = jsonResponse2.getBoolean("success");


                        //서버에서 보내준 값이 true이면?
                        if (success) {
                            String nickname = jsonResponse2.getString("NickName");
                            String userprofile = jsonResponse2.getString("image");

                            Toast.makeText(getApplicationContext(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("nickname",nickname);
                            editor.commit();
                            //로그인에 성공했으므로 MenuPage로 넘어감
                            Intent intent = new Intent(LoginActivity.this, whatthe.class);
                            intent.putExtra("userName", nickname);
                            intent.putExtra("Api", "NAVER");
                            intent.putExtra("userprofile", userprofile);
                            LoginActivity.this.startActivity(intent);
                            finish();

                        } else {//로그인 실패시 회원가입을 실시한다.

                            Intent intent = new Intent(LoginActivity.this, SignupPage_API.class);
                            intent.putExtra("userID", String.valueOf(email));
                            intent.putExtra("Api", "NAVER");

                            LoginActivity.this.startActivity(intent);
                            finish();


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            LoginRequest_API LoginRequest_NAVER = new LoginRequest_API(email, "NAVER", responseListener3);

            RequestQueue queue2 = Volley.newRequestQueue(LoginActivity.this);
            queue2.add(LoginRequest_NAVER);


        }

    }
}