/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.wizard.project.main;

import elemental.html.DragEvent;
import elemental.html.Element;
import elemental.html.SpanElement;
import elemental.js.html.JsElement;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.Resources;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.tree.NodeDataAdapter;
import com.codenvy.ide.ui.tree.NodeRenderer;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.util.input.SignalEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Evgen Vidolob
 */
public class MainPageViewImpl implements MainPageView {
    private static MainPageViewImplUiBinder ourUiBinder = GWT.create(MainPageViewImplUiBinder.class);
    private final DockLayoutPanel rootElement;
    private       ActionDelegate  delegate;
    @UiField
    SimplePanel     categoriesPanel;
    @UiField
    SimplePanel     projectsPanel;
    @UiField
    TextAreaElement descriptionArea;
    private final Tree<String>                            categoriesTree;
    private       Map<String, Set<ProjectTypeDescriptor>> categories;
    private       Map<String, Set<ProjectTypeDescriptor>> samples;
    private final SimpleList<Object>                      projectTypeList;

    @Inject
    public MainPageViewImpl(Resources resources) {
        rootElement = ourUiBinder.createAndBindUi(this);
        categoriesTree = Tree.create(resources, new CategoriesDataAdapter(), new CategoriesNodeRenderer());
        categoriesPanel.add(categoriesTree);
        resources.defaultSimpleListCss().ensureInjected();
        projectTypeList = SimpleList.create((SimpleList.View)projectsPanel.getElement().<JsElement>cast(), resources.defaultSimpleListCss(),
                                            new SimpleList.ListItemRenderer<Object>() {


                                                @Override
                                                public void render(Element listItemBase,
                                                                   Object itemData) {
                                                    SpanElement spanElement = Elements.createSpanElement();
                                                    if (itemData instanceof ProjectTypeDescriptor) {
                                                        spanElement.setInnerText(((ProjectTypeDescriptor)itemData).getProjectTypeName());
                                                    } else if (itemData instanceof ProjectTemplateDescriptor) {
                                                        spanElement.setInnerText(((ProjectTemplateDescriptor)itemData).getDisplayName());
                                                    }
                                                    listItemBase.appendChild(spanElement);
                                                }
                                            }, new SimpleList.ListEventDelegate<Object>() {
                    @Override
                    public void onListItemClicked(Element listItemBase, Object itemData) {
                        selectNextWizardType(itemData);
                    }

                    @Override
                    public void onListItemDoubleClicked(Element listItemBase, Object itemData) {

                    }
                }
                                           );
        Style style = categoriesTree.asWidget().getElement().getStyle();
        style.setWidth(100, Style.Unit.PCT);
        style.setHeight(100, Style.Unit.PCT);
        style.setPosition(Style.Position.RELATIVE);
        categoriesTree.setTreeEventHandler(new Tree.Listener<String>() {
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
            public void onNodeDragStart(TreeNodeElement<String> node, DragEvent event) {

            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<String> node, DragEvent event) {

            }

            @Override
            public void onNodeExpanded(TreeNodeElement<String> node) {

            }

            @Override
            public void onNodeSelected(TreeNodeElement<String> node, SignalEvent event) {
                String key = node.getData();
                if(key.equals(SAMPLES)){
                    projectTypeList.render(Collections.createArray());
                    return;
                }
                if(key.startsWith(SAMPLES)) {
                    Set<ProjectTypeDescriptor> typeDescriptors = samples.get(key.substring(SAMPLES.length()));
                    Array<Object> array = Collections.createArray();
                    for (ProjectTypeDescriptor typeDescriptor : typeDescriptors) {
                        for (ProjectTemplateDescriptor templateDescriptor : typeDescriptor.getTemplates()) {
                            array.add(templateDescriptor);
                        }
                    }
                    projectTypeList.render(array);

                }
                else {
                    Set<ProjectTypeDescriptor> projectTypeDescriptors = categories.get(key);
                    Array<Object> descriptors = Collections.createArray();
                    for (ProjectTypeDescriptor descriptor : projectTypeDescriptors) {
                        descriptors.add(descriptor);
                    }
                    projectTypeList.render(descriptors);
                }
                descriptionArea.setInnerText("");
                if(projectTypeList.size() > 0){
                    selectNextWizardType(projectTypeList.get(0));
                }
            }

            @Override
            public void onRootContextMenu(int mouseX, int mouseY) {

            }

            @Override
            public void onRootDragDrop(DragEvent event) {

            }
        });

    }

    private void selectNextWizardType(Object itemData) {
        projectTypeList.getSelectionModel().setSelectedItem(itemData);
        if(itemData instanceof ProjectTemplateDescriptor) {
            delegate.projectTemplateSelected((ProjectTemplateDescriptor)itemData);
            descriptionArea.setInnerText(((ProjectTemplateDescriptor)itemData).getDescription());
        } else if(itemData instanceof ProjectTypeDescriptor) {
            delegate.projectTypeSelected((ProjectTypeDescriptor)itemData);
            descriptionArea.setInnerText(((ProjectTypeDescriptor)itemData).getProjectTypeName());
        } else{
            descriptionArea.setInnerText("");
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
    public void setProjectTypeCategories(Map<String, Set<ProjectTypeDescriptor>> categories,
                                         Map<String, Set<ProjectTypeDescriptor>> samples) {
        this.categories = categories;
        this.samples = samples;
        categoriesTree.getModel().setRoot("");
        categoriesTree.renderTree();
    }

    private class CategoriesDataAdapter implements NodeDataAdapter<String> {

        private Map<String, TreeNodeElement<String>> elements = new HashMap<>();

        @Override
        public int compare(String a, String b) {
            return 0;
        }

        @Override
        public boolean hasChildren(String data) {
            return SAMPLES.equals(data);
        }

        @Override
        public Array<String> getChildren(String data) {
            if (categories != null) {
                if ("".equals(data)) {
                    Array<String> array = Collections.createArray();
                    for (String s : categories.keySet()) {
                        array.add(s);
                    }
                    if(!samples.isEmpty()){
                        array.add(SAMPLES);
                    }
                    return array;

                }
                if(SAMPLES.equals(data)){
                    Array<String> array = Collections.createArray();
                    for (String s : samples.keySet()) {
                        array.add(SAMPLES + s);
                    }
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
            if(!SAMPLES.equals(data) && data.startsWith(SAMPLES)) {
                data = data.substring(SAMPLES.length());
            }
            spanElement.setInnerText(data);
            return spanElement;
        }

        @Override
        public void updateNodeContents(TreeNodeElement<String> treeNode) {

        }
    }

    interface MainPageViewImplUiBinder
            extends UiBinder<DockLayoutPanel, MainPageViewImpl> {
    }
}