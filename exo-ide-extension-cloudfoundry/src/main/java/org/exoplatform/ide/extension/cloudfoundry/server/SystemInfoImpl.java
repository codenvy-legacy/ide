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
package org.exoplatform.ide.extension.cloudfoundry.server;

import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemResources;

import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SystemInfoImpl implements SystemInfo {
    private SystemResources        limits;
    private SystemResources        usage;
    /** Cloud platform description. */
    private String                 description;
    /** User email. */
    private String                 user;
    /** Cloud platform version. */
    private String                 version;
    /** Cloud platform name. */
    private String                 name;
    /** Support email address. */
    private String                 support;
    /** Supported frameworks. */
    private Map<String, Framework> frameworks;

    /** {@inheritDoc} */
    @Override
    public SystemResources getUsage() {
        return usage;
    }

    /** {@inheritDoc} */
    @Override
    public void setUsage(SystemResources usage) {
        this.usage = usage;
    }

    /** {@inheritDoc} */
    @Override
    public SystemResources getLimits() {
        return limits;
    }

    /** {@inheritDoc} */
    @Override
    public void setLimits(SystemResources limits) {
        this.limits = limits;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public String getUser() {
        return user;
    }

    /** {@inheritDoc} */
    @Override
    public void setUser(String user) {
        this.user = user;
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return version;
    }

    /** {@inheritDoc} */
    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public String getSupport() {
        return support;
    }

    /** {@inheritDoc} */
    @Override
    public void setSupport(String support) {
        this.support = support;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Framework> getFrameworks() {
        return frameworks;
    }

    /** {@inheritDoc} */
    @Override
    public void setFrameworks(Map<String, Framework> frameworks) {
        this.frameworks = frameworks;
    }

    @Override
    public String toString() {
        return "SystemInfoImpl{" +
               "limits=" + limits +
               ", usage=" + usage +
               ", description='" + description + '\'' +
               ", user='" + user + '\'' +
               ", version='" + version + '\'' +
               ", name='" + name + '\'' +
               ", support='" + support + '\'' +
               ", frameworks=" + frameworks +
               '}';
    }
}
