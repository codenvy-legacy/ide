/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
