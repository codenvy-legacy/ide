/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.samples.client.githubimport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.github.load.ProjectData;

/**
 * View for importing projects from GitHub.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubView.java Dec 7, 2011 3:37:28 PM vereshchaka $
 */
public class ImportFromGithubView extends ViewImpl implements ImportFromGithubPresenter.Display {
    private static final String ID = "ideImportFromGithubView";

    private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.importLoadDialogTitle();

    private static final int HEIGHT = 365;

    private static final int WIDTH = 500;

    private static final String READONLY_MODE_FIELD_ID = "ideImportFromGithubViewReadOnlyModeField";

    private static final String NAME_FIELD_ID = "ideImportFromGithubViewNameField";

    private static final String FINISH_BUTTON_ID = "ideImportFromGithubViewFinishButton";

    private static final String CANCEL_BUTTON_ID = "ideImportFromGithubViewCancelButton";

    interface ImportFromGithubViewUiBinder extends UiBinder<Widget, ImportFromGithubView> {
    }

    private static ImportFromGithubViewUiBinder uiBinder = GWT.create(ImportFromGithubViewUiBinder.class);

    /** Cancel button. */
    @UiField
    ImageButton cancelButton;

    /** Next button. */
    @UiField
    ImageButton finishButton;

    /** GitHub repositories grid. */
    @UiField
    GitHubProjectsListGrid repositoriesGrid;

    /** Project's name field. */
    @UiField
    TextInput projectNameField;

    /** Read-only mode field. */
    @UiField
    CheckBox readOnlyModeField;

    public ImportFromGithubView() {
        super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));

        finishButton.setButtonId(FINISH_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);

        projectNameField.setName(NAME_FIELD_ID);
        readOnlyModeField.setName(READONLY_MODE_FIELD_ID);
    }

    /** @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getFinishButton() */
    @Override
    public HasClickHandlers getFinishButton() {
        return finishButton;
    }

    /** @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getRepositoriesGrid() */
    @Override
    public ListGridItem<ProjectData> getRepositoriesGrid() {
        return repositoriesGrid;
    }

    /** @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getProjectNameField() */
    @Override
    public HasValue<String> getProjectNameField() {
        return projectNameField;
    }

    /** @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#setFinishButtonEnabled(boolean) */
    @Override
    public void setFinishButtonEnabled(boolean enabled) {
        finishButton.setEnabled(enabled);
    }

    /** @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getReadOnlyModeField() */
    @Override
    public HasValue<Boolean> getReadOnlyModeField() {
        return readOnlyModeField;
    }
}
