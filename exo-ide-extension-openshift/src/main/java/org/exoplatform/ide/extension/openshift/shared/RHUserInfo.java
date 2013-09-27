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
package org.exoplatform.ide.extension.openshift.shared;

import java.util.List;

/**
 * RedHat user info.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: RHUserInfo.java Mar 14, 2012 3:33:57 PM azatsarynnyy $
 */
public interface RHUserInfo {

    /**
     * Get the domain name of RedHat cloud.
     *
     * @return RedHat cloud domain name.
     */
    public String getRhcDomain();

    /**
     * Set the domain name of RedHat cloud.
     *
     * @param rhcDomain
     *         RedHat cloud domain name.
     */
    public void setRhcDomain(String rhcDomain);

    /**
     * Get the user id.
     *
     * @return use id.
     */
    public String getUuid();

    /**
     * Set the user id.
     *
     * @param uuid
     *         user id.
     */
    public void setUuid(String uuid);

    /**
     * Get the login name of user.
     *
     * @return login name of user.
     */
    public String getRhlogin();

    /**
     * Set the login name of user.
     *
     * @param rhlogin
     *         login name of user.
     */
    public void setRhlogin(String rhlogin);

    /**
     * Get the namespace.
     *
     * @return namespace.
     */
    public String getNamespace();

    /**
     * Set the namespace.
     *
     * @param namespace
     *         namespace.
     */
    public void setNamespace(String namespace);

    /**
     * Return the list of the user's applications.
     *
     * @return list of the applications.
     */
    public List<AppInfo> getApps();

    /**
     * Set the list of the user's applications.
     *
     * @param apps
     *         list of the applications.
     */
    public void setApps(List<AppInfo> apps);

}