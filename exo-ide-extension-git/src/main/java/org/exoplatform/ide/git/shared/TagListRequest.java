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

/**
 * Request to get list of available tags.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TagListRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class TagListRequest extends GitRequest {
    /**
     * List tags with names that match the this pattern. If <code>null</code> then all tags included in result list.
     * <p/>
     * For example:
     * <p/>
     * 
     * <pre>
     * *feature - get all tags which name ends with 'feature'
     * </pre>
     * 
     * @see org.exoplatform.ide.git.server.GitConnection#tagList(TagListRequest)
     */
    private String pattern;

    /**
     * @param pattern tag's names pattern
     * @see #pattern
     */
    public TagListRequest(String pattern) {
        this.pattern = pattern;
    }

    public TagListRequest() {
    }

    /**
     * @return tag's names pattern
     * @see #pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @param pattern tag's names pattern
     * @see #pattern
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
