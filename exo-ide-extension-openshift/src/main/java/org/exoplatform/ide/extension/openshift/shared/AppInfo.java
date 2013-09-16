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
package org.exoplatform.ide.extension.openshift.shared;

import java.util.List;

/**
 * Application info.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: AppInfo.java Mar 13, 2012 4:11:11 PM azatsarynnyy $
 */
public interface AppInfo {
    /**
     * Get name of the application.
     *
     * @return application name.
     */
    String getName();

    /**
     * Set the application name.
     *
     * @param name
     *         application name.
     */
    void setName(String name);

    /**
     * Return the application type.
     *
     * @return application type.
     */
    String getType();

    /**
     * Set type of the application.
     *
     * @param type
     *         application type.
     */
    void setType(String type);

    /**
     * Get url of the application Git-repository.
     *
     * @return url of the application Git-repository.
     */
    String getGitUrl();

    /**
     * Set the url of the application Git-repository.
     *
     * @param gitUrl
     *         url of the application Git-repository.
     */
    void setGitUrl(String gitUrl);

    /**
     * Return the public url of the application.
     *
     * @return public url of the application.
     */
    String getPublicUrl();

    /**
     * Set the public url for application.
     *
     * @param publicUrl
     *         public url of the application.
     */
    void setPublicUrl(String publicUrl);

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
     * Set the time when application was created.
     * <p/>
     * Time returned as double-value because the Java long type cannot be represented in JavaScript as a numeric type.
     * http://code.google.com/intl/ru/webtoolkit/doc/latest/DevGuideCodingBasicsCompatibility.html
     *
     * @param creationTime
     *         time of creation the application.
     */
    void setCreationTime(double creationTime);

    /**
     * Get list of embeddable cartridges added to application.
     *
     * @return list of embeddable cartridges added to application.
     */
    List<OpenShiftEmbeddableCartridge> getEmbeddedCartridges();

    /**
     * Set list of embeddable cartridges added to application.
     *
     * @param embeddedCartridges
     *         list of embeddable cartridges added to application.
     */
    void setEmbeddedCartridges(List<OpenShiftEmbeddableCartridge> embeddedCartridges);
}
