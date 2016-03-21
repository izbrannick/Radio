package dk.glutter.android.knr.model;

import android.os.AsyncTask;
import android.support.v4.media.MediaMetadataCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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

    //http://kristennetradio.dk/scripts/getCurrentSong.php
    //https://api.myjson.com/bins/2d00p
    protected static final String CATALOG_URL =
            "http://private-a6f03-knr.apiary-mock.com/questions";

    protected static final String URL =
            "http://kristennetradio.dk/scripts/getCurrentSong.php";

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

    //public ArrayList<Item> metadataList = new ArrayList<>();

    @Override
    public Iterator<MediaMetadataCompat> iterator() {
        try {
            getXmlFromUrl(URL);

            int slashPos = CATALOG_URL.lastIndexOf('/');
            String path = CATALOG_URL.substring(0, slashPos + 1);
            JSONObject jsonObj = fetchJSONFromUrl();
            ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();
            if (jsonObj != null) {
                JSONArray jsonTracks = jsonObj.getJSONArray(XML_MUSIC);

                if (jsonTracks != null) {
                    for (int j = 0; j < jsonTracks.length(); j++) {
                        tracks.add(buildFromJSON(jsonTracks.getJSONObject(j), path));
                    }
                }
            }
            return tracks.iterator();
        } catch (JSONException e) {
            LogHelper.e(TAG, e, "Could not retrieve music list");
            throw new RuntimeException("Could not retrieve music list", e);
        }
    }

    // "http://kristennetradio.dk/scripts/getCurrentSong.php"
    private static String xml= null;

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


    private MediaMetadataCompat buildFromJSON(JSONObject json, String basePath) throws JSONException {
        String title = json.getString(XML_TITLE);
        String album = json.getString(XML_ALBUM);
        String artist = json.getString(XML_ARTIST);
        String genre = json.getString(XML_GENRE);
        String source = json.getString(XML_SOURCE);
        String iconUrl = json.getString(XML_IMAGE);
        int trackNumber = json.getInt(XML_TRACK_NUMBER);
        int totalTrackCount = json.getInt(XML_TOTAL_TRACK_COUNT);
        int duration = json.getInt(XML_DURATION) * 1000; // ms

        LogHelper.d(TAG, "Found music track: ", json);

        // Media is stored relative to JSON file
        if (!source.startsWith("http")) {
            source = basePath + source;
        }
        if (!iconUrl.startsWith("http")) {
            iconUrl = basePath + iconUrl;
        }
        // Since we don't have a unique ID in the server, we fake one using the hashcode of
        // the music source. In a real world app, this could come from the server.
        String id = String.valueOf(source.hashCode());

        // Adding the music source to the MediaMetadata (and consequently using it in the
        // mediaSession.setMetadata) is not a good idea for a real world music app, because
        // the session metadata can be accessed by notification listeners. This is done in this
        // sample for convenience only.
        //noinspection ResourceType
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
                .putString(MusicProviderSource.CUSTOM_METADATA_TRACK_SOURCE, source)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, iconUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, trackNumber)
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, totalTrackCount)
                .build();
    }


    private String jsonString="{\"music\" : [ {\n" +
            "\"title\" : \"Kristen Net Radio\",\n" +
            "\"album\" : \"Mere end bare musik\",\n" +
            "\"artist\" : \"KNR\",\n" +
            "\"genre\" : \"Mere end bare musik\",\n" +
            "\"source\" : \"http://lyt.kristennetradio.dk:8000\",\n" +
            "\"image\" : \"http://kristennetradio.dk/SBC/samHTMweb/pictures/netradio.jpeg\",\n" +
            "\"trackNumber\" : 1,\n" +
            "\"totalTrackCount\" : 6,\n" +
            "\"duration\" : 103,\n" +
            "\"site\" : \"http://lyt.kristennetradio.dk:8000\"\n" +
            "},\n" +
            "{ \"title\" : \"Kristen Net Radio\",\n" +
            "\"album\" : \"Mere end bare musik\",\n" +
            "\"artist\" : \"KNR\",\n" +
            "\"genre\" : \"Mere end bare musik\",\n" +
            "\"source\" : \"http://lyt.kristennetradio.dk:8000\",\n" +
            "\"image\" : \"http://kristennetradio.dk/SBC/samHTMweb/pictures/netradio.jpeg\",\n" +
            "\"trackNumber\" : 2,\n" +
            "\"totalTrackCount\" : 6,\n" +
            "\"duration\" : 132,\n" +
            "\"site\" : \"http://lyt.kristennetradio.dk:8000\"\n" +
            "},\n" +
            "{ \"title\" : \"Kristen Net Radio\",\n" +
            "\"album\" : \"Mere end bare musik\",\n" +
            "\"artist\" : \"KNR\",\n" +
            "\"genre\" : \"Mere end bare musik\",\n" +
            "\"source\" : \"http://lyt.kristennetradio.dk:8000\",\n" +
            "\"image\" : \"http://kristennetradio.dk/SBC/samHTMweb/pictures/netradio.jpeg\",\n" +
            "\"trackNumber\" : 3,\n" +
            "\"totalTrackCount\" : 6,\n" +
            "\"duration\" : 132,\n" +
            "\"site\" : \"http://lyt.kristennetradio.dk:8000\"\n" +
            "}\n" +
            "]}";

    /**
     * Download a JSON file from a server, parse the content and return the JSON
     * object.
     *
     * @return result JSONObject containing the parsed representation.
     */
    private JSONObject fetchJSONFromUrl() throws JSONException {
        BufferedReader reader = null;
        try {
            //TODO: add json from xml
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            throw e;
        } catch (Exception e) {
            LogHelper.e(TAG, "Failed to parse the json for media list", e);
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
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
                    if (currentSongID.equals("none"))
                    {
                        LogHelper.e("onPostExecute", "none");

                        currentSongID = result.getId();
                        metadataList.add(result);
                    }
                    if (!currentSongID.equals(result.getId()) )
                    {
                        LogHelper.e("onPostExecute", "result.getId()", result.getId());

                        currentSongID = result.getId();
                        metadataList.add(result);
                    }
                }catch (Exception e)
                {
                    LogHelper.e("onPostExecute", "try1 Exception", result.getId());
                }
                try {
                    LogHelper.e("onPostExecute", "getXmlFromUrl(URL)", "Tryning...");

                    run();
                    return;
                }catch (Exception e)
                {
                    LogHelper.e("onPostExecute", "getXmlFromUrl(URL)", e);
                }
            }
        }
    }

    private void run()
    {
        getXmlFromUrl(URL);
    }
}