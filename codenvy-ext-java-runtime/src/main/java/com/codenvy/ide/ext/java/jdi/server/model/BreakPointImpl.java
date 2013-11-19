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
package com.codenvy.ide.ext.java.jdi.server.model;

import com.codenvy.ide.ext.java.jdi.shared.BreakPoint;
import com.codenvy.ide.ext.java.jdi.shared.Location;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BreakPointImpl implements BreakPoint {
    private Location location;
    // Always enable at the moment. Managing states of breakpoint is not supported for now.
    private boolean enabled = true;
    private String expression;

    public BreakPointImpl(Location location, String expression) {
        this.location = location;
        this.expression = expression;
    }

    public BreakPointImpl(Location location) {
        this.location = location;
    }

    public BreakPointImpl() {
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getCondition() {
        return expression;
    }


    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BreakPointImpl other = (BreakPointImpl)o;
        return location == null ? other.location == null : location.equals(other.location);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (location == null ? 0 : location.hashCode());
        return hash;
    }

    @Override
    public String toString() {
        return "BreakPointImpl{" +
               "location=" + location +
               ", enabled=" + enabled +
               '}';
    }
}
