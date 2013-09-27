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
package org.exoplatform.ide.extension.cloudfoundry.server.ext;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryServerConfiguration {
    private String target;
    private String user;
    private String password;

    public CloudfoundryServerConfiguration(String target, String user, String password) {
        this.target = target;
        this.user = user;
        this.password = password;
    }

    public CloudfoundryServerConfiguration() {
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CloudfoundryServerConfiguration)) {
            return false;
        }
        CloudfoundryServerConfiguration other = (CloudfoundryServerConfiguration)o;
        return target == null ? other.target == null : target.equals(other.target);
    }

    @Override
    public final int hashCode() {
        int hash = 7;
        hash = 31 * hash + (target != null ? target.hashCode() : 0);
        return hash;
    }
}
