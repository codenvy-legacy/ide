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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;

/**
 * Event occurs, when AWS application's environment info were changed.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EnvironmentStatusChangedEvent.java Oct 2, 2012 10:50:18 AM azatsarynnyy $
 */
public class EnvironmentInfoChangedEvent extends GwtEvent<EnvironmentInfoChangedHandler> {

    /** Environment. */
    private EnvironmentInfo environment;

    /** Type used to register event. */
    public static final GwtEvent.Type<EnvironmentInfoChangedHandler> TYPE =
            new GwtEvent.Type<EnvironmentInfoChangedHandler>();

    /**
     * @param environment
     *         environment
     */
    public EnvironmentInfoChangedEvent(EnvironmentInfo environment) {
        this.environment = environment;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EnvironmentInfoChangedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(EnvironmentInfoChangedHandler handler) {
        handler.onEnvironmentInfoChanged(this);
    }

    /** @return the environment */
    public EnvironmentInfo getEnvironment() {
        return environment;
    }

}
