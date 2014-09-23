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

import elemental.events.KeyboardEvent.KeyCode;

import com.codenvy.api.project.shared.dto.ProjectImporterDescriptor;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.icon.Icon;
import com.codenvy.ide.api.icon.IconRegistry;
import com.codenvy.ide.ui.list.CategoriesList;
import com.codenvy.ide.ui.list.Category;
import com.codenvy.ide.ui.list.CategoryRenderer;
import com.codenvy.ide.wizard.project.ProjectWizardResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
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

    private static MainPageViewImplUiBinder                                 uiBinder                =
                                                                                                      GWT.create(MainPageViewImplUiBinder.class);
    private final DockLayoutPanel                                           rootElement;
    private final Category.CategoryEventDelegate<ProjectImporterDescriptor> projectImporterDelegate =
                                                                                                      new Category.CategoryEventDelegate<ProjectImporterDescriptor>() {
                                                                                                          @Override
                                                                                                          public void onListItemClicked(Element listItemBase,
                                                                                                                                        ProjectImporterDescriptor itemData) {
                                                                                                              delegate.projectImporterSelected(itemData);
                                                                                                          }
                                                                                                      };

    private final CategoryRenderer<ProjectImporterDescriptor>               projectImporterRenderer =
                                                                                                      new CategoryRenderer<ProjectImporterDescriptor>() {
                                                                                                          @Override
                                                                                                          public void renderElement(com.google.gwt.dom.client.Element element,
                                                                                                                                    ProjectImporterDescriptor data) {
                                                                                                              String str = data.getId();
                                                                                                              str =
                                                                                                                    str.length() > 1
                                                                                                                        ? Character.toUpperCase(str.charAt(0))
                                                                                                                          +
                                                                                                                          str.substring(1)
                                                                                                                        : str.toUpperCase();
                                                                                                              element.setInnerText(str);
                                                                                                          }

                                                                                                          @Override
                                                                                                          public com.google.gwt.dom.client.SpanElement renderCategory(Category<ProjectImporterDescriptor> category) {
                                                                                                              return renderCategoryWithIcon(category.getTitle());
                                                                                                          }
                                                                                                      };

    private final IconRegistry                                              iconRegistry;

    @UiField
    Style                                                                   style;
    @UiField
    SimplePanel                                                             categoriesPanel;
    @UiField
    HTMLPanel                                                               descriptionArea;
    @UiField
    TextBox                                                                 projectName;
    @UiField
    TextArea                                                                projectDescription;
    @UiField
    RadioButton                                                             projectPrivate;
    @UiField
    RadioButton                                                             projectPublic;
    @UiField
    TextBox                                                                 projectUrl;
    @UiField(provided = true)
    ProjectWizardResources                                                  wizardResources;

    private ActionDelegate                                                  delegate;
    private Map<String, Set<ProjectImporterDescriptor>>                     categories;
    private Resources                                                       resources;
    private CategoriesList                                                  list;

    @Inject
    public MainPageViewImpl(Resources resources,
                            ProjectWizardResources wizardResources,
                            IconRegistry iconRegistry) {
        this.resources = resources;
        this.iconRegistry = iconRegistry;
        this.wizardResources = wizardResources;
        rootElement = uiBinder.createAndBindUi(this);
        projectName.getElement().setAttribute("maxlength", "32");
        projectDescription.getElement().setAttribute("maxlength", "256");
    }

    @UiHandler("projectName")
    void onProjectNameChanged(KeyUpEvent event) {
        delegate.projectNameChanged(projectName.getValue());
    }
    
    @UiHandler("projectUrl")
    void onProjectUrlChanged(KeyUpEvent event) {
        delegate.projectUrlChanged(projectUrl.getValue());
    }

    @UiHandler("projectDescription")
    void onProjectDescriptionChanged(KeyUpEvent event) {
        delegate.projectDescriptionChanged(projectDescription.getValue());
    }
    
    @UiHandler({"projectDescription", "projectUrl", "projectName"})
    void onEnterClicked(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCode.ENTER) {
            delegate.onEnterClicked();
        }
    }

    @UiHandler({"projectPublic", "projectPrivate"})
    void visibilityHandler(ValueChangeEvent<Boolean> event) {
        delegate.projectVisibilityChanged(projectPublic.getValue());
    }

    private SpanElement renderCategoryWithIcon(String title) {
        SpanElement textElement = Document.get().createSpanElement();
        textElement.setClassName(resources.defaultCategoriesListCss().headerText());
        textElement.setInnerText(title);
        return textElement;
    }

    @Override
    public void reset() {
        projectName.setText("");
        projectDescription.setText("");
        projectUrl.setText("");
        projectPublic.setValue(true);
        projectPrivate.setValue(false);
        descriptionArea.clear();

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

        String namePanel();

        String projectName();

        String projectDescription();

        String labelPosition();

        String radioButtonPosition();

        String categories();

        String description();

        String configuration();

        String label();

        String horizontalLine();
    }

    /** {@inheritDoc} */
    @Override
    public void setImporters(Map<String, Set<ProjectImporterDescriptor>> categories) {
        this.categories = categories;

        List<Category< ? >> categoriesList = new ArrayList<>();
        for (String s : categories.keySet()) {
            Category<ProjectImporterDescriptor> category =
                                                           new Category<ProjectImporterDescriptor>(s, projectImporterRenderer,
                                                                                                   categories.get(s),
                                                                                                   projectImporterDelegate);
            categoriesList.add(category);
        }
        list.render(categoriesList);
    }

    /** {@inheritDoc} */
    @Override
    public void showNameError() {
        projectName.addStyleName(wizardResources.css().inputError());
    }

    /** {@inheritDoc} */
    @Override
    public void hideNameError() {
        projectName.removeStyleName(wizardResources.css().inputError());
    }

    /** {@inheritDoc} */
    @Override
    public void setImporterDescription(String text) {
        descriptionArea.getElement().setInnerText(text);
    }

    /** {@inheritDoc} */
    @Override
    public void showUrlError() {
        projectUrl.addStyleName(wizardResources.css().inputError());
    }

    /** {@inheritDoc} */
    @Override
    public void hideUrlError() {
        projectUrl.removeStyleName(wizardResources.css().inputError());
    }

    /** {@inheritDoc} */
    @Override
    public void selectImporter(ProjectImporterDescriptor importer) {
       list.selectElement(importer); 
    }

    /** {@inheritDoc} */
    @Override
    public String getProjectName() {
        return projectName.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public void setProjectName(String projectName) {
        this.projectName.setValue(projectName);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInUrlInput() {
        projectUrl.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void setInputsEnableState(boolean isEnabled) {
        projectName.setEnabled(isEnabled);
        projectDescription.setEnabled(isEnabled);
        projectUrl.setEnabled(isEnabled);
    }
}
