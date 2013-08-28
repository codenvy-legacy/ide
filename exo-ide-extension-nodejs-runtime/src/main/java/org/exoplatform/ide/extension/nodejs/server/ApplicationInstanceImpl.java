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
package org.exoplatform.ide.extension.nodejs.server;

import org.exoplatform.ide.extension.nodejs.shared.ApplicationInstance;

/**
 * 
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: ApplicationInstanceImpl.java Apr 18, 2013 5:17:58 PM vsvydenko $
 *
 */
public class ApplicationInstanceImpl implements ApplicationInstance {
    private String name;
    private String host;

    private int port = 80;
    private String stopURL;
    private int lifetime = -1;

    public ApplicationInstanceImpl(String name, String host, String stopURL, int lifetime) {
        this.name = name;
        this.host = host;
        this.stopURL = stopURL;
        this.lifetime = lifetime;
    }

    public ApplicationInstanceImpl(String name, String host, String stopURL) {
        this.name = name;
        this.host = host;
        this.stopURL = stopURL;
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
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
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
    public String getStopURL() {
        return stopURL;
    }

    @Override
    public void setStopURL(String stopURL) {
        this.stopURL = stopURL;
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
    public String toString() {
        return "ApplicationInstanceImpl{" +
               "name='" + name + '\'' +
               ", host='" + host + '\'' +
               ", port=" + port +
               ", stopURL='" + stopURL + '\'' +
               ", lifetime=" + lifetime +
               '}';
    }
}
