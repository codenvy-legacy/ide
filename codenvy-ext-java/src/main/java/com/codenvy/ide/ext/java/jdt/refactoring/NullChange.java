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

import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.runtime.CoreException;


/**
 * A refactoring change that does nothing. The reverse change of a
 * <code>NullChange</code> is a <code>NullChange</code>.
 * <p>
 * Note: this class is not intended to be extended by clients.
 * </p>
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @since 3.0
 */
public class NullChange extends Change {

    private String fName;

    /** Creates a new <code>NullChange</code> with a default name. */
    public NullChange() {
        this(" No operation change");
    }

    /**
     * Creates a new <code>NullChange</code> with the given name.
     *
     * @param name
     *         the human readable name of this change
     */
    public NullChange(String name) {
        Assert.isNotNull(name);
        fName = name;
    }

    /** {@inheritDoc} */
    public String getName() {
        return fName;
    }

    /** {@inheritDoc} */
    public void initializeValidationData() {
        // do nothing
    }

    /** {@inheritDoc} */
    public RefactoringStatus isValid() throws CoreException {
        return new RefactoringStatus();
    }

    /** {@inheritDoc} */
    public Change perform() throws CoreException {
        return new NullChange();
    }

    /** {@inheritDoc} */
    public Object getModifiedElement() {
        return null;
    }
}
