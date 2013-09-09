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
package org.exoplatform.ide.client.operation.deleteitem;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DeleteItemView extends ViewImpl implements
                                             org.exoplatform.ide.client.operation.deleteitem.DeleteItemsPresenter.Display {

    private static final String ID = "ideDeleteItemsView";

    public static final int DEFAULT_WIDTH = 500;

    public static final int DEFAULT_HEIGHT = 150;

    public static final String ID_OK_BUTTON = "ideDeleteItemFormOkButton";

    public static final String ID_CANCEL_BUTTON = "ideDeleteItemFormCancelButton";

    private static final String TITLE = IDE.NAVIGATION_CONSTANT.deleteItemTitle();

    private HorizontalPanel infoLayout;

    private ImageButton deleteButton;

    private ImageButton cancelButton;

    private Label promptField;

    public DeleteItemView() {
        super(ID, ViewType.MODAL, TITLE, new Image(IDEImageBundle.INSTANCE.delete()), DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setCloseOnEscape(true);

        VerticalPanel mainLayout = new VerticalPanel();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setSpacing(10);
        mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        mainLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        add(mainLayout);

        infoLayout = new HorizontalPanel();
        infoLayout.setWidth("100%");
        infoLayout.setHeight(32 + "px");
        infoLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        createImage();
        createPromptField();

        mainLayout.add(infoLayout);
        mainLayout.add(createButtonsLayout());
    }

    private void createImage() {
        Image image = new Image(Images.Dialogs.ASK);
        image.setWidth(32 + "px");
        image.setHeight(32 + "px");
        infoLayout.add(image);
        infoLayout.setCellHorizontalAlignment(image, HasHorizontalAlignment.ALIGN_CENTER);
    }

    private void createPromptField() {
        promptField = new Label();
        promptField.setIsHTML(true);
        infoLayout.add(promptField);
        infoLayout.setCellHorizontalAlignment(promptField, HasHorizontalAlignment.ALIGN_LEFT);
    }

    private HorizontalPanel createButtonsLayout() {
        HorizontalPanel buttonsLayout = new HorizontalPanel();
        buttonsLayout.setHeight(22 + "px");
        buttonsLayout.setSpacing(5);

        deleteButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.yesButton());
        deleteButton.setButtonId(ID_OK_BUTTON);
        deleteButton.setWidth("90px");
        deleteButton.setHeight("22px");
        deleteButton.setImage(new Image(Images.Buttons.YES));

        cancelButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.noButton());
        cancelButton.setButtonId(ID_CANCEL_BUTTON);
        cancelButton.setWidth("90px");
        cancelButton.setHeight("22px");
        cancelButton.setImage(new Image(Images.Buttons.NO));

        buttonsLayout.add(deleteButton);
        buttonsLayout.add(cancelButton);

        return buttonsLayout;
    }

    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    public HasClickHandlers getDeleteButton() {
        return deleteButton;
    }

    @Override
    public HasValue<String> getPromptField() {
        return promptField;
    }

}
