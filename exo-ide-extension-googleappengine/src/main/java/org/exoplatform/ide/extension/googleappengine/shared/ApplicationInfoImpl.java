/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.googleappengine.shared;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ApplicationInfoImpl implements ApplicationInfo {
    private String applicationId;
    private String webURL;

    public ApplicationInfoImpl(String applicationId, String webURL) {
        this.applicationId = applicationId;
        this.webURL = webURL;
    }

    public ApplicationInfoImpl() {
    }

    /** @see org.exoplatform.ide.extension.googleappengine.shared.ApplicationInfo#getWebURL() */
    @Override
    public String getWebURL() {
        return webURL;
    }

    /** @see org.exoplatform.ide.extension.googleappengine.shared.ApplicationInfo#setWebURL(java.lang.String) */
    @Override
    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }

    /** @see org.exoplatform.ide.extension.googleappengine.shared.ApplicationInfo#getApplicationId() */
    @Override
    public String getApplicationId() {
        return applicationId;
    }

    /** @see org.exoplatform.ide.extension.googleappengine.shared.ApplicationInfo#setApplicationId(java.lang.String) */
    @Override
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
