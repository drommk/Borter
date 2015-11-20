package com.drommk.borter.provider.microsoft.network;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by ericpalle on 11/12/15.
 */
public interface AzureTokenService {
    String DATAMARKET_OAUTH_URL = "https://datamarket.accesscontrol.windows.net/v2";
    String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    String SCOPE_TRANSLATOR = "http://api.microsofttranslator.com";


    @POST("/OAuth2-13")
    @FormUrlEncoded
    Observable<AzureToken> getToken(@Field("grant_type") String grantType,
                                    @Field("scope") String scope,
                                    @Field("client_id") String clientId,
                                    @Field("client_secret") String clientSecret);
}
