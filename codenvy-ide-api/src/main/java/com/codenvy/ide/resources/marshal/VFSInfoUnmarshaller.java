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

import static com.codenvy.ide.resources.marshal.JSONDeserializer.BOOLEAN_DESERIALIZER;
import static com.codenvy.ide.resources.marshal.JSONDeserializer.LINK_DESERIALIZER;
import static com.codenvy.ide.resources.marshal.JSONDeserializer.STRING_DESERIALIZER;

/** @author Evgen Vidolob */
public class VFSInfoUnmarshaller implements Unmarshallable<VirtualFileSystemInfo> {
    private VirtualFileSystemInfo virtualFileSystemInfo;

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        JSONObject jsonObject = JSONParser.parseLenient(response.getText()).isObject();

        virtualFileSystemInfo = new VirtualFileSystemInfo();
        virtualFileSystemInfo.setId(STRING_DESERIALIZER.toObject(jsonObject.get("id")));
        virtualFileSystemInfo.setVersioningSupported(BOOLEAN_DESERIALIZER.toObject(jsonObject.get("versioningSupported")));
        virtualFileSystemInfo.setLockSupported(BOOLEAN_DESERIALIZER.toObject(jsonObject.get("lockSupported")));
        virtualFileSystemInfo.setAnonymousPrincipal(STRING_DESERIALIZER.toObject(jsonObject.get("anonymousPrincipal")));
        virtualFileSystemInfo.setAnyPrincipal(STRING_DESERIALIZER.toObject(jsonObject.get("anyPrincipal")));
        virtualFileSystemInfo.setPermissions(STRING_DESERIALIZER.toList(jsonObject.get("permissions")));
        virtualFileSystemInfo
                .setAclCapability(ACLCapability.fromValue(STRING_DESERIALIZER.toObject(jsonObject.get("aclCapability")).toLowerCase()));
        virtualFileSystemInfo.setQueryCapability(
                QueryCapability.fromValue(STRING_DESERIALIZER.toObject(jsonObject.get("queryCapability")).toLowerCase()));
        virtualFileSystemInfo.setUrlTemplates(LINK_DESERIALIZER.toMap(jsonObject.get("urlTemplates")));

        JSONObject root = jsonObject.get("root").isObject();
        virtualFileSystemInfo.setRoot(new Folder(root));
    }

    /** {@inheritDoc} */
    @Override
    public VirtualFileSystemInfo getPayload() {
        return this.virtualFileSystemInfo;
    }
}