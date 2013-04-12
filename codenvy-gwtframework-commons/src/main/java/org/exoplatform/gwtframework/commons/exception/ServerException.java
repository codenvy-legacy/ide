/**
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package org.exoplatform.gwtframework.commons.exception;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.rest.HTTPHeader;

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
