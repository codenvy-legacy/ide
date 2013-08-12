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
package com.codenvy.ide.wizard.newresource;

import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.Resources;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.list.SimpleList.View;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;


/**
 * NewResourcePageViewImpl is the view of NewResource wizard.
 * Provides selecting type of resource for creating new resource.
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
    private ActionDelegate                    delegate;
    private SimpleList<NewResourceWizardData> list;
    private SimpleList.ListItemRenderer<NewResourceWizardData>  listItemRenderer =
            new SimpleList.ListItemRenderer<NewResourceWizardData>() {
                @Override
                public void render(Element itemElement, NewResourceWizardData itemData) {
                    TableCellElement label = Elements.createTDElement();

                    SafeHtmlBuilder sb = new SafeHtmlBuilder();
                    // Add icon
                    sb.appendHtmlConstant("<table><tr><td>");
                    ImageResource icon = itemData.getIcon();
                    if (icon != null) {
                        sb.appendHtmlConstant("<img src=\"" + icon.getSafeUri().asString() + "\">");
                    }
                    sb.appendHtmlConstant("</td>");

                    // Add title
                    sb.appendHtmlConstant("<td>");
                    sb.appendEscaped(itemData.getTitle());
                    sb.appendHtmlConstant("</td></tr></table>");

                    label.setInnerHTML(sb.toSafeHtml().asString());

                    itemElement.appendChild(label);
                }

                @Override
                public Element createElement() {
                    return Elements.createTRElement();
                }
            };
    private SimpleList.ListEventDelegate<NewResourceWizardData> listDelegate     =
            new SimpleList.ListEventDelegate<NewResourceWizardData>() {
                public void onListItemClicked(Element itemElement, NewResourceWizardData itemData) {
                    list.getSelectionModel().setSelectedItem(itemData);
                    delegate.selectedFileType(itemData);
                }

                public void onListItemDoubleClicked(Element listItemBase, NewResourceWizardData itemData) {
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
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setResourceWizard(JsonArray<NewResourceWizardData> resources) {
        list.render(resources);
    }
}