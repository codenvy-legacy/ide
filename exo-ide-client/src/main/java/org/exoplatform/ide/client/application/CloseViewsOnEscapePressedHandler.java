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

package org.exoplatform.ide.client.application;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CloseViewsOnEscapePressedHandler implements ViewActivatedHandler {

    private View activeView;

    public CloseViewsOnEscapePressedHandler() {
        Event.addNativePreviewHandler(nativePreviewHandler);

        IDE.addHandler(ViewActivatedEvent.TYPE, this);
    }

    private NativePreviewHandler nativePreviewHandler = new NativePreviewHandler() {
        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            try {
                if (event.getNativeEvent().getType() == null) {
                    return;
                }
            } catch (Exception e) {
                System.out.println("Undefined exception. " + e.getMessage());
                return;
            }

            if (Event.ONKEYDOWN == event.getTypeInt() && event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE
                && activeView != null && activeView.closeOnEscape()) {
                IDE.getInstance().closeView(activeView.getId());
                event.cancel();
                return;
            }

        }
    };

    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        activeView = event.getView();
    }

}
