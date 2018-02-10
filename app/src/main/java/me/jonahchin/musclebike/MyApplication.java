package me.jonahchin.musclebike;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by jonahchin on 2018-02-10.
 */

public class MyApplication extends Application {
    private static MyApplication ApplicationInstance;

    public static MyApplication getInstance(){
        return ApplicationInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationInstance = this;

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());
    }
}

