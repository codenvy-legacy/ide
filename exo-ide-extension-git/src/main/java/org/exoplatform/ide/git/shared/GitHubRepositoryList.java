/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
 * List of GitHub repositories for paging view.
 * 
 * @author <a href="mailto:ashumilova@codenvy.com">Ann Shumilova</a>
 * @version $Id:
 */
public interface GitHubRepositoryList {

    /**
     * @return {@link List} the list of repositories
     */
    public abstract List<GitHubRepository> getRepositories();

    /**
     * Set the list of GitHub repositories.
     * 
     * @param repositories
     */
    public abstract void setRepositories(List<GitHubRepository> repositories);

    /**
     * Link to the first page of the repositories list, if paging is used.
     * 
     * @return {@link String} first page link
     */
    public abstract String getFirstPage();

    /**
     * @param firstPage first page's link
     */
    public abstract void setFirstPage(String firstPage);

    /**
     * Link to the previous page of the repositories list, if paging is used.
     * 
     * @return {@link String} previous page link
     */
    public abstract String getPrevPage();

    /**
     * @param prevPage previous page's link
     */
    public abstract void setPrevPage(String prevPage);

    /**
     * Link to the next page of the repositories list, if paging is used.
     * 
     * @return {@link String} next page link
     */
    public abstract String getNextPage();

    /**
     * @param nextPage next page's link
     */
    public abstract void setNextPage(String nextPage);

    /**
     * Link to the last page of the repositories list, if paging is used.
     * 
     * @return {@link String} last page's link
     */
    public abstract String getLastPage();

    /**
     * @param lastPage last page's link
     */
    public abstract void setLastPage(String lastPage);

}
