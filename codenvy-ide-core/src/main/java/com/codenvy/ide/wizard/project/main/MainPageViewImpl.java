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

import elemental.dom.Element;
import elemental.events.MouseEvent;
import elemental.html.SpanElement;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.Resources;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ui.tree.NodeDataAdapter;
import com.codenvy.ide.ui.tree.NodeRenderer;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.util.input.SignalEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.Comparator;
import java.util.HashMap;
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
    private final Tree.Listener<String> treeEventHandler = new Tree.Listener<String>() {
        @Override
        public void onNodeAction(TreeNodeElement<String> node) {

        }

        @Override
        public void onNodeClosed(TreeNodeElement<String> node) {

        }

        @Override
        public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<String> node) {

        }

        @Override
        public void onNodeDragStart(TreeNodeElement<String> node, MouseEvent event) {

        }

        @Override
        public void onNodeDragDrop(TreeNodeElement<String> node, MouseEvent event) {

        }

        @Override
        public void onNodeExpanded(TreeNodeElement<String> node) {

        }

        @Override
        public void onNodeSelected(TreeNodeElement<String> node, SignalEvent event) {
            String key = node.getData();
            if (templateOrType.containsKey(key)) {
                selectNextWizardType(templateOrType.get(key));
            }
        }

        @Override
        public void onRootContextMenu(int mouseX, int mouseY) {

        }

        @Override
        public void onRootDragDrop(MouseEvent event) {

        }
    };
    @UiField
    SimplePanel categoriesPanel;
    @UiField
    HTMLPanel   descriptionArea;
    private Tree<String>                                categoriesTree;
    private ActionDelegate                              delegate;
    private Map<String, Set<ProjectTypeDescriptor>>     categories;
    private Map<String, Set<ProjectTemplateDescriptor>> samples;
    private Map<String, Object>                         templateOrType;
    private Resources                                   resources;

    @Inject
    public MainPageViewImpl(Resources resources) {
        this.resources = resources;
        rootElement = ourUiBinder.createAndBindUi(this);
        reset();
    }

    private void selectNextWizardType(Object itemData) {
        if (itemData instanceof ProjectTemplateDescriptor) {
            delegate.projectTemplateSelected((ProjectTemplateDescriptor)itemData);
            descriptionArea.getElement().setInnerText(((ProjectTemplateDescriptor)itemData).getDescription());
        } else if (itemData instanceof ProjectTypeDescriptor) {
            delegate.projectTypeSelected((ProjectTypeDescriptor)itemData);
            descriptionArea.getElement().setInnerText(((ProjectTypeDescriptor)itemData).getProjectTypeName());
        } else {
            descriptionArea.getElement().setInnerText("");
        }
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
                    categoriesTree.getSelectionModel().selectSingleNode(key);
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
        categoriesTree.getModel().setRoot("");
        if (samples.isEmpty()) {
            categoriesTree.renderTree(1);
        } else
            categoriesTree.renderTree(0);
    }

    @Override
    public void reset() {
        categoriesPanel.clear();
        categoriesTree = Tree.create(resources, new CategoriesDataAdapter(), new CategoriesNodeRenderer());
        categoriesPanel.add(categoriesTree);
        Style style = categoriesTree.asWidget().getElement().getStyle();
        style.setWidth(100, Style.Unit.PCT);
        style.setHeight(100, Style.Unit.PCT);
        style.setPosition(Style.Position.RELATIVE);
        categoriesTree.setTreeEventHandler(treeEventHandler);
        descriptionArea.clear();
    }

    interface MainPageViewImplUiBinder
            extends UiBinder<DockLayoutPanel, MainPageViewImpl> {
    }

    private class CategoriesDataAdapter implements NodeDataAdapter<String> , Comparator<String>{

        private Map<String, TreeNodeElement<String>> elements = new HashMap<>();

        @Override
        public int compare(String a, String b) {
            if (a.startsWith(SAMPLES) && !b.startsWith(SAMPLES)) {
                return -1;
            }
            return a.compareTo(b);

        }

        @Override
        public boolean hasChildren(String data) {
            return data.startsWith(SAMPLES) || data.startsWith(CATEGORIES);
        }

        @Override
        public Array<String> getChildren(String data) {
            if (categories != null) {
                if ("".equals(data)) {
                    Array<String> array = Collections.createArray();
                    for (String s : categories.keySet()) {
                        array.add(CATEGORIES + s);
                    }
                    if (!samples.isEmpty()) {
                        for (String s : samples.keySet()) {
                            array.add(SAMPLES + s);
                        }
                    }
                    array.sort(this);
                    return array;

                }

                if (data.startsWith(CATEGORIES)) {
                    String key = data.substring(CATEGORIES.length());
                    Array<String> array = Collections.createArray();
                    Set<ProjectTypeDescriptor> typeDescriptors = categories.get(key);
                    if (typeDescriptors != null) {
                        for (ProjectTypeDescriptor typeDescriptor : typeDescriptors) {
                            String itemKey = DESCRIPTOR + key + '!' + typeDescriptor.getProjectTypeId();
                            array.add(itemKey);
                            templateOrType.put(itemKey, typeDescriptor);
                        }
                    }
                    array.sort(this);
                    return array;
                }
            }
            if (data.startsWith(SAMPLES)) {
                String key = data.substring(SAMPLES.length());
                if (samples.containsKey(key)) {
                    Set<ProjectTemplateDescriptor> descriptors = samples.get(key);
                    Array<String> array = Collections.createArray();
                    for (ProjectTemplateDescriptor descriptor : descriptors) {
                        String itemKey = TEMPLATE + descriptor.getDisplayName();
                        templateOrType.put(itemKey, descriptor);
                        array.add(itemKey);

                    }
                    array.sort(this);
                    return array;
                }
            }


            return null;
        }

        @Override
        public String getNodeId(String data) {
            return data;
        }

        @Override
        public String getNodeName(String data) {
            return data;
        }

        @Override
        public String getParent(String data) {
            return "";
        }

        @Override
        public TreeNodeElement<String> getRenderedTreeNode(String data) {
            return elements.get(data);
        }

        @Override
        public void setNodeName(String data, String name) {

        }

        @Override
        public void setRenderedTreeNode(String data, TreeNodeElement<String> renderedNode) {
            elements.put(data, renderedNode);
        }

        @Override
        public String getDragDropTarget(String data) {
            return null;
        }

        @Override
        public Array<String> getNodePath(String data) {
            return PathUtils.getNodePath(this, data);
        }

        @Override
        public String getNodeByPath(String root, Array<String> relativeNodePath) {
            return null;
        }
    }

    private class CategoriesNodeRenderer implements NodeRenderer<String> {

        @Override
        public Element getNodeKeyTextContainer(SpanElement treeNodeLabel) {
            return null;
        }

        @Override
        public SpanElement renderNodeContents(String data) {
            SpanElement spanElement = Elements.createSpanElement();
            if (data.startsWith(SAMPLES)) {
                data = data.substring(SAMPLES.length());
                spanElement.getStyle().setFontWeight("bold");
            }
            if (data.startsWith(CATEGORIES)) {
                data = data.substring(CATEGORIES.length());
                spanElement.getStyle().setFontWeight("bold");
            }
            if (samples.containsKey(data)) {
                spanElement.getStyle().setFontWeight("bold");
            }
            if (templateOrType.containsKey(data)) {
                Object temOrType = templateOrType.get(data);
                if (temOrType instanceof ProjectTemplateDescriptor) {
                    ProjectTemplateDescriptor template = ((ProjectTemplateDescriptor)temOrType);
                    data = template.getDisplayName();
                } else if (temOrType instanceof ProjectTypeDescriptor) {
                    ProjectTypeDescriptor descriptor = ((ProjectTypeDescriptor)temOrType);
                    data = descriptor.getProjectTypeName();
                }
                spanElement.getStyle().setFontSize("11px");
            }
            spanElement.setInnerHTML(data);
            return spanElement;
        }

        @Override
        public void updateNodeContents(TreeNodeElement<String> treeNode) {

        }
    }
}