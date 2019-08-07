package com.rasel.smsread.retrofitnetwork;


import com.rasel.smsread.ForgetPasswordResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiEndPoint {

    @FormUrlEncoded
    @POST("forgot-password/")
    Call<ForgetPasswordResponse> forgetpassword(
            @Field("username") String username
    );
}
