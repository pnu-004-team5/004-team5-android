package team5.class004.android.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import team5.class004.android.model.AbstractResponse;
import team5.class004.android.model.HabitItem;
import team5.class004.android.model.UserItem;

/**
 * Created by sunriv on 2017. 8. 31..
 */



public final class RestClient extends RestBaseClient
{
    //save the context recievied via constructor in a local variable
    public RestClient(Context context){
        super(context);
    }

    public ListAPI api()
    {
        return (ListAPI) retrofit(ListAPI.class);
    }

    public interface ListAPI
    {
        @FormUrlEncoded
        @POST("/habit")
        Call<HabitItem> createMyHabit(@FieldMap Map<String, String> params);
        @GET("/habits")
        Call<ArrayList<HabitItem>> getMyHabits(@QueryMap Map<String, String> params);
        @PUT("/habit")
        Call<HabitItem> doneMyHabit(@QueryMap Map<String, String> params);
        @DELETE("/habit")
        Call<HabitItem> deleteMyHabit(@QueryMap Map<String, String> params);

        @POST("/user")
        Call<UserItem> createUser(@QueryMap Map<String, String> params);
        @GET("/user")
        Call<UserItem> getUser(@QueryMap Map<String, String> params);
        @PUT("/user")
        Call<UserItem> updateUser(@QueryMap Map<String, String> params);
        @DELETE("/user")
        Call<UserItem> removeUser(@QueryMap Map<String, String> params);
    }
}