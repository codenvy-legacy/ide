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
 * User's credentials on GitHub.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 28, 2012 9:42:33 AM anya $
 * @deprecated not need it any more, we use oauth for GItHub
 */
public class GitHubCredentials implements Credentials {
    /** User's login. */
    private String login;

    /** User's password. */
    private String password;

    public GitHubCredentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    /** @see org.exoplatform.ide.git.shared.Credentials#getLogin() */
    public String getLogin() {
        return login;
    }

    /** @see org.exoplatform.ide.git.shared.Credentials#getPassword() */
    public String getPassword() {
        return password;
    }

    /** @see org.exoplatform.ide.git.shared.Credentials#setLogin(java.lang.String) */
    @Override
    public void setLogin(String login) {
        this.login = login;
    }

    /** @see org.exoplatform.ide.git.shared.Credentials#setPassword(java.lang.String) */
    @Override
    public void setPassword(String password) {
        this.password = password;
    }
}
