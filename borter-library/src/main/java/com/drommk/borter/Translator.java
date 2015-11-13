package com.drommk.borter;

import rx.Observable;

/**
 * Created by ericpalle on 11/12/15.
 */
public interface Translator {
    Observable<String> translate(String input, Language toLanguage);
}
