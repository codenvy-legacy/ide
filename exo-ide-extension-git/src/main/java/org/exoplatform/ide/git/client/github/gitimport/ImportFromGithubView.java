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
package org.exoplatform.ide.git.client.github.gitimport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.Border;
import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * View for importing projects from GitHub.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubView.java Dec 7, 2011 3:37:28 PM vereshchaka $
 */
public class ImportFromGithubView extends ViewImpl implements ImportFromGithubPresenter.Display {
    private static final String ID = "ideImportFromGithubView";

    private static final String TITLE = GitExtension.MESSAGES.importFromGithubTitle();

    private static final int HEIGHT = 380;

    private static final int WIDTH = 520;

    private static final String ACCOUNT_NAME = "ideImportFromGithubViewAccountBox";

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

    /** Account name box. */
    @UiField
    ComboBoxField accountNameBox;

    /**
     * Scroll panel with grid.
     */
    @UiField
    ScrollPanel scrollPanel;

    /**
     * Border to be shown on error.
     */
    @UiField
    Border errorBorder;

    /**
     * Explanation label.
     */
    @UiField
    Label explanationLabel;

    /**
     * Label with error message.
     */
    @UiField
    Label errorLabel;

    /**
     * Handler for event, when scroll reaches end of the page.
     */
    ScrollEndHandler scrollEndHandler;

    /**
     * The list of current values of the account field.
     */
    ArrayList<String> accountValues = new ArrayList<String>();

    /**
     * Used to count scroll position.
     */
    private int incrementSize = 20;

    /**
     * Used to count scroll position.
     */
    private int lastScrollPos = 0;


    public ImportFromGithubView() {
        super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));

        finishButton.setButtonId(FINISH_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);

        accountNameBox.setName(ACCOUNT_NAME);
        projectNameField.setName(NAME_FIELD_ID);

        scrollPanel.addScrollHandler(new ScrollHandler() {

            @Override
            public void onScroll(ScrollEvent event) {
                int oldScrollPos = lastScrollPos;
                lastScrollPos = scrollPanel.getVerticalScrollPosition();

                // If scrolling up, ignore the event.
                if (oldScrollPos >= lastScrollPos) {
                    return;
                }

                // Height of grid contents (including outside the viewable area) - height of the scroll panel
                int maxScrollTop = scrollPanel.getWidget().getOffsetHeight() -
                                   scrollPanel.getOffsetHeight();

                if (lastScrollPos >= maxScrollTop && scrollEndHandler != null) {
                    scrollEndHandler.onScrollEnd();
                }

            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** {@inheritDoc} */
    @Override
    public HasClickHandlers getFinishButton() {
        return finishButton;
    }

    /** {@inheritDoc} */
    @Override
    public ListGridItem<ProjectData> getRepositoriesGrid() {
        return repositoriesGrid;
    }

    /** {@inheritDoc} */
    @Override
    public void refreshRepositoriesGrid() {
        repositoriesGrid.getCellTable().redraw();
    }

    /** {@inheritDoc} */
    @Override
    public HasValue<String> getProjectNameField() {
        return projectNameField;
    }

    /** {@inheritDoc} */
    @Override
    public void setFinishButtonEnabled(boolean enabled) {
        finishButton.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public TextFieldItem getAccountNameBox() {
        return accountNameBox;
    }

    /** {@inheritDoc} */
    @Override
    public void setAccountNameValues(String[] values) {
        accountValues = new ArrayList<String>(Arrays.asList(values));
        accountNameBox.setValueMap(values);
        if (accountNameBox.getValue().isEmpty()) {
            accountNameBox.setValue(values[0], true);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addAccountNameValue(String value) {
        accountValues.add(value);
        accountNameBox.setValueMap(accountValues.toArray(new String[accountValues.size()]));
    }

    /** {@inheritDoc} */
    @Override
    public void setScrollEndHandler(ScrollEndHandler handler) {
        scrollEndHandler = handler;
    }

    /** {@inheritDoc} */
    @Override
    public void addRepositories(List<ProjectData> values) {
        repositoriesGrid.addItems(values);
    }

    /** {@inheritDoc} */
    @Override
    public void changeAccountFieldState(boolean isError) {
        errorLabel.setVisible(isError);
        explanationLabel.setVisible(!isError);
        errorBorder.setBorderSize(isError ? 1 : 0);
    }

    /** {@inheritDoc} */
    @Override
    public void setErrorMessage(String error) {
        errorLabel.getElement().setInnerHTML(error);
    }
}
