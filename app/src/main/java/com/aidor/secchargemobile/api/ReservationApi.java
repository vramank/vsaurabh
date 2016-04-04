package com.aidor.secchargemobile.api;

import com.aidor.secchargemobile.model.BatteryStatusModel;
import com.aidor.secchargemobile.model.EditReservationModel;
import com.aidor.secchargemobile.model.Example;
import com.aidor.secchargemobile.model.Reservationdetail;
import com.aidor.secchargemobile.model.ServerTimeModel;
import com.aidor.secchargemobile.model.StartChargingModel;
import com.aidor.secchargemobile.model.UpdatedSocModel;
import com.aidor.secchargemobile.model.ViewReserveModel;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface ReservationApi {
    @GET("/reserve")
    void getReserveView(@Query("siteID") String siteId, @Query("userid") String userId,
                        Callback<ViewReserveModel> callback);

    @GET("/viewReserve")
    void getMyReservation(@Query("userid") String userId,
                          Callback<Example> callback);

    @GET("/reservationHistrory")
    void getMyReservationHistory(@Query("userid") String userId,
                                 Callback<Example> callback);


    @GET("/getCurrentServerTime")
    void getCurrentServerTime (Callback<ServerTimeModel> callback);

    @GET("/startCharging")
    void getStartCharging(@Query("userid") String userId, Callback<StartChargingModel> callback);

    @GET("/batteryStatus1")
    void getBatteryStatus(@Query("userid") String userId, @Query("BtnChargin") String charging,
                          Callback<BatteryStatusModel> callback);

    @GET("/updateSoc")
    void getUpdatedSOC(@Query("userid") String userId, Callback<UpdatedSocModel> callback);

    @GET("/editReserve")
    void getEditReservation(@Query("reservationId") String reservationId, @Query("reservedate") String reservationDate,
                            @Query("reservestarttime") String reservationStartTime, @Query("reserve_endtime") String reservationEndTime,
                            Callback<EditReservationModel> callback);

    @GET("/preauthorizedReservationId")
    void getReservationIdForCancelation(@Query("userid") String userId, Callback<Reservationdetail> callback);
}
