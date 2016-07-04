package com.example.kanjuice;

import com.example.kanjuice.models.GCMToken;
import com.example.kanjuice.utils.TypedJsonString;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

public interface TokenServer{
    @POST("/device")
    public void send(@Body GCMToken orderJson, Callback<Response> cb);
}
