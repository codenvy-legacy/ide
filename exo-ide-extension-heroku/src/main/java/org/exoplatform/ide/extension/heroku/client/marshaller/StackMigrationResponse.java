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
 * Response of the stack migration operation.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Aug 1, 2011 4:06:44 PM anya $
 */
public class StackMigrationResponse {
    /** Result of the stack migration. */
    private String result;

    /** @return the result of the stack migration */
    public String getResult() {
        return result;
    }

    /**
     * @param result
     *         the result of the stack migration
     */
    public void setResult(String result) {
        this.result = result;
    }
}
