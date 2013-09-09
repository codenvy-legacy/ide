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
package org.exoplatform.ide.client.framework.ui.impl.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Image;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ChangeViewIconEvent extends GwtEvent<ChangeViewIconHandler> {

    public static final GwtEvent.Type<ChangeViewIconHandler> TYPE = new GwtEvent.Type<ChangeViewIconHandler>();

    private String viewId;

    private Image icon;

    public ChangeViewIconEvent(String viewId, Image icon) {
        this.viewId = viewId;
        this.icon = icon;
    }

    public String getViewId() {
        return viewId;
    }

    public Image getIcon() {
        return icon;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ChangeViewIconHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ChangeViewIconHandler handler) {
        handler.onChangeViewIcon(this);
    }

}
