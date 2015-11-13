package com.drommk.borter.provider.microsoft;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ericpalle on 11/15/15.
 */
public class MicrosoftBorterToken {
    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("local_expiration_time")
    public Long localExpirationTime;

    MicrosoftBorterToken(String accessToken, Long localExpirationTime) {
        this.accessToken = accessToken;
        this.localExpirationTime = localExpirationTime;
    }

    @Override
    public String toString() {
        return "MicrosoftBorterToken{" +
                "accessToken='" + accessToken + '\'' +
                ", localExpirationTime=" + localExpirationTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MicrosoftBorterToken that = (MicrosoftBorterToken) o;

        if (accessToken != null ? !accessToken.equals(that.accessToken) : that.accessToken != null)
            return false;
        return !(localExpirationTime != null ? !localExpirationTime.equals(that.localExpirationTime) : that.localExpirationTime != null);

    }

    @Override
    public int hashCode() {
        int result = accessToken != null ? accessToken.hashCode() : 0;
        result = 31 * result + (localExpirationTime != null ? localExpirationTime.hashCode() : 0);
        return result;
    }
}
