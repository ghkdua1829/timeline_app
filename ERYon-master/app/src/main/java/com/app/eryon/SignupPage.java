package com.app.eryon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SignupPage extends Activity {
    private EditText editTextId;
    private EditText editTextPw1,editTextPw2,editTextName;
    private Button gotoalbum;
    private ImageView profile;
    private static final int PICK_FROM_ALBUM = 1;
    private static String temp="";
    private static String imagepath;
    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        imagepath=null;

        TrustManager[] dummyTrustManager = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, dummyTrustManager, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }


        editTextId = (EditText) findViewById(R.id.new_id);
        editTextPw1 = (EditText) findViewById(R.id.new_pw1);
        editTextPw2 = (EditText) findViewById(R.id.new_pw2);
        editTextName = (EditText) findViewById(R.id.new_Name);
        gotoalbum= (Button) findViewById(R.id.gotoalbum);
        profile = (ImageView) findViewById(R.id.profileimage);

        gotoalbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAlbum();
            }
        });
    }
    public void insert(View view) {
        String Id = editTextId.getText().toString();
        String Pw1 = editTextPw1.getText().toString();
        String Pw2 = editTextPw2.getText().toString();
        String Name = editTextName.getText().toString();
        Date now = new Date();
        final String time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH).format(now);
        if(Pw1.equalsIgnoreCase(Pw2)) {
            if(imagepath!=null) {
                new Thread() {
                    public void run() {
                        //        ftp 연결
                        try {
                            FTPClient mFTP = new FTPClient();
                            mFTP.connect("112.175.184.74", 21);// ftp로 접속
                            mFTP.login("ghkdua1829", "gim1855!"); // ftp 로그인 계정/비번
                            mFTP.setFileType(FTP.BINARY_FILE_TYPE); // 바이너리 파일
                            mFTP.setBufferSize(1024 * 1024); // 버퍼 사이즈
                            mFTP.enterLocalPassiveMode();// 패시브 모드로 접속 // 업로드 경로 수정 (선택 사항 )
                            mFTP.cwd("html"); // ftp 상의 업로드 디렉토리
                            mFTP.cwd("id"); // html/id 로 이동 (이 디렉토리로 업로드가 진행)
                            File path = new File(imagepath); // 업로드 할 파일이 있는 경로(예제는 sd카드 사진 폴더)
                            FileInputStream in = new FileInputStream(path);

                            mFTP.storeFile(time + ".png", in);  // remote라는 이름으로 파일을 저장한다.
                        } catch (SocketException e) { // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) { // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }.start();
                insertoToDatabase(Id, Pw1, Name, "http://ghkdua1829.dothome.co.kr/id/" + time + ".png");
            }
            else{
                insertoToDatabase(Id, Pw1, Name, "http://ghkdua1829.dothome.co.kr/id/" + time + ".png");

            }
        }
        else{
            AlertDialog.Builder builder=new AlertDialog.Builder(SignupPage.this);
            builder.setMessage("비번을 일치시켜라.")
                    .setNegativeButton("retry",null)
                    .create()
                    .show();
        }
    }
    private void insertoToDatabase(String Id, String Pw, String Name,String Image) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SignupPage.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equalsIgnoreCase("회원가입에 성공하셨습니다.")){
                    Intent intent = new Intent(SignupPage.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(SignupPage.this);
                    builder.setMessage(s)
                            .setNegativeButton("retry",null)
                            .create()
                            .show();
                }
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String Id = (String) params[0];
                    String Pw = (String) params[1];
                    String Name = (String) params[2];
                    String Image = (String) params[3];

                    String link = "https://eryon.000webhostapp.com/on_post.php";
                    String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");
                    data += "&" + URLEncoder.encode("Pw", "UTF-8") + "=" + URLEncoder.encode(Pw, "UTF-8");
                    data += "&" + URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(Name, "UTF-8");
                    data += "&" + URLEncoder.encode("Image", "UTF-8") + "=" + URLEncoder.encode(Image, "UTF-8");

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
        task.execute(Id, Pw, Name,Image);
    }

    public void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_ALBUM) {
            if (resultCode == RESULT_OK) {   //나갈때 오류 없애준다.
                Uri photoUri = data.getData();
                imagepath = getRealPathFromURI(photoUri);
                Cursor cursor = null;
                try {
                    String[] proj = {MediaStore.Images.Media.DATA};
                    assert photoUri != null;
                    cursor = getApplicationContext().getContentResolver().query(photoUri, proj, null, null, null);
                    assert cursor != null;
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    tempFile = new File(cursor.getString(column_index));
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                setImage();
            }
        }
    }
    public String getRealPathFromURI(Uri contentUri) {
        int column_index=0; String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getApplication().getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }

    public void setImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        BitMapToString(originalBm);
        profile.setImageBitmap(originalBm);
    }

    public void BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);    //bitmap compress
        byte [] arr=baos.toByteArray();
        String image= Base64.encodeToString(arr, Base64.DEFAULT);
        try{
            temp="&imagedevice="+ URLEncoder.encode(image,"utf-8");
        }catch (Exception e){
            Log.e("exception",e.toString());
        }catch(OutOfMemoryError e){
            Toast.makeText(getApplicationContext(), "이미지 용량이 너무 큽니다.", Toast.LENGTH_SHORT).show();
        }


    }
}
