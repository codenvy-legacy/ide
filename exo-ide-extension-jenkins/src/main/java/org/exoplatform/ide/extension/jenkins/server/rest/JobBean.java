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
package org.exoplatform.ide.extension.jenkins.server.rest;

import org.exoplatform.ide.extension.jenkins.shared.Job;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JobBean implements Job {
    private String name;

    private String buildUrl;

    private String statusUrl;

    public JobBean(String name, String buildUrl, String statusUrl) {
        this.name = name;
        this.buildUrl = buildUrl;
        this.statusUrl = statusUrl;
    }

    public JobBean() {
    }

    /** @see org.exoplatform.ide.extension.jenkins.shared.Job#getName() */
    public String getName() {
        return name;
    }

    /** @see org.exoplatform.ide.extension.jenkins.shared.Job#setName(java.lang.String) */
    public void setName(String name) {
        this.name = name;
    }

    /** @see org.exoplatform.ide.extension.jenkins.shared.Job#getBuildUrl() */
    public String getBuildUrl() {
        return buildUrl;
    }

    /** @see org.exoplatform.ide.extension.jenkins.shared.Job#setBuildUrl(java.lang.String) */
    public void setBuildUrl(String buildUrl) {
        this.buildUrl = buildUrl;
    }

    /** @see org.exoplatform.ide.extension.jenkins.shared.Job#getStatusUrl() */
    public String getStatusUrl() {
        return statusUrl;
    }

    /** @see org.exoplatform.ide.extension.jenkins.shared.Job#setStatusUrl(java.lang.String) */
    public void setStatusUrl(String statusUrl) {
        this.statusUrl = statusUrl;
    }
}
