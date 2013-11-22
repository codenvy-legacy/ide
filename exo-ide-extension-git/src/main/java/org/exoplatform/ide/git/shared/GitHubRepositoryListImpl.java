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

import java.util.ArrayList;
import java.util.List;

/**
 * List of GitHub repositories for paging view.
 * 
 * @author <a href="mailto:ashumilova@codenvy.com">Ann Shumilova</a>
 * @version $Id:
 */
public class GitHubRepositoryListImpl implements GitHubRepositoryList {

    /**
     * The list of GitHub repositories.
     */
    private List<GitHubRepository> repositories;

    /**
     * Link to the first page of the repositories.
     */
    private String                 firstPage;

    /**
     * Link to the previous page of the repositories (may be <code>null</code>).
     */
    private String                 prevPage;

    /**
     * Link to the next page of the repositories (may be <code>null</code>).
     */
    private String                 nextPage;

    /**
     * Link to the last page of the repositories (may be <code>null</code>).
     */
    private String                 lastPage;

    public GitHubRepositoryListImpl() {
    }

    public GitHubRepositoryListImpl(List<GitHubRepository> repositories) {
        this.repositories = repositories;
    }

    /**
     * @see org.exoplatform.ide.git.shared.GitHubRepositoryList#getRepositories()
     */
    @Override
    public List<GitHubRepository> getRepositories() {
        if (repositories == null) {
            repositories = new ArrayList<GitHubRepository>();
        }
        return repositories;
    }

    /**
     * @see org.exoplatform.ide.git.shared.GitHubRepositoryList#setRepositories(java.util.List)
     */
    @Override
    public void setRepositories(List<GitHubRepository> repositories) {
        this.repositories = repositories;
    }

    /**
     * @see org.exoplatform.ide.git.shared.GitHubRepositoryList#getFirstPage()
     */
    @Override
    public String getFirstPage() {
        return firstPage;
    }

    /**
     * @see org.exoplatform.ide.git.shared.GitHubRepositoryList#setFirstPage(java.lang.String)
     */
    @Override
    public void setFirstPage(String firstPage) {
        this.firstPage = firstPage;
    }

    /**
     * @see org.exoplatform.ide.git.shared.GitHubRepositoryList#getPrevPage()
     */
    @Override
    public String getPrevPage() {
        return prevPage;
    }

    /**
     * @see org.exoplatform.ide.git.shared.GitHubRepositoryList#setPrevPage(java.lang.String)
     */
    @Override
    public void setPrevPage(String prevPage) {
        this.prevPage = prevPage;
    }

    /**
     * @see org.exoplatform.ide.git.shared.GitHubRepositoryList#getNextPage()
     */
    @Override
    public String getNextPage() {
        return nextPage;
    }

    /**
     * @see org.exoplatform.ide.git.shared.GitHubRepositoryList#setNextPage(java.lang.String)
     */
    @Override
    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    /**
     * @see org.exoplatform.ide.git.shared.GitHubRepositoryList#getLastPage()
     */
    @Override
    public String getLastPage() {
        return lastPage;
    }

    /**
     * @see org.exoplatform.ide.git.shared.GitHubRepositoryList#setLastPage(java.lang.String)
     */
    @Override
    public void setLastPage(String lastPage) {
        this.lastPage = lastPage;
    }
}
