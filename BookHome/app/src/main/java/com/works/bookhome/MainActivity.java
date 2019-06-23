package com.works.bookhome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends BaseActivity {
    ListView mListBook;
    RelativeLayout mLayoutSendWatch;
    ArrayList<BookItem> mArBook = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayoutSendWatch = (RelativeLayout) findViewById(R.id.layout_send_watch);

        initListView();

        // Read Json Data and make array
        reqBookDataList(mListBook);

        Button btnRefreshBookList = (Button) findViewById(R.id.btnRefreshBookList);
        btnRefreshBookList.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                initListView();

                // Read Json Data and make array
                reqBookDataList(mListBook);
            }
        });

        Button btnSortByAuthor = (Button) findViewById(R.id.btnSortByAuthor);
        btnSortByAuthor.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mArBook.sort(new AuthorComparator());
                BookItemAdaptor bookListAdapter = (BookItemAdaptor) mListBook.getAdapter();
                bookListAdapter.notifyDataSetChanged();
            }
        });

        Button btnSortByName = (Button) findViewById(R.id.btnSortByName);
        btnSortByName.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mArBook.sort(new NameComparator());
                BookItemAdaptor bookListAdapter = (BookItemAdaptor) mListBook.getAdapter();
                bookListAdapter.notifyDataSetChanged();
            }
        });
/*
        // Sort by Name button
        Button btnSortByName = new Button(this);
        btnSortByName.setText("По названию");

        // Adding Sort by Name button to lisview on top
        mListBook.addHeaderView(btnSortByName);

        btnSortByName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Starting a new async task
                //new loadMoreListView().execute();
            }
        });

 */
        //reqDownloadImageFile();
    }

    public void getBookData_Array(String strJson) {
        final String TITLE = "title";
        final String IMAGE_URL = "imageURL";

        try {
            // parsing JSON code and make JSONArray object
            JSONArray jAr = new JSONArray(strJson);
            int count = jAr.length();
            for(int i=0; i < jAr.length(); i++) {
                JSONObject jObj = jAr.getJSONObject(i);
                String strItem = jObj.toString();
                String title = "", imageURL = "";
                if( strItem.indexOf(TITLE) > 0 )
                    title = jObj.getString(TITLE);
                if( strItem.indexOf(IMAGE_URL) > 0 )
                    imageURL = jObj.getString(IMAGE_URL);

                BookItem bi = new BookItem(title, imageURL);
                mArBook.add(bi);
            }
        } catch (JSONException e) {
            Log.d("tag", "Parse Error");
        }
    }

    protected void initListView() {
        // make  Book ArrayList Object
        mArBook = new ArrayList<BookItem>();
        // make Adapter object & set to ListView
        BookItemAdaptor bookListAdapter = new BookItemAdaptor(this, R.layout.book_list_item, mArBook);
        mListBook = (ListView)findViewById(R.id.listBooks);
        mListBook.setAdapter(bookListAdapter);
    }

    protected void reqDownloadImageFile() {
        for(int i=0; i < mArBook.size(); i++) {
            BookItem bi = mArBook.get(i);

            // loading image from local storage
            Bitmap bmp = loadThumbnamlFromLocal(bi.mImageUrl);

            // if there is Image file in local storage
            if( bmp != null ) {
                bi.mBmpBookImage = bmp;
            }
            // no image in local storage, download from server
            else {
                reqDownloadImageFile(bi.mImageUrl, Integer.toString(i), this);
            }
        }

        BookItemAdaptor bookListAdapter = (BookItemAdaptor) mListBook.getAdapter();
        bookListAdapter.notifyDataSetChanged();
    }

    // file download completed event
    @Override
    public void onDownloadCompleted(String fileId, String filePath, Bitmap bmp) {
        Log.d("tag", "onDownloadCompleted()-" + fileId + " / " + filePath);
        int index = Integer.parseInt(fileId);
        BookItem bi = mArBook.get(index);
        bi.mBmpBookImage = bmp;

        int first = mListBook.getFirstVisiblePosition();
        int last = mListBook.getLastVisiblePosition();
        if( index >= first && index <= last) {
            redrawListView();
        }
    }

    // Read Json Data and make array
    public void reqBookDataList(View v) {

        //String addr = "http://de-coding-test.s3.amazonaws.com/books.json";
        String addr = "knigi.json";
        // Send url address & request data
        new HttpReqTask().execute(addr);
    }

    @Override
    protected void onRecv_BookListJson(String strJson) {
        mLayoutSendWatch.setVisibility(View.VISIBLE);
        getBookData_Array( strJson );
        // mArBook.sort(new AuthorComparator());
        mLayoutSendWatch.setVisibility(View.INVISIBLE);

        redrawListView();
        reqDownloadImageFile();
    }

    protected void redrawListView() {
        BookItemAdaptor bookListAdapter = (BookItemAdaptor) mListBook.getAdapter();
        bookListAdapter.notifyDataSetChanged();
    }

    public void openBrowser(View view){

        //Get url from tag
        String url = (String)view.getTag();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        //pass the url to intent data
        intent.setData(Uri.parse(url));

        startActivity(intent);
    }

    public class AuthorComparator implements Comparator<BookItem> {

        @Override
        public int compare(BookItem bi1, BookItem bi2) {
            String name1 = bi1.getAuthor();
            String name2 = bi2.getAuthor();

            // ascending order (descending order would be: name2.compareTo(name1))
            return name1.compareTo(name2);
        }
    }

    public class NameComparator implements Comparator<BookItem> {

        @Override
        public int compare(BookItem bi1, BookItem bi2) {
            String name1 = bi1.getName();
            String name2 = bi2.getName();

            // ascending order (descending order would be: name2.compareTo(name1))
            return name1.compareTo(name2);
        }
    }
}