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

/**
 * Response of the application's logs.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 19, 2011 2:38:21 PM anya $
 */
public class LogsResponse {
    /** Logs. */
    private String logs;

    /** @return the logs */
    public String getLogs() {
        return logs;
    }

    /**
     * @param logs
     *         the logs to set
     */
    public void setLogs(String logs) {
        this.logs = logs;
    }
}
