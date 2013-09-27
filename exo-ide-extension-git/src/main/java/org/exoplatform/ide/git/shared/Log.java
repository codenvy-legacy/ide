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
package org.exoplatform.ide.git.shared;

import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Log.java 68139 2011-04-08 15:06:00Z andrew00x $
 */
public class Log {
    protected List<Revision> commits;

    public Log(List<Revision> commits) {
        this.commits = commits;
    }

    public Log() {
    }

    public List<Revision> getCommits() {
        return commits;
    }

    public void setCommits(List<Revision> commits) {
        this.commits = commits;
    }
}
