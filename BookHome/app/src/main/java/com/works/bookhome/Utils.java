package com.works.bookhome;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Utils {
    public static final String TAG = "Utils";
    public static App mApp = null;

    // load image file and change Bitmap
    public static Bitmap loadImage(String filePath, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = null;
        // load image file
        try {
            options.inSampleSize = sampleSize;
            bitmap = BitmapFactory.decodeFile(filePath, options);

        }
        // if image size is too big, reduce size and try again
        catch( java.lang.OutOfMemoryError e) {
            bitmap = loadImage(filePath, sampleSize * 2);
        }
        return bitmap;
    }

    // get file name from file path
    public static String filterFileName(String filePath) {
        int pos = -1;
        if( (pos = filePath.lastIndexOf("/")) >= 0 ) {
            filePath = filePath.substring(0,pos) + "_" + filePath.substring(pos+1);
        }
        if( (pos = filePath.lastIndexOf("/")) >= 0 ) {
                filePath = filePath.substring(0, pos) + "_" + filePath.substring(pos + 1);
        }
        if( (pos = filePath.lastIndexOf("/")) >= 0 ) {
            filePath = filePath.substring(pos+1);
        }
        return filePath;
    }

    // get book unique id (six digits) from file path
    public static String getBookURL(String imageURL) {
        int pos = -1;
        if( (pos = imageURL.lastIndexOf("/")) >= 0 ) {
            imageURL = imageURL.substring(0,pos);
        }
        if( (pos = imageURL.lastIndexOf("/")) >= 0 ) {
            imageURL = imageURL.substring(pos + 1);
        }
        return "https://www.labirint.ru/books/" + imageURL;
    }
}
