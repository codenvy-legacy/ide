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
package org.exoplatform.ide.extension.samples.client.githubimport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
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
    private static final String ID               = "ideImportFromGithubView";

    private static final String TITLE            = SamplesExtension.LOCALIZATION_CONSTANT.importFromGithubTitle();

    private static final int    HEIGHT           = 365;

    private static final int    WIDTH            = 500;

    private static final String ACCOUNT_NAME     = "ideImportFromGithubViewAccountBox";

    private static final String NAME_FIELD_ID    = "ideImportFromGithubViewNameField";

    private static final String FINISH_BUTTON_ID = "ideImportFromGithubViewFinishButton";

    private static final String CANCEL_BUTTON_ID = "ideImportFromGithubViewCancelButton";

    interface ImportFromGithubViewUiBinder extends UiBinder<Widget, ImportFromGithubView> {
    }

    private static ImportFromGithubViewUiBinder uiBinder = GWT.create(ImportFromGithubViewUiBinder.class);

    /** Cancel button. */
    @UiField
    ImageButton                                 cancelButton;

    /** Next button. */
    @UiField
    ImageButton                                 finishButton;

    /** GitHub repositories grid. */
    @UiField
    GitHubProjectsListGrid                      repositoriesGrid;

    /** Project's name field. */
    @UiField
    TextInput                                   projectNameField;

    /** Account name box. */
    @UiField
    ComboBoxField                               accountNameBox;

    public ImportFromGithubView() {
        super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));

        finishButton.setButtonId(FINISH_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);

        accountNameBox.setName(ACCOUNT_NAME);
        projectNameField.setName(NAME_FIELD_ID);
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

    /** @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#refreshRepositoriesGrid() */
    @Override
    public void refreshRepositoriesGrid() {
        repositoriesGrid.getCellTable().redraw();
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

    /** @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getAccountNameBox() */
    @Override
    public HasValue<String> getAccountNameBox() {
        return accountNameBox;
    }

    /**
     * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#setAccountNameValues(String[])
     */
    @Override
    public void setAccountNameValues(String[] values) {
        accountNameBox.setValueMap(values);
        if (accountNameBox.getValue().isEmpty()) {
            accountNameBox.setValue(values[0]);
        }
    }
}
