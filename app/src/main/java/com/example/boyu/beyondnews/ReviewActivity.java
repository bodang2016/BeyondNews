package com.example.boyu.beyondnews;

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
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        dbHelper = new ReviewDatabaseHelper(ReviewActivity.this, "reviewDatabase.db", null, 1);
        list = (ListView) this.findViewById(R.id.list_review);
        db = dbHelper.getReadableDatabase();

        swipeLayout = (SwipeRefreshLayout) this.findViewById(R.id.review_refresh);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_red_dark);
        cursor = db.rawQuery("select * from reviewDatabase_info", null);
        if (cursor.getCount() > 0) {
            System.out.println(cursor.getCount());
            cursor.getCount();
            inflateList(cursor);
        }
        list.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                swipeLayout.setEnabled(list.getScrollY() == 0);
            }
        });
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

    }

    private void expendList() {

    }

    @Override
    public void onRefresh() {
        if (!isRefresh) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(false);
                    isRefresh = false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (Data.client == null) {
                                int result = Client.Init();
                                if (result == 1) {
                                    Data.client = new Client();
                                    list2 = Data.client.getNews(1);
                                    Data.client.close();
                                    Data.client = null;
                                    myHandler.sendEmptyMessage(DO_REFRESH);
                                }
                            } else if (Data.client != null) {
                                list2 = Data.client.getNews(1);
                                Data.client.close();
                                Data.client = null;
                                myHandler.sendEmptyMessage(DO_REFRESH);
                            }
                        }
                    }).start();
                }
            }, 3000);
        }
    }

    public void inflateList(Cursor cursor) {
        adapter = new SimpleCursorAdapter(this, R.layout.review_item,
                cursor, new String[]{"user", "content", "date", "_id"}, new int[]{R.id.review_item_user, R.id.review_item_content, R.id.review_item_date},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);
        list.setFocusable(false);
    }
}
