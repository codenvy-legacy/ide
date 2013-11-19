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
package com.codenvy.ide.ext.git.shared;

import com.codenvy.dto.shared.DTO;

import java.util.List;


/**
 * List of GitHub repositories for paging view.
 * 
 * @author <a href="mailto:ashumilova@codenvy.com">Ann Shumilova</a>
 * @version $Id:
 */
@DTO
public interface GitHubRepositoryList {

    /**
     * @return {@link List} the list of repositories
     */
    List<GitHubRepository> getRepositories();
    
    void setRepositories(List<GitHubRepository> repositories);


    /**
     * Link to the first page of the repositories list, if paging is used.
     * 
     * @return {@link String} first page link
     */
    String getFirstPage();
    
    void setFirstPage(String page);

    /**
     * Link to the previous page of the repositories list, if paging is used.
     * 
     * @return {@link String} previous page link
     */
    String getPrevPage();
    
    void setPrevPage(String page);

    /**
     * Link to the next page of the repositories list, if paging is used.
     * 
     * @return {@link String} next page link
     */
    String getNextPage();

    void setNextPage(String page);
    
    /**
     * Link to the last page of the repositories list, if paging is used.
     * 
     * @return {@link String} last page's link
     */
    String getLastPage();
    
    void setLastPage(String page);
}
