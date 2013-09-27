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
package org.exoplatform.ide.git.client.reset;


/**
 * Git file in index. Used for work with index (remove, reset).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 13, 2011 11:57:38 AM anya $
 */
public class IndexFile {
    /** File is indexed by Git. */
    private boolean indexed;

    private String  path;

    /**
     * @param file git file
     * @param indexed if <code>true</code> file is in index
     */
    public IndexFile(String path, boolean indexed) {
        this.path = path;
        this.indexed = indexed;
    }

    /** @return the indexed if <code>true</code> file is in index */
    public boolean isIndexed() {
        return indexed;
    }

    /**
     * @param indexed the indexed if <code>true</code> file is in index
     */
    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public String getPath() {
        return this.path;
    }
}
