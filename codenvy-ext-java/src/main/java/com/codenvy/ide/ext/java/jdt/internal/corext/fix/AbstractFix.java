/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.corext.fix;

import com.codenvy.ide.runtime.IStatus;

public abstract class AbstractFix implements IProposableFix {

    private final String fDisplayString;

    protected AbstractFix(String displayString) {
        fDisplayString = displayString;
    }

    /** {@inheritDoc} */
    public String getAdditionalProposalInfo() {
        return null;
    }

    /** {@inheritDoc} */
    public String getDisplayString() {
        return fDisplayString;
    }

    //   /**
    //    * {@inheritDoc}
    //    */
    //   public LinkedProposalModel getLinkedPositions()
    //   {
    //      return null;
    //   }

    /** {@inheritDoc} */
    public IStatus getStatus() {
        return null;
    }
}
