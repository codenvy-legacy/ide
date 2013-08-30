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
package org.exoplatform.ide.client.framework.ui.impl;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.*;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 3, 2010 $
 */
public class ViewHighlightManager implements ViewClosedHandler {

    /** Instance of this Highlighting Manager. */
    private static ViewHighlightManager instance;

    /** Currently active view. */
    private View currentActiveView;

    /** Previous active view. */
    private View previousActiveView;

    /** Event Bus. */
    private HandlerManager eventBus;

    /**
     * Creates new instance of this Highlighting Manager.
     *
     * @param eventBus
     *         event bus
     */
    public ViewHighlightManager(HandlerManager eventBus) {
        this.eventBus = eventBus;
        instance = this;

        eventBus.addHandler(ViewClosedEvent.TYPE, this);
    }

    /**
     * Sets view activated.
     *
     * @param view
     *         view to be activated
     */
    public void activateView(View view) {
        if (currentActiveView == view) {
            return;
        }

        previousActiveView = currentActiveView;
        if (currentActiveView != null) {
            currentActiveView.fireEvent(new BeforeViewLoseActivityEvent(currentActiveView));
            ((ViewImpl)currentActiveView).setActivated(false);
            currentActiveView.fireEvent(new ViewLostActivityEvent(currentActiveView));
        }

        currentActiveView = view;
        ((ViewImpl)currentActiveView).setActivated(true);

        eventBus.fireEvent(new ViewActivatedEvent(view));
    }

    /**
     * Get instance of this Highlighting manager.
     *
     * @return the instance
     */
    public static ViewHighlightManager getInstance() {
        if (instance == null) {
            new ViewHighlightManager(null);
        }

        return instance;
    }

    /**
     * View Closed Handler
     *
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() != currentActiveView) {
            return;
        }

        if (previousActiveView != null) {
            ((ViewImpl)previousActiveView).setActivated(false);
            currentActiveView = previousActiveView;
            ((ViewImpl)currentActiveView).setActivated(true);
            eventBus.fireEvent(new ViewActivatedEvent(currentActiveView));
        } else {
            currentActiveView = null;
        }
    }

}
