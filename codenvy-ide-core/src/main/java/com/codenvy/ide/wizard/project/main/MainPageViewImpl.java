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
package com.codenvy.ide.wizard.project.main;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.Resources;
import com.codenvy.ide.ui.list.CategoriesList;
import com.codenvy.ide.ui.list.Category;
import com.codenvy.ide.ui.list.CategoryRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Evgen Vidolob
 */
public class MainPageViewImpl implements MainPageView {
    private static final String DESCRIPTOR = "Descriptors";
    private static final String TEMPLATE   = "Temp";
    private static final String CATEGORIES = "Categories";
    private static final String SAMPLES    = "Samples";

    private static MainPageViewImplUiBinder ourUiBinder = GWT.create(MainPageViewImplUiBinder.class);
    private final DockLayoutPanel rootElement;
    private final String                                                    defaultOptions[]        =
            new String[]{"JDK", "Application Server"};
    private final Category.CategoryEventDelegate<ProjectTemplateDescriptor> projectTemplateDelegate =
            new Category.CategoryEventDelegate<ProjectTemplateDescriptor>() {
                @Override
                public void onListItemClicked(Element listItemBase, ProjectTemplateDescriptor itemData) {
                    selectNextWizardType(itemData);
                }
            };
    private final Category.CategoryEventDelegate<ProjectTypeDescriptor>     projectTypeDelegate     =
            new Category.CategoryEventDelegate<ProjectTypeDescriptor>() {
                @Override
                public void onListItemClicked(Element listItemBase, ProjectTypeDescriptor itemData) {
                    selectNextWizardType(itemData);
                }
            };
    private final CategoryRenderer<ProjectTypeDescriptor>                   projectTypeRenderer     =
            new CategoryRenderer<ProjectTypeDescriptor>() {
                @Override
                public void renderElement(com.google.gwt.dom.client.Element element, ProjectTypeDescriptor data) {
                    element.setInnerText(data.getProjectTypeName());
                }

                @Override
                public com.google.gwt.dom.client.SpanElement renderCategory(Category<ProjectTypeDescriptor> category) {
                    com.google.gwt.dom.client.SpanElement spanElement = Document.get().createSpanElement();
                    spanElement.setInnerText(category.getTitle().toUpperCase());
                    return spanElement;
                }
            };
    private final CategoryRenderer<ProjectTemplateDescriptor>               projectTemplateRenderer =
            new CategoryRenderer<ProjectTemplateDescriptor>() {
                @Override
                public void renderElement(com.google.gwt.dom.client.Element element, ProjectTemplateDescriptor data) {
                    element.setInnerText(data.getDisplayName());
                }

                @Override
                public com.google.gwt.dom.client.SpanElement renderCategory(Category<ProjectTemplateDescriptor> category) {
                    com.google.gwt.dom.client.SpanElement spanElement = Document.get().createSpanElement();
                    spanElement.setInnerText(category.getTitle().toUpperCase());
                    return spanElement;
                }
            };
    @UiField
    Style       style;
    @UiField
    SimplePanel categoriesPanel;
    @UiField
    HTMLPanel   descriptionArea;
    @UiField
    HTMLPanel   configurationArea;
    @UiField
    TextBox     projectName;
    @UiField
    TextArea    projectDescription;
    @UiField
    RadioButton projectPrivate;
    @UiField
    RadioButton projectPublic;
    //    private Tree<String>                                categoriesTree;
    private ActionDelegate                              delegate;
    private Map<String, Set<ProjectTypeDescriptor>>     categories;
    private Map<String, Set<ProjectTemplateDescriptor>> samples;
    private Map<String, Object>                         templateOrType;
    private Resources                                   resources;
    private CategoriesList                              list;

    @Inject
    public MainPageViewImpl(Resources resources) {
        this.resources = resources;
        rootElement = ourUiBinder.createAndBindUi(this);
        reset();
        projectName.getElement().setAttribute("title", "Define the name of your project...");
        projectName.getElement().setAttribute("maxlength", "35");
        projectDescription.getElement().setAttribute("title", "Add a description to your project...");
        projectDescription.getElement().setAttribute("maxlength", "140");
    }

    @UiHandler("projectName")
    void onProjectNameChanged(KeyUpEvent event) {
        delegate.projectNameChanged(projectName.getText());
    }

    @UiHandler("projectDescription")
    void onProjectDescriptionChanged(KeyUpEvent event) {
        delegate.projectDescriptionChanged(projectDescription.getValue());
    }

    @UiHandler({"projectPublic", "projectPrivate"})
    void visibilityHandler(ValueChangeEvent<Boolean> event) {
        delegate.projectVisibilityChanged(projectPublic.getValue());
    }

    private void selectNextWizardType(Object itemData) {
        if (itemData instanceof ProjectTemplateDescriptor) {
            delegate.projectTemplateSelected((ProjectTemplateDescriptor)itemData);
            descriptionArea.getElement().setInnerText(((ProjectTemplateDescriptor)itemData).getDescription());
            setConfigOptions(null);
            // TODO: add 'Environment Configuration Options' to ProjectTemplateDescriptor
        } else if (itemData instanceof ProjectTypeDescriptor) {
            delegate.projectTypeSelected((ProjectTypeDescriptor)itemData);
            descriptionArea.getElement().setInnerText(((ProjectTypeDescriptor)itemData).getProjectTypeName());
            setConfigOptions(new String[]{"JDK"});
            // TODO: add 'Environment Configuration Options' to ProjectTypeDescriptor
        } else {
            descriptionArea.getElement().setInnerText("");
            resetConfigOptions();
        }
    }

    private void setConfigOptions(String options[]) {
        if (options == null) options = defaultOptions;
        StringBuilder optionsHTMLBuilder = new StringBuilder();
        for (String option : options) {
            if (option.length() > 0) {
                optionsHTMLBuilder.append("<p>");
                optionsHTMLBuilder.append("- " + option);
                optionsHTMLBuilder.append("</p>\n");
            }
        }
        configurationArea.getElement().setInnerHTML(optionsHTMLBuilder.toString());
    }

    private void resetConfigOptions() {
        configurationArea.getElement().setInnerText("");
    }

    private void changeEnabledState(boolean enabled) {
        projectName.setEnabled(enabled);
        changeEnabledStateAll(enabled);
    }

    private void changeEnabledStateAll(boolean enabled) {
        projectDescription.setEnabled(enabled);
        projectPublic.setEnabled(enabled);
        projectPrivate.setEnabled(enabled);
    }

    @Override
    public void resetName() {
        projectName.setText("");
        projectDescription.setText("");
        projectPublic.setValue(true);
        projectPrivate.setValue(false);
        changeEnabledState(true);
    }

    @Override
    public void enableInput() {
        changeEnabledState(true);
    }

    @Override
    public void disableInput() {
        changeEnabledState(false);
    }

    @Override
    public void setName(String name) {
        projectName.setValue(name, true);
    }

    @Override
    public void setVisibility(boolean visible) {
        projectPublic.setValue(visible, false);
    }

    @Override
    public void removeNameError() {
        projectName.removeStyleName(style.inputError());
    }

    @Override
    public void showNameError() {
        projectName.addStyleName(style.inputError());
    }

    @Override
    public void focusOnName() {
        new Timer() {
            @Override
            public void run() {
                projectName.setFocus(true);
            }
        }.schedule(300);
    }

    @Override
    public void disableAllExceptName() {
        changeEnabledStateAll(false);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    @Override
    public void selectProjectType(final String projectTypeId) {

        ProjectTypeDescriptor typeDescriptor = null;
        for (String category : categories.keySet()) {
            for (ProjectTypeDescriptor descriptor : categories.get(category)) {
                if (descriptor.getProjectTypeId().equals(projectTypeId)) {
                    typeDescriptor = descriptor;
                    break;
                }
            }
            if (typeDescriptor != null) break;
        }
        if (typeDescriptor != null) {
            for (String key : templateOrType.keySet()) {
                if (templateOrType.get(key) == typeDescriptor) {
//                    categoriesTree.getSelectionModel().selectSingleNode(key);
                    selectNextWizardType(typeDescriptor);

                }
            }
        }
    }

    @Override
    public void setProjectTypeCategories(Map<String, Set<ProjectTypeDescriptor>> categories,
                                         Map<String, Set<ProjectTemplateDescriptor>> samples) {
        this.categories = categories;
        this.samples = samples;
        templateOrType = new HashMap<>();

        List<Category<?>> categoriesList = new ArrayList<>();
        for (String s : categories.keySet()) {
            Category<ProjectTypeDescriptor> category = new Category<>(s, projectTypeRenderer, categories.get(s), projectTypeDelegate);
            categoriesList.add(category);
        }

        for (String s : samples.keySet()) {
            Category<ProjectTemplateDescriptor> category = new Category<>(s, projectTemplateRenderer, samples.get(s),
                                                                          projectTemplateDelegate);
            categoriesList.add(category);
        }
        list.render(categoriesList);
    }

    @Override
    public void reset() {
        categoriesPanel.clear();
        list = new CategoriesList(resources);
        categoriesPanel.add(list);
        com.google.gwt.dom.client.Style style = list.getElement().getStyle();
        style.setWidth(100, com.google.gwt.dom.client.Style.Unit.PCT);
        style.setHeight(100, com.google.gwt.dom.client.Style.Unit.PCT);
        style.setPosition(com.google.gwt.dom.client.Style.Position.RELATIVE);
        descriptionArea.clear();
    }

    interface MainPageViewImplUiBinder
            extends UiBinder<DockLayoutPanel, MainPageViewImpl> {
    }

    interface Style extends CssResource {
        String mainPanel();

        String leftPart();

        String rightPart();

        String namePanel();

        String projectName();

        String projectDescription();

        String labelPosition();

        String radioButtonPosition();

        String categories();

        String description();

        String configuration();

        String label();

        String inputError();

        String horizontalLine();
    }
}