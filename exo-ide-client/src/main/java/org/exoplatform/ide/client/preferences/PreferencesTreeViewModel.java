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
package org.exoplatform.ide.client.preferences;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import org.exoplatform.ide.client.framework.preference.PreferenceItem;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 20, 2012 2:44:29 PM anya $
 */
public class PreferencesTreeViewModel implements TreeViewModel {
    /** Custom cell for displaying Outline nodes. */
    public static class PreferenceItemCell extends AbstractCell<PreferenceItem> {
        protected Widget createItemWidget(PreferenceItem preferenceItem) {
            FlowPanel flowPanel = new FlowPanel();
            Element span = DOM.createSpan();
            span.setInnerHTML(preferenceItem.getName());
            if (preferenceItem.getImage() != null) {
                DOM.setStyleAttribute(preferenceItem.getImage().getElement(), "cssFloat", "left");
                DOM.setStyleAttribute(preferenceItem.getImage().getElement(), "marginRight", "5px");
                flowPanel.add(preferenceItem.getImage());
            }
            flowPanel.getElement().appendChild(span);
            return flowPanel;
        }

        /**
         * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
         *      com.google.gwt.safehtml.shared.SafeHtmlBuilder)
         */
        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, PreferenceItem value, SafeHtmlBuilder sb) {
            if (value instanceof PreferenceItem) {
                sb.appendHtmlConstant((createItemWidget((PreferenceItem)value).getElement().getInnerHTML()));
            }
        }

    }

    private SingleSelectionModel<PreferenceItem> selectionModel;

    private ListDataProvider<PreferenceItem> dataProvider = new ListDataProvider<PreferenceItem>();

    public PreferencesTreeViewModel(SingleSelectionModel<PreferenceItem> selectionModel) {
        this.selectionModel = selectionModel;
    }

    /** @see com.google.gwt.view.client.TreeViewModel#getNodeInfo(java.lang.Object) */
    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {
        if (value == null) {
            return new DefaultNodeInfo<PreferenceItem>(dataProvider, new PreferenceItemCell(), selectionModel, null);
        } else {
            return new DefaultNodeInfo<PreferenceItem>(new ListDataProvider<PreferenceItem>(
                    ((PreferenceItem)value).getChildren()), new PreferenceItemCell(), selectionModel, null);
        }
    }

    /** @see com.google.gwt.view.client.TreeViewModel#isLeaf(java.lang.Object) */
    @Override
    public boolean isLeaf(Object value) {
        if (value == null)
            return false;

        if (value instanceof PreferenceItem) {
            return ((PreferenceItem)value).getChildren() == null || ((PreferenceItem)value).getChildren().isEmpty();
        }
        return false;
    }

    public ListDataProvider<PreferenceItem> getDataProvider() {
        return dataProvider;
    }
}
