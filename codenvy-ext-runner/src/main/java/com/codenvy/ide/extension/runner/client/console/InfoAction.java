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
package com.codenvy.ide.extension.runner.client.console;

import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.action.CustomComponentAction;
import com.codenvy.ide.api.ui.action.Presentation;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.google.gwt.user.client.ui.Widget;

/**
 * Action used to show some information in console's toolbar.
 *
 * @author Artem Zatsarynnyy
 */
public class InfoAction extends Action implements CustomComponentAction {
    private String          caption;
    private boolean         isURL;
    private RunnerResources resources;

    public InfoAction(String caption, boolean isURL, RunnerResources resources) {
        super();
        this.caption = caption;
        this.isURL = isURL;
        this.resources = resources;
    }

    @Override
    public Widget createCustomComponent(Presentation presentation) {
        return new InfoLabel(caption, isURL, presentation, resources);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // no need to process any action, for now
    }

}
