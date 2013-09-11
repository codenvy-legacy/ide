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
package org.exoplatform.gwtframework.commons.rest;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;

/**
 * Unmarshaller for "Location" HTTP Header.
 * Uses in {@link AsyncRequest} for run REST Service Asynchronous
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Sep 16, 2011 evgen $
 */
public class LocationUnmarshaller implements Unmarshallable<StringBuilder> {

    private StringBuilder result;

    /** @param result */
    public LocationUnmarshaller(StringBuilder result) {
        super();
        this.result = result;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    public void unmarshal(Response response) throws UnmarshallerException {
        result.append(response.getHeader("Location"));
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#getPayload() */
    public StringBuilder getPayload() {
        return result;
    }

}
