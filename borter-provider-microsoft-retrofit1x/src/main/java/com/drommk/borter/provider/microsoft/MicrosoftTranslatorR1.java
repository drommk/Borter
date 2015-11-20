package com.drommk.borter.provider.microsoft;

import com.drommk.borter.ClockService;
import com.drommk.borter.Language;
import com.drommk.borter.SystemClockService;
import com.drommk.borter.Translator;
import com.drommk.borter.provider.microsoft.network.AzureToken;
import com.drommk.borter.provider.microsoft.network.AzureTokenService;
import com.drommk.borter.provider.microsoft.network.MicrosoftTranslatorAPIService;
import com.squareup.okhttp.OkHttpClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by ericpalle on 11/12/15.
 */
public class MicrosoftTranslatorR1 implements Translator {
    public static final int EXPIRATION_SAFETY_DURATION = 10000;
    final String clientId;
    final String clientSecret;

    MicrosoftTranslatorAPIService translatorService;
    AzureTokenService azureTokenService;
    ClockService clockService;
    OkHttpClient okHttpClient;
    Logger logger;

    MicrosoftBorterToken borterToken;

    private String azureTokenServiceURL;
    private String translationServiceUrl;

    public MicrosoftTranslatorR1(String clientId, String clientSecret) {
        this(new Builder(clientId, clientSecret));
    }

    MicrosoftTranslatorR1(Builder builder) {
        clientId = builder.clientId;
        clientSecret = builder.clientSecret;
        borterToken = builder.azureToken;
        clockService = builder.clockService == null ? new SystemClockService() : builder.clockService;
        azureTokenServiceURL = builder.azureTokenServiceURL == null ? AzureTokenService.DATAMARKET_OAUTH_URL : builder.azureTokenServiceURL;
        translationServiceUrl = builder.translatorServiceURL == null ? MicrosoftTranslatorAPIService.TRANSLATION_SERVICE_URL : builder.translatorServiceURL;
        okHttpClient = builder.okHttpClient == null ? new OkHttpClient() : builder.okHttpClient;
        logger = LoggerFactory.getLogger("MicrosoftTranslator");
    }

    Observable<MicrosoftBorterToken> getCurrentToken() {
        if (isAzureTokenValid()) {
            return Observable.just(borterToken);
        }

        if (azureTokenService == null) {
            initAzureTokenService();
        }

        logger.debug("retrieving azure token from network...");
        return azureTokenService.getToken(AzureTokenService.GRANT_TYPE_CLIENT_CREDENTIALS, AzureTokenService.SCOPE_TRANSLATOR, clientId, clientSecret)
                .flatMap(new Func1<AzureToken, Observable<MicrosoftBorterToken>>() {
                    @Override
                    public Observable<MicrosoftBorterToken> call(AzureToken azureToken) {
                        logger.debug("successfully received token : {}", azureToken);
                        final int expiresInMillis = azureToken.expiresIn * 1000;
                        final int tokenLifeTime = expiresInMillis < EXPIRATION_SAFETY_DURATION ? expiresInMillis / 2 : expiresInMillis - EXPIRATION_SAFETY_DURATION;
                        borterToken = new MicrosoftBorterToken("Bearer " + azureToken.accessToken, clockService.getTimeInMillis() + tokenLifeTime);
                        return Observable.just(borterToken);
                    }
                });
    }

    @Override
    public Observable<String> translate(final String input, final Language toLanguage) {
        if (translatorService == null) {
            initTranslatorService();
        }

        return getCurrentToken().flatMap(new Func1<MicrosoftBorterToken, Observable<String>>() {
            @Override
            public Observable<String> call(MicrosoftBorterToken token) {
                logger.debug("translating '{}' in {} ", input, toLanguage);
                return translatorService.translate(token.accessToken, input, toLanguage.getCode());
            }
        });
    }

    private void initAzureTokenService() {
        logger.debug("initializing AzureTokenService");
        azureTokenService = new RestAdapter.Builder()
                .setEndpoint(azureTokenServiceURL)
                .setClient(new OkClient(okHttpClient))
                .build()
                .create(AzureTokenService.class);
    }

    private void initTranslatorService() {
        logger.debug("initializing TranslatorService");
        translatorService = new RestAdapter.Builder()
                .setEndpoint(translationServiceUrl)
                .setClient(new OkClient(okHttpClient))
                .build()
                .create(MicrosoftTranslatorAPIService.class);
    }

    boolean isAzureTokenValid() {

        final boolean isValid = borterToken != null
                && borterToken.accessToken != null
                && borterToken.localExpirationTime > clockService.getTimeInMillis();

        if (isValid) {
            logger.debug("instance holds a valid token : {}", borterToken);
        } else {
            logger.debug("could not find any valid token");
        }

        return isValid;
    }

    public static final class Builder {
        private final String clientId;
        private final String clientSecret;
        private ClockService clockService;
        private MicrosoftBorterToken azureToken;
        private String azureTokenServiceURL;
        private String translatorServiceURL;
        private OkHttpClient okHttpClient;

        public Builder(String clientId, String clientSecret) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        public Builder clockService(ClockService val) {
            clockService = val;
            return this;
        }

        public Builder token(MicrosoftBorterToken val) {
            azureToken = val;
            return this;
        }

        public Builder setOkHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        Builder azureTokenServiceURL(String val) {
            azureTokenServiceURL = val;
            return this;
        }

        Builder translatorServiceURL(String translatorServiceURL) {
            this.translatorServiceURL = translatorServiceURL;
            return this;
        }

        public MicrosoftTranslatorR1 build() {
            if (clientId == null || clientSecret == null) {
                throw new RuntimeException("you need to define both a client id & a secret");
            }
            return new MicrosoftTranslatorR1(this);
        }
    }
}
