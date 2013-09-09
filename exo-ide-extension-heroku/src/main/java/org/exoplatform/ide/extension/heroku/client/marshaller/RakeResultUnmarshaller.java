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
import org.exoplatform.ide.extension.heroku.client.rake.RakeCommandResult;

/**
 * Unmarshaller for the rake command execution result.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 20, 2011 9:46:43 AM anya $
 */
public class RakeResultUnmarshaller implements Unmarshallable<RakeCommandResult> {
    /** Rake command execution result. */
    private RakeCommandResult rakeCommandResult;

    /**
     * @param rakeCommandResult
     *         rake command execution result
     */
    public RakeResultUnmarshaller(RakeCommandResult rakeCommandResult) {
        this.rakeCommandResult = rakeCommandResult;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            rakeCommandResult.setResult(response.getText());
        } catch (Exception e) {
            throw new UnmarshallerException(e.getMessage());
        }
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public RakeCommandResult getPayload() {
        return rakeCommandResult;
    }

}
