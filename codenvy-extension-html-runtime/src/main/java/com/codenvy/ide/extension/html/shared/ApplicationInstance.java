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
package com.codenvy.ide.extension.html.shared;

/**
 * Interface represents application instance.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInstance.java Jun 26, 2013 1:05:22 PM azatsarynnyy $
 */
public interface ApplicationInstance {
    /**
     * Returns name of the runned application.
     * 
     * @return runned app's name
     */
    String getName();

    /**
     * Set name of the runned application.
     * 
     * @param name runned app's name
     */
    void setName(String name);

    /**
     * Returns port of the runned application.
     * 
     * @return runned app's port
     */
    int getPort();

    /**
     * Set port of the runned application.
     * 
     * @param port runned app's port
     */
    void setPort(int port);

    /**
     * Returns lifetime of application instance in minutes. After this time application may be stopped automatically. Method may return -1
     * if lifetime of instance is unknown.
     * 
     * @return application lifetime, in minutes
     */
    int getLifetime();

    /**
     * Set application lifetime, in minutes.
     * 
     * @param lifetime application lifetime, in minutes
     */
    void setLifetime(int lifetime);
}
