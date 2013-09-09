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
package org.exoplatform.ide.vfs.client.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.vfs.client.JSONDeserializer;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.ACLCapability;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.QueryCapability;

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

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        JSONObject jsonObject = JSONParser.parseLenient(response.getText()).isObject();
        virtualFileSystemInfo.setId(JSONDeserializer.STRING_DESERIALIZER.toObject(jsonObject.get("id")));
        virtualFileSystemInfo.setVersioningSupported(JSONDeserializer.BOOLEAN_DESERIALIZER.toObject(jsonObject
                                                                                                            .get("versioningSupported")))
        ; //
        virtualFileSystemInfo.setLockSupported(JSONDeserializer.BOOLEAN_DESERIALIZER.toObject(jsonObject
                                                                                                      .get("lockSupported"))); //
        virtualFileSystemInfo.setAnonymousPrincipal(JSONDeserializer.STRING_DESERIALIZER.toObject(jsonObject
                                                                                                          .get("anonymousPrincipal"))); //
        virtualFileSystemInfo.setAnyPrincipal(JSONDeserializer.STRING_DESERIALIZER.toObject(jsonObject
                                                                                                    .get("anyPrincipal"))); //
        virtualFileSystemInfo.setPermissions(JSONDeserializer.STRING_DESERIALIZER.toSet(jsonObject.get("permissions"))); //
        virtualFileSystemInfo.setAclCapability(ACLCapability.fromValue(JSONDeserializer.STRING_DESERIALIZER.toObject(
                jsonObject.get("aclCapability")).toLowerCase())); //
        virtualFileSystemInfo.setQueryCapability(QueryCapability.fromValue(JSONDeserializer.STRING_DESERIALIZER.toObject(
                jsonObject.get("queryCapability")).toLowerCase()));
        virtualFileSystemInfo.setUrlTemplates(JSONDeserializer.LINK_DESERIALIZER.toMap(jsonObject.get("urlTemplates")));

        JSONObject root = jsonObject.get("root").isObject();

        virtualFileSystemInfo.setRoot(new FolderModel(root));
    }

    @Override
    public VirtualFileSystemInfo getPayload() {
        return this.virtualFileSystemInfo;
    }

}
