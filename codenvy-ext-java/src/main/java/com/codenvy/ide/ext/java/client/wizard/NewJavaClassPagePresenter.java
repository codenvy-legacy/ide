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
package com.codenvy.ide.ext.java.client.wizard;

import com.codenvy.ide.ext.java.client.JavaClientBundle;
import com.codenvy.ide.ext.java.client.core.JavaConventions;
import com.codenvy.ide.ext.java.client.core.JavaCore;
import com.codenvy.ide.ext.java.client.projectmodel.CompilationUnit;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.client.projectmodel.Package;
import com.codenvy.ide.ext.java.client.projectmodel.SourceFolder;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.runtime.IStatus;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;


/**
 * Java class presenter wizard. It's may create Java class, interface, enum and annotation.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class NewJavaClassPagePresenter extends AbstractWizardPagePresenter implements
                                                                           NewJavaClassPageView.ActionDelegate {

    private enum JavaTypes {
        CLASS("Class"), INTERFACE("Interface"), ENUM("Enum"), ANNOTATION("Annotation");

        private String value;

        private JavaTypes(String value) {
            this.value = value;
        }

        /** @see java.lang.Enum#toString() */
        @Override
        public String toString() {
            return value;
        }
    }

    private static final String TYPE_CONTENT = "\n{\n}";

    private static final String DEFAULT_PACKAGE = "(default package)";

    public static final String CAPTION = "Create new Java class.";

    private final Project activeProject;

    private NewJavaClassPageView view;

    private EditorAgent editorAgent;

    private JsonArray<Folder> parents;

    private Folder parent;

    private String errorMessage;

    private boolean isTypeNameValid;

    private boolean notJavaProject;

    @Inject
    public NewJavaClassPagePresenter(NewJavaClassPageView view, ResourceProvider provider, EditorAgent editorAgent,
                                     SelectionAgent selectionAgent) {
        super(CAPTION, JavaClientBundle.INSTANCE.newClassWizz());
        this.view = view;
        this.editorAgent = editorAgent;
        activeProject = provider.getActiveProject();
        view.setDelegate(this);
        init();

        if (selectionAgent.getSelection() != null && selectionAgent.getSelection().getFirstElement() instanceof Folder) {
            Folder selectedFolder = (Folder)selectionAgent.getSelection().getFirstElement();
            if (parents.indexOf(selectedFolder) >= 0) {
                view.selectParent(parents.indexOf(selectedFolder));
                parent = selectedFolder;
            }
        }
    }

    private void init() {
        if (activeProject instanceof JavaProject) {
            JsonArray<String> classTypes = JsonCollections.createArray();
            for (JavaTypes t : JavaTypes.values()) {
                classTypes.add(t.toString());
            }
            view.setClassTypes(classTypes);
            JavaProject javaProject = (JavaProject)activeProject;
            JsonArray<String> parentNames = JsonCollections.createArray();
            parents = JsonCollections.createArray();
            for (SourceFolder sf : javaProject.getSourceFolders().asIterable()) {
                parentNames.add(sf.getName() + " " + DEFAULT_PACKAGE);
                parents.add(sf);
                for (Resource r : sf.getChildren().asIterable()) {
                    if (r instanceof Package) {
                        parentNames.add(r.getName());
                        parents.add((Folder)r);
                    }
                }
            }
            view.setParents(parentNames);
            parent = parents.get(0);
        } else {
            notJavaProject = true;
            view.disableAllUi();
        }
    }

    /** {@inheritDoc} */
    @Override
    public WizardPagePresenter flipToNext() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFinish() {
        return isCompleted();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return !notJavaProject && isTypeNameValid;
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (notJavaProject) {
            return activeProject.getName() + " is not Java project";
        }
        if (!isTypeNameValid) {
            return errorMessage;
        } else {
            if (errorMessage != null) {
                return errorMessage;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void doFinish() {
        doCreate();
        super.doFinish();

    }

    private void doCreate() {
        if (view.getClassName() == null || view.getClassName().isEmpty()) {
            return;
        }

        try {
            switch (JavaTypes.valueOf(view.getClassType().toUpperCase())) {
                case CLASS:
                    createClass(view.getClassName());
                    break;

                case INTERFACE:
                    createInterface(view.getClassName());
                    break;

                case ENUM:
                    createEnum(view.getClassName());
                    break;

                case ANNOTATION:
                    createAnnotation(view.getClassName());
                    break;
            }
        } catch (Exception e) {
            Log.error(getClass(), e);
        }
    }

    /**
     * add to class name java extension(".java")
     *
     * @return class file name
     */
    private String createCassName() {
        return view.getClassName() + ".java";
    }

    private void createAnnotation(String name) {
        StringBuilder content = new StringBuilder(getPackage());
        content.append("public @interface ").append(name).append(TYPE_CONTENT);
        createClassFile(content.toString());
    }

    private void createEnum(String name) {
        StringBuilder content = new StringBuilder(getPackage());
        content.append("public enum ").append(name).append(TYPE_CONTENT);
        createClassFile(content.toString());

    }

    private void createInterface(String name) {
        StringBuilder content = new StringBuilder(getPackage());
        content.append("public interface ").append(name).append(TYPE_CONTENT);
        createClassFile(content.toString());
    }

    private void createClass(String name) {
        StringBuilder content = new StringBuilder(getPackage());
        content.append("public class ").append(name).append(TYPE_CONTENT);
        createClassFile(content.toString());
    }

    /**
     * Get package declaration. If parent is source folder( default package), return new line.
     *
     * @return the package declaration
     */
    private String getPackage() {
        if (parent instanceof SourceFolder) {
            return "\n";
        }

        String packageName = parent.getName();
        return "package " + packageName + ";\n\n";
    }

    private void createClassFile(final String fileContent) {
        ((JavaProject)activeProject).createCompilationUnit(parent, createCassName(), fileContent,
                                                           new AsyncCallback<CompilationUnit>() {
                                                               @Override
                                                               public void onFailure(Throwable caught) {
                                                                   Log.error(NewJavaClassPagePresenter.class, caught);
                                                               }

                                                               @Override
                                                               public void onSuccess(CompilationUnit result) {
                                                                   editorAgent.openEditor(result);
                                                               }
                                                           });
    }

    /** {@inheritDoc} */
    @Override
    public void parentChanged(int index) {
        parent = parents.get(index);
    }

    /** {@inheritDoc} */
    @Override
    public void checkTypeName() {
        validate(createCassName());
        delegate.updateControls();
    }

    /**
     * Validate compilation unit name.
     *
     * @param value
     *         name of new compilation unit
     */
    private void validate(String value) {
        IStatus status =
                JavaConventions.validateCompilationUnitName(value, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
                                                            JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE));
        switch (status.getSeverity()) {
            case IStatus.WARNING:
                errorMessage = status.getMessage();
                isTypeNameValid = true;
                break;
            case IStatus.OK:
                isTypeNameValid = true;
                errorMessage = null;
                break;

            default:
                isTypeNameValid = false;
                errorMessage = status.getMessage();
                break;
        }
    }
}
