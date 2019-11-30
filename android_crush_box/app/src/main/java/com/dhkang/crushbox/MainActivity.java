package com.dhkang.crushbox;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.dhkang.crushbox.FTPManager;

public class MainActivity extends AppCompatActivity {

    private TextView selected_item_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listview = (ListView)findViewById(R.id.listview);
        selected_item_textview = (TextView)findViewById(R.id.selected_item_textview);


        //데이터를 저장하게 되는 리스트
        final List<String> list = new ArrayList<>();

        //리스트뷰와 리스트를 연결하기 위해 사용되는 어댑터
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);

        //리스트뷰의 어댑터를 지정해준다.
        listview.setAdapter(adapter);

        Thread ftp_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // FTP 예
                FTPManager manager = new FTPManager();
                manager.connect("sbkang.synology.me", "kdh314", "1q2w3e", 21);

                String curr_dir = manager.get_current_directory();

                manager.change_directory("/kdh314");

                String[] file_list = manager.get_file_list(".");

                for(String file : file_list) {
                    list.add(file);

                }
            }
        });

        ftp_thread.start();

        //리스트뷰의 아이템을 클릭시 해당 아이템의 문자열을 가져오기 위한 처리
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long id) {


                //클릭한 아이템의 문자열을 가져옴
                String selected_item = (String)adapterView.getItemAtPosition(position);

                //텍스트뷰에 출력
                selected_item_textview.setText(selected_item);
            }
        });
    }
}