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
package org.exoplatform.ide.git.client.marshaller;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.git.shared.RemoteUpdateRequest;

/**
 * Marshaller for update remote repository request in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 19, 2011 12:16:57 PM anya $
 */
public class RemoteUpdateRequestMarshaller implements Marshallable, Constants {

    /** Update remote repository request. */
    private RemoteUpdateRequest remoteUpdateRequest;

    /**
     * @param remoteUpdateRequest update remote repository request
     */
    public RemoteUpdateRequestMarshaller(RemoteUpdateRequest remoteUpdateRequest) {
        this.remoteUpdateRequest = remoteUpdateRequest;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal() */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(NAME, new JSONString(remoteUpdateRequest.getName()));
        // TODO

        return jsonObject.toString();
    }

}
