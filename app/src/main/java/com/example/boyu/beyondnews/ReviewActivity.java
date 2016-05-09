package com.example.boyu.beyondnews;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    private ReviewDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private SwipeRefreshLayout swipeLayout;
    private boolean isRefresh = false;
    private int scrollindex;
    private int loadindex = 1;
    private SimpleCursorAdapter adapter;
    private Cursor cursor;
    private ListView list;
    private ArrayList<String[]> list2;
    private Button btnReview;
    private EditText review;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bundle = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        dbHelper = new ReviewDatabaseHelper(ReviewActivity.this, "reviewDatabase.db", null, 1);
        list = (ListView) this.findViewById(R.id.list_review);
        db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("select * from reviewDatabase_info", null);
        if (cursor.getCount() > 0) {
            System.out.println(cursor.getCount());
            cursor.getCount();
            inflateList(cursor);
        }
        btnReview = (Button) findViewById(R.id.review_btn);
        review = (EditText) findViewById(R.id.review_text);
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int result = Client.Init();
                        if (result == 1) {
                            Data.client = new Client();
                            Data.client.insertComment(new String[]{Integer.toString(bundle.getInt("NEWS_ID")), MainActivity.UnameValue, review.getText().toString()});
                            list2 = Data.client.loadComment(bundle.getInt("NEWS_ID"));
                        }
                        myHandler.sendEmptyMessage(DO_REFRESH);
                    }
                }).start();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = Client.Init();
                if (result == 1) {
                    Data.client = new Client();
                    list2 = Data.client.loadComment(bundle.getInt("NEWS_ID"));
                    System.out.println(list2.size()+" review");
                }
                myHandler.sendEmptyMessage(DO_REFRESH);
            }
        }).start();

    }

    private final static int DO_REFRESH = 0;
    private final static int DO_EXPLIST = 1;
    private final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case DO_REFRESH:
                    refresh();
                    break;
                case DO_EXPLIST:
                    expendList();
                    break;
            }
        }
    };

    private void refresh() {
        if (list2 != null) {
            System.out.println("list2 is not null");
            Data.deleteReview(dbHelper.getReadableDatabase());
            Data.num = 1;
            loadindex = 1;
            for (int i = 0; i < list2.size(); i++) {
                ContentValues values = new ContentValues();
                values.put("user", list2.get(i)[0]);
                values.put("content", list2.get(i)[1]);
                values.put("date", list2.get(i)[2]);
//                values.put("_id", list2.get(i)[0]);
//                Data.num++;
                Data.insertReview(dbHelper.getReadableDatabase(), values);
            }
            list2.clear();
            cursor = db.rawQuery("select * from reviewDatabase_info", null);
            if (cursor.getCount() > 0) {
                cursor.getCount();
                inflateList(cursor);
            }
            Toast.makeText(MainActivity.activity, "Refreshed", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.activity, "Connect the Internet", Toast.LENGTH_LONG).show();
        }
        adapter.notifyDataSetChanged();
        review.setText("");
    }


    private void expendList() {

    }

    public void inflateList(Cursor cursor) {
        adapter = new SimpleCursorAdapter(this, R.layout.review_item,
                cursor, new String[]{"user", "content", "date", "_id"}, new int[]{R.id.review_item_user, R.id.review_item_content, R.id.review_item_date},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        list.setFocusable(true);
    }
}
