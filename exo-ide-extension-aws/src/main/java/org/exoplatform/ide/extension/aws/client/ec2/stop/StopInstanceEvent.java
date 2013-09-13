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
package org.exoplatform.ide.extension.aws.client.ec2.stop;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to stop an EC2 instance.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: RestartAppServerEvent.java Sep 28, 2012 3:49:51 PM azatsarynnyy $
 */
public class StopInstanceEvent extends GwtEvent<StopInstanceHandler> {

    /** Type, used to register event. */
    public static final GwtEvent.Type<StopInstanceHandler> TYPE = new GwtEvent.Type<StopInstanceHandler>();

    private String instanceId;

    public StopInstanceEvent(String instanceId) {
        this.instanceId = instanceId;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<StopInstanceHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(StopInstanceHandler handler) {
        handler.onStopInstance(this);
    }

    /** @return  */
    public String getInstanceId() {
        return instanceId;
    }

}
