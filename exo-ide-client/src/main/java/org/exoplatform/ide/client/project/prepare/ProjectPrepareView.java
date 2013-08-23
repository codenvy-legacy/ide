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
package org.exoplatform.ide.client.project.prepare;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ProjectPrepareView extends ViewImpl implements
                                                 ProjectPreparePresenter.Display {
    private static ProjectPrepareViewUiBinder uiBinder = GWT.create(ProjectPrepareViewUiBinder.class);

    interface ProjectPrepareViewUiBinder extends UiBinder<Widget, ProjectPrepareView> {
    }

    public static final String ID = "ideProjectPrepareView";

    public static final String TITLE = "Select project type";

    /** Initial width of this view */
    private static final int WIDTH = 350;

    /** Initial height of this view */
    private static final int HEIGHT = 100;

    @UiField
    ImageButton okButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    SelectItem projectTypeField;

    public ProjectPrepareView() {
        super(ID, "modal", TITLE, new Image(IDEImageBundle.INSTANCE.projectExplorer()), WIDTH, HEIGHT, false);
        setCloseOnEscape(false);
        setCanBeClosed(false);
        add(uiBinder.createAndBindUi(this));
    }

    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    @Override
    public HasValue<String> getProjectTypeField() {
        return projectTypeField;
    }

    @Override
    public void setProjectTypeValues(String[] types) {
        projectTypeField.setValueMap(types);
    }
}
