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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.configuration;

import com.google.gwt.user.client.ui.TextBox;

/**
 * Wrapper on TextBox with field that marker input modifiable.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ModifiableTextBox extends TextBox {
    private boolean modified;

    /**
     * If input field is modified by user.
     *
     * @return true if modified.
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Set modified value.
     *
     * @param modified
     *         true if field is modified.
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
