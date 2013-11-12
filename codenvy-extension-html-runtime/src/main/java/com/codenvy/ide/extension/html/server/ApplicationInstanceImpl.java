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
package com.codenvy.ide.extension.html.server;

import com.codenvy.ide.extension.html.shared.ApplicationInstance;

/**
 * Default implementation of {@link ApplicationInstance}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInstanceImpl.java Jun 26, 2013 1:05:49 PM azatsarynnyy $
 */
public class ApplicationInstanceImpl implements ApplicationInstance {
    /** Application's port. */
    private String name;

    /** Application's url. */
    private String applicationUrl;

    /** Application's port. */
    private int port = 80;

    /** Lifetime of application instance in minutes. After this time application may be stopped automatically. */
    private int lifetime = -1;

    /**
     * Constructs new instance of {@link ApplicationInstanceImpl} with the given name and lifetime.
     *
     * @param name app's name
     * @param lifetime app's lifetime
     */
    public ApplicationInstanceImpl(String name, int lifetime, String applicationUrl) {
        this.name = name;
        this.lifetime = lifetime;
        this.applicationUrl = applicationUrl;
    }

    /**
     * Constructs new instance of {@link ApplicationInstanceImpl} with the given name.
     *
     * @param name app's name
     */
    public ApplicationInstanceImpl(String name) {
        this.name = name;
    }

    public ApplicationInstanceImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getLifetime() {
        return lifetime;
    }

    @Override
    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    public String getApplicationUrl() {
        return applicationUrl;
    }

    @Override
    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    @Override
    public String toString() {
        return "ApplicationInstanceImpl{" +
               "name='" + name + '\'' +
               ", port=" + port +
               ", lifetime=" + lifetime +
               '}';
    }
}
