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
package org.exoplatform.ide.shell.client.model;

import org.exoplatform.ide.shell.shared.ShellConfiguration;
import org.exoplatform.ide.shell.shared.UserInfo;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 12, 2011 evgen $
 */
public class ShellConfigurationBean implements ShellConfiguration {
    private UserInfo userInfo;

    private String   vfsBaseUrl;

    /**
     *
     */
    public ShellConfigurationBean() {
    }

    /**
     * @param userInfo
     * @param vfsBaseUrl
     */
    public ShellConfigurationBean(UserInfo userInfo, String vfsBaseUrl) {
        super();
        this.userInfo = userInfo;
        this.vfsBaseUrl = vfsBaseUrl;
    }

    /** @see org.exoplatform.ide.shell.shared.ShellConfiguration#getUserInfo() */
    @Override
    public UserInfo getUser() {
        return userInfo;
    }

    /** @see org.exoplatform.ide.shell.shared.ShellConfiguration#setUserInfo(org.exoplatform.ide.shell.shared.UserInfo) */
    @Override
    public void setUser(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String getVfsBaseUrl() {
        return vfsBaseUrl;
    }

    @Override
    public void setVfsBaseUrl(String vfsBaseUrl) {
        this.vfsBaseUrl = vfsBaseUrl;
    }


}
