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
package com.codenvy.ide.wizard.newproject.pages.template;

import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.ide.Resources;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.list.SimpleList.View;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * The implementation of {@link ChooseTemplatePageView}.
 *
 * @author Andrey Plotnikov
 */
public class ChooseTemplatePageViewImpl extends Composite implements ChooseTemplatePageView {
    private static TemplateViewUiBinder uiBinder  = GWT.create(TemplateViewUiBinder.class);
    private static ListResources        RESOURCES = GWT.create(ListResources.class);

    static {
        RESOURCES.templateListCss().ensureInjected();
    }

    @UiField
    ScrollPanel templates;
    @UiField(provided = true)
    Resources   res;
    private ActionDelegate                        delegate;
    private SimpleList<ProjectTemplateDescriptor> list;
    private SimpleList.ListItemRenderer<ProjectTemplateDescriptor>  listItemRenderer =
            new SimpleList.ListItemRenderer<ProjectTemplateDescriptor>() {
                @Override
                public void render(Element itemElement, ProjectTemplateDescriptor itemData) {
                    TableCellElement label = Elements.createTDElement();
                    SafeHtmlBuilder sb = new SafeHtmlBuilder();
                    sb.appendHtmlConstant("<table><tr>");
                    sb.appendHtmlConstant("<td style=\"font-weight: bold;\">");
                    sb.appendEscaped(itemData.getTemplateTitle());
                    sb.appendHtmlConstant("</td></tr>");
                    sb.appendHtmlConstant("<tr><td style=\"padding: 10px\">");
                    sb.appendEscaped(itemData.getTemplateDescription());
                    sb.appendHtmlConstant("</td></tr></table>");
                    label.setInnerHTML(sb.toSafeHtml().asString());
                    itemElement.appendChild(label);
                }

                @Override
                public Element createElement() {
                    return Elements.createTRElement();
                }
            };
    private SimpleList.ListEventDelegate<ProjectTemplateDescriptor> listDelegate     =
            new SimpleList.ListEventDelegate<ProjectTemplateDescriptor>() {
                public void onListItemClicked(Element itemElement, ProjectTemplateDescriptor itemData) {
                    delegate.onTemplateSelected(itemData);
                }

                public void onListItemDoubleClicked(Element listItemBase, ProjectTemplateDescriptor itemData) {
                }
            };

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    protected ChooseTemplatePageViewImpl(Resources resources) {
        this.res = resources;

        initWidget(uiBinder.createAndBindUi(this));

        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        list = SimpleList.create((View)tableElement, RESOURCES.templateListCss(), listItemRenderer, listDelegate);
        this.templates.add(list);
    }

    /** {@inheritDoc} */
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setTemplates(Array<ProjectTemplateDescriptor> templates) {
        list.render(templates);
    }

    /** {@inheritDoc} */
    @Override
    public void selectItem(ProjectTemplateDescriptor template) {
        list.getSelectionModel().setSelectedItem(template);
    }

    protected interface TemplateList extends SimpleList.Css {
        @Override
        int menuListBorderPx();

        @Override
        @ClassName("tListItem")
        String listItem();

        @Override
        @ClassName("tListBase")
        String listBase();

        @Override
        @ClassName("tListContainer")
        String listContainer();
    }

    protected interface ListResources extends ClientBundle {
        @Source({"TemplateList.css", "com/codenvy/ide/common/constants.css", "com/codenvy/ide/api/ui/style.css"})
        TemplateList templateListCss();
    }

    interface TemplateViewUiBinder extends UiBinder<Widget, ChooseTemplatePageViewImpl> {
    }
}