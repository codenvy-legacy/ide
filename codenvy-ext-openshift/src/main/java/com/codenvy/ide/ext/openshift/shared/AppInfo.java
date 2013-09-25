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
public interface AppInfo {
    /**
     * Get name of the application.
     *
     * @return application name.
     */
    String getName();

    /**
     * Return the application type.
     *
     * @return application type.
     */
    String getType();

    /**
     * Get url of the application Git-repository.
     *
     * @return url of the application Git-repository.
     */
    String getGitUrl();

    /**
     * Return the public url of the application.
     *
     * @return public url of the application.
     */
    String getPublicUrl();

    /**
     * Return time when application was created.
     * <p/>
     * Time returned as double-value because the Java long type cannot be represented in JavaScript as a numeric type.
     * http://code.google.com/intl/ru/webtoolkit/doc/latest/DevGuideCodingBasicsCompatibility.html
     *
     * @return time of creation the application.
     */
    double getCreationTime();

    /**
     * Get list of embeddable cartridges added to application.
     *
     * @return list of embeddable cartridges added to application.
     */
    JsonArray<OpenShiftEmbeddableCartridge> getEmbeddedCartridges();
}
