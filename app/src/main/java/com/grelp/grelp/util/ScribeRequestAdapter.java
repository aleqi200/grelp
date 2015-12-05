package com.grelp.grelp.util;

import android.net.Uri;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.scribe.model.OAuthRequest;
import org.scribe.model.ParameterList;
import org.scribe.model.Verb;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ScribeRequestAdapter extends OAuthRequest {
    private HttpUriRequest httpUriRequest;
    private HashMap<String, String> oauthParameters;

    public ScribeRequestAdapter(HttpUriRequest httpUriRequest) {
        super(Verb.GET, httpUriRequest.getURI().toString());
        this.httpUriRequest = httpUriRequest;
        this.oauthParameters = new HashMap();
    }

    public void addOAuthParameter(String key, String value) {
        this.oauthParameters.put(key, value);
    }

    public Map<String, String> getOauthParameters() {
        return this.oauthParameters;
    }

    public void addHeader(String key, String value) {
        this.httpUriRequest.addHeader(key, value);
    }

    public void addQuerystringParameter(String key, String value) {
        if(key.equals("access_token")) {
            this.addQuerystringParameter("oauth_token", value);
        }

        Uri updatedUri = Uri.parse(this.httpUriRequest.getURI().toString()).buildUpon().appendQueryParameter(key, value).build();
        ((HttpRequestBase)this.httpUriRequest).setURI(URI.create(updatedUri.toString()));
    }

    public ParameterList getQueryStringParams() {
        try {
            return this.parseQueryParams();
        } catch (UnsupportedEncodingException var2) {
            return new ParameterList();
        }
    }

    public ParameterList getBodyParams() {
        return this.getVerb() != Verb.GET && this.getVerb() != Verb.DELETE?this.parseEntityParams():new ParameterList();
    }

    public String getCompleteUrl() {
        return this.getHttpRequest().getURI().toString();
    }

    public String getSanitizedUrl() {
        return this.getCompleteUrl().replaceAll("\\?.*", "").replace("\\:\\d{4}", "");
    }

    public Verb getVerb() {
        return Verb.valueOf(this.getHttpRequest().getMethod());
    }

    public String toString() {
        return String.format("@Request(%s %s)", new Object[]{this.getVerb(), this.getCompleteUrl()});
    }

    protected HttpUriRequest getHttpRequest() {
        return this.httpUriRequest;
    }

    private ParameterList parseEntityParams() {
        HttpEntity entity = null;
        ArrayList parameters = null;

        try {
            entity = ((HttpEntityEnclosingRequestBase)this.httpUriRequest).getEntity();
            parameters = new ArrayList(URLEncodedUtils.parse(entity));
        } catch (Exception var6) {
            return new ParameterList();
        }

        ParameterList list = new ParameterList();
        Iterator var5 = parameters.iterator();

        while(var5.hasNext()) {
            NameValuePair pair = (NameValuePair)var5.next();
            list.add(pair.getName(), pair.getValue());
        }

        return list;
    }

    private ParameterList parseQueryParams() throws UnsupportedEncodingException {
        ParameterList params = new ParameterList();
        String queryString = URI.create(this.getCompleteUrl()).getQuery();
        if(queryString == null) {
            return params;
        } else {
            String[] var6;
            int var5 = (var6 = queryString.split("&")).length;

            for(int var4 = 0; var4 < var5; ++var4) {
                String param = var6[var4];
                String[] pair = param.split("=");
                String key = URLDecoder.decode(pair[0], "UTF-8");
                String value = "";
                if(pair.length > 1) {
                    value = URLDecoder.decode(pair[1], "UTF-8");
                }

                params.add(new String(key), new String(value));
            }

            return params;
        }
    }

    public String getRealm() {
        return null;
    }
}
