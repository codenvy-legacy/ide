/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.openshift.shared;

import com.codenvy.ide.json.JsonArray;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface RHUserInfo {
    /**
     * Get the domain name of RedHat cloud.
     *
     * @return RedHat cloud domain name.
     */
    public String getRhcDomain();

//    /**
//     * Set the domain name of RedHat cloud.
//     *
//     * @param rhcDomain
//     *         RedHat cloud domain name.
//     */
//    public void setRhcDomain(String rhcDomain);

    /**
     * Get the user id.
     *
     * @return use id.
     */
    public String getUuid();

//    /**
//     * Set the user id.
//     *
//     * @param uuid
//     *         user id.
//     */
//    public void setUuid(String uuid);

    /**
     * Get the login name of user.
     *
     * @return login name of user.
     */
    public String getRhlogin();

//    /**
//     * Set the login name of user.
//     *
//     * @param rhlogin
//     *         login name of user.
//     */
//    public void setRhlogin(String rhlogin);

    /**
     * Get the namespace.
     *
     * @return namespace.
     */
    public String getNamespace();

//    /**
//     * Set the namespace.
//     *
//     * @param namespace
//     *         namespace.
//     */
//    public void setNamespace(String namespace);

    /**
     * Return the list of the user's applications.
     *
     * @return list of the applications.
     */
    public JsonArray<AppInfo> getApps();

//    /**
//     * Set the list of the user's applications.
//     *
//     * @param apps
//     *         list of the applications.
//     */
//    public void setApps(List<AppInfo> apps);
}
