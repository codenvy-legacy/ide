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
package org.exoplatform.ide.client.ui.panel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class HidePanelEvent extends GwtEvent<HidePanelHandler> {

    public static final GwtEvent.Type<HidePanelHandler> TYPE = new GwtEvent.Type<HidePanelHandler>();

    private String panelId;

    public HidePanelEvent(String panelId) {
        this.panelId = panelId;
    }

    public String getPanelId() {
        return panelId;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<HidePanelHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(HidePanelHandler handler) {
        handler.onHidePanel(this);
    }

}
