
package dk.glutter.android.knr;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;

import dk.glutter.android.knr.ui.FullScreenPlayerActivity;
import io.fabric.sdk.android.Fabric;

/**
 * The {@link Application} for the KNR application.
 */
public class KNRApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        String applicationId = getResources().getString(R.string.cast_application_id);
        VideoCastManager.initialize(
                getApplicationContext(),
                new CastConfiguration.Builder(applicationId)
                        .enableWifiReconnection()
                        .enableAutoReconnect()
                        .enableDebug()
                        .setTargetActivity(FullScreenPlayerActivity.class)
                        .build());
    }
}