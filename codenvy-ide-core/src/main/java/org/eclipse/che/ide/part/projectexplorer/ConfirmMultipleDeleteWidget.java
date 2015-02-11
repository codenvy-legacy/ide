/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.part.projectexplorer;

import java.util.List;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.project.tree.generic.StorableNode;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmMultipleDeleteWidget implements IsWidget {

    /**
     * The UI binder instance.
     */
    private static final UiBinder UIBINDER = GWT.create(UiBinder.class);

    /**
     * The widget itself.
     */
    private Widget widget;

    /**
     * The list of deleted items shown to the user for confirmation.
     */
    @UiField
    TextArea items;
    @UiField
    Label message;

    public ConfirmMultipleDeleteWidget(final List<StorableNode> nodes, final CoreLocalizationConstant localization) {
        this.widget = UIBINDER.createAndBindUi(this);
        this.message.setText(localization.deleteMultipleDialogMessage());
        final StringBuilder sb = new StringBuilder();
        String toAppend = "";
        for (StorableNode node : nodes) {
            sb.append(toAppend);
            toAppend = "\n";
            sb.append(node.getName());
        }
        items.setText(sb.toString());
    }

    @Override
    public Widget asWidget() {
        return this.widget;
    }

    /**
     * The UI Binder interface for this component.
     */
    public interface UiBinder extends com.google.gwt.uibinder.client.UiBinder<Widget, ConfirmMultipleDeleteWidget> {
    }
}
