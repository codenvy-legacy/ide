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
package org.exoplatform.ide.extension.heroku.client.rake;

/**
 * The response of the rake command execution result.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 20, 2011 9:40:09 AM anya $
 */
public class RakeCommandResult {
    /** Rake command result. */
    private String result;

    /** @return {@link String} rake command result */
    public String getResult() {
        return result;
    }

    /**
     * @param result
     *         rake command result
     */
    public void setResult(String result) {
        this.result = result;
    }
}
