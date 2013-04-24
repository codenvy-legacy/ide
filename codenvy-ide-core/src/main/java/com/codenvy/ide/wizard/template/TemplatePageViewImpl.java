/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.wizard.template;

import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.ProjectTypeAgent;
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


/** @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a> */
public class TemplatePageViewImpl extends Composite implements TemplatePageView {
    private static TemplateViewUiBinder uiBinder = GWT.create(TemplateViewUiBinder.class);

    @UiField
    ScrollPanel templates;

    private ActionDelegate delegate;

    private SimpleList<Template> list;

    private SimpleList.ListItemRenderer<Template> listItemRenderer = new SimpleList.ListItemRenderer<Template>() {
        @Override
        public void render(Element itemElement, Template itemData) {
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

    private SimpleList.ListEventDelegate<Template> listDelegate = new SimpleList.ListEventDelegate<Template>() {
        public void onListItemClicked(Element itemElement, Template itemData) {
            list.getSelectionModel().setSelectedItem(itemData);
            delegate.selectedTemplate(itemData);
        }

        public void onListItemDoubleClicked(Element listItemBase, Template itemData) {
        }
    };

    interface TemplateViewUiBinder extends UiBinder<Widget, TemplatePageViewImpl> {
    }

    @Inject
    protected TemplatePageViewImpl(Resources resources, TemplateAgentImpl templateAgent, ProjectTypeAgent projectTypeAgent) {
        initWidget(uiBinder.createAndBindUi(this));

        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        list = SimpleList.create((View)tableElement, resources.defaultSimpleListCss(), listItemRenderer, listDelegate);

        this.templates.setStyleName(resources.coreCss().simpleListContainer());
        this.templates.add(list);

        String projectType = projectTypeAgent.getSelectedProjectType();
        JsonArray<Template> templates = templateAgent.getTemplatesForProjectType(projectType);

        list.render(templates);
    }

    /** {@inheritDoc} */
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }
}