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
package org.exoplatform.ide.extension.openshift.server;

import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;

import java.util.List;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RHUserInfoImpl implements RHUserInfo {
    private String rhcDomain;

    private String uuid;

    private String rhlogin;

    private String namespace;

    private List<AppInfo> apps;

    public RHUserInfoImpl(String rhcDomain, String uuid, String rhlogin, String namespace) {
        this.rhcDomain = rhcDomain;
        this.uuid = uuid;
        this.rhlogin = rhlogin;
        this.namespace = namespace;
    }

    public RHUserInfoImpl() {
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#getRhcDomain() */
    @Override
    public String getRhcDomain() {
        return rhcDomain;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#setRhcDomain(java.lang.String) */
    @Override
    public void setRhcDomain(String rhcDomain) {
        this.rhcDomain = rhcDomain;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#getUuid() */
    @Override
    public String getUuid() {
        return uuid;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#setUuid(java.lang.String) */
    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#getRhlogin() */
    @Override
    public String getRhlogin() {
        return rhlogin;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#setRhlogin(java.lang.String) */
    @Override
    public void setRhlogin(String rhlogin) {
        this.rhlogin = rhlogin;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#getNamespace() */
    @Override
    public String getNamespace() {
        return namespace;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#setNamespace(java.lang.String) */
    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#getApps() */
    @Override
    public List<AppInfo> getApps() {
        return apps;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.RHUserInfo#setApps(java.util.List) */
    @Override
    public void setApps(List<AppInfo> apps) {
        this.apps = apps;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return "RHUserInfoImpl{" +
               "rhcDomain='" + rhcDomain + '\'' +
               ", uuid='" + uuid + '\'' +
               ", rhlogin='" + rhlogin + '\'' +
               ", namespace='" + namespace + '\'' +
               ", apps=" + apps +
               '}';
    }
}
