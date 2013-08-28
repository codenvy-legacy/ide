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
package org.eclipse.jdt.client.internal.corext.codemanipulation;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.TreeViewModel;
import com.google.inject.Inject;

import org.eclipse.jdt.client.codeassistant.CompletionProposalLabelProvider;
import org.eclipse.jdt.client.core.dom.IVariableBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class GetterSetterTreeModel implements TreeViewModel {

    private final class FieldCell extends CompositeCell<Object> {
        /** @param hasCells */
        public FieldCell(List<HasCell<Object, ?>> hasCells) {
            super(hasCells);
        }

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, Object value, SafeHtmlBuilder sb) {
            sb.appendHtmlConstant("<span style=\"height:24px;\">");
            super.render(context, value, sb);
            if (value instanceof IVariableBinding) {
                IVariableBinding val = (IVariableBinding)value;
                sb.appendHtmlConstant(AbstractImagePrototype.create(
                        CompletionProposalLabelProvider.createFieldImageDescriptor(val.getModifiers())).getHTML());
                sb.appendEscaped(val.getName());
            } else {
                GetterSetterEntry entry = (GetterSetterEntry)value;
                sb.appendHtmlConstant(AbstractImagePrototype.create(
                        CompletionProposalLabelProvider.createFieldImageDescriptor(entry.getField().getModifiers())).getHTML());
                if (entry.isGetter()) {
                    sb.appendEscaped(GetterSetterUtil.getGetterName(entry.getField(), null) + "()"); //$NON-NLS-1$
                } else {
                    sb.appendEscaped(GetterSetterUtil.getSetterName(entry.getField(),
                                                                    null) + '(' + entry.getField().getType().getName() + ')');
                }
            }
            sb.appendHtmlConstant("</span>");
        }

        /**
         * @see com.google.gwt.cell.client.CompositeCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
         *      com.google.gwt.safehtml.shared.SafeHtmlBuilder, com.google.gwt.cell.client.HasCell)
         */
        @Override
        protected <X> void render(com.google.gwt.cell.client.Cell.Context context, Object value, SafeHtmlBuilder sb,
                                  HasCell<Object, X> hasCell) {
            Cell<X> cell = hasCell.getCell();
            sb.appendHtmlConstant("<span>");
            cell.render(context, hasCell.getValue(value), sb);
            sb.appendHtmlConstant("</span>");
        }
    }

    private final class RefreshDataProvider extends AsyncDataProvider<Object> {

        private List<Object> objectList;

        private HasData<Object> display;

        public RefreshDataProvider(List<Object> objectList) {

            this.objectList = objectList;
        }

        public void refresh() {
            updateRowCount(0, true);
            updateRowData(0, Collections.EMPTY_LIST);
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    updateRowCount(objectList.size(), true);
                    updateRowData(0, objectList);
                }
            });
        }

        @Override
        protected void onRangeChanged(HasData<Object> display) {
            this.display = display;
            updateRowCount(objectList.size(), true);
            updateRowData(0, objectList);
        }
    }

    private final MultiSelectionModel<Object> selectionModel;

    private boolean allowFinal;

    private List<HasCell<Object, ?>> fieldsCells;

    private final DefaultSelectionEventManager<Object> selectionManager = DefaultSelectionEventManager.createCheckboxManager();

    private Map<IVariableBinding, GetterSetterEntry[]> fields;

    private RefreshDataProvider rootDataProvider;

    /**
     *
     */
    @Inject
    public GetterSetterTreeModel(GetterSetterEntryProvider provider, final MultiSelectionModel<Object> selectionModel) {
        fields = provider.getFields();
        allowFinal = provider.allowSetterForFinalFields();
        this.selectionModel = selectionModel;
        selectionModel.addSelectionChangeHandler(new Handler() {
            private Set<Object> selected = Collections.emptySet();

            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                Set<Object> set = selectionModel.getSelectedSet();
                Object o = getChange(set);
                if (o != null) {
                    if (o instanceof IVariableBinding) {
                        boolean selected = set.contains(o);

                        GetterSetterEntry[] entries = fields.get(o);
                        if (entries == null) {
                            return;
                        }
                        for (GetterSetterEntry e : entries) {
                            selectionModel.setSelected(e, selected);
                        }

                    }
                }
                selected = new HashSet<Object>(set);
            }

            private Object getChange(Set<Object> set) {
                Set<Object> in, out;
                if (set.size() > selected.size()) {
                    in = set;
                    out = selected;
                } else {
                    in = selected;
                    out = set;
                }
                for (Object o : in) {
                    if (!out.contains(o)) {
                        return o;
                    }
                }
                return null;
            }
        });

        fieldsCells = new ArrayList<HasCell<Object, ?>>();
        fieldsCells.add(new HasCell<Object, Boolean>() {

            private CheckboxCell cell = new CheckboxCell(true, false);

            public Cell<Boolean> getCell() {
                return cell;
            }

            public FieldUpdater<Object, Boolean> getFieldUpdater() {
                return null;
            }

            public Boolean getValue(Object object) {
                return selectionModel.isSelected(object);
            }
        });
    }

    /** @see com.google.gwt.view.client.TreeViewModel#getNodeInfo(java.lang.Object) */
    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {
        if (value == null) {
            Set<IVariableBinding> set = fields.keySet();
            rootDataProvider = new RefreshDataProvider(new ArrayList<Object>(set));
            return new DefaultNodeInfo<Object>(rootDataProvider, new FieldCell(fieldsCells), selectionModel,
                                               selectionManager, null);
        }
        if (value instanceof IVariableBinding) {
            GetterSetterEntry[] entry = fields.get(value);
            ArrayList<Object> entries = new ArrayList<Object>();
            for (GetterSetterEntry en : entry) {
                if (en.isGetter() || (en.isFinal() && allowFinal) || !en.isFinal()) {
                    entries.add(en);
                }
            }
            return new DefaultNodeInfo<Object>(new ListDataProvider<Object>(entries), new FieldCell(fieldsCells),
                                               selectionModel, selectionManager, null);
        }
        return null;
    }

    /** @see com.google.gwt.view.client.TreeViewModel#isLeaf(java.lang.Object) */
    @Override
    public boolean isLeaf(Object value) {
        if (value == null) {
            return false;
        }
        return !(value instanceof IVariableBinding);
    }

    /** @return the selectionModel */
    public MultiSelectionModel<Object> getSelectionModel() {
        return selectionModel;
    }

    public void setAllowFinalSetters(Boolean value) {
        allowFinal = value;
        rootDataProvider.refresh();
    }
}
