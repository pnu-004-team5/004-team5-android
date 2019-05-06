package team5.class004.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import team5.class004.android.model.UserItem;
import team5.class004.android.utils.RestClient;

public class GlobalApp extends Application {
    private final String TAG = Application.class.getSimpleName();
    private static volatile GlobalApp instance = null;
    public RestClient restClient;
    public SharedPreferences prefs;
    Gson gson = new Gson();
    public UserItem userItem = null;

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
        prefs = PreferenceManager.getDefaultSharedPreferences(getInstance());

//        prefs.edit().putString("user", null).apply();
        userItem = gson.fromJson(prefs.getString("user", null), UserItem.class);

    }
}
