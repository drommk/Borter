package com.drommk.borter;

/**
 * Created by ericpalle on 11/13/15.
 */
public enum Language {
    ARABIC("ar"),
    BOSNIAN("bs_Latn"),
    BULGARIAN("bg"),
    CATALAN("ca"),
    CHINESE_SIMPLIFIED("zh-CHS"),
    CHINESE_TRADITIONAL("zh-CHT"),
    CZECH("cs"),
    DANISH("da"),
    DUTCH("nl"),
    ENGLISH("en"),
    ESTONIAN("et"),
    FINNISH("fi"),
    FRENCH("fr"),
    GERMAN("de"),
    GREEK("el"),
    HAITIAN_CREOLE("ht"),
    HEBREW("he"),
    HINDI("hi"),
    HMONG_DAW("mww"),
    HUNGARIAN("hu"),
    INDONESIAN("id"),
    ITALIAN("it"),
    JAPANESE("ja"),
    KISWAHILI("sw"),
    KLINGON("tlh"),
    KLINGON_pIqaD("tlh-Qaak"),
    KOREAN("ko"),
    LATVIAN("lv"),
    LITHUANIAN("lt"),
    MALAY("ms"),
    MALTESE("mt"),
    NORWEGIAN("no"),
    PERSIAN("fa"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    QUERETARO_OTOMI("otq"),
    ROMANIAN("ro"),
    RUSSIAN("ru"),
    SERBIAN_CYRILLIC("sr-Cyrl"),
    SERBIAN_LATIN("sr_Latn"),
    SLOVAK("sk"),
    SLOVENIAN("sl"),
    SPANISH("es"),
    SWEDISH("sv"),
    THAI("th"),
    TURKISH("tr"),
    UKRAINIAN("uk"),
    URDU("ur"),
    VIETNAMESE("vi"),
    WELSH("cy"),
    YUCATEC_MAYA("yua");

    String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
