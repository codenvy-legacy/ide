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
package org.eclipse.jdt.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.create.CreateJavaClassPresenter.Display;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.editor.java.client.JavaEditorExtension;

import java.util.Collection;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CreateJavaClassView extends ViewImpl implements Display {

    private static CreateJavaViewUiBinder uiBinder = GWT.create(CreateJavaViewUiBinder.class);

    private static final String ID = "ideCreateJavaClass";

    @UiField
    SelectItem sourceFolderField;

    @UiField
    SelectItem packageField;

    @UiField
    TextInput classNameField;

    @UiField
    SelectItem classTypeField;

    @UiField
    ImageButton createButton;

    @UiField
    ImageButton cancelButton;

    interface CreateJavaViewUiBinder extends UiBinder<Widget, CreateJavaClassView> {
    }

    public CreateJavaClassView() {
        super(ID, ViewType.MODAL, JavaEditorExtension.MESSAGES.createJavaClassTitle(), null, 460, 230, false);
        add(uiBinder.createAndBindUi(this));
        setCloseOnEscape(true);
    }

    /** @see org.eclipse.jdt.client.create.CreateJavaClassPresenter.Display#cancelButton() */
    @Override
    public HasClickHandlers cancelButton() {
        return cancelButton;
    }

    /** @see org.eclipse.jdt.client.create.CreateJavaClassPresenter.Display#createButton() */
    @Override
    public HasClickHandlers createButton() {
        return createButton;
    }

    /** @see org.eclipse.jdt.client.create.CreateJavaClassPresenter.Display#classNameField() */
    @Override
    public HasValue<String> classNameField() {
        return classNameField;
    }

    /** @see org.eclipse.jdt.client.create.CreateJavaClassPresenter.Display#classTypeField() */
    @Override
    public HasValue<String> classTypeField() {
        return classTypeField;
    }

    /** @see org.eclipse.jdt.client.create.CreateJavaClassPresenter.Display#setClassTypes(java.util.Collection) */
    @Override
    public void setClassTypes(Collection<String> types) {
        classTypeField.setValueMap(types.toArray(new String[types.size()]), types.iterator().next());
    }

    /** @see org.eclipse.jdt.client.create.CreateJavaClassPresenter.Display#enableCreateButton(boolean) */
    @Override
    public void enableCreateButton(boolean enabled) {
        createButton.setEnabled(enabled);
    }

    /** @see org.eclipse.jdt.client.create.CreateJavaClassPresenter.Display#sourceFolderField() */
    @Override
    public HasValue<String> sourceFolderField() {
        return sourceFolderField;
    }

    /** @see org.eclipse.jdt.client.create.CreateJavaClassPresenter.Display#packageField() */
    @Override
    public HasValue<String> packageField() {
        return packageField;
    }

    /** @see org.eclipse.jdt.client.create.CreateJavaClassPresenter.Display#setSourceFolders(java.util.Collection) */
    @Override
    public void setSourceFolders(Collection<String> sourceFolders) {
        sourceFolderField.setValueMap(sourceFolders.toArray(new String[sourceFolders.size()]), sourceFolders.iterator().next());
    }

    /** @see org.eclipse.jdt.client.create.CreateJavaClassPresenter.Display#setPackages(java.util.Collection) */
    @Override
    public void setPackages(Collection<String> packages) {
        packageField.setValueMap(packages.toArray(new String[packages.size()]), packages.iterator().next());
    }

    @Override
    public void focusInClassNameField() {
        classNameField.selectAll();
        classNameField.focus();
    }

}
