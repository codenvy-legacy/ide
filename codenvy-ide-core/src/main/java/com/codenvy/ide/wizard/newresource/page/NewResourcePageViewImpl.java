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
package com.codenvy.ide.wizard.newresource.page;

import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.list.SimpleList.View;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.validation.constraints.NotNull;


/**
 * NewResourcePageViewImpl is the view of {@link NewResourcePagePresenter}. Provides selecting type of resource for creating new resource.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewResourcePageViewImpl extends Composite implements NewResourcePageView {
    interface NewResourceViewUiBinder extends UiBinder<Widget, NewResourcePageViewImpl> {
    }

    private static NewResourceViewUiBinder uiBinder = GWT.create(NewResourceViewUiBinder.class);

    @UiField
    ScrollPanel resources;
    @UiField(provided = true)
    Resources   res;
    @UiField
    TextBox     resourceName;
    @UiField
    ListBox     parent;
    private ActionDelegate                  delegate;
    private SimpleList<NewResourceProvider> list;
    private SimpleList.ListItemRenderer<NewResourceProvider>  listItemRenderer =
            new SimpleList.ListItemRenderer<NewResourceProvider>() {
                @Override
                public void render(Element itemElement,
                                   NewResourceProvider itemData) {
                    TableCellElement label =
                            Elements.createTDElement();

                    SafeHtmlBuilder sb = new SafeHtmlBuilder();
                    // Add icon
                    sb.appendHtmlConstant("<table><tr><td>");
                    Image icon = itemData.getIcon();
                    if (icon != null) {
                        sb.appendHtmlConstant("<img height=\"16\" width=\"16\" src=\""
                                              + icon.getUrl()
                                              + "\">");
                    }
                    sb.appendHtmlConstant("</td>");

                    // Add title
                    sb.appendHtmlConstant("<td>");
                    sb.appendEscaped(itemData.getTitle());
                    sb.appendHtmlConstant("</td></tr></table>");

                    label.setInnerHTML(sb.toSafeHtml().asString());

                    itemElement.appendChild(label);
                    UIObject.ensureDebugId((com.google.gwt.dom.client.Element)itemElement, "file-newOther-" + itemData.getId());
                }

                @Override
                public Element createElement() {
                    return Elements.createTRElement();
                }
            };
    private SimpleList.ListEventDelegate<NewResourceProvider> listDelegate     =
            new SimpleList.ListEventDelegate<NewResourceProvider>() {
                public void onListItemClicked(Element itemElement,
                                              NewResourceProvider itemData) {
                    delegate.onResourceTypeSelected(itemData);
                }

                public void onListItemDoubleClicked(Element listItemBase,
                                                    NewResourceProvider itemData) {
                }
            };

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    protected NewResourcePageViewImpl(Resources resources) {
        this.res = resources;

        initWidget(uiBinder.createAndBindUi(this));

        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        list = SimpleList.create((View)tableElement, resources.defaultSimpleListCss(), listItemRenderer, listDelegate);
        this.resources.add(list);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public String getResourceName() {
        return resourceName.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setResourceName(@NotNull String name) {
        resourceName.setText(name);
    }

    /** {@inheritDoc} */
    @Override
    public void setResourceWizard(@NotNull Array<NewResourceProvider> resources) {
        list.render(resources);
    }

    /** {@inheritDoc} */
    @Override
    public void focusResourceName() {
        resourceName.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void selectResourceType(@NotNull NewResourceProvider resourceType) {
        list.getSelectionModel().setSelectedItem(resourceType);
    }

    @UiHandler("resourceName")
    public void onResourceNameKeyUp(KeyUpEvent event) {
        delegate.onResourceNameChanged();
    }

    @UiHandler("parent")
    void onChange(ChangeEvent e) {
        delegate.onResourceParentChanged();
    }

    /** {@inheritDoc} */
    @Override
    public String getPackageName() {
        int index = parent.getSelectedIndex();
        return (index != -1) ? parent.getValue(index) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setPackages(Array<String> packages) {
        this.parent.clear();
        for (String p : packages.asIterable()) {
            this.parent.addItem(p);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void selectPackage(@NotNull int index) {
        parent.setSelectedIndex(index);
    }

}
