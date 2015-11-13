package com.drommk.borter.provider.microsoft;

import com.drommk.borter.ClockService;
import com.drommk.borter.Language;
import com.drommk.borter.provider.microsoft.network.AzureToken;
import com.google.gson.Gson;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;

import retrofit.HttpException;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by ericpalle on 11/13/15.
 */
public class MicrosoftTranslatorTest {

    public static final String TEST_CLIENT_ID = "test_client_id";
    public static final String TEST_CLIENT_SECRET = "test_client_secret";
    public static final String TEST_ACCESS_TOKEN = "test_access_token";
    public static final String TEST_SCOPE = "test_scope";

    public static final int TEST_EXPIRES_IN = 100;
    public static final String TEST_TOKEN_TYPE = "test_token_type";
    public static final String HTTP_BAD_REQUEST_RESPONSE = "{\n" +
            "            \"error\": \"invalid_client\",\n" +
            "                \"error_description\": \"ACS50012: Authentication failed.\\r\\nTrace ID: 1cbb053d-a57f-422d-a70f-dd96745b6b75\\r\\nCorrelation ID: 487f7b50-705e-4a7f-ac26-de033f805640\\r\\nTimestamp: 2015-11-14 17:47:42Z\"\n" +
            "        }";
    public static final String TEST_TRANSLATE_OUTPUT = new Gson().toJson("je suis un chien");
    public static final long TEST_LOCAL_EXPIRATION_TIME = 100L;
    public static final long TEST_CURRENT_TIME = 111L;

    MicrosoftTranslator translator;
    MockWebServer mockWebServer;

    public void setupMockWebServer(MockResponse... mockResponses) throws IOException {
        mockWebServer = new MockWebServer();
        for (MockResponse response : mockResponses) {
            mockWebServer.enqueue(response);
        }
        mockWebServer.start();
    }

    @After
    public void tearDown() throws Exception {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
        mockWebServer = null;
    }

    @Test
    public void testAzureTokenJustExpired() {
        //PREPARE
        MicrosoftBorterToken token = new MicrosoftBorterToken(TEST_ACCESS_TOKEN, TEST_LOCAL_EXPIRATION_TIME);
        final ClockService clockMock = mock(ClockService.class);
        when(clockMock.getTimeInMillis()).thenReturn(TEST_LOCAL_EXPIRATION_TIME + 1);
        translator = new MicrosoftTranslator.Builder(TEST_CLIENT_ID, TEST_CLIENT_SECRET)
                .token(token)
                .clockService(clockMock)
                .build();

        translator.clockService = clockMock;

        //EXECUTE
        boolean isValid = translator.isAzureTokenValid();

        //ASSERT
        assertFalse(isValid);
    }

    @Test
    public void testAzureTokenNotExpiredYet() {
        //PREPARE
        MicrosoftBorterToken token = new MicrosoftBorterToken(TEST_ACCESS_TOKEN, TEST_LOCAL_EXPIRATION_TIME);
        final ClockService clockMock = mock(ClockService.class);
        when(clockMock.getTimeInMillis()).thenReturn(TEST_LOCAL_EXPIRATION_TIME - 1);
        translator = new MicrosoftTranslator.Builder(TEST_CLIENT_ID, TEST_CLIENT_SECRET)
                .clockService(clockMock)
                .token(token)
                .build();

        translator.clockService = clockMock;

        //EXECUTE
        boolean isValid = translator.isAzureTokenValid();

        //ASSERT
        assertTrue(isValid);
    }

    @Test
    public void testGetCorrectToken() throws IOException {
        //PREPARE
        setupMockWebServer(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(new Gson().toJson(buildTestAzureToken())));

        ClockService clockMock = mock(ClockService.class);
        when(clockMock.getTimeInMillis()).thenReturn(TEST_CURRENT_TIME);

        translator = new MicrosoftTranslator.Builder(TEST_CLIENT_ID, TEST_CLIENT_SECRET)
                .azureTokenServiceURL(mockWebServer.url("/").toString())
                .clockService(clockMock)
                .build();
        TestSubscriber<MicrosoftBorterToken> testSubscriber = new TestSubscriber<>();

        //EXECUTE
        translator.getCurrentToken().subscribe(testSubscriber);

        //ASSERT
        testSubscriber.assertNoErrors();
        MicrosoftBorterToken expectedBorterToken = new MicrosoftBorterToken("Bearer " + TEST_ACCESS_TOKEN, TEST_CURRENT_TIME + TEST_EXPIRES_IN * 1000 - MicrosoftTranslator.EXPIRATION_SAFETY_DURATION);
        testSubscriber.assertValue(expectedBorterToken);
    }

    @Test
    public void testGetTokenInvalidClientId() throws IOException {
        //PREPARE
        setupMockWebServer(new MockResponse().setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST).setBody(HTTP_BAD_REQUEST_RESPONSE));
        TestSubscriber<MicrosoftBorterToken> testSubscriber = new TestSubscriber<>();
        translator = new MicrosoftTranslator.Builder("wrong_client_id", TEST_CLIENT_SECRET)
                .azureTokenServiceURL(mockWebServer.url("/").toString())
                .build();

        //EXECUTE
        translator.getCurrentToken().subscribe(testSubscriber);

        //ASSERT
        testSubscriber.assertError(HttpException.class);
    }

    @Test
    public void testTranslateStuffWithTokenAlreadySet() throws IOException {
        //PREPARE
        setupMockWebServer(new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK).setBody(TEST_TRANSLATE_OUTPUT));
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();

        ClockService clockMock = mock(ClockService.class);
        when(clockMock.getTimeInMillis()).thenReturn(TEST_LOCAL_EXPIRATION_TIME - 1);

        translator = new MicrosoftTranslator.Builder(TEST_CLIENT_ID, TEST_CLIENT_SECRET)
                .translatorServiceURL(mockWebServer.url("/").toString())
                .token(new MicrosoftBorterToken(TEST_ACCESS_TOKEN, TEST_LOCAL_EXPIRATION_TIME))
                .clockService(clockMock)
                .build();

        //EXECUTE
        translator.translate("doesn't matter, I'm mocked", Language.FRENCH).subscribe(testSubscriber);

        //ASSERT
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertReceivedOnNext(Arrays.asList("je suis un chien"));
    }

    private AzureToken buildTestAzureToken() {
        final AzureToken testAzureToken = new AzureToken();
        testAzureToken.expiresIn = TEST_EXPIRES_IN;
        testAzureToken.accessToken = TEST_ACCESS_TOKEN;
        testAzureToken.scope = TEST_SCOPE;
        testAzureToken.tokenType = TEST_TOKEN_TYPE;

        return testAzureToken;
    }
}
