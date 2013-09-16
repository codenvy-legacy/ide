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
package org.exoplatform.ide.extension.heroku.client.marshaller;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;

/**
 * Unmarshaller for stack migration response.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Aug 1, 2011 4:07:40 PM anya $
 */
public class StackMigrationUnmarshaller implements Unmarshallable<StackMigrationResponse> {
    /** Stack migration response. */
    private StackMigrationResponse stackMigrationResponse;

    /**
     * @param stackMigrationResponse
     *         stack migration response
     */
    public StackMigrationUnmarshaller(StackMigrationResponse stackMigrationResponse) {
        this.stackMigrationResponse = stackMigrationResponse;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        stackMigrationResponse.setResult(response.getText());
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public StackMigrationResponse getPayload() {
        return stackMigrationResponse;
    }

}
