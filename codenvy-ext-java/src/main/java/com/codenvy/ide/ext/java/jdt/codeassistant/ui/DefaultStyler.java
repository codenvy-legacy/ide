/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.codeassistant.ui;

import com.codenvy.ide.ext.java.jdt.codeassistant.ui.StyledString.Styler;

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
