package com.example.kanjuice;


import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.*;

public interface JuiceServer {

    @GET("/api/beverages/")
    public void getJuices(Callback<List<Juice>> cb);

    @GET("/api/users/{cardNumber}")
    public void getUserByCardNumber(@Path("cardNumber") int cardNumber, Callback<User> cb);

    @GET("/api/users/{euid}")
    public void getUserByEuid(@Path("euid") String euid, Callback<User> cb);

    @FormUrlEncoded
    @POST("/api/orders")
    public void placeOrder(@Body Order order, Callback<Response> cb);
}
