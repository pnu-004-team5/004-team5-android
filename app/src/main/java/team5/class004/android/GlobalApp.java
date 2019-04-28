package team5.class004.android;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import team5.class004.android.utils.RestClient;

public class GlobalApp extends Application {
    private final String TAG = Application.class.getSimpleName();
    private static volatile GlobalApp instance = null;
    public RestClient restClient;
    public SharedPreferences prefs;
    Gson gson = new Gson();

    /**
     * singleton 애플리케이션 객체를 얻는다.
     *
     * @return singleton 애플리케이션 객체
     */
    public static GlobalApp getInstance() {
        if (instance == null) {
            synchronized (GlobalApp.class) {
                if (instance == null) {
                    instance = new GlobalApp();
                }
            }
        }
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        restClient = new RestClient(this);
    }
}
