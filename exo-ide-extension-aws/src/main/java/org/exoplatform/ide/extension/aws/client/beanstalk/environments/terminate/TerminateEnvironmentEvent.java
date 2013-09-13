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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.terminate;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;

/**
 * Event occurs, when user tries to terminate application's environment.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 20, 2012 4:52:19 PM anya $
 */
public class TerminateEnvironmentEvent extends GwtEvent<TerminateEnvironmentHandler> {

    /** Type, used to register event. */
    public static final GwtEvent.Type<TerminateEnvironmentHandler> TYPE =
            new GwtEvent.Type<TerminateEnvironmentHandler>();

    private EnvironmentInfo environmentInfo;

    private TerminateEnvironmentStartedHandler terminateEnvironmentStartedHandler;

    public TerminateEnvironmentEvent(EnvironmentInfo environmentInfo,
                                     TerminateEnvironmentStartedHandler terminateEnvironmentStartedHandler) {
        this.environmentInfo = environmentInfo;
        this.terminateEnvironmentStartedHandler = terminateEnvironmentStartedHandler;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<TerminateEnvironmentHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(TerminateEnvironmentHandler handler) {
        handler.onTerminateEnvironment(this);
    }

    public EnvironmentInfo getEnvironmentInfo() {
        return environmentInfo;
    }

    /** @return the terminateEnvironmentStartedHandler */
    public TerminateEnvironmentStartedHandler getTerminateEnvironmentStartedHandler() {
        return terminateEnvironmentStartedHandler;
    }

}
