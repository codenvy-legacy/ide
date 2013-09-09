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
package org.exoplatform.ide.extension.php.shared;

/**
 * Interface represents application instance.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInstance.java Apr 17, 2013 4:33:05 PM azatsarynnyy $
 *
 */
public interface ApplicationInstance {
    String getName();

    void setName(String name);

    String getHost();

    void setHost(String host);

    int getPort();

    void setPort(int port);

    String getStopURL();

    void setStopURL(String url);

    /**
     * Lifetime of application instance in minutes. After this time instance may be stopped.
     * Method may return -1 if lifetime of instance is unknown.
     *
     * @return application instance lifetime in minutes
     */
    int getLifetime();

    void setLifetime(int lifetime);
}
