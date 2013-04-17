/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.openproject;

import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.Resources;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.list.SimpleList.View;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The implementation of {@link OpenProjectView}.
 * Provides selecting project what want to open.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class OpenProjectViewImpl extends DialogBox implements OpenProjectView {
    private static OpenProjectViewImplUiBinder uiBinder = GWT.create(OpenProjectViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnCancel;

    @UiField
    com.codenvy.ide.ui.Button btnOpen;

    @UiField
    ScrollPanel listPanel;

    private ActionDelegate delegate;

    private SimpleList<String> list;

    private SimpleList.ListItemRenderer<String> listItemRenderer = new SimpleList.ListItemRenderer<String>() {
        @Override
        public void render(Element itemElement, String itemData) {
            TableCellElement label = Elements.createTDElement();
            label.setInnerHTML(itemData);
            itemElement.appendChild(label);
        }

        @Override
        public Element createElement() {
            return Elements.createTRElement();
        }
    };

    private SimpleList.ListEventDelegate<String> listDelegate = new SimpleList.ListEventDelegate<String>() {
        public void onListItemClicked(Element itemElement, String itemData) {
            list.getSelectionModel().setSelectedItem(itemData);
            delegate.selectedProject(itemData);
        }

        public void onListItemDoubleClicked(Element listItemBase, String itemData) {
        }
    };

    interface OpenProjectViewImplUiBinder extends UiBinder<Widget, OpenProjectViewImpl> {
    }

    /**
     * Create view.
     *
     * @param projects
     * @param resources
     */
    public OpenProjectViewImpl(JsonArray<String> projects, Resources resources) {
        Widget widget = uiBinder.createAndBindUi(this);

        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        list = SimpleList.create((View)tableElement, resources.defaultSimpleListCss(), listItemRenderer, listDelegate);

        this.listPanel.setStyleName(resources.coreCss().simpleListContainer());
        this.listPanel.add(list);

        this.setText("Open Project");
        this.setWidget(widget);

        list.render(projects);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setOpenButtonEnabled(boolean isEnabled) {
        btnOpen.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

    @UiHandler("btnCancel")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("btnOpen")
    void onBtnOpenClick(ClickEvent event) {
        delegate.onOpenClicked();
    }
}