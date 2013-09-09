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
package org.exoplatform.ide.codeassistant.storage;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: UpdateStorageResult.java Oct 29, 2012 vetal $
 */
public class UpdateStorageResult {

    private String erroMessage;

    private int exitCode;

    public UpdateStorageResult() {
        exitCode = 0;
    }

    /**
     * @param erroMessage
     * @param exitCode
     */
    public UpdateStorageResult(String erroMessage, int exitCode) {
        super();
        this.erroMessage = erroMessage;
        this.exitCode = exitCode;
    }

    /** @return the erroMessage */
    public String getErroMessage() {
        return erroMessage;
    }

    /**
     * @param erroMessage
     *         the erroMessage to set
     */
    public void setErroMessage(String erroMessage) {
        this.erroMessage = erroMessage;
    }

    /** @return the exitCode */
    public int getExitCode() {
        return exitCode;
    }

    /**
     * @param exitCode
     *         the exitCode to set
     */
    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

}
