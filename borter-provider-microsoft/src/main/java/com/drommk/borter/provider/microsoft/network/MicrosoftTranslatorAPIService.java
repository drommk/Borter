package com.drommk.borter.provider.microsoft.network;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by ericpalle on 11/12/15.
 */
public interface MicrosoftTranslatorAPIService {
        String TRANSLATION_SERVICE_URL = "http://api.microsofttranslator.com/V2/Ajax.svc/";


    @GET("Translate")
    Observable<String> translate(@Query("appId") String token,
                           @Query("text") String inputText,
                           @Query("to") String toLanguage);
}
