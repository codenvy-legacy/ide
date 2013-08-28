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
package org.exoplatform.ide.extension.samples.client.github.load;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;

import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: GithubSamplesView.java Aug 30, 2011 12:13:08 PM vereshchaka $
 */
public class ShowSamplesView extends ViewImpl implements ShowSamplesPresenter.Display {

    private static final String ID = "GithubSamplesView";

    private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.importLoadDialogTitle();

    private static final int HEIGHT = 365;

    private static final int WIDTH = 580;

    /**
     *
     */
    interface GithubSamplesViewUiBinder extends UiBinder<Widget, ShowSamplesView> {
    }

    /** UIBinder instance */
    private static GithubSamplesViewUiBinder uiBinder = GWT.create(GithubSamplesViewUiBinder.class);

    @UiField
    SamplesListGrid samplesListGrid;

    @UiField
    ImageButton nextButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    TextInput loadProjectName;

    public ShowSamplesView() {
        super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));
    }

    /** @see org.exoplatform.ide.client.ShowSamplesPresenter.samples.GithubSamplesPresenter.Display#getNextButton() */
    @Override
    public HasClickHandlers getNextButton() {
        return nextButton;
    }

    /** @see org.exoplatform.ide.client.ShowSamplesPresenter.samples.GithubSamplesPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.client.ShowSamplesPresenter.samples.GithubSamplesPresenter.Display#enableNextButton(boolean) */
    @Override
    public void enableNextButton(boolean enable) {
        nextButton.setEnabled(enable);
    }

    /** @see org.exoplatform.ide.client.ShowSamplesPresenter.samples.GithubSamplesPresenter.Display#getSamplesListGridAlt() */
    @Override
    public ListGridItem<ProjectData> getSamplesListGrid() {
        return samplesListGrid;
    }

    /** @see org.exoplatform.ide.client.ShowSamplesPresenter.samples.GithubSamplesPresenter.Display#getSelectedItems() */
    @Override
    public List<ProjectData> getSelectedItems() {
        return samplesListGrid.getSelectedItems();
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.load.ShowSamplesPresenter.Display#getProjectNameField() */
    @Override
    public HasValue<String> getProjectNameField() {
        return loadProjectName;
    }

    /** @see org.exoplatform.ide.extension.samples.client.github.load.ShowSamplesPresenter.Display#focusInNameField() */
    @Override
    public void focusInNameField() {
        loadProjectName.focus();
    }

}
