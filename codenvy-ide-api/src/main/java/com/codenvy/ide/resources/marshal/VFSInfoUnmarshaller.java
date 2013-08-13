/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package com.codenvy.ide.resources.marshal;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.VirtualFileSystemInfo;
import com.codenvy.ide.resources.model.VirtualFileSystemInfo.ACLCapability;
import com.codenvy.ide.resources.model.VirtualFileSystemInfo.QueryCapability;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;


/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: VFSInfoUnmarshaller Feb 2, 2011 2:16:15 PM evgen $
 */
public class VFSInfoUnmarshaller implements Unmarshallable<VirtualFileSystemInfo> {

    private final VirtualFileSystemInfo virtualFileSystemInfo;

    /** @param virtualFileSystemInfo */
    public VFSInfoUnmarshaller(VirtualFileSystemInfo virtualFileSystemInfo) {
        this.virtualFileSystemInfo = virtualFileSystemInfo;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        JSONObject jsonObject = JSONParser.parseLenient(response.getText()).isObject();
        virtualFileSystemInfo.setId(JSONDeserializer.STRING_DESERIALIZER.toObject(jsonObject.get("id")));
        virtualFileSystemInfo.setVersioningSupported(JSONDeserializer.BOOLEAN_DESERIALIZER.toObject(jsonObject
                                                                                                            .get("versioningSupported")));
        virtualFileSystemInfo.setLockSupported(JSONDeserializer.BOOLEAN_DESERIALIZER.toObject(jsonObject
                                                                                                      .get("lockSupported"))); //
        virtualFileSystemInfo.setAnonymousPrincipal(JSONDeserializer.STRING_DESERIALIZER.toObject(jsonObject
                                                                                                          .get("anonymousPrincipal"))); //
        virtualFileSystemInfo.setAnyPrincipal(JSONDeserializer.STRING_DESERIALIZER.toObject(jsonObject
                                                                                                    .get("anyPrincipal"))); //
        virtualFileSystemInfo.setPermissions(JSONDeserializer.STRING_DESERIALIZER.toList(jsonObject.get("permissions"))); //
        virtualFileSystemInfo.setAclCapability(ACLCapability.fromValue(JSONDeserializer.STRING_DESERIALIZER.toObject(
                jsonObject.get("aclCapability")).toLowerCase())); //
        virtualFileSystemInfo.setQueryCapability(QueryCapability.fromValue(JSONDeserializer.STRING_DESERIALIZER.toObject(
                jsonObject.get("queryCapability")).toLowerCase()));
        virtualFileSystemInfo.setUrlTemplates(JSONDeserializer.LINK_DESERIALIZER.toMap(jsonObject.get("urlTemplates")));

        JSONObject root = jsonObject.get("root").isObject();

        virtualFileSystemInfo.setRoot(new Folder(root));
    }

    /** {@inheritDoc} */
    @Override
    public VirtualFileSystemInfo getPayload() {
        return this.virtualFileSystemInfo;
    }
}