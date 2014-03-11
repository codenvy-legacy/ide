package com.codenvy.ide.ext.java.client.format;

import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Roman Nikitenko
 */
public class FormatClientService {


    public static final String CODENVY = "/codenvy";

    private final AsyncRequestFactory asyncRequestFactory;
    private final String              baseHttpUrl;
    public final  String              formatServicePath;

    @Inject
    public FormatClientService(@Named("restContext") String baseHttpUrl,
                               @Named("workspaceId") String workspaceId,
                               AsyncRequestFactory asyncRequestFactory) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.formatServicePath = "/formattingSettings/" + workspaceId;
        this.baseHttpUrl = baseHttpUrl;
    }

    public void formattingCodenvySettings(AsyncRequestCallback<String> callback) {
        String url = baseHttpUrl + formatServicePath + CODENVY;
        asyncRequestFactory.createGetRequest(url).send(callback);
    }
}
