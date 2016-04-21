package dk.glutter.android.knr.model;

import android.os.AsyncTask;
import android.support.v4.media.MediaMetadataCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import dk.glutter.android.knr.Constants;
import dk.glutter.android.knr.utils.LogHelper;

/**
 * Utility class to get a list of MusicTrack's based on a server-side JSON
 * configuration.
 */
public class RemoteSource implements MusicProviderSource {

    private static final String TAG = LogHelper.makeLogTag(RemoteSource.class);

    public static String ID = "0";
    public static String TITLE = "title";
    public static String ALBUM = "album";
    public static String ARTIST = "artist";
    public static String GENRE = "genre";
    public static String SOURCE = "source";
    public static String IMAGE = "image";
    public static int TRACK_NUMBER = 0;
    public static long DURATION = 0;

    @Override
    public Iterator<MediaMetadataCompat> iterator() {
        try {
            ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();
            tracks.add(bliuldFirstMusicList());

            return tracks.iterator();
        } catch (Exception e) {
            LogHelper.e(TAG, e, "Could not retrieve music list");
            throw new RuntimeException("Could not retrieve music list", e);
        }
    }

    private MediaMetadataCompat bliuldFirstMusicList(){

        TITLE = Constants.DEFAULT_TITLE;
        ALBUM = Constants.DEFAULT_ALBUM;
        ARTIST = Constants.DEFAULT_ARTIST;
        GENRE = Constants.DEFAULT_GENRE;
        SOURCE = Constants.STREAM_URL;
        IMAGE = Constants.IMAGE_ROOT + Constants.DEFAULT_IMAGE;
        TRACK_NUMBER = Constants.DEFAULT_TRACK_NUMBER;
        DURATION = Constants.IMAGE_DURATION;

        // Since we don't have a unique ID in the server, we fake one using the hashcode of
        // the music source. In a real world app, this could come from the server.
        ID = String.valueOf(SOURCE.hashCode());

        // Adding the music source to the MediaMetadata (and consequently using it in the
        // mediaSession.setMetadata) is not a good idea for a real world music app, because
        // the session metadata can be accessed by notification listeners. This is done in this
        // sample for convenience only.
        //noinspection ResourceType
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, ID)
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

    public void getXmlFromUrl(String url) {
        try {
            new DownloadXmlTask().execute(url);
        } catch (Exception e) {
            LogHelper.e("KNR", "Failed getting XML: ", e.getMessage());
        }

    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        java.net.URL url = new URL(urlString);
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

    public static boolean metaIsChanged = false;

    public class DownloadXmlTask extends AsyncTask<String, Void, Item> {

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
            if (result != null) {


                ID = result.getId();
                TITLE = result.getTitle();
                ALBUM = result.getAlbum();
                ARTIST = result.getArtist();
                //GENRE = Constants.DEFAULT_GENRE;
                //SOURCE = Constants.STREAM_URL;

                if (result.getPicture() != null && !result.getPicture().equals("none")) {
                    IMAGE = Constants.IMAGE_ROOT + result.getPicture();
                }
                //TRACK_NUMBER = Constants.DEFAULT_TRACK_NUMBER;
                try {
                    DURATION = Long.parseLong(result.getDuration());
                }

                catch (NumberFormatException n){
                    LogHelper.i("PostExecute", "DATA DURATION = ", n.getMessage());
                }

                IMAGE = IMAGE.replace(" ", "%20");

                LogHelper.i("PostExecute", "DATA ID = ", ID);
                LogHelper.i("PostExecute", "DATA Title = ", TITLE);
                LogHelper.i("PostExecute", "DATA Album = ", ALBUM);
                LogHelper.i("PostExecute", "DATA Artist = ", ARTIST);
                LogHelper.i("PostExecute", "DATA Duration = ", DURATION);



                try {
                    LogHelper.i("PostExecute", "try");
                    metaIsChanged = false;
                    if (items.size() > 0)
                    {
                        LogHelper.i("PostExecute", "List Size > 0");
                        if (!items.get(items.size() - 1).getId().equals(result.getId()))
                        {
                            LogHelper.i("PostExecute", "List Elements are different");
                            items.add(result);
                            metaIsChanged = true;
                        }
                    }
                    if (items.size() == 0) {
                        LogHelper.i("PostExecute", "List Size = 0");
                        items.add(result);
                        metaIsChanged = true;
                    }
                } catch (Exception e) {
                    LogHelper.e("onPostExecute", "getXmlFromUrl(URL)", e.getMessage());
                }
            }
        }
    }


}