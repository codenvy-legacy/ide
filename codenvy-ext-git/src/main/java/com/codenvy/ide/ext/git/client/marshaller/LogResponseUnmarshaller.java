/*
 * Copyright (C) 2011 eXo Platform SAS.
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
 */
package com.codenvy.ide.ext.git.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 14, 2011 4:10:34 PM anya $
 */
public class LogResponseUnmarshaller implements Unmarshallable<LogResponse> {
    /** Log response. */
    private DtoClientImpls.LogResponseImpl logResponse;

    /**
     * @param logResponse
     *         log response
     */
    public LogResponseUnmarshaller(DtoClientImpls.LogResponseImpl logResponse) {
        this.logResponse = logResponse;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        JSONObject logObject = JSONParser.parseStrict(text).isObject();
        String value = logObject.toString();
        DtoClientImpls.LogResponseImpl logResponse = DtoClientImpls.LogResponseImpl.deserialize(value);

        this.logResponse.setTextLog(logResponse.getTextLog());
        this.logResponse.setCommits(logResponse.getCommits());
    }

    /** {@inheritDoc} */
    @Override
    public LogResponse getPayload() {
        return logResponse;
    }
}