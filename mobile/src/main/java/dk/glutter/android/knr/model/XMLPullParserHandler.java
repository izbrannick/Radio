package dk.glutter.android.knr.model;

/**
 * Created by luther on 12/03/2016.
 */

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import dk.glutter.android.knr.utils.LogHelper;

/**
 * Created by U321424 on 08-03-2016.
 */

public class XMLPullParserHandler {
    private Item item;
    private String text;

    public Item parse(InputStream is) {

        //handle input Stream with bad characters


        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        ReplacingInputStream replacingInputStream;

        try {

            LogHelper.i("KNR", "XmlParser started ");

            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }



            String str = total.toString();
            str = str.replaceAll("(&[^s]+;�%)", "");
            str = str.replace("&", "");
            str = str.replace("�", "");
            str = str.replace("_", "");
            str = str.replace("(", "");
            str = str.replace(")", "");


            StringReader strReader = new StringReader(str);


            //TODO: clean up! :-)
            parser.setInput(strReader);
            //parser.setInput(is, null);
            //parser.setInput(is, "iso-8859-1");
            //parser.setInput(is, "UTF-16");

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {


                String tagname = parser.getName();
                LogHelper.i("KNR", "XmlParser cleaning tagname for chars ", tagname);

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("item")) {
                            // create a new instance of Item
                            item = new Item();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText().replaceAll("(&[^\\s]+?;)", "");//TODO: verify if this works.... XML should contain this chars:(&[^\s]+?;)
                        LogHelper.c(1,"KNR", "XmlText ", text);
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
            LogHelper.i("KNR", "XmlPullParserException ", e);
        } catch (IOException e) {
            LogHelper.i("KNR", "Xml IOException ", e);
        }

        return item;
    }

}
