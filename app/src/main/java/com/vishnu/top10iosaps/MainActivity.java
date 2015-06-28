package com.vishnu.top10iosaps;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadData extends AsyncTask<String,Void,String>
    {

        String xmlData;

        @Override
        protected String doInBackground(String... urls) {
            try {

                data = downloadXML(urls[0]);

            }catch (Exception e)
            {
                Log.e("DownloadData", e.toString());
                return "Cannot download xml file";
            }

            return "";
        }
    }

    private String downloadXML(String url) throws IOException{

        // how many characters at a time we are gonna download from file
        final int BUFFER_SIZE  = 2000;

        InputStream inputStream = null;
        String xmlContents;


        try
        {
            URL mUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) mUrl.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("GET");;
            httpURLConnection.setDoInput(true);

            int response = httpURLConnection.getResponseCode();
            Log.d("ResponseCode", response+"");
        }
        catch (Exception e)
        {
            Log.e("downLoadXML", e.toString());
        }
        finally {
            if (inputStream!=null)
            {
                    inputStream.close();
            }
        }


        return "";
    }
}
