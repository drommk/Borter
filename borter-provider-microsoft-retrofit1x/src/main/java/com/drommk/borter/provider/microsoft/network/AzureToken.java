package com.drommk.borter.provider.microsoft.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ericpalle on 11/13/15.
 */
public class AzureToken {
    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("expires_in")
    public Integer expiresIn;

    @SerializedName("scope")
    public String scope;

    @SerializedName("token_type")
    public String tokenType;

    @Override
    public String toString() {
        return "AzureToken{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", scope='" + scope + '\'' +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
}
