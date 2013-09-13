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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionModel;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.eclipse.jdt.client.JavaPreferencesSettings;
import org.eclipse.jdt.client.UpdateOutlineEvent;
import org.eclipse.jdt.client.UpdateOutlineHandler;
import org.eclipse.jdt.client.core.Flags;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.IMethodBinding;
import org.eclipse.jdt.client.core.dom.ITypeBinding;
import org.eclipse.jdt.client.core.dom.IVariableBinding;
import org.eclipse.jdt.client.core.dom.Modifier;
import org.eclipse.jdt.client.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.client.core.util.TypeFinder;
import org.eclipse.jdt.client.event.GenerateNewConstructorUsingFieldsEvent;
import org.eclipse.jdt.client.event.GenerateNewConstructorUsingFieldsHandler;
import org.eclipse.jdt.client.internal.corext.dom.Bindings;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class GenerateNewConstructorUsingFieldsPresenter implements GenerateNewConstructorUsingFieldsHandler,
                                                                   ViewClosedHandler, UpdateOutlineHandler, EditorActiveFileChangedHandler {
    private final Provider<Display> displayProvider;

    private final IDE ide;

    public interface Display extends IsView {
        String ID = "ideGenerateNewConstructorFields";

        HasData<IVariableBinding> getDataDisplay();

        HasClickHandlers getOkButton();

        HasClickHandlers getCancelButton();

        HasClickHandlers getSelectAllButton();

        HasClickHandlers getDeselectelectButton();

        TakesValue<Boolean> getGenerateComment();

        TakesValue<Boolean> getOmitSuper();

        HasValue<Boolean> getPublic();

        HasValue<Boolean> getProtected();

        HasValue<Boolean> getDefault();

        HasValue<Boolean> getPrivate();

        int getConstructorIndex();

        void setConstructors(IMethodBinding[] constructors);

        HasChangeHandlers getConstructorChangeHandlers();

        void setOmitSuperEnabled(boolean enabled);
    }

    private Display display;

    private int insertPos;

    private IDocument document;

    private CompilationUnit unit;

    private Editor editor;

    private ListDataProvider<IVariableBinding> provider;

    private IMethodBinding[] constructors;

    private ITypeBinding typeBinding;

    private final Dialogs dialogs;

    /**
     *
     */
    @Inject
    public GenerateNewConstructorUsingFieldsPresenter(HandlerManager eventBus, IDE ide, Provider<Display> displayProvider,
                                                      Dialogs dialogs) {
        this.ide = ide;
        this.displayProvider = displayProvider;
        this.dialogs = dialogs;
        eventBus.addHandler(GenerateNewConstructorUsingFieldsEvent.TYPE, this);
        eventBus.addHandler(ViewClosedEvent.TYPE, this);
        eventBus.addHandler(UpdateOutlineEvent.TYPE, this);
        eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    /** @see org.eclipse.jdt.client.event.GenerateNewConstructorUsingFieldsHandler#onGenerateNewConstructorUsingFields(org.eclipse.jdt
     * .client.event.GenerateNewConstructorUsingFieldsEvent) */
    @Override
    public void onGenerateNewConstructorUsingFields(GenerateNewConstructorUsingFieldsEvent event) {
        AbstractTypeDeclaration type = findType();
        if (type == null || (typeBinding = type.resolveBinding()) == null) {
            dialogs.showError("The operation is not applicable to the current selection. Select a class.");
            return;
        }

        HashSet<IVariableBinding> fieldsToBindings = new HashSet<IVariableBinding>();
        ArrayList<IVariableBinding> selected = new ArrayList<IVariableBinding>();

        IVariableBinding[] candidates = typeBinding.getDeclaredFields();
        for (int i = 0; i < candidates.length; i++) {
            IVariableBinding curr = candidates[i];
            if (curr.isSynthetic()) {
                continue;
            }
            if (Modifier.isStatic(curr.getModifiers())) {
                continue;
            }
            if (Modifier.isFinal(curr.getModifiers())) {
                ASTNode declaringNode = unit.findDeclaringNode(curr);
                if (declaringNode instanceof VariableDeclarationFragment
                    && ((VariableDeclarationFragment)declaringNode).getInitializer() != null) {
                    continue; // Do not add final fields which have been set in the <clinit>
                }
            }
            fieldsToBindings.add(curr);
            selected.add(curr);
        }
        if (fieldsToBindings.isEmpty()) {
            dialogs.showError(
                    "The selected type contains no fields which may be initialized in a constructor.");
            return;
        }

        ArrayList<IVariableBinding> fields = new ArrayList<IVariableBinding>();
        fields.addAll(fieldsToBindings); // paranoia code, should not happen
        provider = new ListDataProvider<IVariableBinding>(fields);
        constructors = null;

        if (typeBinding.isAnonymous()) {
            dialogs.showError("=Anonymous classes cannot contain explicitly declared constructors.");
            return;
        }
        if (typeBinding.isEnum()) {
            constructors = new IMethodBinding[]{getObjectConstructor(unit.getAST())};
        } else {
            constructors = StubUtility2.getVisibleConstructors(typeBinding, false, true);
            if (constructors.length == 0) {
                dialogs.showError("There are no constructors from the superclass which may be used.");
                return;
            }
        }
        if (display == null) {
            display = displayProvider.get();
            ide.openView(display.asView());
            bind();
        }
    }

    private IMethodBinding getObjectConstructor(AST ast) {
        final ITypeBinding binding = ast.resolveWellKnownType("java.lang.Object"); //$NON-NLS-1$
        return Bindings.findMethodInType(binding, "Object", new ITypeBinding[0]); //$NON-NLS-1$
    }

    /**
     *
     */
    private AbstractTypeDeclaration findType() {
        if (editor != null) {
            document = editor.getDocument();

            try {
                insertPos = document.getLineOffset(editor.getCursorRow() - 1);
                TypeFinder f = new TypeFinder(insertPos);
                unit.accept(f);
                return f.type;
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    private void bind() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ide.closeView(Display.ID);
            }
        });

        display.getOkButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                generate();
                ide.closeView(Display.ID);
            }
        });

        display.getSelectAllButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                selectAll();
            }
        });

        display.getDeselectelectButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                SelectionModel<? super IVariableBinding> selectionModel = display.getDataDisplay().getSelectionModel();
                if (selectionModel instanceof MultiSelectionModel)
                    ((MultiSelectionModel<IVariableBinding>)selectionModel).clear();
            }
        });

        display.getConstructorChangeHandlers().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                int constructorIndex = display.getConstructorIndex();
                boolean enabled = constructors[constructorIndex].getParameterTypes().length == 0;
                display.setOmitSuperEnabled(enabled);
                if (!enabled) {
                    display.getOmitSuper().setValue(Boolean.FALSE);
                }

            }
        });
        provider.addDataDisplay(display.getDataDisplay());
        selectAll();
        display.setConstructors(constructors);
        IMethodBinding constructor = constructors[display.getConstructorIndex()];
        if(constructor.getParameterTypes().length != 0){
            display.setOmitSuperEnabled(false);
            display.getOmitSuper().setValue(Boolean.FALSE);
        }

    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    private void selectAll() {
        SelectionModel<? super IVariableBinding> selectionModel = display.getDataDisplay().getSelectionModel();
        if (selectionModel instanceof MultiSelectionModel)
            ((MultiSelectionModel<IVariableBinding>)selectionModel).clear();
        for (IVariableBinding b : provider.getList())
            selectionModel.setSelected(b, true);
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    private void generate() {
        SelectionModel<? super IVariableBinding> selectionModel = display.getDataDisplay().getSelectionModel();
        Set<IVariableBinding> selected = null;
        if (selectionModel instanceof MultiSelectionModel)
            selected = ((MultiSelectionModel<IVariableBinding>)selectionModel).getSelectedSet();
        else
            return;
        if (selected == null || selected.isEmpty())
            return;

        CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings();
        settings.createComments = display.getGenerateComment().getValue();
        IMethodBinding constructor = constructors[display.getConstructorIndex()];
        AddCustomConstructorOperation operation =
                new AddCustomConstructorOperation(unit, typeBinding, selected.toArray(new IVariableBinding[selected.size()]),
                                                  constructor, insertPos, settings, document);
        int flags = 0;
        if (display.getPublic().getValue())
            flags |= Flags.AccPublic;
        if (display.getProtected().getValue())
            flags |= Flags.AccProtected;
        if (display.getDefault().getValue())
            flags |= Flags.AccDefault;
        if (display.getPrivate().getValue())
            flags |= Flags.AccPrivate;
        operation.setVisibility(flags);
        if (constructor.getParameterTypes().length == 0)
            operation.setOmitSuper(display.getOmitSuper().getValue());
        operation.run();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display)
            display = null;
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        editor = event.getEditor();
    }

    /** @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent) */
    @Override
    public void onUpdateOutline(UpdateOutlineEvent event) {
        unit = event.getCompilationUnit();
    }
}
