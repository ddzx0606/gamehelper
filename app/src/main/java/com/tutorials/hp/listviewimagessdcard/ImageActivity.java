package com.tutorials.hp.listviewimagessdcard;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.example.jiamiaohe.gamehelper.MyApplication;
import com.example.jiamiaohe.gamehelper.R;

import java.io.File;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {

    ListView mLv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_list_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLv= (ListView) findViewById(R.id.lv);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                lv.setAdapter(new CustomAdapter(ImageActivity.this,getData()));
//
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLv.setAdapter(new CustomAdapter(ImageActivity.this,getData()));
    }

    private ArrayList<Spacecraft> getData()
    {
        ArrayList<Spacecraft> spacecrafts=new ArrayList<>();
        //TARGET FOLDER
        File downloadsFolder= MyApplication.getContext().getExternalFilesDir("screenshot").getAbsoluteFile();

        Spacecraft s;

        if(downloadsFolder.exists())
        {
            //GET ALL FILES IN DOWNLOAD FOLDER
            File[] files=downloadsFolder.listFiles();

            //LOOP THRU THOSE FILES GETTING NAME AND URI
            for (int i=0;i<files.length;i++)
            {
                File file=files[i];

                s=new Spacecraft();
                s.setName(file.getName());
                s.setUri(Uri.fromFile(file));

                spacecrafts.add(s);
            }

            File externalFile = Environment.getExternalStorageDirectory().getAbsoluteFile();
            File[] subFile = externalFile.listFiles();
            for (File tmp : subFile) {
                if (tmp.getAbsolutePath().endsWith(".png") || tmp.getAbsolutePath().endsWith(".jpg") || tmp.getAbsolutePath().endsWith("jpeg")) {
                    s=new Spacecraft();
                    s.setName(tmp.getName());
                    s.setUri(Uri.fromFile(tmp));
                    spacecrafts.add(s);
                }
            }
            // 测试

        }

        return spacecrafts;
    }


}
