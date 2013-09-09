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

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.git.shared.LogRequest;

/**
 * Marshaller for log request in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 14, 2011 5:42:15 PM anya $
 */
public class LogRequestMarshaller implements Marshallable, Constants {
    /** Log request. */
    private LogRequest logRequest;

    /**
     * @param logRequest log request
     */
    public LogRequestMarshaller(LogRequest logRequest) {
        this.logRequest = logRequest;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal() */
    @Override
    public String marshal() {
        JSONObject jsonObject = new JSONObject();
        return jsonObject.toString();
    }

}
