/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [$today.year] Codenvy, S.A. 
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
package com.codenvy.ide.rest;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id:
 */

import com.google.gwt.http.client.Response;

/** Deserializer for responses body. */
public class StringUnmarshaller implements Unmarshallable<String> {
    protected String builder;

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) {
        builder = response.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getPayload() {
        return builder;
    }
}
