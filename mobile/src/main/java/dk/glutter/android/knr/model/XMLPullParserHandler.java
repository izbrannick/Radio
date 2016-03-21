package dk.glutter.android.knr.model;

/**
 * Created by luther on 12/03/2016.
 */

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by U321424 on 08-03-2016.
 */
public class XMLPullParserHandler {
    private Item item;
    private String text;

    public Item getItem()
    {
        return item;
    }

    public Item parse(InputStream is) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            item = new Item();

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
                        text = parser.getText().replaceAll("(&[^\\s]+?;)", "");//TODO: verify if this works.... XML should contain this chars:(&[^\s]+?;)
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("item")) {
                            // add Item object to list
                            //items.add(item);
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
                            item.setPicture(text);
                        }else if (tagname.equalsIgnoreCase("buycd")) {
                            item.setBuycd(text);
                        }else if (tagname.equalsIgnoreCase("date_played")) {
                            item.setDate_played(text);
                        }else if (tagname.equalsIgnoreCase("duration")) {
                            item.setDuration(text);
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

        return item;
    }

}
