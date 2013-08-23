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

package org.exoplatform.gwtframework.ui.client.wrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Wrapper extends Composite implements RequiresResize {

    public static final int DEFAULT_PADDING = 5;

    private static WrapperUiBinder uiBinder = GWT.create(WrapperUiBinder.class);

    interface WrapperUiBinder extends UiBinder<Widget, Wrapper> {
    }

    interface Style extends CssResource {

        String contentCellHighlighted();

    }

    @UiField
    Style style;

    @UiField
    SimplePanel contentPanel;

    @UiField
    TableElement wraperTable;

    @UiField
    TableCellElement contentCell;

    private boolean highlighted = false;

    public Wrapper() {
        this(DEFAULT_PADDING);
    }

    public Wrapper(int padding) {
        initWidget(uiBinder.createAndBindUi(this));
        setPadding(padding);

        DOM.setElementAttribute(getElement(), "component-name", "wrapper");
        DOM.setElementAttribute(contentPanel.getElement(), "component-name", "wrapper-content-panel");
    }

    public void add(Widget w) {
        contentPanel.add(w);
    }

    public void setPadding(int padding) {
        contentCell.getStyle().setProperty("borderWidth", padding + "px");
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        if (this.highlighted != highlighted) {
            if (highlighted) {
                contentCell.addClassName(style.contentCellHighlighted());
            } else {
                contentCell.removeClassName(style.contentCellHighlighted());
            }
        }

        this.highlighted = highlighted;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        DOM.setStyleAttribute(getParent().getElement(), "overflow", "hidden");
    }

    @Override
    public void onResize() {
    }

}
