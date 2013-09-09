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

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.git.shared.InitRequest;

/**
 * Marshaller for creation request in JSON format for {@link InitRequest}.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 24, 2011 11:48:40 AM anya $
 */
public class InitRequestMarshaller implements Marshallable, Constants {
    /** Initialize repository request. */
    private InitRequest initRequest;

    /**
     * @param initRequest initialize repository request
     */
    public InitRequestMarshaller(InitRequest initRequest) {
        this.initRequest = initRequest;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal() */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(WORKNG_DIR, new JSONString(initRequest.getWorkingDir()));
        jsonObject.put(BARE, JSONBoolean.getInstance(initRequest.isBare()));
        return jsonObject.toString();
    }

}
