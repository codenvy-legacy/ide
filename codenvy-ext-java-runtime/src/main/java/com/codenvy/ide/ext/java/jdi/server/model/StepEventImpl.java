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


import com.codenvy.ide.ext.java.jdi.shared.Location;
import com.codenvy.ide.ext.java.jdi.shared.StepEvent;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class StepEventImpl implements StepEvent {
    private Location location;

    public StepEventImpl(Location location) {
        this.location = location;
    }

    public StepEventImpl() {
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public final int getType() {
        return STEP;
    }

    @Override
    public String toString() {
        return "StepEventImpl{" +
               "location=" + location +
               '}';
    }
}
