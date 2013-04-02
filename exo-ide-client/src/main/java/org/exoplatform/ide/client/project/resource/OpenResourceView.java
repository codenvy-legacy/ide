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

package org.exoplatform.ide.client.project.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OpenResourceView extends ViewImpl implements
                                               org.exoplatform.ide.client.project.resource.OpenResourcePresenter.Display {

    private static OpenResourceViewUiBinder uiBinder = GWT.create(OpenResourceViewUiBinder.class);

    interface OpenResourceViewUiBinder extends UiBinder<Widget, OpenResourceView> {
    }

    /** Initial width of this view */
    private static final int WIDTH = 550;

    /** Initial height of this view */
    private static final int HEIGHT = 350;

    /** View ID */
    public static final String ID = "ideOpenResourceView";

    /** View Title */
    public static final String TITLE = "Open Resource";

    /** Files list grid */
    @UiField
    FilesListGrid filesListGrid;

    /** Open button */
    @UiField
    ImageButton openButton;

    /** Cancel button */
    @UiField
    ImageButton cancelButton;

    /** File name field */
    @UiField
    TextInput fileNameField;

    /** Parent panel for  icon "folder" */
    @UiField
    DivElement folderIconElement;

    /** Parent panel for folder path */
    @UiField
    DivElement folderNameElement;

    public OpenResourceView() {
        super(ID, "modal", TITLE, new Image(IDEImageBundle.INSTANCE.openResource()), WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
        fileNameField.focus();
        String imageHTML = ImageHelper.getImageHTML(IDEImageBundle.INSTANCE.folder());
        folderIconElement.setInnerHTML(imageHTML);
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
    public TextFieldItem getFileNameField() {
        return fileNameField;
    }

    @Override
    public ListGridItem<FileModel> getFilesListGrid() {
        return filesListGrid;
    }

    @Override
    public void focusListGrid() {
        filesListGrid.getCellTable().setFocus(true);
    }

    @Override
    public List<FileModel> getSelectedItems() {
        return filesListGrid.getSelectedItems();
    }

    @Override
    public HasAllKeyHandlers listGrid() {
        return filesListGrid;
    }

    @Override
    public void setItemFolderName(String folderName) {
        if (folderName == null) {
            folderNameElement.getStyle().setDisplay(Display.NONE);
            folderIconElement.getStyle().setDisplay(Display.NONE);
            folderNameElement.removeAttribute("title");
        } else {
            folderNameElement.setInnerHTML(folderName);
            folderNameElement.setAttribute("title", folderName);
            folderNameElement.getStyle().setDisplay(Display.BLOCK);
            folderIconElement.getStyle().setDisplay(Display.BLOCK);
        }
    }

}
