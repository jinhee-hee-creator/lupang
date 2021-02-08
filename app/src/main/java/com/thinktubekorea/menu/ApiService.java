package com.thinktubekorea.menu;



import com.thinktubekorea.menu.model.Category;
import com.thinktubekorea.menu.model.Item;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiService {

//    //필수 항목 리스트
//    @GET("/requirement/{id}/list/sync")
//    Call<List<Requirement>> getRequirementList(@Header("Authorization") String authorization, @Path("id") long id, @Query("date") String date);

    @GET("/menu/category.json")
    Call<ResponseBody> getCategory();
    @GET("/menu/items.json")
    Call<ResponseBody> getItem();
}
