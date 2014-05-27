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
package com.codenvy.ide.ui.dialogs.askValue;

/**
 * Handler for user interaction in {@link AskValueDialog}.
 *
 * @author Vitaly Parfonov
 * @author Artem Zatsarynnyy
 */
public abstract class AskValueCallback {

    /**
     * Call if user click Ok button.
     *
     * @param value
     *         entered value
     */
    public abstract void onOk(String value);

    /**
     * Call if user click cancel button.
     * If need custom interaction override it.
     */
    public void onCancel() {
    }
}
