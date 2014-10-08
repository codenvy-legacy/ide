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
package com.codenvy.ide.wizard.project.importproject;

import com.codenvy.api.project.shared.dto.ProjectImporterDescriptor;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.icon.IconRegistry;
import com.codenvy.ide.api.projectimporter.ProjectImporter;
import com.codenvy.ide.ui.list.CategoriesList;
import com.codenvy.ide.ui.list.Category;
import com.codenvy.ide.ui.list.CategoryRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * UI implementation for {@link MainPageView}.
 *
 * @author Ann Shumilova
 */
public class MainPageViewImpl implements MainPageView {

    private static MainPageViewImplUiBinder uiBinder = GWT.create(MainPageViewImplUiBinder.class);
    private final DockLayoutPanel rootElement;
    private final Category.CategoryEventDelegate<ProjectImporterDescriptor> projectImporterDelegate =
            new Category.CategoryEventDelegate<ProjectImporterDescriptor>() {
                @Override
                public void onListItemClicked(Element listItemBase,
                                              ProjectImporterDescriptor itemData) {
                    delegate.projectImporterSelected(itemData);
                }
            };

    private final CategoryRenderer<ProjectImporterDescriptor> projectImporterRenderer = new CategoryRenderer<ProjectImporterDescriptor>() {
        @Override
        public void renderElement(Element element, ProjectImporterDescriptor data) {
            String str = data.getId();
            str = str.length() > 1 ? Character.toUpperCase(str.charAt(0)) + str.substring(1) : str.toUpperCase();
            element.setInnerText(str);
        }

        @Override
        public SpanElement renderCategory(Category<ProjectImporterDescriptor> category) {
            return renderCategoryWithIcon(category.getTitle());
        }
    };

    private final IconRegistry iconRegistry;

    @UiField
    Style       style;
    @UiField
    SimplePanel importerPanel;
    @UiField
    SimplePanel categoriesPanel;
    @UiField(provided = true)
    com.codenvy.ide.Resources   resources;
    private ActionDelegate                    delegate;
    private Map<String, Set<ProjectImporterDescriptor>> categories;
    private CategoriesList                    list;

    @Inject
    public MainPageViewImpl(Resources resources,
                            IconRegistry iconRegistry) {
        this.resources = resources;
        this.iconRegistry = iconRegistry;
        rootElement = uiBinder.createAndBindUi(this);
    }

    private SpanElement renderCategoryWithIcon(String title) {
        SpanElement textElement = Document.get().createSpanElement();
        textElement.setClassName(resources.defaultCategoriesListCss().headerText());
        textElement.setInnerText(title);
        return textElement;
    }

    @Override
    public void reset() {
        categoriesPanel.clear();
        list = new CategoriesList(resources);
        categoriesPanel.add(list);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    interface MainPageViewImplUiBinder
            extends UiBinder<DockLayoutPanel, MainPageViewImpl> {
    }

    interface Style extends CssResource {
        String mainPanel();

        String leftPart();

        String rightPart();

        String categories();
    }

    /** {@inheritDoc} */
    @Override
    public void setImporters(Map<String, Set<ProjectImporterDescriptor>> categories) {
        this.categories = categories;

        List<Category<?>> categoriesList = new ArrayList<>();
        for (String s : categories.keySet()) {
            Category<ProjectImporterDescriptor> category = new Category<>(s, projectImporterRenderer,
                                                                categories.get(s),
                                                                projectImporterDelegate);
            categoriesList.add(category);
        }
        list.render(categoriesList);
    }

    @Override
    public AcceptsOneWidget getImporterPanel() {
        return importerPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void selectImporter(ProjectImporterDescriptor importer) {
        list.selectElement(importer);
    }
}
