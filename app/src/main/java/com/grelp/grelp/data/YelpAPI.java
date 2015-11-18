package com.grelp.grelp.data;

import android.support.annotation.NonNull;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.Map;

/**
 * Code sample for accessing the Yelp API V2.
 * <p/>
 * This program demonstrates the capability of the Yelp API version 2.0 by using the Search API to
 * query for businesses by a search term and location, and the Business API to query additional
 * information about the top result from the search query.
 * <p/>
 * <p/>
 * See <a href="http://www.yelp.com/developers/documentation">Yelp Documentation</a> for more info.
 */
public class YelpAPI {

    private static final String API_HOST = "api.yelp.com";
    private static final String BUSINESS_PATH = "/v2/business";

    private static final String CONSUMER_KEY = "R_9pJ-PZx1APpAXpUj3ncw";
    private static final String CONSUMER_SECRET = "IfhZLHF2LPdOpQCcBVYRmQqF_vc";
    private static final String TOKEN = "RPAihLIADmTo1TVg0RYs8jLE_0wJQIKY";
    private static final String TOKEN_SECRET = "aON-rQbAnXesYqJ-59K0DOQHrds";

    OAuthService service;
    Token accessToken;
    AsyncHttpClient client;

    /**
     * Setup the Yelp API OAuth credentials.
     *
     * @param consumerKey    Consumer key
     * @param consumerSecret Consumer secret
     * @param token          Token
     * @param tokenSecret    Token secret
     */
    private YelpAPI(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service =
                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(consumerKey)
                        .apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
        client = new AsyncHttpClient();
    }

    public static YelpAPI getInstance() {
        return new YelpAPI(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
    }

    /**
     * Creates and sends a request to the Business API by business ID.
     * <p/>
     * See <a href="http://www.yelp.com/developers/documentation/v2/business">Yelp Business API V2</a>
     * for more info.
     *
     * @param businessID <tt>String</tt> business ID of the requested business
     */
    public void searchByBusinessId(String businessID, AsyncHttpResponseHandler handler) {
        String url = getUrl(BUSINESS_PATH + "/" + businessID);
        RequestParams params = new RequestParams();
        signRequest(params, url);

        client.get(url, params, handler);
    }

    private void signRequest(RequestParams params, String url) {
        OAuthRequest oAuthRequest = createOAuthRequest(url);
        Map<String, String> oauthParameters = oAuthRequest.getOauthParameters();
        for (String header : oauthParameters.keySet()) {
            client.addHeader(header, oauthParameters.get(header));
        }
        client.addHeader(OAuthConstants.HEADER, oAuthRequest.getHeaders().get(OAuthConstants.HEADER));
    }

    /**
     * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
     *
     * @param url API endpoint to be queried
     * @return <tt>OAuthRequest</tt>
     */
    private OAuthRequest createOAuthRequest(String url) {
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        service.signRequest(accessToken, request);
        return request;
    }

    @NonNull
    private String getUrl(String path) {
        return "https://" + API_HOST + path;
    }
}