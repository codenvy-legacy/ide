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
package org.exoplatform.ide.git.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.git.shared.*;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: GitAutoBeanFactory.java Aug 3, 2012
 */
public interface GitAutoBeanFactory extends AutoBeanFactory {
    AutoBean<Commiters> commiters();

    /**
     * A factory method for a log entry bean.
     *
     * @return an {@link AutoBean} of type {@link GitHubRepository}
     */
    AutoBean<GitHubRepository> repositoryExt();

    /**
     * Factory method for GitHub credentials bean.
     *
     * @return {@link AutoBean} of type {@link Credentials}
     */
    AutoBean<Credentials> githubCredentials();

    AutoBean<Collaborators> collaborators();

    AutoBean<Status> status();
    
    /**
     * Factory method for GitHub user bean.
     * 
     * @return {@link AutoBean} of type {@link GitHubUser}
     */
    AutoBean<GitHubUser> gitHubUser();
    
    /**
     * Factory method for GitHub repositories list bean.
     * 
     * @return {@link AutoBean} of type {@link GitHubRepositoryList}
     */
    AutoBean<GitHubRepositoryList> gitHubRepositoryList();
}
