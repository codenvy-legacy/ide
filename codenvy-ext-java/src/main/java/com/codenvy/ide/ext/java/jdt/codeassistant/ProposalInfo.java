/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.codeassistant;

import com.codenvy.ide.ext.java.jdt.core.IJavaElement;

import com.google.gwt.user.client.ui.Widget;

public abstract class ProposalInfo {

    /**
     * Gets the text for this proposal info formatted as HTML, or <code>null</code> if no text is available.
     *
     * @return the additional info text
     */
    public abstract Widget getInfo();

    /** Returns the Java element. */
    public abstract IJavaElement getJavaElement();

}
