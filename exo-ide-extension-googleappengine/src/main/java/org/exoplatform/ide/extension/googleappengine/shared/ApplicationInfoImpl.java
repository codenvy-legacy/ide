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
