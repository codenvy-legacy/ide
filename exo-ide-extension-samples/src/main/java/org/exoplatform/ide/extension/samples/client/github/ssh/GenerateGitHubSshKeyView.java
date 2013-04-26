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
package org.exoplatform.ide.extension.samples.client.github.ssh;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;

/**
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 */
public class GenerateGitHubSshKeyView extends ViewImpl implements GenerateGitHubSshKeyPresenter.Display {

    private static final String ID               = "ideGenerateGitHubSshKeyView";

    private static final int    WIDTH            = 450;

    private static final int    HEIGHT           = 180;

    private static final String LABEL_ID         = "ideGenerateGitHubSshKeyViewLabel";

    private static final String OK_BUTTON_ID     = "ideGenerateGitHubSshKeyViewOkButton";

    private static final String CANCEL_BUTTON_ID = "ideGenerateGitHubSshKeyViewCancelButton";


    interface GenerateGitHubSshKeyViewUiBinder extends UiBinder<Widget, GenerateGitHubSshKeyView> {
    }

    private static GenerateGitHubSshKeyViewUiBinder uiBinder = GWT.create(GenerateGitHubSshKeyViewUiBinder.class);

    @UiField
    ImageButton                                     okButton;

    @UiField
    ImageButton                                     cancelButton;

    @UiField
    Label                                           label;

    public GenerateGitHubSshKeyView() {
        super(ID, ViewType.MODAL, SamplesExtension.LOCALIZATION_CONSTANT.githubSshKeyTitle(),
              new Image(SamplesClientBundle.INSTANCE.gitHub()), WIDTH, HEIGHT, true);
        add(uiBinder.createAndBindUi(this));
        label.setID(LABEL_ID);
        okButton.setId(OK_BUTTON_ID);
        cancelButton.setId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.samples.client.oauth.GenerateGitHubSshKeyView.Display#getOkButton() */
    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    /** @see org.exoplatform.ide.extension.samples.client.oauth.GenerateGitHubSshKeyView.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.samples.client.oauth.GenerateGitHubSshKeyView.Display#getLabel() */
    @Override
    public HasValue<String> getLabel() {
        return label;
    }
}
