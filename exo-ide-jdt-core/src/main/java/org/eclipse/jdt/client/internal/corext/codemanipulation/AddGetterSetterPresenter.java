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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.eclipse.jdt.client.JavaPreferencesSettings;
import org.eclipse.jdt.client.UpdateOutlineEvent;
import org.eclipse.jdt.client.UpdateOutlineHandler;
import org.eclipse.jdt.client.core.Flags;
import org.eclipse.jdt.client.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.ITypeBinding;
import org.eclipse.jdt.client.core.dom.IVariableBinding;
import org.eclipse.jdt.client.core.dom.TypeDeclaration;
import org.eclipse.jdt.client.core.util.TypeFinder;
import org.eclipse.jdt.client.event.AddGetterSetterEvent;
import org.eclipse.jdt.client.event.AddGetterSetterHandler;
import org.eclipse.jdt.client.runtime.CoreException;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class AddGetterSetterPresenter implements AddGetterSetterHandler, ViewClosedHandler, UpdateOutlineHandler,
                                                 EditorActiveFileChangedHandler, GetterSetterEntryProvider {

    public interface Display extends IsView {
        String ID = "IdeAddGetterSetter";

        HasClickHandlers getOkButton();

        HasClickHandlers getCancelButton();

        HasClickHandlers getSelectAllButton();

        HasClickHandlers getDeselectAllButton();

        HasClickHandlers getSelectGettersButton();

        HasClickHandlers getSelectSettersButton();

        HasClickHandlers getAllowFinalSettersBox();

        GetterSetterTreeModel getTreeModel();

        int getSortOrder();

        void setSortVariants(String[] var);

        TakesValue<Boolean> getGenerateComment();

        TakesValue<Boolean> getFinal();

        TakesValue<Boolean> getSynchronized();

        TakesValue<Boolean> getAllowSettersFinal();

        HasValue<Boolean> getPublic();

        HasValue<Boolean> getProtected();

        HasValue<Boolean> getDefault();

        HasValue<Boolean> getPrivate();

        void openField(IVariableBinding field);

    }

    private static GetterSetterEntryProvider instance;

    private Display display;

    private final IDE ide;

    private CompilationUnit unit;

    private Editor editor;

    private Map<IVariableBinding, GetterSetterEntry[]> map;

    private ITypeBinding type;

    private TypeDeclaration typeDeclaration;

    private int insertPos;

    private IDocument document;

    private boolean fSort;

    private final Provider<Display> displayProvider;

    /** @param eventBus */
    @Inject
    public AddGetterSetterPresenter(HandlerManager eventBus, IDE ide, Provider<Display> displayProvider) {
        super();
        this.displayProvider = displayProvider;
        instance = this;
        this.ide = ide;
        eventBus.addHandler(AddGetterSetterEvent.TYPE, this);
        eventBus.addHandler(ViewClosedEvent.TYPE, this);
        eventBus.addHandler(UpdateOutlineEvent.TYPE, this);
        eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    /** @see org.eclipse.jdt.client.event.AddGetterSetterHandler#onAddGetterSetter(org.eclipse.jdt.client.event.AddGetterSetterEvent) */
    @Override
    public void onAddGetterSetter(AddGetterSetterEvent event) {

        AbstractTypeDeclaration typeDeclaration = findType();
        if (typeDeclaration == null) {
            Dialogs.getInstance().showError(
                    "The Generate Getters and Setters operation is only applicable to types and fields in source files.");
            return;
        }
        type = typeDeclaration.resolveBinding();

        if (type.isAnnotation()) {
            Dialogs.getInstance()
                   .showError("The Generate Getters and Setters operation is not applicable to annotations.");
            return;
        }
        if (type.isInterface()) {
            Dialogs.getInstance().showError("The Generate Getters and Setters operation is not applicable to interfaces");
            return;
        }
        if (type.isEnum()) {
            Dialogs.getInstance().showError("The Generate Getters and Setters operation is not applicable to enums");
            return;
        }
        map = createGetterSetterMapping(type);
        if (map.isEmpty()) {
            Dialogs.getInstance().showError("The type contains no fields or all fields have getters/setters already.");
            return;
        }

        this.typeDeclaration = (TypeDeclaration)typeDeclaration;
        if (display == null) {
            display = displayProvider.get();
            bind();
            ide.openView(display.asView());
        }
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
     * @param type
     *         the type
     * @return map IVariableBinding -> GetterSetterEntry[]
     */
    private Map<IVariableBinding, GetterSetterEntry[]> createGetterSetterMapping(ITypeBinding type) {
        IVariableBinding[] fields = type.getDeclaredFields();
        Map<IVariableBinding, GetterSetterEntry[]> result = new LinkedHashMap<IVariableBinding, GetterSetterEntry[]>();
        for (int i = 0; i < fields.length; i++) {
            IVariableBinding field = fields[i];
            int flags = field.getModifiers();
            if (!Flags.isEnum(flags)) {
                List<GetterSetterEntry> l = new ArrayList<GetterSetterEntry>(2);
                if (GetterSetterUtil.getGetter(field) == null) {
                    l.add(new GetterSetterEntry(field, true, Flags.isFinal(flags)));
                }

                if (GetterSetterUtil.getSetter(field) == null) {
                    l.add(new GetterSetterEntry(field, false, Flags.isFinal(flags)));
                }

                if (!l.isEmpty())
                    result.put(field, l.toArray(new GetterSetterEntry[l.size()]));
            }
        }
        return result;
    }

    /**
     *
     */
    private void bind() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ide.closeView(Display.ID);
            }
        });

        display.getSelectAllButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                MultiSelectionModel<Object> model = display.getTreeModel().getSelectionModel();
                for (IVariableBinding b : map.keySet()) {
                    model.setSelected(b, true);
                    for (GetterSetterEntry entry : map.get(b))
                        model.setSelected(entry, true);
                }
            }
        });

        display.getDeselectAllButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                display.getTreeModel().getSelectionModel().clear();
            }
        });

        display.getSelectGettersButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                MultiSelectionModel<Object> model = display.getTreeModel().getSelectionModel();
                model.clear();
                for (IVariableBinding b : map.keySet()) {
                    for (GetterSetterEntry entry : map.get(b)) {
                        if (entry.isGetter()) {
                            model.setSelected(entry, true);
                            display.openField(b);
                        }
                    }
                }
            }
        });

        display.getSelectSettersButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                MultiSelectionModel<Object> model = display.getTreeModel().getSelectionModel();
                model.clear();
                for (IVariableBinding b : map.keySet()) {
                    for (GetterSetterEntry entry : map.get(b)) {
                        if (!entry.isGetter()) {
                            model.setSelected(entry, true);
                            display.openField(b);
                        }
                    }
                }
            }
        });

        display.getAllowFinalSettersBox().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                display.getTreeModel().setAllowFinalSetters(display.getAllowSettersFinal().getValue());

            }
        });

        display.getOkButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                generate();
                ide.closeView(Display.ID);
            }
        });

        display.setSortVariants(new String[]{"Fields in getter/setter pairs", "First getters, then setters"});
    }

    /**
     *
     */
    private void generate() {
        Set<Object> result = display.getTreeModel().getSelectionModel().getSelectedSet();
        final Set<IVariableBinding> keySet = new LinkedHashSet<IVariableBinding>(map.keySet());
        CodeGenerationSettings codeGenerationSettings = JavaPreferencesSettings.getCodeGenerationSettings();
        codeGenerationSettings.createComments = display.getGenerateComment().getValue();
        IVariableBinding[] getterFields, setterFields, getterSetterFields;
        fSort = display.getSortOrder() == 1;
        if (fSort) {
            getterFields = getGetterFields(result, keySet);
            setterFields = getSetterFields(result, keySet);
            getterSetterFields = new IVariableBinding[0];
        } else {
            getterFields = getGetterOnlyFields(result, keySet);
            setterFields = getSetterOnlyFields(result, keySet);
            getterSetterFields = getGetterSetterFields(result, keySet);
        }
        AddGetterSetterOperation op =
                new AddGetterSetterOperation(type, typeDeclaration, insertPos, document, getterFields, setterFields,
                                             getterSetterFields, unit, codeGenerationSettings, true, true);
        setOperationStatusFields(op);
        try {
            op.run();
        } catch (CoreException e) {
            e.printStackTrace();
        }

    }

    private void setOperationStatusFields(AddGetterSetterOperation op) {
        // Set the status fields corresponding to the visibility and modifiers set
        int flags = 0;
        if (display.getPublic().getValue())
            flags |= Flags.AccPublic;
        if (display.getProtected().getValue())
            flags |= Flags.AccProtected;
        if (display.getDefault().getValue())
            flags |= Flags.AccDefault;
        if (display.getPrivate().getValue())
            flags |= Flags.AccPrivate;
        if (display.getSynchronized().getValue()) {
            flags |= Flags.AccSynchronized;
        }
        if (display.getFinal().getValue()) {
            flags |= Flags.AccFinal;
        }
        op.setSort(fSort);
        op.setVisibility(flags);
    }

    // returns a list of fields with setter entries checked
    private static IVariableBinding[] getSetterFields(Set<Object> result, Set<IVariableBinding> set) {
        List<IVariableBinding> list = new ArrayList<IVariableBinding>(0);
        GetterSetterEntry entry = null;
        for (Object each : result) {
            if ((each instanceof GetterSetterEntry)) {
                entry = (GetterSetterEntry)each;
                if (!entry.isGetter()) {
                    list.add(entry.getField());
                }
            }
        }
        list = reorderFields(list, set);
        return list.toArray(new IVariableBinding[list.size()]);
    }

    // returns a list of fields with getter entries checked
    private static IVariableBinding[] getGetterFields(Set<Object> result, Set<IVariableBinding> set) {
        List<IVariableBinding> list = new ArrayList<IVariableBinding>(0);
        GetterSetterEntry entry = null;
        for (Object each : result) {
            if ((each instanceof GetterSetterEntry)) {
                entry = (GetterSetterEntry)each;
                if (entry.isGetter()) {
                    list.add(entry.getField());
                }
            }
        }
        list = reorderFields(list, set);
        return list.toArray(new IVariableBinding[list.size()]);
    }

    // returns a list of fields with only getter entries checked
    private static IVariableBinding[] getGetterOnlyFields(Set<Object> result, Set<IVariableBinding> set) {
        List<IVariableBinding> list = new ArrayList<IVariableBinding>(0);
        GetterSetterEntry entry = null;
        boolean getterSet = false;
        for (Object each : result) {
            if ((each instanceof GetterSetterEntry)) {
                entry = (GetterSetterEntry)each;
                if (entry.isGetter()) {
                    list.add(entry.getField());
                    getterSet = true;
                }
                if ((!entry.isGetter()) && getterSet) {
                    list.remove(entry.getField());
                    getterSet = false;
                }
            } else
                getterSet = false;
        }
        list = reorderFields(list, set);
        return list.toArray(new IVariableBinding[list.size()]);
    }

    // returns a list of fields with only setter entries checked
    private static IVariableBinding[] getSetterOnlyFields(Set<Object> result, Set<IVariableBinding> set) {
        List<IVariableBinding> list = new ArrayList<IVariableBinding>(0);
        GetterSetterEntry entry = null;
        boolean getterSet = false;
        for (Object each : result) {
            if ((each instanceof GetterSetterEntry)) {
                entry = (GetterSetterEntry)each;
                if (entry.isGetter()) {
                    getterSet = true;
                }
                if ((!entry.isGetter()) && (getterSet != true)) {
                    list.add(entry.getField());
                    getterSet = false;
                }
            } else
                getterSet = false;
        }
        list = reorderFields(list, set);
        return list.toArray(new IVariableBinding[list.size()]);
    }

    // returns a list of fields with both entries checked
    private static IVariableBinding[] getGetterSetterFields(Set<Object> result, Set<IVariableBinding> set) {
        List<IVariableBinding> list = new ArrayList<IVariableBinding>(0);
        GetterSetterEntry entry = null;
        boolean getterSet = false;
        for (Object each : result) {
            if ((each instanceof GetterSetterEntry)) {
                entry = (GetterSetterEntry)each;
                if (entry.isGetter()) {
                    getterSet = true;
                }
                if ((!entry.isGetter()) && (getterSet == true)) {
                    list.add(entry.getField());
                    getterSet = false;
                }
            } else
                getterSet = false;
        }
        list = reorderFields(list, set);
        return list.toArray(new IVariableBinding[list.size()]);
    }

    private static List<IVariableBinding> reorderFields(List<IVariableBinding> collection, Set<IVariableBinding> set) {
        final List<IVariableBinding> list = new ArrayList<IVariableBinding>(collection.size());
        for (final Iterator<IVariableBinding> iterator = set.iterator(); iterator.hasNext(); ) {
            final IVariableBinding field = iterator.next();
            if (collection.contains(field))
                list.add(field);
        }
        return list;
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display)
            display = null;
    }

    /** @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent) */
    @Override
    public void onUpdateOutline(UpdateOutlineEvent event) {
        unit = event.getCompilationUnit();
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        editor = event.getEditor();
    }

    /** @see org.eclipse.jdt.client.internal.corext.codemanipulation.GetterSetterEntryProvider#getFields() */
    @Override
    public Map<IVariableBinding, GetterSetterEntry[]> getFields() {
        return map;
    }

    @Override
    public boolean allowSetterForFinalFields() {
        return false;//display.getAllowSettersFinal().getValue();
    }

    /**
     */
    public static GetterSetterEntryProvider get() {
        return instance;
    }

}
