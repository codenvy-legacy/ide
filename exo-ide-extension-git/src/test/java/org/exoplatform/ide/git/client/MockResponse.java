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
package org.exoplatform.ide.git.client;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 27, 2011 11:42:28 AM anya $
 */
public class MockResponse extends Response {
    private String text;

    /**
     *
     */
    public MockResponse(String text) {
        this.text = text;
    }

    /** @see com.google.gwt.http.client.Response#getHeader(java.lang.String) */
    @Override
    public String getHeader(String header) {
        return "";
    }

    /** @see com.google.gwt.http.client.Response#getHeaders() */
    @Override
    public Header[] getHeaders() {
        return new Header[0];
    }

    /** @see com.google.gwt.http.client.Response#getHeadersAsString() */
    @Override
    public String getHeadersAsString() {
        return "";
    }

    /** @see com.google.gwt.http.client.Response#getStatusCode() */
    @Override
    public int getStatusCode() {
        return 0;
    }

    /** @see com.google.gwt.http.client.Response#getStatusText() */
    @Override
    public String getStatusText() {
        return "";
    }

    /** @see com.google.gwt.http.client.Response#getText() */
    @Override
    public String getText() {
        return text;
    }
}
