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
package com.codenvy.ide.ext.openshift.shared;

import com.codenvy.ide.dto.DTO;
import com.codenvy.ide.json.JsonArray;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@DTO
public interface RHUserInfo {
    /**
     * Get the domain name of RedHat cloud.
     *
     * @return RedHat cloud domain name.
     */
    public String getRhcDomain();

    /**
     * Get the user id.
     *
     * @return use id.
     */
    public String getUuid();

    /**
     * Get the login name of user.
     *
     * @return login name of user.
     */
    public String getRhlogin();

    /**
     * Get the namespace.
     *
     * @return namespace.
     */
    public String getNamespace();

    /**
     * Return the list of the user's applications.
     *
     * @return list of the applications.
     */
    public JsonArray<AppInfo> getApps();
}
