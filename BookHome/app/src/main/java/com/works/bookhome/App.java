package com.works.bookhome;

import android.app.Application;

public class App extends Application {
    public FileDownloader mFileDownloader = null;

    // App start event function
    @Override
    public void onCreate() {
        super.onCreate();
        // init member variable
        initVariable();
    }

    // App close event function
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    // init mamber variable
    public void initVariable() {

        Utils.mApp = this;
        // make file downloader
        mFileDownloader = new FileDownloader(this);
    }

}
