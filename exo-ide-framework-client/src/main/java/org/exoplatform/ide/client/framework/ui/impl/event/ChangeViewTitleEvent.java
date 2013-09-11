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

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ChangeViewTitleEvent extends GwtEvent<ChangeViewTitleHandler> {

    public static final GwtEvent.Type<ChangeViewTitleHandler> TYPE = new GwtEvent.Type<ChangeViewTitleHandler>();

    private String viewId;

    private String title;

    public ChangeViewTitleEvent(String viewId, String title) {
        this.viewId = viewId;
        this.title = title;
    }

    public String getViewId() {
        return viewId;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ChangeViewTitleHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ChangeViewTitleHandler handler) {
        handler.onChangeViewTitle(this);
    }

}
