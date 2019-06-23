package com.works.bookhome;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.works.bookhome.Utils.filterFileName;

public class BaseActivity extends AppCompatActivity
        implements FileDownloader.EventListener {

    protected App mApp = null;          // Application Class instance
    final private String TAG_WRITE_READ_FILE = "TAG_WRITE_READ_FILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init member variable
        initVariable();
    }

    // init member variable
    public void initVariable() {
        // get Application class instance
        mApp = (App) getApplication();
    }

    // download image file
    protected void reqDownloadImageFile(String imageId1, String eventId, FileDownloader.EventListener listener) {
        // if there is no file, return
        if( imageId1 == null || imageId1.length() < 1 )
            return;
        // server image url address
        String imageUrl = imageId1;

        // get file name from url address
        String fileName = filterFileName(imageUrl);
        // request file download
        mApp.mFileDownloader.reqDownloadFile(imageUrl, fileName, eventId, listener);
    }

    // file download completed event
    @Override
    public void onDownloadCompleted(String fileId, String filePath, Bitmap bmp) {
    }

    // file download failed event
    @Override
    public void onDownloadFailed(String fileId) {
        Log.d("tag", "onDownloadFailed() - " + fileId);
    }

    // loading image from local storage
    protected Bitmap loadThumbnamlFromLocal(String imageId) {
        // get file name from url address
        imageId = filterFileName(imageId);

        // add extension to file name
        if( imageId.indexOf(".") < 0 )
            imageId += ".jpg";
        // get storage path
        String filePath = this.getFilesDir() + "/" + imageId;

        // Open file
        File file = new File( filePath );
        // if image file not exist, return
        if( !file.exists() ) {
            return null;
        }
        // load image file and change to Bitmap
        Bitmap bmp = Utils.loadImage(filePath, 1);
        return bmp;
    }

private void getFromWeb() {
    StringBuilder builder = new StringBuilder();
    try {
        Document doc = Jsoup.connect("https://www.labirint.ru/books/").get();
        Elements imgs = doc.select("img[data-src]");
        JSONArray jsonObject = new JSONArray();
        for (Element img : imgs) {
            String bool = img.attr("data-src");
            if (bool.contains("https://img2.labirint.ru/books")) {
                JSONObject jsonObject1 = new JSONObject();
                int count = img.attr("alt").length() - 14;
                jsonObject1.put("title", img.attr("alt").substring(0, count));
                jsonObject1.put("imageURL", img.attr("data-src"));
                jsonObject.put(jsonObject1);
            }
        }

        builder.append(jsonObject);

        String filename = "knigi.json";
        String fileContents = jsonObject.toString().replace("\\/","/");
        Context ctx = getApplicationContext();
        try
        {
            FileOutputStream fileOutputStream = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            writeDataToFile(fileOutputStream, fileContents);
        }catch(FileNotFoundException ex)
        {
            Log.e(TAG_WRITE_READ_FILE, ex.getMessage(), ex);
        }

    }
    catch (IOException e) {
        Log.e("getFromWeb", "File not written: " + e.toString());
        builder.append("getFromWeb : ").append(e.getMessage()).append("\n");
    } catch (JSONException e) {
        Log.e("getFromWeb", "File JSON: " + e.toString());
        e.printStackTrace();
    }
}
    // This method will write data to file.
    private void writeDataToFile(File file, String data)
    {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            this.writeDataToFile(fileOutputStream, data);
            fileOutputStream.close();
        }catch(FileNotFoundException ex)
        {
            Log.e(TAG_WRITE_READ_FILE, ex.getMessage(), ex);
        }catch(IOException ex)
        {
            Log.e(TAG_WRITE_READ_FILE, ex.getMessage(), ex);
        }
    }

    // This method will write data to FileOutputStream.
    private void writeDataToFile(FileOutputStream fileOutputStream, String data)
    {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            bufferedWriter.write(data);

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStreamWriter.close();
        }catch(FileNotFoundException ex)
        {
            Log.e(TAG_WRITE_READ_FILE, ex.getMessage(), ex);
        }catch(IOException ex)
        {
            Log.e(TAG_WRITE_READ_FILE, ex.getMessage(), ex);
        }
    }

    // This method will read data from FileInputStream.
    private String readFromFileInputStream(FileInputStream fileInputStream)
    {
        StringBuffer retBuf = new StringBuffer();

        try {
            if (fileInputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String lineData = bufferedReader.readLine();
                while (lineData != null) {
                    retBuf.append(lineData);
                    lineData = bufferedReader.readLine();
                }
            }
        }catch(IOException ex)
        {
            Log.e(TAG_WRITE_READ_FILE, ex.getMessage(), ex);
        }

        return retBuf.toString();
    }

    // return result of HTTP request
    public String getHttpConnResult(String strUrl) {
        String line, result = new String();

        try {
            if (strUrl.contains("http")) {

                // make Http client
                URL url = new URL(strUrl);
                HttpURLConnection conn = (HttpURLConnection)
                        url.openConnection();
                // set connect information
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // start connect
                conn.connect();

                // get data
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));

                while ((line = reader.readLine()) != null) {
                    result += line + '\n';
                    if (result.length() > 1000000) break;
                }
                // close connection
                reader.close();
                conn.disconnect();
            }
            else {
                //getWebsite();
                getFromWeb();
                //result = "[{\"title\" = \"No books found\"},{\"imageURL\" = \"\"}]";
                String tempStr  = "temp";
                try {
                    Context ctx = getApplicationContext();
                    FileInputStream fileInputStream = ctx.openFileInput(strUrl);
                    tempStr = readFromFileInputStream(fileInputStream);
                }catch(FileNotFoundException ex)
                {
                    Log.e(TAG_WRITE_READ_FILE, ex.getMessage(), ex);
                }
                //https://www.dev2qa.com/android-read-write-internal-storage-file-example/
                if (tempStr.length() == 0){
                    result = "[{\"title\" = \"No books found\"},{\"imageURL\" = \"\"}]";
                } else {
                    result = tempStr;
                }

            }
        }
        catch(Exception e) {
            Log.d("tag", "HttpURLConnection error");
        }
        return result;
    }

    // define thread class
    protected class HttpReqTask extends AsyncTask<String,String,String> {
        @Override // Running thread
        protected String doInBackground(String... arg) {
            String response = "";
            // request data from server
            if( arg.length == 1 ) {
                return getHttpConnResult(arg[0]);
            }
            return response;
        }

        // after thread completed
        protected void onPostExecute(String result) {
            onRecv_BookListJson(result);
        }
    }

    protected void onRecv_BookListJson(String strJson) {

    }

}
