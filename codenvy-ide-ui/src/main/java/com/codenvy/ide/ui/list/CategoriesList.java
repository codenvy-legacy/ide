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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import org.vectomatic.dom.svg.ui.SVGResource;

import java.util.List;

/**
 * @author Evgen Vidolob
 */
public class CategoriesList extends Composite {
    /** Defines the attribute used to indicate selection. */
    private static final String       SELECTED_ATTRIBUTE = "SELECTED";
    private final Resources           resources;
    private final SelectionManager    selectionManager;
    private FlowPanel                 root;
    private Array<CategoryNodeElement> categoryNodeElements;

    public CategoriesList(Resources resources) {
        this.resources = resources;
        this.categoryNodeElements = Collections.createArray();
        root = new FlowPanel();
        initWidget(root);
        root.setStyleName(resources.defaultCategoriesListCss().listContainer());
        selectionManager = new SelectionManager();
    }

    /**
     * Refreshes list of items.
     * <p/>
     * <p>This method tries to keep selection.
     *
     * @param categories
     *         the categories
     */
    public void render(List<Category<?>> categories) {
        this.categoryNodeElements = Collections.createArray();
        for (Category category : categories) {
            CategoryNodeElement categoryNodeElement = new CategoryNodeElement(category, selectionManager, resources);
            categoryNodeElements.add(categoryNodeElement);
            root.add(categoryNodeElement);
        }
    }
    
    /**
     * Select object in the list.
     * 
     * @param element
     * @return
     */
    public boolean selectElement(Object element){
        if (categoryNodeElements == null || categoryNodeElements.isEmpty()) {
            return false;
        }
        for (CategoryNodeElement category : categoryNodeElements.asIterable()) {
            if (category.containsItem(element)){
                category.selectItem(element);
                return true;
            }
        }
        return false;
    }

    class SelectionManager {

        private Element selectedItem;

        public void selectItem(Element item) {
            if (selectedItem != null) {
                selectedItem.removeAttribute(SELECTED_ATTRIBUTE);
            }
            selectedItem = item;
            selectedItem.setAttribute(SELECTED_ATTRIBUTE, SELECTED_ATTRIBUTE);
        }
    }

    /** Item style selectors for a categories list item. */
    public interface Css extends CssResource {
        int menuListBorderPx();

        String categoryItem();

        String listBase();

        String listContainer();

        String category();

        String categoryLabel();

        String expandControl();

        String categoryHeader();

        String expandedImage();

        String itemContainer();

        String headerIcon();

        String headerText();
    }

    public interface Resources extends ClientBundle {
        @Source({"CategoriesList.css", "com/codenvy/ide/common/constants.css", "com/codenvy/ide/api/ui/style.css"})
        Css defaultCategoriesListCss();

        @Source("expansionIcon.svg")
        SVGResource expansionImage();
    }
}
