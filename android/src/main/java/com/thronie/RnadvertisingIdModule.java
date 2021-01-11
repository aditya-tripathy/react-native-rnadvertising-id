package com.thronie;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.google.common.util.concurrent.*;
import androidx.ads.identifier.AdvertisingIdClient;
import androidx.ads.identifier.AdvertisingIdInfo;

public class RnadvertisingIdModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
     private Promise adIdResult;

    public RnadvertisingIdModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RnadvertisingId";
    }

    @ReactMethod
    public void getAdvertisingId(final Promise promise) {

        adIdResult = promise;
        if (AdvertisingIdClient.isAdvertisingIdProviderAvailable()) {
            ListenableFuture <AdvertisingIdInfo> advertisingIdInfoListenableFuture = AdvertisingIdClient.getAdvertisingIdInfo(reactContext);
            Futures.addCallback(advertisingIdInfoListenableFuture, new FutureCallback < AdvertisingIdInfo > () {
                
                @Override
                public void onSuccess(AdvertisingIdInfo adInfo) {

                    WritableMap map = Arguments.createMap();
                    map.putString("advertisingId", adInfo.getId());
                    map.putBoolean("isLimitAdTrackingEnabled", adInfo.isLimitTrackingEnabled());
                    speechTextPromise.resolve(map);
                    // Any exceptions thrown by getAdvertisingIdInfo()
                    // cause this method to get called.
                }

                @Override
                public void onFailure(Throwable throwable) {
                    adIdResult.reject("Failed", "Something Went Wrong");
                    // Try to connect to the Advertising ID provider again,
                    // or fall back to an ads solution that doesn't require
                    // using the Advertising ID library.
                };
            });
        }
        else {
            // The Advertising ID client library is unavailable. Use a different
            // library to perform any required ads use cases.
            adIdResult.reject("Failed", "The Advertising ID client library is unavailable.");
        }
        
    }
    
}
