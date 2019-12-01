package com.dhkang.crushbox;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AutoPermissionsListener {
    private final String TAG = "CrushBox";
    private final String root_dir = "/kdh314/";
    private TextView selected_item_textview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoPermissions.Companion.loadAllPermissions(this, 101);


        ListView listview = (ListView) findViewById(R.id.listview);
        selected_item_textview = (TextView) findViewById(R.id.selected_item_textview);

        //데이터를 저장하게 되는 리스트
        final List<String> list = new ArrayList<>();

        //리스트뷰와 리스트를 연결하기 위해 사용되는 어댑터
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);

        //리스트뷰의 어댑터를 지정해준다.
        listview.setAdapter(adapter);


        final FTPManager manager = new FTPManager();

        class FileListUpdateAsyncTask extends AsyncTask<Void, Integer, String[]> {
            @Override
            protected String[] doInBackground(Void... strings) {
                manager.connect("sbkang.synology.me", "kdh314", "1q2w3e", 21);
                manager.change_directory(root_dir);
                String[] file_list = manager.get_file_list(".");
                return file_list;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(String[] s) {
                list.addAll(new ArrayList<>(Arrays.asList(s)));
                adapter.notifyDataSetChanged();
            }

            //@Override
            //protected void onCancelled(Boolean s) { super.onCancelled(s);}
        }

        FileListUpdateAsyncTask task = new FileListUpdateAsyncTask();
        task.execute();


        //리스트뷰의 아이템을 클릭시 해당 아이템의 문자열을 가져오기 위한 처리
        class FileDownloadAsyncTask extends AsyncTask<Void, Integer, Boolean> {
            String mFilename;

            FileDownloadAsyncTask(String filename) {
                mFilename = filename;
            }

            @Override
            protected Boolean doInBackground(Void... strings) {
                String local_path = Environment.getExternalStorageState();
                if (local_path.equals(Environment.MEDIA_MOUNTED)) {
                    local_path = "/sdcard/android/data/com.dhkang.crushbox";
                    File file = new File(local_path);
                    if (!file.exists())
                        file.mkdirs();
                }
                local_path += File.separator + mFilename;

                File download_file = new File(local_path);
                if (!download_file.exists()) {
                    try {
                        download_file.createNewFile();
                    } catch (Exception e) {
                        Log.d(TAG, "create new file fail");
                    }
                }

                boolean result = manager.download(root_dir + mFilename, local_path);

                return result;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Boolean s) {

                Intent intent = new Intent(MainActivity.this, Video.class);
                startActivity(intent);

            }
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long id) {

                //클릭한 아이템의 문자열을 가져옴
                final String selected_item = (String) adapterView.getItemAtPosition(position);

                FileDownloadAsyncTask task = new FileDownloadAsyncTask(selected_item);
                task.execute();


                //텍스트뷰에 출력
                selected_item_textview.setText(selected_item);

            }
        });


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int requestCode, String[] permissions) {
        Toast.makeText(this, "permissions denied : " + permissions.length, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGranted(int requestCode, String[] permissions) {
        Toast.makeText(this, "permissions granted : " + permissions.length, Toast.LENGTH_LONG).show();
    }
}