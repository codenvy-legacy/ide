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
package com.codenvy.ide.factory.client.factory;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.json.client.JSONObject;

/**
 * This event is fired just after successfully reading a Factory bean by {@link FactoryHandler}
 * 
 * @author Vitaliy Guluy
 */
public class FactoryReceivedEvent extends GwtEvent<FactoryReceivedHandler> {

    public static final GwtEvent.Type<FactoryReceivedHandler> TYPE = new GwtEvent.Type<FactoryReceivedHandler>();

    private JSONObject                                        factory;

    public FactoryReceivedEvent(JSONObject factory) {
        this.factory = factory;
    }


    public JSONObject getFactory() {
        return factory;
    }


    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<FactoryReceivedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FactoryReceivedHandler handler) {
        handler.onFactoryReceived(this);
    }

}
