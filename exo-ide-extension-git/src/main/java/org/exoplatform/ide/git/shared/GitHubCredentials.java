/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
