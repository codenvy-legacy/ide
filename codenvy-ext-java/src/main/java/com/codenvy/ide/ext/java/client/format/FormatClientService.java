package com.codenvy.ide.ext.java.client.format;

import elemental.js.util.Json;

import com.codenvy.ide.collections.js.JsoStringMap;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import static com.codenvy.ide.MimeType.APPLICATION_JSON;
import static com.codenvy.ide.rest.HTTPHeader.ACCEPT;

/**
 * @author Roman Nikitenko
 */
public class FormatClientService {


    public static final String CODENVY = "/codenvy";

    private final AsyncRequestFactory asyncRequestFactory;
    private final String                      baseHttpUrl;
    public final String                 FORMATSERVICEPATH;

    @Inject
    public FormatClientService(@Named("restContext") String baseHttpUrl,
                               @Named("workspaceId") String workspaceId,
                               AsyncRequestFactory asyncRequestFactory) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.FORMATSERVICEPATH = "/formattingSettings/" + workspaceId;
        this.baseHttpUrl = baseHttpUrl;
    }

    public void formattingCodenvySettings(AsyncRequestCallback<String> callback) {
        String url = baseHttpUrl + FORMATSERVICEPATH + CODENVY;
        asyncRequestFactory.createGetRequest(url).send(callback);
    }
}
