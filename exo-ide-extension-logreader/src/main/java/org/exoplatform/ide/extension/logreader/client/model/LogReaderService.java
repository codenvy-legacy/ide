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
package org.exoplatform.ide.extension.logreader.client.model;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.extension.logreader.shared.LogEntry;

/**
 * This service provides access to information stored in the logs created on current tenant.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class LogReaderService {

    private String restContext;

    private Loader loader;

    private static LogReaderService instance;

    public static LogReaderService get() {
        return instance;
    }

    /**
     * @param restContext
     * @param loader
     */
    public LogReaderService(String restContext, String wsName, Loader loader) {
        super();
        this.restContext = restContext + wsName;
        this.loader = loader;
        instance = this;
    }

    /**
     * Get last log file
     *
     * @param callback
     * @throws RequestException
     */
    public void getLastLog(AsyncRequestCallback<LogEntry> callback) throws RequestException {
        String url = restContext + "/log-reader-service/last-log";
        sendRequest(url, callback);
    }

    /**
     * Get previous log file for current log file
     *
     * @param token
     *         of current log
     * @param callback
     * @throws RequestException
     */
    public void getPrevLog(String token, AsyncRequestCallback<LogEntry> callback) throws RequestException {
        String url = restContext + "/log-reader-service/prev-log?lrtoken=" + token;
        sendRequest(url, callback);
    }

    /**
     * Get next log file for current log
     *
     * @param token
     *         of current log
     * @param callback
     * @throws RequestException
     */
    public void getNextLog(String token, AsyncRequestCallback<LogEntry> callback) throws RequestException {
        String url = restContext + "/log-reader-service/next-log?lrtoken=" + token;
        sendRequest(url, callback);
    }

    /**
     * Update log
     *
     * @param token
     *         of log
     * @param callback
     * @throws RequestException
     */
    public void getLog(String token, AsyncRequestCallback<LogEntry> callback) throws RequestException {
        String url = restContext + "/log-reader-service/log?lrtoken=" + token;
        sendRequest(url, callback);
    }

    private void sendRequest(String url, AsyncRequestCallback<LogEntry> callback) throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

}
