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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.rebuild;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;

/**
 * Event occurs, when user tries to rebuild application's environment.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: RebuildEnvironmentEvent.java Sep 28, 2012 3:49:51 PM azatsarynnyy $
 */
public class RebuildEnvironmentEvent extends GwtEvent<RebuildEnvironmentHandler> {

    /** Type, used to register event. */
    public static final GwtEvent.Type<RebuildEnvironmentHandler> TYPE = new GwtEvent.Type<RebuildEnvironmentHandler>();

    private EnvironmentInfo environmentInfo;

    private RebuildEnvironmentStartedHandler rebuildEnvironmentStartedHandler;

    public RebuildEnvironmentEvent(EnvironmentInfo environmentInfo,
                                   RebuildEnvironmentStartedHandler rebuildEnvironmentStartedHandler) {
        this.environmentInfo = environmentInfo;
        this.rebuildEnvironmentStartedHandler = rebuildEnvironmentStartedHandler;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RebuildEnvironmentHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(RebuildEnvironmentHandler handler) {
        handler.onRebuildEnvironment(this);
    }

    /** @return  */
    public EnvironmentInfo getEnvironmentInfo() {
        return environmentInfo;
    }

    /** @return the rebuildEnvironmentStartedHandler */
    public RebuildEnvironmentStartedHandler getRebuildEnvironmentStartedHandler() {
        return rebuildEnvironmentStartedHandler;
    }

}
