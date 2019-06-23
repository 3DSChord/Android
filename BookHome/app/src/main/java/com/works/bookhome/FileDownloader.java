/*
 FileDownloader : Download image file class
 UsesPermission :
                  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
                  <uses-permission android:name="android.permission.INTERNET" />
                  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
                  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
*/
package com.works.bookhome;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FileDownloader {
    public Context mContext;
    public boolean mIsDownloading = false;
    public ArrayList<DownInfo> mArDown = new ArrayList<DownInfo>();

    class DownInfo {
        public String url = null;
        public String fileName = null;
        public String fileId = null;
        public boolean isDownloading = false;
        public Bitmap bmpCopy = null;

        public DownInfo(String strUrl, String fileName, String fileId) {
            this.url = strUrl;
            this.fileName = fileName;
            this.fileId = fileId;
        }
    }

    public FileDownloader(Context context) {
        mContext = context;
    }

    public void onDownloadSucceed(String result, String fileId, String filePath, Bitmap bmp) {
        int index = getIndexDownArray(fileId);
        if( index >= 0 && index < mArDown.size() )
            mArDown.remove(index);

        if( mEventListener != null ) {
            if (result.length() > 0) {
                mEventListener.onDownloadCompleted(fileId, filePath, bmp);
            }
            else {
                mEventListener.onDownloadFailed(filePath);
            }
        }

        startDownloadNext_Delay();
    }

    public void startDownloadNextFile() {
        if( mIsDownloading )
            return;
        if( mArDown.size() < 1 )
            return;
        for( DownInfo di : mArDown ) {
            if( di.isDownloading )
                continue;

            if( di.bmpCopy != null ) {
                String strFileName = di.fileName;
                if( strFileName.indexOf(".") < 0 )
                    strFileName += ".jpg";
                String filePath = mContext.getFilesDir() + "/" + strFileName;
                onDownloadSucceed("True", di.fileId, filePath, di.bmpCopy);
                return;
            }

            di.isDownloading = true;
            startDownloadFile(di.url, di.fileName, di.fileId);
            return;
        }
    }

    public int getIndexDownArray(String fileId) {
        for(int i=0; i < mArDown.size(); i ++) {
            DownInfo di = mArDown.get(i);
            if( fileId.equals(di.fileId) )
                return i;
        }
        return -1;
    }

    public void reqDownloadFile(String strUrl, String fileName, String fileId, EventListener listener) {
        setListener(listener);
        reqDownloadFile(strUrl, fileName, fileId);
    }

    public void reqDownloadFile(String strUrl, String fileName, String fileId) {
        DownInfo di = new DownInfo(strUrl, fileName, fileId);
        mArDown.add(di);
        startDownloadNext_Delay();
    }

    public void startDownloadNext_Delay() {
        mTimer_startDownloadNext.removeMessages(0);
        mTimer_startDownloadNext.sendEmptyMessageDelayed(0, 30);
        //mTimer_startDownloadNext.sendEmptyMessageDelayed(0, 1000);
    }

    Handler mTimer_startDownloadNext = new Handler() {
        public void handleMessage(Message msg) {
            startDownloadNextFile();
        }
    };

    public void startDownloadFile(String strUrl, String fileName, String fileId) {
        if( mIsDownloading )
            return;
        new HttpReqTask().execute(strUrl, fileName, fileId);
    }

    private class HttpReqTask extends AsyncTask<String,String,String> {
        Bitmap mBmp = null;
        String mFilePath = null;
        String mFileId = null;
        String mFileName = null;

        @Override
        protected String doInBackground(String... arg) {
            if( arg.length < 3 )
                return "";
            mIsDownloading = true;
            mFileId = arg[2];

            boolean result = true;
            mFileName = arg[1];
            String strFileName = arg[1];

            strFileName = Utils.filterFileName(strFileName);
            if( strFileName.indexOf(".") < 0 )
                strFileName += ".jpg";
            mFilePath = mContext.getFilesDir() + "/" + strFileName;
            //Log.d("tag", "HttpReqTask-FilePath " + mFilePath);
            File file = new File( mFilePath );
            if( file.exists() == false ) {
                result = downloadFile(arg[0], strFileName);
            }

            if (result) {
                mBmp = BitmapFactory.decodeFile( mFilePath );
                return "True";
            }
            return "";
        }

        protected void onPostExecute(String result) {
            mIsDownloading = false;
            onDownloadSucceed(result, mFileId, mFilePath, mBmp);

            copyBitmapSameReq(mFileId, mFileName, mBmp);
            /*
            int index = getIndexDownArray(mFileId);
            if( index >= 0 && index < mArDown.size() )
                mArDown.remove(index);

            if( mEventListener != null ) {
                if (result.length() > 0) {
                    mEventListener.onDownloadCompleted(mFileId, mFilePath, mBmp);
                }
                else {
                    mEventListener.onDownloadFailed(mFileId);
                }
            }

            startDownloadNext_Delay();*/
        }
    }

    public interface EventListener {
        void onDownloadCompleted(String fileId, String filePath, Bitmap bmp);

        void onDownloadFailed(String fileId);
    }

    private EventListener mEventListener = null;

    public void setListener(EventListener listener){
        mEventListener = listener;
    }

    boolean downloadFile(String strUrl, String fileName) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            InputStream is = conn.getInputStream();
            FileOutputStream fos = mContext.openFileOutput(fileName, 0);

            byte[] buf = new byte[1024];
            int count;
            while( (count = is.read(buf)) > 0 ) {
                fos.write(buf, 0, count);
            }
            conn.disconnect();
            fos.close();
        } catch (Exception e) {
            Log.d("tag", "Image download error.");
            return false;
        }
        return true;
    }

    public void copyBitmapSameReq(String fileId, String fileName, Bitmap bmp) {
        for(DownInfo di:mArDown) {
            if( di.fileId.equals(fileId) )
                continue;
            if( di.bmpCopy != null )
                continue;
            String itemFileName = di.fileName;
            if( itemFileName.equals(fileName) ) {
                di.bmpCopy = bmp;
                /*if (bmp.isRecycled())
                    di.bmpCopy = bmp;
                else
                    di.bmpCopy = bmp.copy(bmp.getConfig(), true);*/
            }
        }
    }

}
