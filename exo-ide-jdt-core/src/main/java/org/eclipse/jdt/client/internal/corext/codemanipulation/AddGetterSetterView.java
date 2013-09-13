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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.jdt.client.core.dom.IVariableBinding;
import org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display;
import org.exoplatform.gwtframework.ui.client.CellTreeResource;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class AddGetterSetterView extends ViewImpl implements Display {

    private CellTree.Resources res = GWT.create(CellTreeResource.class);

    /**
     *
     */
    private static AddGetterSetterViewUiBinder uiBinder = GWT.create(AddGetterSetterViewUiBinder.class);

    @UiField
    CellTree fieldTree;

    @UiField
    ImageButton selectAll;

    @UiField
    ImageButton deselectAll;

    @UiField
    ImageButton selectGetters;

    @UiField
    ImageButton selectSetters;

    @UiField
    ListBox sortTypeBox;

    @UiField
    SimpleCheckBox generateComent;

    @UiField
    RadioButton publicRadio;

    @UiField
    RadioButton protectedRadio;

    @UiField
    RadioButton defaultRadio;

    @UiField
    RadioButton privateRadio;

    @UiField
    CheckBox finalBox;

    @UiField
    CheckBox syncBox;

    @UiField
    SimpleCheckBox allowFinalBox;

    @UiField
    ImageButton cancelButton;

    @UiField
    ImageButton okButton;

    private final GetterSetterTreeModel model;

    interface AddGetterSetterViewUiBinder extends UiBinder<Widget, AddGetterSetterView> {
    }

    @Inject
    public AddGetterSetterView(GetterSetterTreeModel model) {
        super(ID, ViewType.POPUP, "Generate Getters and Setters", null, 550, 500, false);
        this.model = model;
        add(uiBinder.createAndBindUi(this));
    }

    @UiFactory
    CellTree createFieldTree() {
        CellTree cellTree = new CellTree(model, null, res);
        return cellTree;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getOkButton() */
    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getSelectAllButton() */
    @Override
    public HasClickHandlers getSelectAllButton() {
        return selectAll;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getDeselectAllButton() */
    @Override
    public HasClickHandlers getDeselectAllButton() {
        return deselectAll;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getSelectGettersButton() */
    @Override
    public HasClickHandlers getSelectGettersButton() {
        return selectGetters;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getSelectSettersButton() */
    @Override
    public HasClickHandlers getSelectSettersButton() {
        return selectSetters;
    }

    @Override
    public HasClickHandlers getAllowFinalSettersBox() {
        return allowFinalBox;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getTreeModel() */
    @Override
    public GetterSetterTreeModel getTreeModel() {
        return model;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getSortOrder() */
    @Override
    public int getSortOrder() {
        return sortTypeBox.getSelectedIndex();
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#setSortVariants(java.lang.String[]) */
    @Override
    public void setSortVariants(String[] var) {
        for (String s : var)
            sortTypeBox.addItem(s);
        sortTypeBox.setSelectedIndex(0);
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getGenerateComment() */
    @Override
    public TakesValue<Boolean> getGenerateComment() {
        return generateComent;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getFinal() */
    @Override
    public TakesValue<Boolean> getFinal() {
        return finalBox;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getSynchronized() */
    @Override
    public TakesValue<Boolean> getSynchronized() {
        return syncBox;
    }

    @Override
    public TakesValue<Boolean> getAllowSettersFinal() {
        return allowFinalBox;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getPublic() */
    @Override
    public HasValue<Boolean> getPublic() {
        return publicRadio;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getProtected() */
    @Override
    public HasValue<Boolean> getProtected() {
        return protectedRadio;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getDefault() */
    @Override
    public HasValue<Boolean> getDefault() {
        return defaultRadio;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#getPrivate() */
    @Override
    public HasValue<Boolean> getPrivate() {
        return privateRadio;
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.AddGetterSetterPresenter.Display#openField(org.eclipse.jdt.client
     * .core.dom.IVariableBinding) */
    @Override
    public void openField(IVariableBinding field) {
        TreeNode root = fieldTree.getRootTreeNode();
        for (int i = 0; i < root.getChildCount(); i++) {
            Object childValue = root.getChildValue(i);
            if (childValue.equals(field)) {
                root.setChildOpen(i, true);
                return;
            }
        }
    }

}
