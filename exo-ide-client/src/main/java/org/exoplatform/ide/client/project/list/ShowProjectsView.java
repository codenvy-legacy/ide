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

package org.exoplatform.ide.client.project.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowProjectsView extends ViewImpl implements
                                               org.exoplatform.ide.client.project.list.ShowProjectsPresenter.Display {

    private static ShowProjectsViewUiBinder uiBinder = GWT.create(ShowProjectsViewUiBinder.class);

    interface ShowProjectsViewUiBinder extends UiBinder<Widget, ShowProjectsView> {
    }

    public static final String ID = "ideShowProjectsView";

    public static final String TITLE = "Projects";

    /** Initial width of this view */
    private static final int WIDTH = 500;

    /** Initial height of this view */
    private static final int HEIGHT = 280;

    @UiField
    ProjectsListGrid projectsListGrid;

    @UiField
    ImageButton openButton;

    @UiField
    ImageButton cancelButton;

    public ShowProjectsView() {
        super(ID, "modal", TITLE, new Image(IDEImageBundle.INSTANCE.projectExplorer()), WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
    }

    @Override
    public ListGridItem<ProjectModel> getProjectsListGrid() {
        return projectsListGrid;
    }

    @Override
    public HasClickHandlers getOpenButton() {
        return openButton;
    }

    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    @Override
    public void setOpenButtonEnabled(boolean enabled) {
        openButton.setEnabled(enabled);
    }

    @Override
    public List<ProjectModel> getSelectedItems() {
        return projectsListGrid.getSelectedItems();
    }

}
