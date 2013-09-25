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
package com.codenvy.ide.ext.git.shared;

import com.codenvy.ide.dto.DTO;
import com.codenvy.ide.json.JsonArray;

/**
 * Git reference bean.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 20, 2011 2:41:39 PM anya $
 */
@DTO
public interface Reference {
    public enum RefType {
        LOCAL_BRANCH,
        REMOTE_BRANCH,
        TAG;
    }

    /** @return the displayName */
    String getDisplayName();

    /** @return the fullName */
    String getFullName();

    /** @return the refType */
    RefType getRefType();

    /** @return available branches */
    JsonArray<Reference> getBranches();
}