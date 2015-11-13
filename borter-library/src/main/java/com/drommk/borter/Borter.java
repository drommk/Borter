package com.drommk.borter;

import rx.Observable;

/**
 * Created by ericpalle on 11/12/15.
 */
public class Borter {
    Translator translator;
    Language defaultTargetLanguage = Language.ENGLISH;

    public Observable<String> translate(String input) {
        return translator.translate(input, defaultTargetLanguage);
    }

    public Observable<String> translate(String input, Language targetLanguage) {
        return translator.translate(input, targetLanguage);
    }

    public static class Builder {
        private final Borter borter;

        public Builder() {
            borter = new Borter();
        }

        public Builder withTranslator(Translator translator) {
            borter.translator = translator;
            return this;
        }

        public Builder setDefaultTargetLanguage(Language defaultTargetLanguage) {
            borter.defaultTargetLanguage = defaultTargetLanguage;
            return this;
        }

        public Borter build() {
            if (borter.translator == null) {
                throw new RuntimeException("You need to define a translator");
            }

            return borter;
        }
    }

    Borter() {
        //nope
    }


}
