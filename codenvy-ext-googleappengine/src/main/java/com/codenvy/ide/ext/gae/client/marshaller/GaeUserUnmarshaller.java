/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.gae.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.gae.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.gae.shared.GaeUser;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class GaeUserUnmarshaller implements Unmarshallable<GaeUser> {
    private DtoClientImpls.GaeUserImpl gaeUser;

    public GaeUserUnmarshaller(DtoClientImpls.GaeUserImpl gaeUser) {
        this.gaeUser = gaeUser;
    }

    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        JSONObject gaeObject = JSONParser.parseStrict(text).isObject();
        if (gaeObject == null) {
            return;
        }

        DtoClientImpls.GaeUserImpl dtoGaeUser = DtoClientImpls.GaeUserImpl.deserialize(gaeObject.toString());
        gaeUser.setId(dtoGaeUser.getId());
        gaeUser.setEmail(dtoGaeUser.getEmail());
        gaeUser.setToken(dtoGaeUser.getToken());
    }

    @Override
    public GaeUser getPayload() {
        return gaeUser;
    }
}
