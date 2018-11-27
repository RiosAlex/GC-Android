package com.gascloud.www.gc;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {
    @GET("/posts")
    Call<ResponseBody> getPost();
}
