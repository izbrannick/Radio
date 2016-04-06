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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

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

        try {
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

            str = encodeThenDecode(str);
            LogHelper.e("KNR", "encodeThenDecode str= ", str);
            StringReader strReader = new StringReader(str);

            parser.setInput(strReader);
            //parser.setInput(is, null);
            //parser.setInput(is, "iso-8859-1");
            //parser.setInput(is, "UTF-16");

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
                        } else if (tagname.equalsIgnoreCase("picture")) {
                            item.setPicture(text);
                        } else if (tagname.equalsIgnoreCase("buycd")) {
                            item.setBuycd(text);
                        } else if (tagname.equalsIgnoreCase("date_played")) {
                            item.setDate_played(text);
                        } else if (tagname.equalsIgnoreCase("duration")) {
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

    private String cleanBadChars(String str) throws InterruptedException, CharacterCodingException {
                CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
                decoder.onMalformedInput(CodingErrorAction.REPLACE);
                decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
                ByteBuffer bb = ByteBuffer.wrap(new byte[]{
                        (byte) 0xD0, (byte) 0x9F, // 'П'
                        (byte) 0xD1, (byte) 0x80, // 'р'
                        (byte) 0xD0,              // corrupted UTF-8, was 'и'
                        (byte) 0xD0, (byte) 0xB2, // 'в'
                        (byte) 0xD0, (byte) 0xB5, // 'е'
                        (byte) 0xD1, (byte) 0x82  // 'т'
                });
                CharBuffer parsed = decoder.decode(bb);
                System.out.println(parsed);
                // this prints: Пр?вет

        return parsed.toString();
    }

    private String encodeThenDecode(String str)
    {
        Charset charset = Charset.forName("UTF-8");
        str = charset.decode(charset.encode(str)).toString();

        return str;
    }

    private String replaceBadCharactersOneByOne(String str)
    {
        str = str.replaceAll("(&[^s]+;�%)", "");
        str = str.replace("&", "");
        str = str.replace("�", "");
        str = str.replace("_", "");
        str = str.replace("(", "");
        str = str.replace(")", "");
        str = str.replace("[", "");
        str = str.replace("]", "");
        /*
        if (str.regionMatches(true, 37, "<content><item><id>",38, 18))
            LogHelper.e("string", "MATCHES");
        if (str.regionMatches(true, 38, "<content><item><id>",39, 18))
            LogHelper.e("string", "MATCHES_II");
        */

        return str;
    }
}
