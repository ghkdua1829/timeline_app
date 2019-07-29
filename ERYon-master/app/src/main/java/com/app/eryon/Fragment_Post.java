package com.app.eryon;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONObject;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class Fragment_Post extends Fragment{
    private Button add_img,post;
    private File tempFile;
    private ImageView imageView1;
    private EditText postcontent;
    private static final int PICK_FROM_ALBUM = 1;
    private static String temp="";
    private static String imagepath;
    public static int text;
    ProgressDialog loading2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post,container,false);

        imagepath=null;


        imageView1 = view.findViewById(R.id.post_image);
        postcontent=view.findViewById(R.id.post_edit);
        add_img = view.findViewById(R.id.add_img);
        post=view.findViewById(R.id.post);
        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAlbum();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date now = new Date();
                final String time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH).format(now);  //파일이름을 다르게 저장하기 위한 장치
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
                            mFTP.cwd("files"); // public/files 로 이동 (이 디렉토리로 업로드가 진행)

                                File path = new File(imagepath); // 업로드 할 파일이 있는 경로(예제는 sd카드 사진 폴더)
                                FileInputStream in = new FileInputStream(path);
                                mFTP.storeFile(time + ".png", in);  // 이름을 닉네임+저장된숫자 로 했는데 중복의 위험이 있긴하다

                        } catch (SocketException e) { // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) { // TODO Auto-generated catch block
                            e.printStackTrace(); }
                    }
                }.start();
                    insertoToDatabaseContent(postcontent.getText().toString(),"http://ghkdua1829.dothome.co.kr/files/"+time+".png",whatthe.userNickname,whatthe.userprofile);
                }
                else{
                    insertoToDatabaseContent(postcontent.getText().toString(),"null",whatthe.userNickname,whatthe.userprofile);
                }


            }
        });
        return view;
    }


    public void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_ALBUM) {
            if(resultCode==RESULT_OK) {   //나갈때 오류 없애준다.
                Uri photoUri = data.getData();
                imagepath = getRealPathFromURI(photoUri);
                Cursor cursor = null;
                try {
                    String[] proj = {MediaStore.Images.Media.DATA};
                    assert photoUri != null;
                    cursor = getContext().getContentResolver().query(photoUri, proj, null, null, null);
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
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }

    public void setImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        BitMapToString(originalBm);
        imageView1.setImageBitmap(originalBm);
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
            Toast.makeText(getContext(), "이미지 용량이 너무 큽니다.", Toast.LENGTH_SHORT).show();
        }


    }
    private void insertoToDatabaseContent(String content, String imgurl, String nickname,String userprofile) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getContext(), "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equalsIgnoreCase("글이 등록되었습니다.")){
                    Intent intent = new Intent(getContext(), whatthe.class);
                    intent.putExtra("userName", whatthe.userNickname);
                    intent.putExtra("userprofile", whatthe.userprofile);
                    startActivity(intent);
                    getActivity().finish();
                }
                else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                    builder.setMessage(s)
                            .setNegativeButton("retry",null)
                            .create()
                            .show();
                }
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String Content = (String) params[0];
                    String ImgUrl = (String) params[1];
                    String NickName = (String) params[2];
                    String userprofile = (String) params[3];

                    String link = "https://eryon.000webhostapp.com/img.php";
                    String data = URLEncoder.encode("Content", "UTF-8") + "=" + URLEncoder.encode(Content, "UTF-8");
                    data += "&" + URLEncoder.encode("ImgUrl", "UTF-8") + "=" + URLEncoder.encode(ImgUrl, "UTF-8");
                    data += "&" + URLEncoder.encode("NickName", "UTF-8") + "=" + URLEncoder.encode(NickName, "UTF-8");
                    data += "&" + URLEncoder.encode("userprofile", "UTF-8") + "=" + URLEncoder.encode(userprofile, "UTF-8");

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
        task.execute(content, imgurl, nickname,userprofile);
    }

}

