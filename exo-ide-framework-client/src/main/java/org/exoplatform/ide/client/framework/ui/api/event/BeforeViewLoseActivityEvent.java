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
package org.exoplatform.ide.client.framework.ui.api.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.client.framework.ui.api.View;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class BeforeViewLoseActivityEvent extends GwtEvent<BeforeViewLoseActivityHandler> {

    public static final GwtEvent.Type<BeforeViewLoseActivityHandler> TYPE =
            new GwtEvent.Type<BeforeViewLoseActivityHandler>();

    private View view;

    public BeforeViewLoseActivityEvent(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<BeforeViewLoseActivityHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(BeforeViewLoseActivityHandler handler) {
        handler.onBeforeViewLoseActivity(this);
    }

}
