package com.example.android.uamp.model;

/**
 * Created by luther on 12/03/2016.
 */

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by U321424 on 08-03-2016.
 */
public class XMLPullParserHandler {
    ArrayList<Item> items;
    private Item item;
    private String text;

    public ArrayList<Item> getItems() {
        return items;
    }

    public Item getItem()
    {
        return item;
    }

    public ArrayList<Item> parse(InputStream is) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
            parser.setInput(is, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("item")) {
                            // create a new instance of Item
                            item = new Item();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("item")) {
                            // add Item object to list
                            items.add(item);
                        } else if (tagname.equalsIgnoreCase("id")) {
                            item.setId(text);
                        } else if (tagname.equalsIgnoreCase("artist")) {
                            item.setArtist(text);
                        } else if (tagname.equalsIgnoreCase("title")) {
                            item.setTitle(text);
                        } else if (tagname.equalsIgnoreCase("album")) {
                            item.setAlbum(text);
                        } else if (tagname.equalsIgnoreCase("year")) {
                            item.setYear(text);
                        }else if (tagname.equalsIgnoreCase("picture")) {
                            item.setYear(text);
                        }else if (tagname.equalsIgnoreCase("buycd")) {
                            item.setYear(text);
                        }else if (tagname.equalsIgnoreCase("date_played")) {
                            item.setYear(text);
                        }else if (tagname.equalsIgnoreCase("duration")) {
                            item.setYear(text);
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

}
