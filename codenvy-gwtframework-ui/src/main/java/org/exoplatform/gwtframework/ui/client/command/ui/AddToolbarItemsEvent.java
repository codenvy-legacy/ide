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
package org.exoplatform.gwtframework.ui.client.command.ui;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $
 */

public class AddToolbarItemsEvent extends GwtEvent<AddToolbarItemsHandler> {

    public static final GwtEvent.Type<AddToolbarItemsHandler> TYPE = new GwtEvent.Type<AddToolbarItemsHandler>();

    private Widget widget;
    
    private boolean rightDocking;

    public AddToolbarItemsEvent(Widget widget) {
        this.widget = widget;
        rightDocking = false;
    }
    
    public AddToolbarItemsEvent(Widget widget, boolean rightDocking) {
        this.widget = widget;
        this.rightDocking = rightDocking;
    }

    @Override
    protected void dispatch(AddToolbarItemsHandler handler) {
        handler.onAddToolbarItems(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<AddToolbarItemsHandler> getAssociatedType() {
        return TYPE;
    }

    public Widget getWidget() {
        return widget;
    }

    public boolean isRightDocking() {
        return rightDocking;
    }

}
