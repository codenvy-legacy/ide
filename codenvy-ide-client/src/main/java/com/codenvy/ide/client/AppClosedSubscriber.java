/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.client;

import com.codenvy.api.factory.dto.Action;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.AppClosedActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.toolbar.PresentationFactory;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Sergii Leschenko
 */
public class AppClosedSubscriber {
    private final AppContext    appContext;
    private final ActionManager actionManager;

    @Inject
    public AppClosedSubscriber(AppContext appContext,
                               ActionManager actionManager) {
        this.appContext = appContext;
        this.actionManager = actionManager;
    }

    /**
     * Adds before unload event listener.
     */
    public native void addUnloadHandler() /*-{
        var instance = this;

        $wnd.onbeforeunload = function () {
            return instance.@com.codenvy.ide.client.AppClosedSubscriber::checkCancel()();
        }
    }-*/;

    private String checkCancel() {
        List<Action> actions = getOnAppClosedActions();
        for (Action action : actions) {
            com.codenvy.ide.api.action.Action ideAction = actionManager.getAction(action.getId());
            if (ideAction != null) {
                AppClosedActionEvent e = new AppClosedActionEvent("", new PresentationFactory().getPresentation(ideAction), actionManager,
                                                                  0, action.getProperties());
                ideAction.update(e);
                if (e.getPresentation().isEnabled() && e.getPresentation().isVisible()) {
                    ideAction.actionPerformed(e);

                    if (e.getAlertMessage() != null) {
                        return e.getAlertMessage();
                    }
                }
            }
        }

        return null;
    }

    private List<Action> getOnAppClosedActions() {
        if (appContext.getFactory() == null || appContext.getFactory().getIde() == null
            || appContext.getFactory().getIde().getOnAppClosed() == null ||
            appContext.getFactory().getIde().getOnAppClosed().getActions() == null) {
            return null;
        }

        return appContext.getFactory().getIde().getOnAppClosed().getActions();
    }
}
