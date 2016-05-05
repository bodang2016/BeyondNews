package com.example.boyu.beyondnews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ReadActivity extends AppCompatActivity {

    TextView title;
    TextView passage;
    ImageView image;
    String[] news;
    Bundle bundle;

    private final static int DO_REFRESH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        title = (TextView) findViewById(R.id.readTitle);
        passage = (TextView) findViewById(R.id.readPassage);
        image = (ImageView) findViewById(R.id.readImage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        bundle = getIntent().getExtras();
        new Thread(new Runnable() {
            @Override
            public void run() {
                news = Data.client.getContent(bundle.getInt("id"));
                myHandler.sendEmptyMessage(DO_REFRESH);
            }
        }).start();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton review = (FloatingActionButton) this.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ReadActivity.this, ReviewActivity.class);
                intent.putExtra("NEWS_ID", bundle.getInt("id"));
            }
        });

    }


    private final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case DO_REFRESH:
                    refresh();
                    break;
            }
        }
    };

    private void refresh() {
        title.setText(news[0]);
        passage.setText(news[1]);
        System.out.println(news[0]);
        System.out.println(news[1]);
    }

}
