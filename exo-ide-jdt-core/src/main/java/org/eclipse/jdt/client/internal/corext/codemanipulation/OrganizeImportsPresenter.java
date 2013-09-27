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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

import org.eclipse.jdt.client.UpdateOutlineEvent;
import org.eclipse.jdt.client.UpdateOutlineHandler;
import org.eclipse.jdt.client.core.ISourceRange;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.search.TypeNameMatch;
import org.eclipse.jdt.client.event.OrganizeImportsEvent;
import org.eclipse.jdt.client.event.OrganizeImportsHandler;
import org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsOperation.IChooseImportQuery;
import org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsOperation.ITextEditCallback;
import org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsOperation.TypeNameMatchCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.edits.MalformedTreeException;
import org.exoplatform.ide.editor.shared.text.edits.TextEdit;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class OrganizeImportsPresenter implements UpdateOutlineHandler, OrganizeImportsHandler,
                                                 EditorActiveFileChangedHandler, ViewClosedHandler {
    public interface Display extends IsView {
        String ID = "ideOrganizeImportsView";

        HasClickHandlers getBackButton();

        HasClickHandlers getNextButton();

        HasClickHandlers getCancelButton();

        HasClickHandlers getFinishButton();

        HasData<TypeNameMatch> getTypeList();

        HasValue<String> getFilterInput();

        HasText getPageLabel();

        void setNextButtonEnabled(boolean enabled);

        void setBackButtonEnabled(boolean enabled);

        void setFinishButtonEnabled(boolean b);

        void addDoubleClickHandler(DoubleClickHandler handler);

        void addKeyHandler(KeyDownHandler handler);

        void setFocusInList();
    }

    private FileModel activeFile;

    private CompilationUnit compilationUnit;

    private Editor editor;

    private TypeNameMatchCallback callback;

    private TypeNameMatch[][] openChoices;

    private Display display;

    private ListDataProvider<TypeNameMatch> dataProvider;

    private TypeNameMatch[] chosen;

    private int index;

    private SingleSelectionModel<TypeNameMatch> selectionModel;

    /** @param event */
    public OrganizeImportsPresenter(HandlerManager eventBus) {
        super();
        eventBus.addHandler(UpdateOutlineEvent.TYPE, this);
        eventBus.addHandler(OrganizeImportsEvent.TYPE, this);
        eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        eventBus.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent) */
    @Override
    public void onUpdateOutline(UpdateOutlineEvent event) {
        activeFile = event.getFile();
        compilationUnit = event.getCompilationUnit();
    }

    /** @see org.eclipse.jdt.client.event.OrganizeImportsHandler#onOrganizeImports(org.eclipse.jdt.client.event.OrganizeImportsEvent) */
    @Override
    public void onOrganizeImports(OrganizeImportsEvent event) {
        if (editor == null)
            return;
        OrganizeImportsOperation operation =
                new OrganizeImportsOperation(editor.getDocument(), compilationUnit, false, true, true,
                                             new IChooseImportQuery() {

                                                 @Override
                                                 public void chooseImports(TypeNameMatch[][] openChoices, ISourceRange[] ranges,
                                                                           TypeNameMatchCallback callback) {
                                                     OrganizeImportsPresenter.this.callback = callback;
                                                     OrganizeImportsPresenter.this.openChoices = openChoices;
                                                     showForm();
                                                 }

                                             }, activeFile.getProject().getId());
        operation.createTextEdit(new ITextEditCallback() {

            @Override
            public void textEditCreated(TextEdit edit) {
                try {
                    edit.apply(editor.getDocument());
                } catch (MalformedTreeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (BadLocationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void showForm() {
        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bind();
    }

    /**
     *
     */
    private void bind() {
        chosen = new TypeNameMatch[openChoices.length];
        index = 0;
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                callback.typeNameMatch(null);
                IDE.getInstance().closeView(Display.ID);

            }
        });

        display.getNextButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                next();
            }
        });

        display.getBackButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                back();
            }
        });

        display.getFinishButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                finish();
            }

        });

        display.setBackButtonEnabled(false);
        if (openChoices.length == 1) {
            display.setNextButtonEnabled(false);
        }
        display.setFinishButtonEnabled(false);

        selectionModel = new SingleSelectionModel<TypeNameMatch>();
        selectionModel.addSelectionChangeHandler(new Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                chosen[index] = selectionModel.getSelectedObject();
                boolean hasNoNull = true;
                for (TypeNameMatch type : chosen) {
                    if (type == null) {
                        hasNoNull = false;
                        break;
                    }
                }
                if (hasNoNull) {
                    display.setFinishButtonEnabled(true);
                }

            }
        });

        display.getFilterInput().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                if (event.getValue() == null || event.getValue().isEmpty()) {
                    updateForm();
                    return;
                }
                List<TypeNameMatch> types = new ArrayList<TypeNameMatch>();
                for (TypeNameMatch type : openChoices[index]) {
                    if (type.getFullyQualifiedName().startsWith(event.getValue()))
                        types.add(type);
                }

                dataProvider.setList(types);
            }
        });

        display.addDoubleClickHandler(new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                nextOrFinish();
            }
        });

        display.addKeyHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    nextOrFinish();
                }
            }
        });

        display.getTypeList().setSelectionModel(selectionModel);
        updateForm();
    }

    /**
     *
     */
    private void updateForm() {
        display.getPageLabel().setText("Page " + (index + 1) + " of " + openChoices.length);
        dataProvider = new ListDataProvider<TypeNameMatch>(Arrays.asList(openChoices[index]));
        dataProvider.addDataDisplay(display.getTypeList());
        selectionModel.setSelected(openChoices[index][0], true);
        display.setFocusInList();
    }

    private void finish() {
        callback.typeNameMatch(chosen);
        IDE.getInstance().closeView(Display.ID);
    }

    /**
     *
     */
    private void back() {
        display.setNextButtonEnabled(true);
        index--;
        updateForm();
        if (index == 0)
            display.setBackButtonEnabled(false);
    }

    /**
     *
     */
    private void next() {
        display.setBackButtonEnabled(true);
        index++;
        updateForm();
        if (index >= chosen.length - 1) {
            display.setNextButtonEnabled(false);
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getFile() == null)
            return;
        if (event.getFile().getMimeType().equals(MimeType.APPLICATION_JAVA)) {
            editor = event.getEditor();
        } else
            editor = null;
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView().getId().equals(Display.ID)) {
            display = null;
        }
    }

    /**
     *
     */
    private void nextOrFinish() {
        if (index < chosen.length - 1)
            next();
        else
            finish();
    }

}
