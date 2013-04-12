/*
 * Copyright (C) 2011 eXo Platform SAS.
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
