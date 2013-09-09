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
package org.exoplatform.ide.git.client.marshaller;

import org.exoplatform.ide.git.shared.Log;

/**
 * The response with the log of commits.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 14, 2011 4:32:17 PM anya $
 */
public class LogResponse extends Log {
    /** The text format of the log response. */
    private String textLog;

    /** @return the textLog text format of the log response */
    public String getTextLog() {
        return textLog;
    }

    /**
     * @param textLog the textLog text format of the log response
     */
    public void setTextLog(String textLog) {
        this.textLog = textLog;
    }
}
