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
package com.codenvy.ide.ext.git.client.merge;

import com.codenvy.ide.collections.Array;


/**
 * Git reference bean.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 20, 2011 2:41:39 PM anya $
 */
public class Reference {
    enum RefType {
        LOCAL_BRANCH,
        REMOTE_BRANCH,
        TAG;
    }

    /** Short name of the reference to display. */
    private String displayName;

    /** Full name of the reference. */
    private String fullName;

    /** Type of the reference. */
    private RefType refType;

    private Array<Reference> branches;

    /**
     * @param fullName full name of the reference
     * @param displayName short name of the reference to display
     * @param refType type the reference
     */
    public Reference(String fullName, String displayName, RefType refType) {
        this.displayName = displayName;
        this.fullName = fullName;
        this.refType = refType;
    }

    /** @return the displayName */
    public String getDisplayName() {
        return displayName;
    }

    /** @return the fullName */
    public String getFullName() {
        return fullName;
    }

    /** @return the refType */
    public RefType getRefType() {
        return refType;
    }

    /** @return the branches */
    public Array<Reference> getBranches() {
        return branches;
    }

    /** @param branches the branches to set */
    public void setBranches(Array<Reference> branches) {
        this.branches = branches;
    }
}
