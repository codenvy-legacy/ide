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

package com.codenvy.ide.ui.list;

import com.codenvy.ide.util.AnimationController;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.UIObject;

import java.util.HashMap;

/**
 * Overlay type for the base element for a Category Node in the list.
 * Nodes that have children, but that have never been expanded (nodes render
 * lazily on expansion), have an empty DIV element.
 * <p/>
 * <pre>
 *
 * <li class="treeNode">
 *   <div class="treeNodeBody">
 *     <span class="treeNodeLabel"></span><div class="expandControl"></div>
 *   </div>
 *   <ul class="childrenContainer">
 *   </ul>
 * </li>
 *
 * </pre>
 *
 * @author Evgen Vidolob
 */
public class CategoryNodeElement extends FlowPanel {
    private final Category                        category;
    private       CategoriesList.SelectionManager selectionManager;
    private final FlowPanel                       container;
    private final AnimationController             animator;
    private       CategoriesList.Resources        resources;
    private       boolean                         expanded;
    private final DivElement                      expandControl;
    private       HashMap<Object, Element>        elementsMap;

    @SuppressWarnings("unchecked")
    CategoryNodeElement(Category category,
                        CategoriesList.SelectionManager selectionManager, CategoriesList.Resources resources) {
        this.category = category;
        this.selectionManager = selectionManager;
        CategoryRenderer renderer = category.getRenderer();
        this.resources = resources;
        setStyleName(resources.defaultCategoriesListCss().category());
        FlowPanel header = new FlowPanel();
        header.sinkEvents(Event.ONCLICK);
        header.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                expandOrCollapse();
            }
        }, ClickEvent.getType());
        header.setStyleName(resources.defaultCategoriesListCss().categoryHeader());
        SpanElement label = Document.get().createSpanElement();
        label.setClassName(resources.defaultCategoriesListCss().categoryLabel());

        label.appendChild(renderer.renderCategory(category));

        header.getElement().appendChild(label);
        header.ensureDebugId("projectWizard-" + category.getTitle());

        expandControl = Document.get().createDivElement();
        expandControl.appendChild(resources.expansionImage().getSvg().getElement());
        expandControl.setClassName(resources.defaultCategoriesListCss().expandControl());
        header.getElement().appendChild(expandControl);
        container = new FlowPanel();
        container.setStyleName(resources.defaultCategoriesListCss().itemContainer());
        container.sinkEvents(Event.ONCLICK);
        container.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                event.getNativeEvent().getEventTarget();
                selectElement(Element.as(event.getNativeEvent().getEventTarget()));
            }
        }, ClickEvent.getType());
        add(header);
        add(container);
        animator = new AnimationController.Builder().setCollapse(true).setFade(true).build();
        animator.hideWithoutAnimating((elemental.dom.Element)container.getElement());
        expanded = true;
        renderChildren();
        animator.show((elemental.dom.Element)container.getElement());
        expandControl.addClassName(resources.defaultCategoriesListCss().expandedImage());
    }

    @SuppressWarnings("unchecked")
    private void selectElement(Element eventTarget) {
        selectionManager.selectItem(eventTarget);
        category.getEventDelegate().onListItemClicked(eventTarget, ListItem.cast(eventTarget).getData());
    }

    private void expandOrCollapse() {
        if (!expanded) {
            expanded = true;
            if (container.getElement().getChildCount() == 0) {
                renderChildren();
            }
            animator.show((elemental.dom.Element)container.getElement());
            expandControl.addClassName(resources.defaultCategoriesListCss().expandedImage());
        } else {
            animator.hide((elemental.dom.Element)container.getElement());
            expandControl.removeClassName(resources.defaultCategoriesListCss().expandedImage());
            expanded = false;
        }
    }

    @SuppressWarnings("unchecked")
    private void renderChildren() {
        elementsMap = new HashMap<Object, Element>();
        CategoryRenderer categoryRenderer = category.getRenderer();
        for (Object o : category.getData()) {
            ListItem<?> element = ListItem.create(categoryRenderer, resources.defaultCategoriesListCss(), o);
            categoryRenderer.renderElement(element, o);
            elementsMap.put(o, element);
            UIObject.ensureDebugId(element, "projectWizard-" + element.getInnerText());
            container.getElement().appendChild(element);
        }
    }

    /**
     * Checks whether the category contains the pointed item.
     *
     * @param item
     *         item to find
     * @return boolean <code>true</code> if contains
     */
    public boolean containsItem(Object item) {
        if (elementsMap == null || elementsMap.isEmpty()) {
            return false;
        }
        return elementsMap.containsKey(item);
    }

    /**
     * Selects the item in the category list.
     *
     * @param item
     */
    public void selectItem(Object item) {
        if (elementsMap == null || elementsMap.isEmpty()) {
            return;
        }

        if (elementsMap.containsKey(item)) {
            selectElement(elementsMap.get(item));
        }
    }

    /**
     * A javascript overlay object which ties a list item's DOM element to its
     * associated data.
     */
    final static class ListItem<M> extends Element {
        /**
         * Creates a new ListItem overlay object by creating a div element,
         * assigning it the listItem css class, and associating it to its data.
         */
        public static <M> ListItem<M> create(CategoryRenderer<M> factory, CategoriesList.Css css, M data) {
            Element element = factory.createElement();
            element.addClassName(css.categoryItem());

            ListItem<M> item = ListItem.cast(element);
            item.setData(data);
            return item;
        }

        /**
         * Casts an element to its ListItem representation. This is an unchecked
         * cast so we extract it into this static factory method so we don't have to
         * suppress warnings all over the place.
         */
        @SuppressWarnings("unchecked")
        public static <M> ListItem<M> cast(Element element) {
            return (ListItem<M>)element;
        }

        protected ListItem() {
            // Unused constructor
        }

        public final native M getData() /*-{
            return this.__data;
        }-*/;

        public final native void setData(M data) /*-{
            this.__data = data;
        }-*/;

    }
}
