/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.samples.client.startpage;

import com.codenvy.ide.commons.IDEWorkspace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;

import java.util.List;


/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id: ReadOnlyUserView.java Jun 26, 2013 vetal $
 */
public class ReadOnlyUserView extends ViewImpl {

    private static final String             ID               = "ideReadOnlyUserView";

    private static final int                WIDTH            = 450;

    private static final int                HEIGHT           = 180;

    public static final String              LABEL_ID         = "ideReadOnlyUserViewLabel";

    private static final String             ACTION_BUTTON_ID = "ideReadOnlyUserViewJoinButton";

    private static final String             CANCEL_BUTTON_ID = "ideReadOnlyUserViewCancelButton";

    private static ReadOnlyUserViewUiBinder uiBinder         = GWT.create(ReadOnlyUserViewUiBinder.class);

    interface ReadOnlyUserViewUiBinder extends UiBinder<Widget, ReadOnlyUserView> {
    }

    @UiField
    ImageButton actionButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    Label       label;

    public ReadOnlyUserView(final List<IDEWorkspace> workspaces) {
        super(ID, ViewType.MODAL, workspaces.size() == 0 ? SamplesExtension.LOCALIZATION_CONSTANT.joinCodenvyTitle()
            : SamplesExtension.LOCALIZATION_CONSTANT.switchWorkspaceTitle(),
              null, WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));
        label.setID(LABEL_ID);
        label.setIsHTML(true);
        actionButton.setId(ACTION_BUTTON_ID);
        cancelButton.setId(CANCEL_BUTTON_ID);
        if (workspaces.size() == 0) {
            label.setValue(SamplesExtension.LOCALIZATION_CONSTANT.joinCodenvyMessage());
            actionButton.setText(SamplesExtension.LOCALIZATION_CONSTANT.joinCodenvyTitle());
        }
        else {
            label.setValue(SamplesExtension.LOCALIZATION_CONSTANT.switchWorkspaceMessage());
            actionButton.setText(SamplesExtension.LOCALIZATION_CONSTANT.switchWorkspace());
        }

        actionButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                UrlBuilder builder = new UrlBuilder();
                if (workspaces.size() == 0)
                    Window.Location.replace(builder.setProtocol(Location.getProtocol()).setHost(Location.getHost()).setPath("/login")
                                                   .buildString());
                else if (workspaces.size() == 1)
                    Window.Location.replace(workspaces.get(0).getUrl());
                else
                    Window.Location.replace(builder.setProtocol(Location.getProtocol()).setHost(Location.getHost())
                                                   .setPath("/private/select-tenant").buildString());
            }
        });

        cancelButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(ID);
            }
        });
    }


}
