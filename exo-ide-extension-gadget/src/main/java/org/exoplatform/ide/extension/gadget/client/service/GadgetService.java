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
package org.exoplatform.ide.extension.gadget.client.service;

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
import org.exoplatform.ide.extension.gadget.shared.Gadget;
import org.exoplatform.ide.extension.gadget.shared.TokenRequest;
import org.exoplatform.ide.extension.gadget.shared.TokenResponse;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class GadgetService {

    private static GadgetService instance;

    public static GadgetService getInstance() {
        return instance;
    }

    private Loader loader;

    private String restServiceContext;

    private String gadgetServer;

    public GadgetService(Loader loader, String gadgetServer, String publicContext) {
        this.loader = loader;
        this.restServiceContext = Utils.getRestContext();
        this.gadgetServer = gadgetServer;
        instance = this;
    }

    public void getGadgetMetadata(String gadgetUrl, AsyncRequestCallback<Gadget> callback) throws RequestException {
        String data =
                "{\"context\":{\"country\":\"default\",\"language\":\"default\",\"view\":\"default\",\"container\":\"default\"}," +
                "\"gadgets\":["
                + "{\"moduleId\":" + 0L + ",\"url\":\"" + gadgetUrl + "\",\"prefs\":[]}]}";

        String url = gadgetServer + "metadata";
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(data).send(callback);
    }

    //TODO : temporary don't call this method
    public void getSecurityToken(TokenRequest request, AsyncRequestCallback<TokenResponse> callback)
            throws RequestException {
        String tokenRequest = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(request)).getPayload();

        String url = restServiceContext + Utils.getWorkspaceName() + "/shindig/securitytoken/createToken";
        AsyncRequest.build(RequestBuilder.POST, url).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(tokenRequest).send(callback);
    }

}
