package com.prodev.moringaalumni.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAh9bTMc0:APA91bE3oHb40M_vq2fvMLSldI8LmGeFUPJjfIobbQQ1hgdVOM_eSjvDQ0bT2QEWNG30ZSFXT0Ht549FfX42G2oAzAZm7xh6tn2YxzUV240TwCV85rew8qG09rb9oDVnosI75geZCSsT"

    })
    @POST("form/send")
    Call<Response> sendNotification(@Body Sender body);


}
