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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.ssh.dto.GenKeyRequest;
import com.codenvy.ide.ext.ssh.dto.KeyItem;
import com.codenvy.ide.ext.ssh.dto.PublicKey;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.ui.loader.Loader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link SshKeyService}.
 *
 * @author Evgen Vidolob
 */
@Singleton
public class SshKeyServiceImpl implements SshKeyService {
    private final String                    baseUrl;
    private final String                    workspaceId;
    private final Loader                    loader;
    private final DtoFactory                dtoFactory;
    private final AsyncRequestFactory       asyncRequestFactory;
    private final StringMap<SshKeyProvider> sshKeyProviders;

    @Inject
    protected SshKeyServiceImpl(@Named("restContext") String baseUrl, @Named("workspaceId") String workspaceId, Loader loader,
                                DtoFactory dtoFactory, AsyncRequestFactory asyncRequestFactory) {
        this.baseUrl = baseUrl;
        this.workspaceId = workspaceId;
        this.loader = loader;
        this.dtoFactory = dtoFactory;
        this.asyncRequestFactory = asyncRequestFactory;
        this.sshKeyProviders = Collections.createStringMap();
    }

    /** {@inheritDoc} */
    @Override
    public void getAllKeys(@NotNull AsyncRequestCallback<Array<KeyItem>> callback) {
        loader.setMessage("Getting SSH keys....");
        loader.show();
        asyncRequestFactory.createGetRequest(baseUrl + "/ssh-keys/" + workspaceId + "/all").send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void generateKey(@NotNull String host, @NotNull AsyncRequestCallback<GenKeyRequest> callback) {
        String url = baseUrl + "/ssh-keys/" + workspaceId + "/gen";

        GenKeyRequest keyRequest = dtoFactory.createDto(GenKeyRequest.class).withHost(host);

        loader.setMessage("Generate keys for " + host);
        asyncRequestFactory.createPostRequest(url, keyRequest).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getPublicKey(@NotNull KeyItem keyItem, @NotNull AsyncRequestCallback<PublicKey> callback) {
        loader.setMessage("Getting public SSH key for " + keyItem.getHost());
        loader.show();
        asyncRequestFactory.createGetRequest(keyItem.getPublicKeyUrl()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteKey(@NotNull KeyItem keyItem, @NotNull AsyncRequestCallback<Void> callback) {
        loader.setMessage("Deleting SSH keys for " + keyItem.getHost());
        loader.show();
        asyncRequestFactory.createGetRequest(keyItem.getRemoteKeyUrl()).send(callback);
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