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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;

import org.eclipse.jdt.client.codeassistant.CompletionProposalLabelProvider;
import org.eclipse.jdt.client.core.dom.IMethodBinding;
import org.eclipse.jdt.client.core.dom.IVariableBinding;
import org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter.Display;
import org.eclipse.jdt.client.ui.BindingLabelProvider;
import org.eclipse.jdt.client.ui.JavaElementLabels;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class GenerateNewConstructorUsingFieldsView extends ViewImpl implements Display {

    private static GenerateNewConstructorUsingFieldsViewUiBinder uiBinder = GWT
            .create(GenerateNewConstructorUsingFieldsViewUiBinder.class);

    private final class FieldCell extends CompositeCell<IVariableBinding> {
        /** @param hasCells */
        public FieldCell(List<HasCell<IVariableBinding, ?>> hasCells) {
            super(hasCells);
        }

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, IVariableBinding value, SafeHtmlBuilder sb) {
            sb.appendHtmlConstant("<span style=\"height:24px;\">");
            super.render(context, value, sb);
            sb.appendHtmlConstant(AbstractImagePrototype.create(
                    CompletionProposalLabelProvider.createFieldImageDescriptor(value.getModifiers())).getHTML());
            sb.appendEscaped(value.getName());
            sb.appendHtmlConstant("</span>");
        }

        /**
         * @see com.google.gwt.cell.client.CompositeCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
         *      com.google.gwt.safehtml.shared.SafeHtmlBuilder, com.google.gwt.cell.client.HasCell)
         */
        @Override
        protected <X> void render(com.google.gwt.cell.client.Cell.Context context, IVariableBinding value,
                                  SafeHtmlBuilder sb, HasCell<IVariableBinding, X> hasCell) {
            Cell<X> cell = hasCell.getCell();
            sb.appendHtmlConstant("<span>");
            cell.render(context, hasCell.getValue(value), sb);
            sb.appendHtmlConstant("</span>");
        }
    }

    @UiField
    CellList<IVariableBinding> cellList;

    @UiField
    ImageButton selectAllButton;

    @UiField
    ImageButton deselectAllButton;

    @UiField
    ListBox superConstructorBox;

    @UiField
    ImageButton okButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    CheckBox generateCommentsBox;

    @UiField
    CheckBox omitCallSuperBox;

    @UiField
    RadioButton publicRadio;

    @UiField
    RadioButton protectedRadio;

    @UiField
    RadioButton defaultRadio;

    @UiField
    RadioButton privateRadio;

    private MultiSelectionModel<IVariableBinding> selectionModel;

    interface GenerateNewConstructorUsingFieldsViewUiBinder extends
                                                            UiBinder<Widget, GenerateNewConstructorUsingFieldsView> {
    }

    public GenerateNewConstructorUsingFieldsView() {
        super(ID, ViewType.MODAL, "Generate Constructor using fields", null, 490, 480, false);
        selectionModel = new MultiSelectionModel<IVariableBinding>();
        add(uiBinder.createAndBindUi(this));
    }

    @UiFactory
    CellList<IVariableBinding> createList() {
        ArrayList<HasCell<IVariableBinding, ?>> fieldsCells = new ArrayList<HasCell<IVariableBinding, ?>>();
        fieldsCells.add(new HasCell<IVariableBinding, Boolean>() {

            private CheckboxCell cell = new CheckboxCell(true, false);

            public Cell<Boolean> getCell() {
                return cell;
            }

            public FieldUpdater<IVariableBinding, Boolean> getFieldUpdater() {
                return null;
            }

            public Boolean getValue(IVariableBinding object) {
                return selectionModel.isSelected(object);
            }
        });

        CellList<IVariableBinding> cellList2 = new CellList<IVariableBinding>(new FieldCell(fieldsCells));
        cellList2.setSelectionModel(selectionModel,
                                    DefaultSelectionEventManager.<IVariableBinding>createCheckboxManager());
        return cellList2;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter.Display#getDataDisplay() */
    @Override
    public HasData<IVariableBinding> getDataDisplay() {
        return cellList;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter.Display#getOkButton() */
    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }


    /** {@inheritDoc} */
    @Override
    public void setConstructors(IMethodBinding[] constructors) {
        BindingLabelProvider provider = new BindingLabelProvider(JavaElementLabels.ALL_DEFAULT, 0);
        for (IMethodBinding b : constructors) {
            superConstructorBox.addItem(provider.getText(b));
        }
    }

    /** {@inheritDoc} */
    @Override
    public HasChangeHandlers getConstructorChangeHandlers() {
        return superConstructorBox;
    }

    /** {@inheritDoc} */
    @Override
    public void setOmitSuperEnabled(boolean enabled) {
        omitCallSuperBox.setEnabled(enabled);
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter
     * .Display#getConstructorIndex() */
    @Override
    public int getConstructorIndex() {
        return superConstructorBox.getSelectedIndex();
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter
     * .Display#getGenerateComment() */
    @Override
    public TakesValue<Boolean> getGenerateComment() {
        return generateCommentsBox;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter.Display#getOmitSuper() */
    @Override
    public TakesValue<Boolean> getOmitSuper() {
        return omitCallSuperBox;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter.Display#getPublic() */
    @Override
    public HasValue<Boolean> getPublic() {
        return publicRadio;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter.Display#getProtected() */
    @Override
    public HasValue<Boolean> getProtected() {
        return protectedRadio;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter.Display#getDefault() */
    @Override
    public HasValue<Boolean> getDefault() {
        return defaultRadio;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter.Display#getPrivate() */
    @Override
    public HasValue<Boolean> getPrivate() {
        return privateRadio;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter
     * .Display#getSelectAllButton() */
    @Override
    public HasClickHandlers getSelectAllButton() {
        return selectAllButton;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.GenerateNewConstructorUsingFieldsPresenter.Display#getDeselectelectButton() */
    @Override
    public HasClickHandlers getDeselectelectButton() {
        return deselectAllButton;
    }

}
