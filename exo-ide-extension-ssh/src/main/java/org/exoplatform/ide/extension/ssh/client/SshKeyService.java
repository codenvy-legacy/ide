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
package org.exoplatform.ide.extension.ssh.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.ssh.shared.GenKeyRequest;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;
import org.exoplatform.ide.extension.ssh.shared.ListKeyItem;
import org.exoplatform.ide.extension.ssh.shared.PublicKey;

import java.util.List;

/**
 * Client service for SSH key service.
 */
public class SshKeyService {

    private static SshKeyService instance;

    private final String restContext;

    private final Loader loader;

    public SshKeyService(String restContext, Loader loader) {
        this.restContext = restContext;
        this.loader = loader;
        instance = this;
    }

    public static SshKeyService get() {
        return instance;
    }

    /** Receive all ssh key, stored on server. */
    public void getAllKeys(AsyncRequestCallback<ListKeyItem> callback) throws RequestException {
        String url = restContext + Utils.getWorkspaceName() + "/ssh-keys/all";

        loader.setMessage("Fetching SSH keys...");
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** Generate new ssh key pare. */
    public void generateKey(String host, AsyncRequestCallback<Void> callback) throws RequestException {
        String url = restContext + Utils.getWorkspaceName() + "/ssh-keys/gen";

        GenKeyRequest genKeyRequestBean = SshKeyExtension.AUTO_BEAN_FACTORY.genKeyRequest().as();
        genKeyRequestBean.setHost(host);

        String genKeyRequest = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(genKeyRequestBean)).getPayload();

        loader.setMessage("Generate keys for " + genKeyRequestBean.getHost());
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(genKeyRequest)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** Get public ssh key. */
    public void getPublicKey(KeyItem keyItem, AsyncRequestCallback<PublicKey> callback) throws RequestException {
        String url = restContext + Utils.getWorkspaceName() + "/ssh-keys?host=" + keyItem.getHost();

        loader.setMessage("Fetching SSH keys...");
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** Delete ssh key. */
    public void deleteKey(KeyItem keyItem, AsyncRequestCallback<Void> callback) throws RequestException {
        String url = restContext + Utils.getWorkspaceName() + "/ssh-keys/remove";

        loader.setMessage("Remove key for " + keyItem.getHost());
        AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_FORM_URLENCODED)
                    .loader(loader).data("host=" + keyItem.getHost()).send(callback);
    }

}
