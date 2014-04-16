/*
* CODENVY CONFIDENTIAL
* __________________
*
* [2012] - [2013] Codenvy, S.A.
* All Rights Reserved.
*
* NOTICE: All information contained herein is, and remains
* the property of Codenvy S.A. and its suppliers,
* if any. The intellectual and technical concepts contained
* herein are proprietary to Codenvy S.A.
* and its suppliers and may be covered by U.S. and Foreign Patents,
* patents in process, and are protected by trade secret or copyright law.
* Dissemination of this information or reproduction of this material
* is strictly forbidden unless prior written permission is obtained
* from Codenvy S.A..
*/
package com.codenvy.ide.openproject;

import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.GWT;
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
 * The implementation of {@link OpenProjectView}.
 * Provides selecting project what want to open.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class OpenProjectViewImpl extends Window implements OpenProjectView {
    private static OpenProjectViewImplUiBinder uiBinder = GWT.create(OpenProjectViewImplUiBinder.class);

    Button                    btnCancel;
    Button                    btnOpen;
    @UiField
    ScrollPanel               listPanel;
    @UiField(provided = true)
    com.codenvy.ide.Resources res;
    private CoreLocalizationConstant localization;
    private ActionDelegate     delegate;
    private SimpleList<String> list;
    private SimpleList.ListItemRenderer<String>  listItemRenderer = new SimpleList.ListItemRenderer<String>() {
        @Override
        public void render(Element itemElement, String itemData) {
            TableCellElement label = Elements.createTDElement();
            label.setInnerHTML(itemData);
            itemElement.appendChild(label);
            UIObject.ensureDebugId((com.google.gwt.dom.client.Element)itemElement, "file-openProject-" + itemData);
        }

        @Override
        public Element createElement() {
            return Elements.createTRElement();
        }
    };
    private SimpleList.ListEventDelegate<String> listDelegate     = new SimpleList.ListEventDelegate<String>() {
        public void onListItemClicked(Element itemElement, String itemData) {
            list.getSelectionModel().setSelectedItem(itemData);
            delegate.selectedProject(itemData);
        }

        public void onListItemDoubleClicked(Element listItemBase, String itemData) {
        }
    };

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    protected OpenProjectViewImpl(com.codenvy.ide.Resources resources, CoreLocalizationConstant localization) {
        this.res = resources;
        this.localization = localization;

        Widget widget = uiBinder.createAndBindUi(this);

        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        list = SimpleList.create((SimpleList.View)tableElement, res.defaultSimpleListCss(), listItemRenderer, listDelegate);
        this.listPanel.add(list);

        this.setTitle("Open Project");
        this.setWidget(widget);
        createButtons();
    }
    
    private void createButtons() {
        btnOpen = createButton(localization.open(), "file-openProject-open", new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                delegate.onOpenClicked();
            }
        });
        
        btnCancel = createButton(localization.cancel(), "file-openProject-cancel", new ClickHandler() {
            
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        
        getFooter().add(btnCancel);
        getFooter().add(btnOpen);
    }
    
    @Override
    protected void onClose() {
        delegate.onCancelClicked();
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
    public void setProjects(Array<String> projects) {
        list.render(projects);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
    }

    interface OpenProjectViewImplUiBinder extends UiBinder<Widget, OpenProjectViewImpl> {
    }
}