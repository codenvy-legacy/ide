/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.refactoring;

import com.codenvy.ide.text.Document;

/** Helper class for text file changes. */
public class TextChanges {

    private TextChanges() {
        // no instance
    }

    public static RefactoringStatus isValid(Document document, int length) {
        RefactoringStatus result = new RefactoringStatus();
        if (length != document.getLength()) {
            result.addFatalError("The content of the document has changed.");
        }
        return result;
    }
}
