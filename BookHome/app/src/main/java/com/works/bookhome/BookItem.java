package com.works.bookhome;

import android.graphics.Bitmap;

public class BookItem {

    protected String mTitle;
    protected String mAuthor;
    protected String mName;
    protected String mImageUrl;
    protected Bitmap mBmpBookImage = null;

    // Init member variable
    BookItem(String title, String imageUrl) {
        mTitle = title;
        int pos = -1;
        if( (pos = title.indexOf(" - ")) >= 0 ) {
            mAuthor = title.substring(0,pos);
            mName = title.substring(pos+3);
        } else {
            mAuthor = "( без автора )";
            mName = title;
        }
        mImageUrl = imageUrl;

    }

    public String getTitle () {return mTitle;}
    public String getAuthor () {return mAuthor;}
    public String getName () {return mName;}
    public String getImageUrl () {return mImageUrl;}
}
