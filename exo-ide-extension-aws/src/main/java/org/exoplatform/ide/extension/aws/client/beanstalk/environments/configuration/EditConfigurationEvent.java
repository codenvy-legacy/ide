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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: EditConfigurationEvent.java Oct 5, 2012 1:17:17 PM azatsarynnyy $
 */
public class EditConfigurationEvent extends GwtEvent<EditConfigurationHandler> {
    /** Type used to register the event. */
    public static final GwtEvent.Type<EditConfigurationHandler> TYPE = new GwtEvent.Type<EditConfigurationHandler>();

    private EnvironmentInfo environment;

    private UpdateEnvironmentStartedHandler updateEnvironmentStartedHandler;

    public EditConfigurationEvent(EnvironmentInfo environment,
                                  UpdateEnvironmentStartedHandler updateEnvironmentStartedHandler) {
        this.environment = environment;
        this.updateEnvironmentStartedHandler = updateEnvironmentStartedHandler;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(EditConfigurationHandler handler) {
        handler.onEditConfiguration(this);
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditConfigurationHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Returns the environment.
     *
     * @return the environment
     */
    public EnvironmentInfo getEnvironment() {
        return environment;
    }

    /** @return the updateEnvironmentStartedHandler */
    public UpdateEnvironmentStartedHandler getUpdateEnvironmentStartedHandler() {
        return updateEnvironmentStartedHandler;
    }

}
