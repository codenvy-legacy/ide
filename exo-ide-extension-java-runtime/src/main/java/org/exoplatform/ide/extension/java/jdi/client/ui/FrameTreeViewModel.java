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
package org.exoplatform.ide.extension.java.jdi.client.ui;

import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.Field;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class FrameTreeViewModel implements TreeViewModel {
    private SingleSelectionModel<Variable> selectionModel;

    private ListDataProvider<Variable> dataProvider = new ListDataProvider<Variable>();

    private DebuggerInfo debuggerInfo;

    public FrameTreeViewModel(SingleSelectionModel<Variable> selectionModel, DebuggerInfo debuggerInfo) {
        this.selectionModel = selectionModel;
        this.debuggerInfo = debuggerInfo;
    }

    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {
        if (value == null) {
            return new DefaultNodeInfo<Variable>(dataProvider, new VariableCell(), selectionModel, null);
        }

        if (value instanceof Field) {
            return new DefaultNodeInfo<Variable>(new ValueDataProvider((Field)value, debuggerInfo), new VariableCell(), selectionModel,
                                                 null);
        } else {
            return new DefaultNodeInfo<Variable>(new ValueDataProvider((Variable)value, debuggerInfo), new VariableCell(), selectionModel,
                                                 null);
        }

    }

    @Override
    public boolean isLeaf(Object value) {
        if (value != null && value instanceof Variable)
            return ((Variable)value).isPrimitive();
        return false;
    }

    public ListDataProvider<Variable> getDataProvider() {
        return dataProvider;
    }

}
