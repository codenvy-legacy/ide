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
package org.exoplatform.ide.client.project.create;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.client.framework.util.ProjectResolver;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 24, 2012 4:41:51 PM anya $
 */
public class CreateProjectView extends ViewImpl implements CreateProjectPresenter.Display {
    private static final String ID = "eXoCreateNewProjectView";

    private static final String CREATE_TITLE = IDE.TEMPLATE_CONSTANT.createProjectFromTemplateTitle();

    private static final int HEIGHT = 460;

    private static final int WIDTH = 890;

    private final String NAME_FIELD_ID = "eXoCreateNewProjectViewNameField";

    private final String BACK_BUTTON_ID = "eXoCreateNewProjectViewBackButton";

    private final String NEXT_BUTTON_ID = "eXoCreateNewProjectViewNextButton";

    private final String FINISH_BUTTON_ID = "eXoCreateNewProjectViewFinishButton";

    private final String CANCEL_BUTTON_ID = "eXoCreateNewProjectViewCancelButton";

    private final String USE_JREBEL_PLUGIN_FIELD_ID = "usejrebelpluginfield";

    private final String JREBEL_PROFILE_FIELDS_ID = "jrebelprofilefields";

    private static CreateProjectViewUiBinder uiBinder = GWT.create(CreateProjectViewUiBinder.class);

    interface CreateProjectViewUiBinder extends UiBinder<Widget, CreateProjectView> {
    }

    @UiField
    ImageButton nextButton;

    @UiField
    ImageButton backButton;

    @UiField
    ImageButton finishButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    Grid projectTypesGrid;

    @UiField
    Grid targetGrid;

    @UiField
    TextInput projectNameField;

    @UiField
    Label errorLabel;

    @UiField
    ProjectTemplateGrid templatesGrid;

    @UiField
    CheckBox useJRebelPluginField;

    @UiField
    FlowPanel createProjectStep;

    @UiField
    DockLayoutPanel chooseTemplateStep;

    @UiField
    FlowPanel deployProjectStep;

    @UiField
    DockLayoutPanel jRebelPanel;

    @UiField
    DockLayoutPanel jRebelProfileFields;

    @UiField
    Image chooseTechnologyTooltip;

    @UiField
    Image choosePaaSTooltip;

    private List<ToggleButton> projectTypeButtonsList = new LinkedList<ToggleButton>();

    private List<ToggleButton> targetButtonsList = new LinkedList<ToggleButton>();

    private Map<ToggleButton, ProjectType> projectTypesMap = new HashMap<ToggleButton, ProjectType>();

    private Map<ToggleButton, PaaS> targetsMap = new HashMap<ToggleButton, PaaS>();

    private Map<PaaS, ToggleButton> paasButtonsMap = new HashMap<PaaS, ToggleButton>();

    private boolean showJRebelStoredForm;

    public CreateProjectView() {
        super(ID, ViewType.MODAL, CREATE_TITLE, null, WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));

        backButton.setButtonId(BACK_BUTTON_ID);
        nextButton.setButtonId(NEXT_BUTTON_ID);
        finishButton.setButtonId(FINISH_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);

        jRebelProfileFields.getElement().setId(JREBEL_PROFILE_FIELDS_ID);
        useJRebelPluginField.getElement().setId(USE_JREBEL_PLUGIN_FIELD_ID);

        projectNameField.setName(NAME_FIELD_ID);

        deployProjectStep.setVisible(false);
    }

    /** @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#switchToCreateModule() */
    @Override
    public void switchToCreateModule() {
        setTitle(IDE.TEMPLATE_CONSTANT.createProjectFromTemplateNewModuleTitle());
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#getNextButton() */
    @Override
    public HasClickHandlers getNextButton() {
        return nextButton;
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#getFinishButton() */
    @Override
    public HasClickHandlers getFinishButton() {
        return finishButton;
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#getNameField() */
    @Override
    public HasValue<String> getNameField() {
        return projectNameField;
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#enableNextButton(boolean) */
    @Override
    public void enableNextButton(boolean enabled) {
        nextButton.setEnabled(enabled);
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#enableFinishButton(boolean) */
    @Override
    public void enableFinishButton(boolean enabled) {
        finishButton.setEnabled(enabled);
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#showCreateProjectStep() */
    @Override
    public void showCreateProjectStep() {
        createProjectStep.setVisible(true);
        chooseTemplateStep.setVisible(false);
        deployProjectStep.setVisible(false);

        backButton.setVisible(false);
        nextButton.setVisible(true);
        finishButton.setVisible(true);
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#showChooseTemlateStep() */
    @Override
    public void showChooseTemlateStep() {
        createProjectStep.setVisible(false);
        chooseTemplateStep.setVisible(true);
        deployProjectStep.setVisible(false);

        backButton.setVisible(true);
        nextButton.setVisible(true);
        finishButton.setVisible(true);
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#showDeployProjectStep() */
    @Override
    public void showDeployProjectStep() {
        createProjectStep.setVisible(false);
        chooseTemplateStep.setVisible(false);
        deployProjectStep.setVisible(true);

        backButton.setVisible(true);
        nextButton.setVisible(false);
        finishButton.setVisible(true);
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#getBackButton() */
    @Override
    public HasClickHandlers getBackButton() {
        return backButton;
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#setDeployView(com.google.gwt.user.client.ui
     * .Composite) */
    @Override
    public void setDeployView(Composite deployView) {
        deployProjectStep.clear();
        deployProjectStep.add(deployView);
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#setProjectTypes(java.util.List) */
    @Override
    public void setProjectTypes(List<ProjectType> projectTypeList) {
        projectTypesGrid.setSize("100%", "100%");

        int columnCount = 10;
        int rowCount = (int)Math.ceil((double)projectTypeList.size() / columnCount);
        projectTypesGrid.resize(rowCount, columnCount);

        int buttonNum = 0;
        for (int rowNum = 0; rowNum < rowCount; rowNum++) {
            for (int colNum = 0; colNum < columnCount; colNum++) {
                // add empty DockPanels because the spacing between the buttons should be the same on all lines
                if (buttonNum >= projectTypeList.size()) {
                    DockPanel dock = new DockPanel();
                    dock.setSpacing(4);
                    dock.add(getNewButtonLabel(null), DockPanel.SOUTH);
                    projectTypesGrid.setWidget(rowNum, colNum, dock);
                    continue;
                }

                DockPanel dock = new DockPanel();
                dock.setSpacing(4);
                dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);

                ProjectType projectType = projectTypeList.get(buttonNum++);
                Image image = new Image(ProjectResolver.getLargeImageForProject(projectType));
                ToggleButton projectTypeButton = getNewButton(image, null);

                String type = projectType.value();
                // TODO
                if (projectType == ProjectType.JSP) {
                    type = "Java Web Application (WAR)";
                } else if (projectType == ProjectType.JAR) {
                    type = "Java Library (JAR)";
                } else if (projectType == ProjectType.SPRING) {
                    type = "Java Spring";
                } else if (projectType == ProjectType.RUBY_ON_RAILS) {
                    type = "Ruby on Rails";
                } else if (projectType == ProjectType.NODE_JS) {
                    type = "Node.js";
                }

                dock.add(projectTypeButton, DockPanel.NORTH);
                dock.add(getNewButtonLabel(type), DockPanel.SOUTH);

                projectTypeButtonsList.add(projectTypeButton);
                projectTypesMap.put(projectTypeButton, projectType);
                projectTypeButton.getElement().setId("CREATE-PROJECT-" + projectType);
                projectTypesGrid.setWidget(rowNum, colNum, dock);
            }
        }
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#setTargets(java.util.List) */
    @Override
    public void setTargets(List<PaaS> targetList) {
        targetGrid.setSize("100%", "100%");

        int columnCount = 10;
        int rowCount = (int)Math.ceil((double)targetList.size() / columnCount);
        targetGrid.resize(rowCount, columnCount);

        int buttonNum = 0;
        for (int rowNum = 0; rowNum < rowCount; rowNum++) {
            for (int colNum = 0; colNum < columnCount; colNum++) {
                // add empty DockPanels because the spacing between the buttons should be the same on all lines
                if (buttonNum >= targetList.size()) {
                    DockPanel dock = new DockPanel();
                    dock.setSpacing(4);
                    dock.add(getNewButtonLabel(null), DockPanel.SOUTH);
                    targetGrid.setWidget(rowNum, colNum, dock);
                    continue;
                }

                DockPanel dock = new DockPanel();
                dock.setSpacing(4);
                dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);

                PaaS target = targetList.get(buttonNum++);
                Image targetImageEnabled = target.getImageEnabled();
                Image targetImageDisabled = target.getImageDisabled();
                ToggleButton targetButton = getNewButton(targetImageEnabled, targetImageDisabled);
                targetButton.setEnabled(false);

                dock.add(targetButton, DockPanel.NORTH);
                dock.add(getNewButtonLabel(target.getTitle()), DockPanel.SOUTH);

                targetButtonsList.add(targetButton);
                targetsMap.put(targetButton, target);
                paasButtonsMap.put(target, targetButton);
                dock.getElement().setId("CREATE-PROJECT-PAAS-" + target.getId());
                targetGrid.setWidget(rowNum, colNum, dock);
            }
        }
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#getTemplatesGrid() */
    @Override
    public ListGridItem<ProjectTemplate> getTemplatesGrid() {
        return templatesGrid;
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#getUseJRebelPlugin() */
    @Override
    public HasValue<Boolean> getUseJRebelPlugin() {
        return useJRebelPluginField;
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#selectTemplate(org.exoplatform.ide.client.framework
     * .template.ProjectTemplate) */
    @Override
    public void selectTemplate(ProjectTemplate projectTemplate) {
        templatesGrid.selectItem(projectTemplate);
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#getErrorLabel() */
    @Override
    public HasValue<String> getErrorLabel() {
        return errorLabel;
    }

    /**
     * @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#toggleUpAllButtons(java.util.List,
     *      com.google.gwt.user.client.ui.ToggleButton)
     */
    @Override
    public void toggleUpAllButtons(List<ToggleButton> buttonsList, ToggleButton currentButton) {
        for (ToggleButton button : buttonsList) {
            if (button != currentButton) {
                button.setValue(false);
            }
        }
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#getProjectTypeButtons() */
    @Override
    public List<ToggleButton> getProjectTypeButtons() {
        return projectTypeButtonsList;
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#getTargetButtons() */
    @Override
    public List<ToggleButton> getTargetButtons() {
        return targetButtonsList;
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#getProjectTypeByButton(com.google.gwt.user.client
     * .ui.ToggleButton) */
    @Override
    public ProjectType getProjectTypeByButton(ToggleButton button) {
        return projectTypesMap.get(button);
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#getTargetByButton(com.google.gwt.user.client.ui
     * .ToggleButton) */
    @Override
    public PaaS getTargetByButton(ToggleButton button) {
        return targetsMap.get(button);
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#enableButtonsForSupportedTargets(java.util.List) */
    @Override
    public void enableButtonsForSupportedTargets(List<PaaS> list) {
        for (Entry<PaaS, ToggleButton> entry : paasButtonsMap.entrySet()) {
            if (list.contains(entry.getKey())) {
                entry.getValue().setEnabled(true);
            } else {
                entry.getValue().setEnabled(false);
            }
        }
    }

    /** @see org.exoplatform.ide.client.project.create2.CreateProjectPresenter.Display#selectTarget(org.exoplatform.ide.client.framework
     * .paas.PaaS) */
    @Override
    public void selectTarget(PaaS target) {
        for (Entry<PaaS, ToggleButton> entry : paasButtonsMap.entrySet()) {
            entry.getValue().setValue(false);
            if (entry.getKey() == target) {
                entry.getValue().setValue(true, true);
            }
        }
    }

    /** @see org.exoplatform.ide.client.project.create.CreateProjectPresenter.Display#setJRebelPanelVisibility(boolean) */
    @Override
    public void setJRebelPanelVisibility(boolean isVisible) {
        jRebelPanel.setVisible(isVisible);
    }

    @Override
    public void setJRebelFormVisible(boolean visible) {
        jRebelProfileFields.setVisible(visible);
        for (int i = 0; i < getProjectTypeButtons().size(); i++) {
            if (getProjectTypeButtons().get(i).isDown()) {
                ProjectType projectType = ProjectType.fromValue(projectTypesMap.get(getProjectTypeButtons().get(i)).value());
                if (projectType == ProjectType.JSP || projectType == ProjectType.SPRING) {
                    if (visible) {
                        if (!showJRebelStoredForm) {
                            chooseTemplateStep.setHeight("88%");
                            return;
                        }
                        chooseTemplateStep.setHeight("82%");
                    } else {
                        chooseTemplateStep.setHeight("88%");
                    }
                    return;
                }
            }
        }
        chooseTemplateStep.setHeight("100%");
    }

    /**
     * Creates a {@link ToggleButton} with the specified images and preconfigured style settings.
     *
     * @param enabledImage
     *         image for enabled button state
     * @param disabledImage
     *         image for disabled button state
     * @return a {@link ToggleButton}
     */
    private ToggleButton getNewButton(Image enabledImage, Image disabledImage) {
        ToggleButton button = new ToggleButton();
        if (enabledImage != null) {
            button.getUpFace().setImage(enabledImage);
        }
        if (disabledImage != null) {
            button.getUpDisabledFace().setImage(disabledImage);
        }
        button.setSize("48px", "48px");
        button.getElement().getStyle().setPropertyPx("borderRadius", 10);
        button.getElement().getStyle().setPropertyPx("outline", 0);
        return button;
    }

    /**
     * Creates an HTML widget with the specified text content and preconfigured style settings.
     *
     * @param label
     *         the new widget's text content
     * @return an HTML widget
     */
    private HTML getNewButtonLabel(String label) {
        HTML titleLabel = new HTML();
        if (label != null) {
            titleLabel.setHTML(label);
        }
        titleLabel.setWidth("70px");
        titleLabel.setHeight("46px");
        titleLabel.getElement().getStyle().setProperty("fontFamily", "Verdana, Bitstream Vera Sans, sans-serif");
        titleLabel.getElement().getStyle().setFontSize(11, Unit.PX);
        titleLabel.getElement().getStyle().setColor("#545454");
        return titleLabel;
    }

    @Override
    public void setJRebelStoredFormVisible(boolean visible) {
        showJRebelStoredForm = visible;
    }

    @Override
    public HasClickHandlers getChooseTechnologyTooltip() {
        return chooseTechnologyTooltip;
    }

    @Override
    public HasClickHandlers getChoosePaaSTooltip() {
        return choosePaaSTooltip;
    }

    @Override
    public void setJRebelPanelEnable(boolean isEnabled) {
        useJRebelPluginField.setValue(isEnabled);
        useJRebelPluginField.setEnabled(isEnabled);
    }
}
