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
package org.exoplatform.gwtframework.commons.exception;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@SuppressWarnings("serial")
public class UnauthorizedException extends Exception {

    private Response response;

    private AsyncRequest request;

    public UnauthorizedException(Response response, AsyncRequest request) {
        super(response.getText());
        this.response = response;
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public AsyncRequest getRequest() {
        return request;
    }

}
