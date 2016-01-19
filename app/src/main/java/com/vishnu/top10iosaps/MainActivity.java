/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Vishnu Sosale
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.vishnu.top10iosaps;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends Activity {

    Button parseButton;
    ListView parseListView;

    String mXmlData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parseButton = (Button) findViewById(R.id.parseButton);
        parseListView = (ListView) findViewById(R.id.parseListView);

        new DownloadData().
                execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");

        parseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseXMLData parseXMLData = new ParseXMLData(mXmlData);
                boolean operationStatus = parseXMLData.process();

                if (operationStatus) {
                    ArrayList<Application> arrayList = parseXMLData.getApplicationArrayList();
                    ArrayAdapter<Application> applicationArrayAdapter =
                            new ArrayAdapter<>(MainActivity.this, R.layout.item_list_app, arrayList);
                    parseListView.setVisibility(View.VISIBLE);
                    parseListView.setAdapter(applicationArrayAdapter);

                } else {
                    Log.e("Parsing XML", " error");
                }

            }
        });


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


    /**
     * AsyncTask that helps us download the XML data
     * Takes URL as String and returns XML data as String
     */
    private class DownloadData extends AsyncTask<String, Void, String> {

        String xmlData;

        @Override
        protected String doInBackground(String... urls) {
            try {

                xmlData = downloadXML(urls[0]);

            } catch (IOException e) {
                Log.e("DownloadData", e.toString());
                return "Cannot download xml file";
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            mXmlData = xmlData;
        }

        private String downloadXML(String url) throws IOException {

            // how many characters at a time we are gonna download from file
            final int BUFFER_SIZE = 2000;

            InputStream inputStream = null;
            String xmlContents = "";


            try {
                URL mUrl = new URL(url);

                /*
                    java.net.HttpURLConnection extends java.net.URLConnection abstract class.
                    java.net.URLConnection has built in protocols like 'File', 'FTP', 'HTTP', 'HTTPS'
                    and JAR. When we call openConnection() on a URL which contains 'https' it returns
                    an object of type HttpsURLConnection. HttpsURLConnection extends HttpURLConnection.

                    A javax.net.ssl.HttpsURLConnection will have access to the negotiated cipher suite,
                    the server certificate chain, and the client certificate chain if any.

                    Use getErrorStream() to read the error response. getHeaderFields() gives us the header fields.

                    HttpURLConnection uses GET (default), POST, OPTIONS, HEAD, PUT, DELETE and TRACE methods.

                */

                HttpURLConnection httpURLConnection = (HttpURLConnection) mUrl.openConnection();
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("GET");

                httpURLConnection.setDoInput(true);

                int response = httpURLConnection.getResponseCode();
                //Log.d("ResponseCode", response + "");

                inputStream = httpURLConnection.getInputStream();

                /*
                    java.io.InputStreamReader turns byte stream into a character stream
                 */
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int charRead;
                char[] inputBuffer = new char[BUFFER_SIZE];

                try {
                    /*
                        read() function reads a single character from inputBuffer and returns
                        it as an integer with 2 higher order bytes set to 0
                     */
                    while ((charRead = inputStreamReader.read(inputBuffer)) > 0) {
                        String readString = String.copyValueOf(inputBuffer, 0, charRead);
                        xmlContents += readString;
                        inputBuffer = new char[BUFFER_SIZE];
                    }

                    return xmlContents;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            } catch (IOException e) {
                Log.e("downLoadXML", e.toString());
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

            }
            return "";
        }
    }
}
