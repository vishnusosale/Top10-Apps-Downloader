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

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by vishnu on 29/6/15.
 */
public class ParseXMLData {

    private String mXmlData = "";
    private ArrayList<Application> applicationArrayList;

    public ParseXMLData(String xmlData) {

        this.mXmlData = xmlData;
        applicationArrayList = new ArrayList<>();
    }

    public ArrayList<Application> getApplicationArrayList() {
        return applicationArrayList;
    }

    public boolean process() {
        boolean operationStatus = true;
        boolean inEntry = false;
        Application currentRecord = null;
        String textValue = "";

        try {

            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();

            xmlPullParser.setInput(new StringReader(mXmlData));
            int eventType = xmlPullParser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xmlPullParser.getName();

                if (eventType == XmlPullParser.START_TAG) {
                    if (tagName.equalsIgnoreCase("entry")) {
                        inEntry = true;
                        currentRecord = new Application();

                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    textValue = xmlPullParser.getText();
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (inEntry) {
                        if (tagName.equalsIgnoreCase("entry")) {
                            applicationArrayList.add(currentRecord);
                            inEntry = false;
                        }
                        if (tagName.equalsIgnoreCase("name")) {
                            currentRecord.setName(textValue);
                        } else if (tagName.equalsIgnoreCase("artist")) {
                            currentRecord.setArtist(textValue);
                        } else if (tagName.equalsIgnoreCase("releaseDate")) {
                            currentRecord.setReleaseDate(textValue);
                        }
                    }
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            operationStatus = false;
        }

        for (Application app : applicationArrayList) {
            Log.e("LOG", "****************************");
            Log.e("LOG", app.getName());
            Log.e("LOG", app.getArtist());
            Log.e("LOG", app.getReleaseDate());
            Log.e("LOG", "****************************");
        }

        return operationStatus;
    }
}
