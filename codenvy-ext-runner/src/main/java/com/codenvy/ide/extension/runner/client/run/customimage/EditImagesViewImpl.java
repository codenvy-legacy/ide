/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension.runner.client.run.customimage;

import elemental.dom.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link EditImagesView}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class EditImagesViewImpl extends Window implements EditImagesView {

    @UiField
    ScrollPanel listPanel;
    private Button                     btnAdd;
    private Button                     btnRemove;
    private Button                     btnEdit;
    private ActionDelegate             delegate;
    private RunnerLocalizationConstant localizationConstants;
    private SimpleList<ItemReference>  list;

    @Inject
    protected EditImagesViewImpl(com.codenvy.ide.Resources resources, RunnerLocalizationConstant localizationConstants,
                                 EditImagesViewImplUiBinder uiBinder) {
        this.localizationConstants = localizationConstants;
        this.setTitle(localizationConstants.editImagesViewTitle());
        Widget widget = uiBinder.createAndBindUi(this);
        this.setWidget(widget);
        createButtons();

        final SimpleList.ListEventDelegate<ItemReference> eventDelegate = new SimpleList.ListEventDelegate<ItemReference>() {
            @Override
            public void onListItemClicked(Element itemElement, ItemReference itemData) {
                list.getSelectionModel().setSelectedItem(itemData);
                delegate.onImageSelected(itemData);
            }

            @Override
            public void onListItemDoubleClicked(Element listItemBase, ItemReference itemData) {
                list.getSelectionModel().setSelectedItem(itemData);
                delegate.onImageSelected(itemData);
                delegate.onEditClicked();
            }
        };

        final SimpleList.ListItemRenderer<ItemReference> itemRenderer = new SimpleList.ListItemRenderer<ItemReference>() {
            @Override
            public void render(Element itemElement, ItemReference itemData) {
                TableCellElement label = Elements.createTDElement();
                label.setInnerHTML(itemData.getName());
                itemElement.appendChild(label);
                UIObject.ensureDebugId((com.google.gwt.dom.client.Element)itemElement, "editImages-openFile-" + itemData);
            }

            @Override
            public Element createElement() {
                return Elements.createTRElement();
            }
        };

        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");

        list = SimpleList.create((SimpleList.View)tableElement, resources.defaultSimpleListCss(), itemRenderer, eventDelegate);
        listPanel.add(list);
    }

    private void createButtons() {
        btnAdd = createButton(localizationConstants.buttonAdd(), "editImages-add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onAddClicked();
            }
        });

        btnRemove = createButton(localizationConstants.buttonRemove(), "editImages-remove", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onRemoveClicked();
            }
        });

        btnEdit = createButton(localizationConstants.buttonEdit(), "editImages-edit", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onEditClicked();
            }
        });

        final Button btnClose = createButton(localizationConstants.buttonClose(), "editImages-close", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseClicked();
            }
        });

        getFooter().add(btnClose);
        getFooter().add(btnEdit);
        getFooter().add(btnRemove);
        getFooter().add(btnAdd);
    }

    @Override
    protected void onClose() {
        delegate.onCloseClicked();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setAddButtonEnabled(boolean isEnabled) {
        btnAdd.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setRemoveButtonEnabled(boolean isEnabled) {
        btnRemove.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setEditButtonEnabled(boolean isEnabled) {
        btnEdit.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setImages(Array<ItemReference> images) {
        list.render(images);
    }

    /** {@inheritDoc} */
    @Override
    public void closeDialog() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
    }

    interface EditImagesViewImplUiBinder extends UiBinder<Widget, EditImagesViewImpl> {
    }
}