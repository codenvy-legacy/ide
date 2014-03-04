package com.codenvy.ide.ext.java.client.format;

import com.codenvy.ide.collections.js.JsoStringMap;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.google.inject.Inject;

/**
 * @author Roman Nikitenko
 */
public class FormatClientService {

    public static final String FORMATSERVICEPATH = "formattingSettings";
    public static final String CODENVY = "/codenvy";

    private final AsyncRequestFactory asyncRequestFactory;
    private final String                      baseHttpUrl;


    @Inject
    public FormatClientService(String baseHttpUrl, AsyncRequestFactory asyncRequestFactory) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.baseHttpUrl = baseHttpUrl;
    }

    public void formattingCodenvySettings(AsyncRequestCallback<JsoStringMap<String>> callback) {
        String url = baseHttpUrl + FORMATSERVICEPATH + CODENVY;
        asyncRequestFactory.createGetRequest(url).send(callback);
    }
}
