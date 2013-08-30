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
package org.exoplatform.ide.testframework.http;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class MockResponse extends Response {

    /** Response text. */
    private String text = new String();

    /** Status text. */
    private String statusText = new String();

    /** Status code. */
    private int statusCode;

    /** Headers. */
    private Header[] headers;

    /**
     *
     */
    public MockResponse() {
    }

    /**
     * @param text
     * @param statusText
     * @param statusCode
     * @param headers
     */
    public MockResponse(String text, String statusText, int statusCode, Header[] headers) {
        this.text = text;
        this.statusText = statusText;
        this.statusCode = statusCode;
        this.headers = headers;
    }

    /**
     * @param text
     * @param statusCode
     */
    public MockResponse(String text, int statusCode) {
        this.text = text;
        this.statusCode = statusCode;
    }

    /** @param text */
    public MockResponse(String text) {
        this.text = text;
    }

    /**
     * @param text
     *         the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @param statusText
     *         the statusText to set
     */
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    /**
     * @param statusCode
     *         the statusCode to set
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * @param headers
     *         the headers to set
     */
    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    @Override
    public String getHeader(String header) {
        for (Header h : headers) {
            if (header.equals(h.getName())) {
                return h.getValue();
            }
        }
        return null;
    }

    @Override
    public Header[] getHeaders() {
        return headers;
    }

    @Override
    public String getHeadersAsString() {
        String result = new String();
        for (Header header : headers) {
            result += "\n" + header.getName() + " : " + header.getValue();
        }
        return result.substring(1);
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getStatusText() {
        return statusText;
    }

    @Override
    public String getText() {
        return text;
    }

}
