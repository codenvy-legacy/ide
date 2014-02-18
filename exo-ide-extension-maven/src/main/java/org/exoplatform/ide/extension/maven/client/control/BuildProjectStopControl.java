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
package org.exoplatform.ide.extension.maven.client.control;

import com.google.gwt.user.client.ui.HTML;

import org.exoplatform.gwtframework.ui.client.command.StatusTextControl;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.maven.client.BuilderClientBundle;
import org.exoplatform.ide.extension.maven.client.event.*;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@RolesAllowed({"workspace/developer"})
public class BuildProjectStopControl extends StatusTextControl implements IDEControl,
                                                                          BuildProjectHandler,
                                                                          ProjectBuiltHandler {
    public static final String ID = "__request-build-cancel-notification-control";

    public BuildProjectStopControl() {
        super(ID);
        setPrompt(new HTML("Cancel Build").getText());
        setText(htmlCancelButton());
        setSize(25);
        setEvent(new BuildProjectStopEvent());
    }

    @Override
    public void initialize() {
        setVisible(false);
        setEnabled(false);

        IDE.addHandler(BuildProjectEvent.TYPE, this);
        IDE.addHandler(ProjectBuiltEvent.TYPE, this);
    }

    @Override
    public void onBuildProject(BuildProjectEvent event) {
        setVisible(true);
        setEnabled(true);
    }

    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        setVisible(false);
        setEnabled(true);
    }

    private String htmlCancelButton() {

        return  "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"height:16px; border-collapse: collapse;width:100%; " +
                "table-layout: fixed;\">"
                + "<tr>"
                + "<td style=\"border: none; padding-top: 3px;"
                + " text-align: center; overflow: hidden; white-space: nowrap; width:auto; text-overflow:"
                + " ellipsis;\">"
                + ImageHelper.getImageHTML(BuilderClientBundle.INSTANCE.buildStop())
                + "</td>"
                + "</tr>" + "</table>";
    }
}