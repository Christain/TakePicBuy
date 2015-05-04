package com.unionbigdata.takepicbuy;

import android.app.Application;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.unionbigdata.takepicbuy.utils.PhoneManager;

import java.io.File;

/**
 * Created by Christain on 15/4/18.
 */
public class TakePicBuyApplication extends Application {

    private static TakePicBuyApplication INSTANCE;
    private boolean isCheckViersion = false;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        /*******初始化Facebook Fresco********/
        DiskCacheConfig mainDiskCacheConfig = DiskCacheConfig.newBuilder()
                .setBaseDirectoryPath(new File(PhoneManager.getAppRootPath()))
                .setBaseDirectoryName("cache")
                .setMaxCacheSize(50 * 1024 * 1024)
                .setMaxCacheSizeOnLowDiskSpace(30 * 1024 * 1024)
                .setMaxCacheSizeOnVeryLowDiskSpace(15 * 1024 * 1024)
                .setVersion(1)
                .build();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(INSTANCE)
//                .setBitmapMemoryCacheParamsSupplier(bitmapCacheParamsSupplier)
//                .setCacheKeyFactory(cacheKeyFactory)
//                .setEncodedMemoryCacheParamsSupplier(encodedCacheParamsSupplier)
//                .setExecutorSupplier(executorSupplier)
//                .setImageCacheStatsTracker(imageCacheStatsTracker)
                .setMainDiskCacheConfig(mainDiskCacheConfig)
//                .setMemoryTrimmableRegistry(memoryTrimmableRegistry)
//                .setNetworkFetchProducer(networkFetchProducer)
//                .setPoolFactory(poolFactory)
//                .setProgressiveJpegConfig(progressiveJpegConfig)
//                .setRequestListeners(requestListeners)
//                .setSmallImageDiskCacheConfig(smallImageDiskCacheConfig)
                .build();
        Fresco.initialize(INSTANCE, config);

    }

    public static synchronized TakePicBuyApplication getInstance() {
        return INSTANCE;
    }

    public boolean isCheckViersion() {
        return isCheckViersion;
    }

    public void setCheckViersion(boolean isCheckViersion) {
        this.isCheckViersion = isCheckViersion;
    }
}
