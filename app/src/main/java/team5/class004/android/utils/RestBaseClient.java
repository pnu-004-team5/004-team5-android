package team5.class004.android.utils;

/**
 * Created by sunriv on 2017. 1. 10..
 */

import android.content.Context;
import android.util.Log;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import team5.class004.android.interfaces.AppConstants;

public class RestBaseClient
{
    private static Context context;

    //save the context recievied via constructor in a local variable
    public RestBaseClient(Context context){
        this.context=context;
    }

    protected static Object retrofit(Class<?> className)
    {
        // Add access_token query_string with all requests
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
//                        .addQueryParameter("access_token", GlobalApp.getInstance().getMyToken(GlobalApp.getInstance(), GlobalApp.getInstance().prefs))
                        .build();

                Request.Builder requestBuilder = original.newBuilder().url(url);
                Request request = requestBuilder.build();

                return chain.proceed(request);
            }
        };

        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
        OkHttpClient builder = new OkHttpClient().newBuilder()
                .connectTimeout(AppConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(AppConstants.READ_TIMEOUT, TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                .addInterceptor(interceptor)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.API_URL)
                .client(builder)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(className);
    }
}