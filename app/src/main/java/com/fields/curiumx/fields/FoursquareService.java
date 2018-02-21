package com.fields.curiumx.fields;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

 //FoursquareService provides a Retrofit interface for the Foursquare API.

public interface FoursquareService {

    // A request to search for nearby coffee shop recommendations via the Foursquare API.



    @GET("search/recommendations?v=20161101&intent=soccer")
    Call<FoursquareJSON> searchFields(@Query("client_id") String clientID,
                                      @Query("client_secret") String clientSecret,
                                      @Query("ll") String ll,
                                      @Query("llAcc") double llAcc,
                                      @Query("categoryId") String categoryId);


}
