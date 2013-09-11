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
import org.exoplatform.ide.extension.openshift.shared.OpenShiftEmbeddableCartridge;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AppInfoImpl implements AppInfo {
    private String                             name;
    private String                             type;
    private String                             gitUrl;
    private String                             publicUrl;
    private double                             creationTime;
    private List<OpenShiftEmbeddableCartridge> embeddedCartridges;

    public AppInfoImpl(String name, String type, String gitUrl, String publicUrl, long creationTime) {
        this.name = name;
        this.type = type;
        this.gitUrl = gitUrl;
        this.publicUrl = publicUrl;
        this.creationTime = creationTime;
    }

    public AppInfoImpl() {
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.AppInfo#getName() */
    @Override
    public String getName() {
        return name;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.AppInfo#setName(java.lang.String) */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.AppInfo#getType() */
    @Override
    public String getType() {
        return type;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.AppInfo#setType(java.lang.String) */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.AppInfo#getGitUrl() */
    @Override
    public String getGitUrl() {
        return gitUrl;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.AppInfo#setGitUrl(java.lang.String) */
    @Override
    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.AppInfo#getPublicUrl() */
    @Override
    public String getPublicUrl() {
        return publicUrl;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.AppInfo#setPublicUrl(java.lang.String) */
    @Override
    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.AppInfo#getCreationTime() */
    @Override
    public double getCreationTime() {
        return creationTime;
    }

    /** @see org.exoplatform.ide.extension.openshift.shared.AppInfo#setCreationTime(double) */
    @Override
    public void setCreationTime(double creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public List<OpenShiftEmbeddableCartridge> getEmbeddedCartridges() {
        if (embeddedCartridges == null) {
            embeddedCartridges = new ArrayList<OpenShiftEmbeddableCartridge>(2);
        }
        return embeddedCartridges;
    }

    @Override
    public void setEmbeddedCartridges(List<OpenShiftEmbeddableCartridge> embeddedCartridges) {
        this.embeddedCartridges = embeddedCartridges;
    }

    @Override
    public String toString() {
        return "AppInfoImpl{" +
               "name='" + name + '\'' +
               ", type='" + type + '\'' +
               ", gitUrl='" + gitUrl + '\'' +
               ", publicUrl='" + publicUrl + '\'' +
               ", creationTime=" + creationTime +
               ", embeddedCartridges=" + embeddedCartridges +
               '}';
    }
}
