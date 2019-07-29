package com.app.eryon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class alarmAdapter extends RecyclerView.Adapter<alarmAdapter.ViewHolder> {
    private ArrayList<alarm> mData = null ;
    private Context context;
    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_alarmcontent;

        ViewHolder(View itemView) {
            super(itemView) ;
            textView_alarmcontent = (TextView) itemView.findViewById(R.id.alarmcontent);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    alarmAdapter(ArrayList<alarm> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public alarmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.recyclerview_alarm, parent, false) ;
        alarmAdapter.ViewHolder vh = new alarmAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(alarmAdapter.ViewHolder holder,final int position) {

        holder.textView_alarmcontent.setText(String.valueOf(mData.get(position).getAlarmcontent()));

        holder.textView_alarmcontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("친구요청");
                builder.setMessage("수락하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                insertrelationship(
                                        String.valueOf(mData.get(position).getSendname()),String.valueOf(mData.get(position).getUsername()),String.valueOf(mData.get(position).getAlarmcontent()));
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context,"아니오를 선택했습니다.",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();
            }
        });

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    private void insertrelationship(String one_name, String two_name,String alarm_Post) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equalsIgnoreCase("친구신청에 응하셨습니다.")){
                    Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, whatthe.class);
                    intent.putExtra("userName", whatthe.userNickname);
                    intent.putExtra("userprofile", whatthe.userprofile);
                    intent.putExtra("Api", whatthe.API);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
                else{
                    android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(context);
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

                    String link = "https://eryon.000webhostapp.com/friend_respond.php";
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