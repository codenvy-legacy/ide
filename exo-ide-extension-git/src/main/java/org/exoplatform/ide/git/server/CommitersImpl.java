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
package org.exoplatform.ide.git.server;

import org.exoplatform.ide.git.shared.Commiters;
import org.exoplatform.ide.git.shared.GitUser;

import java.util.List;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: CommitersImpl.java Aug 3, 2012
 */
public class CommitersImpl implements Commiters {
    private List<GitUser> commiters;

    public CommitersImpl() {
    }

    public CommitersImpl(List<GitUser> commiters) {
        this.commiters = commiters;
    }


    /** @see org.exoplatform.ide.git.shared.Commiters#getCommiters() */
    @Override
    public List<GitUser> getCommiters() {
        return commiters;
    }

    /** @see org.exoplatform.ide.git.shared.Commiters#setCommiters(java.util.List) */
    @Override
    public void setCommiters(List<GitUser> commiters) {
        this.commiters = commiters;
    }

}
