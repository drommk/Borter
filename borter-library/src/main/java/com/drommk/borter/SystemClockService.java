package com.drommk.borter;

/**
 * Created by ericpalle on 11/15/15.
 */
public class SystemClockService implements ClockService {
    @Override
    public Long getTimeInMillis() {
        return System.currentTimeMillis();
    }
}
