package dk.glutter.android.knr.model;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.media.MediaMetadataCompat;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import dk.glutter.android.knr.utils.LogHelper;

/**
 * Utility class to get a list of MusicTrack's based on a server-side JSON
 * configuration.
 */
public class RemoteJSONSource implements MusicProviderSource {

    private static final String TAG = LogHelper.makeLogTag(RemoteJSONSource.class);

    protected static final String CATALOG_URL =
            "http://private-a6f03-knr.apiary-mock.com/questions";

    protected static final String URL =
            "http://kristennetradio.dk/scripts/getCurrentSong.php";

    protected static final String STREAM_URL = "http://lyt.kristennetradio.dk:8000/;";

    private static String XML_MUSIC = "music";
    private static String XML_TITLE = "title";
    private static String XML_ALBUM = "album";
    private static String XML_ARTIST = "artist";
    private static String XML_GENRE = "genre";
    private static String XML_SOURCE = "source";
    private static String XML_IMAGE = "image";
    private static String XML_TRACK_NUMBER = "trackNumber";
    private static String XML_TOTAL_TRACK_COUNT = "totalTrackCount";
    private static String XML_DURATION = "duration";

    private static String TITLE = "title";
    private static String ALBUM = "album";
    private static String ARTIST = "artist";
    private static String GENRE = "genre";
    private static String SOURCE = "source";
    private static String IMAGE = "image";
    private static int TRACK_NUMBER = 0;
    private static long DURATION = 0;

    @Override
    public Iterator<MediaMetadataCompat> iterator() {
        try {
            getXmlFromUrl(URL);

            ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();
            tracks.add(bliuldFirstMusicList());

            return tracks.iterator();
        } catch (Exception e) {
            LogHelper.e(TAG, e, "Could not retrieve music list");
            throw new RuntimeException("Could not retrieve music list", e);
        }
    }

    public void getXmlFromUrl(String url) {
        try {
            new DownloadXmlTask().execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();

        return stream;
    }

    public Item GetXML(String url)
    {
        XMLPullParserHandler parser = new XMLPullParserHandler();
        InputStream inputStream = null;

        try {
            inputStream = downloadUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return parser.parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private MediaMetadataCompat bliuldFirstMusicList() throws JSONException {

        TITLE = "Kristen Net Radio";
        ALBUM = "Mere end bare musik";
        ARTIST = "KNR";
        GENRE = "Mere end bare musik";
        SOURCE = STREAM_URL;
        IMAGE = "http://kristennetradio.dk/SBC/samHTMweb/pictures/netradio.jpeg";
        TRACK_NUMBER = 1;
        DURATION = 20000; // ms

        // Since we don't have a unique ID in the server, we fake one using the hashcode of
        // the music source. In a real world app, this could come from the server.
        String id = String.valueOf(SOURCE.hashCode());

        // Adding the music source to the MediaMetadata (and consequently using it in the
        // mediaSession.setMetadata) is not a good idea for a real world music app, because
        // the session metadata can be accessed by notification listeners. This is done in this
        // sample for convenience only.
        //noinspection ResourceType
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
                .putString(MusicProviderSource.CUSTOM_METADATA_TRACK_SOURCE, SOURCE)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, ALBUM)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, ARTIST)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, DURATION)
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, GENRE)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, IMAGE)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, TITLE)
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, TRACK_NUMBER)
                .build();
    }

    String currentSongID = "none";

    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String, Void, Item> {

        @Override
        protected Item doInBackground(String... urls) {
            try {

                return GetXML(urls[0]);
            } catch (Exception e) {
                 e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Item result) {
            if (result != null)
            {
                try{
                    if (!currentSongID.equals(result.getId()) )
                    {
                        LogHelper.e("onPostExecute", "result.getId() = ", result.getId() + "and currentSongID = " + currentSongID);

                        currentSongID = result.getId();
                        metadataList.add(result);
                    }
                }catch (Exception e)
                {
                    LogHelper.c(1, "onPostExecute", "try1 Exception: ", result.getId());
                }
                try {
                    //getXmlFromUrl(URL);
                    run();
                }catch (Exception e)
                {
                    LogHelper.c(1, "onPostExecute", "getXmlFromUrl(URL)", e.getMessage());
                }
            }
        }
    }

    Runnable runnable = null;
    Handler handler;
    private void run()
    {
        if(runnable != null)
            handler.removeCallbacks(runnable);

        if(handler == null) {
            handler = new Handler();

            runnable = new Runnable() {
                public void run() {

                    LogHelper.c(1, "onPostExecute", "RUN()", "Running....");
                    getXmlFromUrl(URL);
                    handler.postDelayed(this, 1000); // now is every 1 minutes
                }
            };
            handler.postDelayed(runnable , 1000); // Every 120000 ms (2 minutes)
        }

    }
}