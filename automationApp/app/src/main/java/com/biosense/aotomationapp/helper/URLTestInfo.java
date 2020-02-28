package com.biosense.aotomationapp.helper;

import com.biosense.aotomationapp.model.Json_model;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by maniteja on 9/18/17.
 */

public interface URLTestInfo
{
   // @POST()
   // public Call<Json_model> postData(@Body Json_model patientInfo_model);

    @POST()
    public Call<Json_model> profilePicture(@Url String url, @Body Json_model patientInfo_model);
}
