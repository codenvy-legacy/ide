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

package org.exoplatform.ide.client.project.properties;

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
import org.exoplatform.ide.vfs.shared.Property;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectPropertiesView extends ViewImpl implements
                                                    org.exoplatform.ide.client.project.properties.ProjectPropertiesPresenter.Display {

    private static ProjectPropertiesViewUiBinder uiBinder = GWT.create(ProjectPropertiesViewUiBinder.class);

    interface ProjectPropertiesViewUiBinder extends UiBinder<Widget, ProjectPropertiesView> {
    }

    public static final String ID = "ideProjectPropertiesView";

    public static final String TITLE = "Project Properties";

    /** Initial width of this view */
    private static final int WIDTH = 550;

    /** Initial height of this view */
    private static final int HEIGHT = 280;

    @UiField
    PropertiesListGrid propertiesListGrid;

    // @UiField
    // ImageButton addButton;

    @UiField
    ImageButton editButton;

    @UiField
    ImageButton deleteButton;

    @UiField
    ImageButton okButton;

    @UiField
    ImageButton cancelButton;

    public ProjectPropertiesView() {
        super(ID, "modal", TITLE, new Image(IDEImageBundle.INSTANCE.projectProperties()), WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
    }

    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    @Override
    public void setOkButtonEnabled(boolean enabled) {
        okButton.setEnabled(enabled);
    }

    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    @Override
    public ListGridItem<Property> getPropertiesListGrid() {
        return propertiesListGrid;
    }

    // @Override
    // public HasClickHandlers getAddButton()
    // {
    // return addButton;
    // }

    @Override
    public HasClickHandlers getEditButton() {
        return editButton;
    }

    @Override
    public void setEditButtonEnabled(boolean enabled) {
        editButton.setEnabled(enabled);
    }

    @Override
    public HasClickHandlers getDeleteButton() {
        return deleteButton;
    }

    @Override
    public void setDeleteButtonEnabled(boolean enabled) {
        deleteButton.setEnabled(enabled);
    }

}
