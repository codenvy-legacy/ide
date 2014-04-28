/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [$today.year] Codenvy, S.A. 
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
package com.codenvy.ide.ui.dialogs;

/**
 * Handler for user interaction in Ask dialog window
 *
 * @author Vitaly Parfonov
 */
public abstract class AskHandler {

    /**
     * Call if user click Ok button
     */
    public abstract void onOk();

    /**
     * Call if user click cancel button.
     * By default nothing todo.
     * If need custom interaction override it.
     */
    public void onCancel() {
        //by default nothing todo
    }
}
