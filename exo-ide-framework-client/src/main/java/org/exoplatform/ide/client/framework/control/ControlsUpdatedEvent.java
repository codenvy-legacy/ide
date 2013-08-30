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
package org.exoplatform.ide.client.framework.control;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.gwtframework.ui.client.command.Control;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ControlsUpdatedEvent extends GwtEvent<ControlsUpdatedHandler> {

    public static final GwtEvent.Type<ControlsUpdatedHandler> TYPE = new GwtEvent.Type<ControlsUpdatedHandler>();

    private List<Control> controls;

    public ControlsUpdatedEvent(List<Control> controls) {
        this.controls = controls;
    }

    public List<Control> getControls() {
        return controls;
    }

    @Override
    protected void dispatch(ControlsUpdatedHandler handler) {
        handler.onControlsUpdated(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ControlsUpdatedHandler> getAssociatedType() {
        return TYPE;
    }

}
