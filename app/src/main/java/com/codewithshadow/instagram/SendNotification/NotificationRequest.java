package com.codewithshadow.instagram.SendNotification;






import com.codewithshadow.instagram.SendNotification.NotiModel.NotificationReq;
import com.codewithshadow.instagram.SendNotification.NotiModel.NotificationResponce;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationRequest {

    @Headers({"Content-Type:application/json","Authorization:key=AAAAWhgA1GU:APA91bGlRFnKXEPEyhps9wep4RYUxXGdMiqAwPpXUztR0ULqjfz2Ew9VtSgbI0TVoLbQEBPE_PkX0Yn0I1x1OY6nSFDPEd2M-Hv44PzL7YjjUuoggi7nhx6uXpQQzMFtnAlTmG3XEOY9"})
    @POST("send")
    Call<NotificationResponce> sent(@Body NotificationReq req);

}
