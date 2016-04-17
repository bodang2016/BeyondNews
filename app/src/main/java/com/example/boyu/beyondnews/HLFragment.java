package com.example.boyu.beyondnews;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HLFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HLFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HLFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private SwipeRefreshLayout swipeLayout;
    private boolean isRefresh = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private ViewPager viewPager;
    private ArrayList<View> pageViews;
    private ImageView imageView;
    private ImageView[] imageViews;
    private ViewGroup main;
    private ViewGroup group;
    private LocalDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private CustomListView list;
    private ScrollView scrollView;
    private SimpleCursorAdapter adapter;
    private Cursor cursor;
    private ArrayList<String[]> list2;
    private int scrollindex;
    private int loadindex = 1;

    public HLFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HLFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HLFragment newInstance(String param1, String param2) {
        HLFragment fragment = new HLFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        int[] img = new int[]{R.mipmap.no1, R.mipmap.no2, R.mipmap.no3,
                R.mipmap.no4, R.mipmap.no5};
        pageViews = new ArrayList<View>();
        for (int i = 0; i < img.length; i++) {
            LinearLayout layout = new LinearLayout(MainActivity.activity);
            ViewGroup.LayoutParams ltp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            final ImageView imageView = new ImageView(MainActivity.activity);
            imageView.setImageResource(img[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

//        Toast.makeText(this.getActivity(), img[i], Toast.LENGTH_SHORT).show();
//            imageView.setPadding(15, 30, 15, 30);
            layout.addView(imageView, ltp);
            pageViews.add(layout);
        }
        imageViews = new ImageView[pageViews.size()];
        main = (ViewGroup) inflater.inflate(R.layout.fragment_headline, container, false);
        scrollView = (ScrollView) main.findViewById(R.id.scrollView);
        group = (ViewGroup) main.findViewById(R.id.viewGroup);
        viewPager = (ViewPager) main.findViewById(R.id.guidePages);

        dbHelper = new LocalDatabaseHelper(MainActivity.activity, "localDatabase.db", null, 1);
        list = (CustomListView) main.findViewById(R.id.list_headline);

        db = dbHelper.getReadableDatabase();
//        cursor = db.rawQuery("select * from localDatabase_info", null);

        swipeLayout = (SwipeRefreshLayout) main.findViewById(R.id.refresh_layout);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_red_dark);

        for (int i = 0; i < pageViews.size(); i++) {
            LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            margin.setMargins(10, 0, 0, 0);
            imageView = new ImageView(MainActivity.activity);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(15, 15));
            imageViews[i] = imageView;
            if (i == 0) {
                imageViews[i]
                        .setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                imageViews[i]
                        .setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            group.addView(imageViews[i], margin);
        }
        viewPager.setAdapter(new GuidePageAdapter());
        viewPager.setOnPageChangeListener(new GuidePageChangeListener());
        cursor = db.rawQuery("select * from localDatabase_info where type=1", null);
        if (cursor.getCount() > 0) {
            System.out.println(cursor.getCount());
            cursor.getCount();
            inflateList(cursor);
//            listView.setFocusable(false);
        }
//        scrollView.smoothScrollTo(0,20);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                swipeLayout.setEnabled(scrollView.getScrollY() == 0);
            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        break;
                    case MotionEvent.ACTION_MOVE:
                        scrollindex++;
                        break;
                    default:
                        break;
                }
                if (event.getAction() == MotionEvent.ACTION_UP && scrollindex > 0) {
                    scrollindex = 0;
                    View view = ((ScrollView) v).getChildAt(0);
                    if (view.getMeasuredHeight() <= v.getScrollY() + v.getHeight()) {
                        System.out.println("Load more");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (Data.client == null) {
                                    int result = Client.Init();
                                    if (result == 1) {
                                        Data.client = new Client();
                                        list2.clear();
                                        list2 = Data.client.getNews(1, loadindex);
                                        System.out.println("size=" + list2.size());
                                        Data.client.close();
                                        Data.client = null;
                                        System.out.println(1111111111);
                                        myHandler.sendEmptyMessage(DO_EXPLIST);
                                    }
                                } else if (Data.client != null) {
                                    list2.clear();
                                    list2 = Data.client.getNews(1, loadindex);
                                    Data.client.close();
                                    Data.client = null;
                                    System.out.println(222222222);
                                    myHandler.sendEmptyMessage(DO_EXPLIST);
                                }
                                if (list2.size() != 0) {
                                    System.out.println(loadindex);
                                    loadindex++;

                                }
                            }
                        }).start();
                    }
                }
                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor obj = (Cursor) adapter.getItem(position);
                obj.moveToPosition(position);
                int pos = (Integer) obj.getInt(0);
                System.out.println(pos);
                Intent intent = new Intent();
                intent.setClass(main.getContext(),ReadActivity.class);
                intent.putExtra("id",pos);
                startActivity(intent);

            }
        });

        return main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = Client.Init();
                if (result == 1) {
                    Data.client = new Client();
                    list2 = Data.client.getNews(1);
                }
                myHandler.sendEmptyMessage(DO_REFRESH);
            }
        }).start();
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
            Data.deleteData(dbHelper.getReadableDatabase());
            Data.num = 1;
            loadindex = 1;
            int[] image = new int[]{R.mipmap.no6, R.mipmap.no7, R.mipmap.no8, R.mipmap.no9, R.mipmap.no10};
            for (int i = 0; i < list2.size(); i++) {
                ContentValues values = new ContentValues();
                values.put("type", list2.get(i)[2]);
                values.put("title", list2.get(i)[3]);
                values.put("desc", list2.get(i)[4]);
                values.put("image", image[i % 5]);
                values.put("date", Data.getDateTime());
                values.put("accountid", "1");
                values.put("accountname", "1");
                values.put("favtype", "1");
                values.put("accountcomm", "1");
                values.put("_id", list2.get(i)[0]);
//                Data.num++;
                Data.insertData(dbHelper.getReadableDatabase(), values);
            }
            list2.clear();
            cursor = db.rawQuery("select * from localDatabase_info where type=1", null);
            if (cursor.getCount() > 0) {
                cursor.getCount();
                inflateList(cursor);
                adapter.notifyDataSetChanged();
            }
            Toast.makeText(MainActivity.activity, "Refreshed", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.activity, "Connect the Internet", Toast.LENGTH_LONG).show();
        }
    }

    private void expendList() {
        if (list2 != null) {
            System.out.println("list2 is not null");
            int[] image = new int[]{R.mipmap.no6, R.mipmap.no7, R.mipmap.no8, R.mipmap.no9, R.mipmap.no10};
            for (int i = 0; i < list2.size(); i++) {
                ContentValues values = new ContentValues();
                values.put("type", list2.get(i)[2]);
                values.put("title", list2.get(i)[3]);
                values.put("desc", list2.get(i)[4]);
                values.put("image", image[i % 5]);
                values.put("date", Data.getDateTime());
                values.put("accountid", "1");
                values.put("accountname", "1");
                values.put("favtype", "1");
                values.put("accountcomm", "1");
                values.put("_id", list2.get(i)[0]);
//                Data.num++;
                Data.insertData(dbHelper.getReadableDatabase(), values);
                System.out.println(i);
            }
            list2.clear();
            cursor = db.rawQuery("select * from localDatabase_info where type=1", null);
            if (cursor.getCount() > 0) {
                cursor.getCount();
                inflateList(cursor);
            }
            Toast.makeText(MainActivity.activity, "Loaded", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.activity, "Connect the Internet", Toast.LENGTH_LONG).show();
        }
    }

    public void inflateList(Cursor cursor) {
        adapter = new SimpleCursorAdapter(MainActivity.activity, R.layout.list_item,
                cursor, new String[]{"title", "desc", "image",
                "date", "type", "_id"}, new int[]{R.id.list_title, R.id.list_desc, R.id.list_image},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor,
                                        int columnIndex) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.list_image) {
                    ImageView list_image = (ImageView) view;
                    int resID = getActivity().getApplicationContext().getResources()
                            .getIdentifier(cursor.getString(columnIndex),
                                    "drawable",
                                    getActivity().getApplicationContext().getPackageName());
                    list_image.setImageDrawable(MainActivity.activity.getApplicationContext()
                            .getResources().getDrawable(resID));
                    return true;
                }
                return false;
            }
        });
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);
        setListViewHeightBasedOnChildren(list);
        list.setFocusable(false);
    }


    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public class GuidePageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            // TODO Auto-generated method stub
            ((ViewPager) arg0).removeView(pageViews.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            // TODO Auto-generated method stub
            ((ViewPager) arg0).addView(pageViews.get(arg1));
            return pageViews.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }
    }

    public class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[arg0]
                        .setBackgroundResource(R.drawable.page_indicator_focused);

                if (arg0 != i) {
                    imageViews[i]
                            .setBackgroundResource(R.drawable.page_indicator_unfocused);
                }
            }
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void onStart() {
        super.onStart();
//        scrollView.smoothScrollTo(0, 20);
//        System.out.println(1111);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
