# Borter

Borter is a reactive translation library for Java/Android. 

- It's dead simple to integrate/use. 
- You can switch from one API to another with next to zero effort
- It returns RxJava `Observable` objects

## Getting started

Build your borter instance

```
Borter borter = new Borter.Builder()
    .withTranslator(new MicrosoftTranslator(AZURE_CLIENTID, AZURE_CLIENTSECRET))
    .setDefaultTargetLanguage(Language.KLINGON)
    .build();
    
```  

Use it

```
Observable<String> translation = borter.translate("I am a dog!");
```

## Translators
By default, borter is just an empty shell that can't do much. In order to use it, you need at least one translator.
You can use one we provide or implement your own

### Microsoft (Bing) Translator API 

`[TBD]`


## Dependency

### Gradle

`[TBD]`

### Maven

`[TBD]`


