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
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.vfs.client.JSONDeserializer;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * Unmarshaller for the list of virtual file systems.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2011 9:41:55 AM anya $
 */
public class VFSListUnmarshaller implements Unmarshallable<List<VirtualFileSystemInfo>> {
    /** The list of virtual file systems. */
    private List<VirtualFileSystemInfo> vfsList;

    /**
     * @param vfsList
     *         the list of virtual file systems
     */
    public VFSListUnmarshaller(List<VirtualFileSystemInfo> vfsList) {
        this.vfsList = vfsList;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            JSONArray jsonArray = JSONParser.parseLenient(response.getText()).isArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                VirtualFileSystemInfo vfsInfo = JSONDeserializer.VFSINFO_DESERIALIZER.toObject(jsonArray.get(i));
                vfsList.add(vfsInfo);
            }
        } catch (Exception e) {
            throw new UnmarshallerException("Can't parse the list of virtual file systems.");
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public List<VirtualFileSystemInfo> getPayload() {
        return vfsList;
    }
}
