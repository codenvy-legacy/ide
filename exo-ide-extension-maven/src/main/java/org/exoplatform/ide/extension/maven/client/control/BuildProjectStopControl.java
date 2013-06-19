/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
@RolesAllowed("developer")
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