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
package org.exoplatform.ide.extension.cloudfoundry.shared;

import java.util.Map;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public interface SystemInfo {
    SystemResources getUsage();

    void setUsage(SystemResources usage);

    SystemResources getLimits();

    void setLimits(SystemResources limits);

    String getDescription();

    void setDescription(String description);

    String getUser();

    void setUser(String user);

    String getVersion();

    void setVersion(String version);

    String getName();

    void setName(String name);

    String getSupport();

    void setSupport(String support);

    Map<String, Framework> getFrameworks();

    void setFrameworks(Map<String, Framework> frameworks);
}