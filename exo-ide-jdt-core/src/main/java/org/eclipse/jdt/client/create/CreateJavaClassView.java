/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
