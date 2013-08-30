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
package org.exoplatform.ide.testframework.server.openshift;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RHUserInfo {
    private String rhcDomain;

    private String uuid;

    private String rhlogin;

    private String namespace;

    private List<AppInfo> apps;

    public RHUserInfo(String rhcDomain, String uuid, String rhlogin, String namespace) {
        this.rhcDomain = rhcDomain;
        this.uuid = uuid;
        this.rhlogin = rhlogin;
        this.namespace = namespace;
    }

    public RHUserInfo() {
    }

    public String getRhcDomain() {
        return rhcDomain;
    }

    public void setRhcDomain(String rhcDomain) {
        this.rhcDomain = rhcDomain;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRhlogin() {
        return rhlogin;
    }

    public void setRhlogin(String rhlogin) {
        this.rhlogin = rhlogin;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public List<AppInfo> getApps() {
        return apps;
    }

    public void setApps(List<AppInfo> apps) {
        this.apps = apps;
    }

    @Override
    public String toString() {
        return "RHUserInfo [rhcDomain=" + rhcDomain + ", uuid=" + uuid + ", rhlogin=" + rhlogin + ", namespace="
               + namespace + "]";
    }
}
