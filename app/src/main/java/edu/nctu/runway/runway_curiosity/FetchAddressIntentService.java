package edu.nctu.runway.runway_curiosity;

import android.app.Service;
import android.content.Intent;
import android.location.Geocoder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Locale;

/**
 * Created by ZongLin Hsieh on 2018/2/5.
 */

public class FetchAddressIntentService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void onHandleIntent(Intent intent){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
    }
}
