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

package com.codenvy.ide.commons.exception;

import com.codenvy.ide.rest.HTTPHeader;
import com.google.gwt.http.client.Response;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@SuppressWarnings("serial")
public class ServerException extends Exception {

    private Response response;

    private String msg = "";

    private boolean errorMessageProvided;

    public ServerException(Response response) {
        this.response = response;
        this.msg = "";
        this.errorMessageProvided = checkErrorMessageProvided();
    }

    public ServerException(Response response, String msg) {
        this.response = response;
        this.msg = msg;
    }

    public int getHTTPStatus() {
        return response.getStatusCode();
    }

    public String getStatusText() {
        return response.getStatusText();
    }

    @Override
    public String getMessage() {
        if (response.getText().length() > 0)
            return msg + response.getText();
        else
            return msg + response.getStatusText();
    }

    public String getHeader(String key) {
        return response.getHeader(key);
    }

    private boolean checkErrorMessageProvided() {
        String value = response.getHeader(HTTPHeader.JAXRS_BODY_PROVIDED);
        if (value != null) {
            return true;
        }

        return false;
    }

    public boolean isErrorMessageProvided() {
        return errorMessageProvided;
    }
}
