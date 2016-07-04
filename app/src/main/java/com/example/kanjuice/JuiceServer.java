package com.example.kanjuice;


import com.example.kanjuice.models.Juice;
import com.example.kanjuice.models.User;
import com.example.kanjuice.utils.TypedJsonString;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface JuiceServer {

    @GET("/api/beverages/juices")
    public void getJuices(Callback<List<Juice>> cb);

    @GET("/api/beverages/fruits")
    public void getFruits(Callback<List<Juice>> cb);

    @GET("/api/users/internalNumber/{cardNumber}")
    public void getUserByCardNumber(@Path("cardNumber") int cardNumber, Callback<User> cb);

    @GET("/api/users/empId/{euid}")
    public void getUserByEuid(@Path("euid") String euid, Callback<User> cb);

    @POST("/api/orders")
    public void placeOrder(@Body TypedJsonString orderJson, Callback<Response> cb);

    @POST("/api/beverages/updateWithUpsert")
    void updateJuice(@Body TypedJsonString body, Callback<Response> cb);

    @POST("/api/register/")
    public void register(@Body TypedJsonString userJson, Callback<Response> cb);

    @POST("/api/log/")
    public void saveLogData(@Body TypedJsonString message, Callback<Response> cb);

}
