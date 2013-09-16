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
package org.eclipse.jdt.client.core.formatter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.core.formatter.FormatterProfilePresenter.Display;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.framework.module.EditorNotFoundException;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.shared.text.IDocument;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 10:09:02 AM Apr 4, 2012 evgen $
 */
public class FormatterProfileView extends ViewImpl implements Display {

    private static FormatterProfileViewUiBinder uiBinder = GWT.create(FormatterProfileViewUiBinder.class);

    @UiField
    FlowPanel editorPanel;

    @UiField
    SelectItem profilesSelect;

    @UiField
    ImageButton okButton;

    private Editor editor;

    interface FormatterProfileViewUiBinder extends UiBinder<Widget, FormatterProfileView> {
    }

    public FormatterProfileView() {
        super(ID, ViewType.MODAL, "Formatter", null, 725, 390, true);
        add(uiBinder.createAndBindUi(this));

        try {
            editor = IDE.getInstance().getFileTypeRegistry().getEditor(MimeType.APPLICATION_JAVA);
            editor.asWidget().setSize("100%", "100%");
            editorPanel.add(editor);
            editor.setText(JdtClientBundle.INSTANCE.formatterSample().getText());
        } catch (EditorNotFoundException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e.getMessage()));
        }
    }

    /** @see org.eclipse.jdt.client.core.formatter.FormatterProfilePresenter.Display#getOkButton() */
    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    /** @see org.eclipse.jdt.client.core.formatter.FormatterProfilePresenter.Display#getProfilesSelect() */
    @Override
    public SelectItem getProfilesSelect() {
        return profilesSelect;
    }

    /** @see org.eclipse.jdt.client.core.formatter.FormatterProfilePresenter.Display#getDocument() */
    @Override
    public IDocument getDocument() {
        return editor.getDocument();
    }
}
