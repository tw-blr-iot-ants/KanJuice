package com.example.kanjuice;


import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.*;
import retrofit.mime.TypedString;

public interface JuiceServer {

    @GET("/api/beverages/")
    public void getJuices(Callback<List<Juice>> cb);

    @GET("/api/users/internalNumber/{cardNumber}")
    public void getUserByCardNumber(@Path("cardNumber") int cardNumber, Callback<User> cb);

    @GET("/api/users/empId/{euid}")
    public void getUserByEuid(@Path("euid") String euid, Callback<User> cb);

    @POST("/api/orders")
    public void placeOrder(@Body TypedJsonString orderJson, Callback<Response> cb);

    @PUT("/api/beverages/{id}")
    void updateJuice(@Path("id") String id, TypedString juice, Callback<Response> cb);
}
