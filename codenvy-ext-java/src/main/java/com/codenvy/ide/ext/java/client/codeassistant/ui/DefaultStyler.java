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
package com.codenvy.ide.ext.java.client.codeassistant.ui;

import com.codenvy.ide.ext.java.client.codeassistant.ui.StyledString.Styler;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 12:30:04 PM 34360 2009-07-22 23:58:59Z evgen $
 */
public class DefaultStyler extends Styler {

    private String className;

    /** @param className */
    public DefaultStyler(String className) {
        super();
        this.className = className;
    }

    /** @see org.eclipse.jdt.client.codeassistant.ui.StyledString.Styler#applyStyles(java.lang.String) */
    @Override
    public String applyStyles(String text) {
        StringBuilder b = new StringBuilder();
        b.append("<span ").append("class=\"").append(className).append("\">");
        b.append(text).append("</span>");
        return b.toString();
    }

}
