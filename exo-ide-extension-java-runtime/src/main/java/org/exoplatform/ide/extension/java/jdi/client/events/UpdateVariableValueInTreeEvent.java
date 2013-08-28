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
package org.exoplatform.ide.extension.java.jdi.client.events;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.java.jdi.shared.Variable;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: UpdateVariableValueInTreeEvent.java Apr 28, 2012 10:05:37 AM azatsarynnyy $
 */
public class UpdateVariableValueInTreeEvent extends GwtEvent<UpdateVariableValueInTreeHandler> {

    /** Type used to register this event. */
    public static final GwtEvent.Type<UpdateVariableValueInTreeHandler> TYPE =
            new GwtEvent.Type<UpdateVariableValueInTreeHandler>();

    /** Variable whose value need to update. */
    private Variable variable;

    /** New variables value. */
    private String value;

    public UpdateVariableValueInTreeEvent(Variable variable, String value) {
        this.variable = variable;
        this.value = value;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<UpdateVariableValueInTreeHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(UpdateVariableValueInTreeHandler handler) {
        handler.onUpdateVariableValueInTree(this);
    }

    /**
     * Returns variable whose value need to update.
     *
     * @return variable
     */
    public Variable getVariable() {
        return variable;
    }

    /**
     * Returns new variables value.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

}
