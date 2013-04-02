/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.core;

import com.codenvy.eclipse.jdt.core.IJavaProject;
import com.codenvy.eclipse.jdt.internal.compiler.util.GenericXMLWriter;
import com.codenvy.eclipse.jdt.internal.core.util.Util;

import java.io.Writer;


/** @since 3.0 */
class XMLWriter extends GenericXMLWriter {

    public XMLWriter(Writer writer, IJavaProject project, boolean printXmlVersion) {
        super(writer, Util.getLineSeparator((String)null, project), printXmlVersion);
    }
}
