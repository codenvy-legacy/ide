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
package org.exoplatform.ide.client.outline;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.outline.OutlineItemCreator;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 20, 2012 2:44:29 PM anya $
 */
public class OutlineTreeViewModel implements TreeViewModel {
    /** Custom cell for displaying Outline nodes. */
    public static class TokenCell extends AbstractCell<Object> {
        /**
         * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
         *      com.google.gwt.safehtml.shared.SafeHtmlBuilder)
         */
        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, Object value, SafeHtmlBuilder sb) {
            if (value instanceof TokenBeenImpl) {
                sb.appendHtmlConstant((createItemWidget((TokenBeenImpl)value).getElement().getInnerHTML()));
            } else if (value instanceof EmptyTreeMessage) {
                EmptyTreeMessage emptyTreeMessage = (EmptyTreeMessage)value;
                if (emptyTreeMessage.getImage() != null) {
                    sb.appendHtmlConstant(emptyTreeMessage.getImage().toString());
                }
                sb.appendEscaped(emptyTreeMessage.getMessage());
            }
        }

        protected Widget createItemWidget(Token token) {
            OutlineItemCreator outlineItemCreator = IDE.getInstance().getOutlineItemCreator(((TokenBeenImpl)token).getMimeType());
            if (outlineItemCreator != null) {
                return outlineItemCreator.getOutlineItemWidget(token);
            } else {
                FlowPanel flowPanel = new FlowPanel();
                Element span = DOM.createSpan();
                span.setInnerHTML(token.getName());
                flowPanel.getElement().appendChild(span);
                return flowPanel;
            }
        }
    }

    private SingleSelectionModel<Object> selectionModel;

    private ListDataProvider<Object>     dataProvider = new ListDataProvider<Object>();

    public OutlineTreeViewModel(SingleSelectionModel<Object> selectionModel) {
        this.selectionModel = selectionModel;
    }

    /** @see com.google.gwt.view.client.TreeViewModel#getNodeInfo(java.lang.Object) */
    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {
        if (value == null) {
            return new DefaultNodeInfo<Object>(dataProvider, new TokenCell(), selectionModel, null);
        } else {
            List<Object> tokens = new ArrayList<Object>();
            for (TokenBeenImpl token : ((TokenBeenImpl)value).getSubTokenList()) {
                if (token.getName() != null && token.getType() != null) {
                    tokens.add(token);
                }
            }
            return new DefaultNodeInfo<Object>(new ListDataProvider<Object>(tokens), new TokenCell(), selectionModel, null);
        }
    }

    /** @see com.google.gwt.view.client.TreeViewModel#isLeaf(java.lang.Object) */
    @Override
    public boolean isLeaf(Object value) {
        if (value == null)
            return false;

        if (value instanceof TokenBeenImpl) {
            return ((TokenBeenImpl)value).getSubTokenList() == null || ((TokenBeenImpl)value).getSubTokenList().isEmpty();
        } else if (value instanceof EmptyTreeMessage) {
            return true;
        }
        return false;
    }

    public ListDataProvider<Object> getDataProvider() {
        return dataProvider;
    }
}
