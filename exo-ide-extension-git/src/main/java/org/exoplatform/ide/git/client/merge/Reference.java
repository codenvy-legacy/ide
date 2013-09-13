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
package org.exoplatform.ide.git.client.merge;

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
    private String  displayName;

    /** Full name of the reference. */
    private String  fullName;

    /** Type of the reference. */
    private RefType refType;

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
}
