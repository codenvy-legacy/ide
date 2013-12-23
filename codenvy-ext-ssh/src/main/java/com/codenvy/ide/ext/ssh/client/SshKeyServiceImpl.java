/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.ssh.client;

import com.codenvy.ide.MimeType;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.ssh.dto.GenKeyRequest;
import com.codenvy.ide.ext.ssh.dto.KeyItem;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link SshKeyService}.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshService May 18, 2011 4:49:49 PM evgen $
 */
@Singleton
public class SshKeyServiceImpl implements SshKeyService {
    private final String                    restContext;
    private final Loader                    loader;
    private final String                    wsName;
    private final DtoFactory                dtoFactory;
    private       StringMap<SshKeyProvider> sshKeyProviders;

    /**
     * Create service.
     *
     * @param restContext
     * @param loader
     */
    @Inject
    protected SshKeyServiceImpl(@Named("restContext") String restContext, Loader loader, DtoFactory dtoFactory) {
        this.restContext = restContext;
        this.loader = loader;
        this.wsName = '/' + Utils.getWorkspaceName();
        this.dtoFactory = dtoFactory;
        this.sshKeyProviders = Collections.createStringMap();
    }

    /** {@inheritDoc} */
    @Override
    public void getAllKeys(@NotNull AsyncRequestCallback<JavaScriptObject> callback) throws RequestException {
        loader.setMessage("Getting SSH keys....");
        loader.show();
        AsyncRequest.build(RequestBuilder.GET, restContext + wsName + "/ssh-keys/all").send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void generateKey(@NotNull String host, @NotNull AsyncRequestCallback<GenKeyRequest> callback) throws RequestException {
        String url = restContext + wsName + "/ssh-keys/gen";

        GenKeyRequest keyRequest = dtoFactory.createDto(GenKeyRequest.class).withHost(host);

        loader.setMessage("Generate keys for " + host);
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(dtoFactory.toJson(keyRequest))
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getPublicKey(@NotNull KeyItem keyItem, @NotNull AsyncRequestCallback<JavaScriptObject> callback) throws RequestException {
        loader.setMessage("Getting public SSH key for " + keyItem.getHost());
        loader.show();
        AsyncRequest.build(RequestBuilder.GET, keyItem.getPublicKeyUrl()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteKey(@NotNull KeyItem keyItem, @NotNull AsyncRequestCallback<Void> callback)  throws RequestException {
        loader.setMessage("Deleting SSH keys for " + keyItem.getHost());
        loader.show();
        AsyncRequest.build(RequestBuilder.GET, keyItem.getRemoteKeyUrl()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public StringMap<SshKeyProvider> getSshKeyProviders() {
        return sshKeyProviders;
    }

    /** {@inheritDoc} */
    @Override
    public void registerSshKeyProvider(@NotNull String host, @NotNull SshKeyProvider sshKeyProvider) {
        sshKeyProviders.put(host, sshKeyProvider);
    }
}