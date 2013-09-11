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
package org.eclipse.jdt.client.outline;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.*;

import org.eclipse.jdt.client.core.dom.*;
import org.eclipse.jdt.client.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.client.internal.corext.dom.GenericVisitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class QuickOutlineViewModel implements TreeViewModel {

    /**
     * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
     * @version $Id:
     */
    public class RootDataProvider extends AbstractDataProvider<ASTNode> {

        private final CompilationUnit compilationUnit;

        private HasData<ASTNode> display;

        /** @param unit */
        public RootDataProvider(CompilationUnit unit) {
            compilationUnit = unit;
        }

        /** @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData) */
        @Override
        protected void onRangeChanged(HasData<ASTNode> display) {
            this.display = display;
            List<ASTNode> list = getNodes();
            display.setRowData(0, list);

        }

        /** @return  */
        private List<ASTNode> getNodes() {
            List<ASTNode> list = new ArrayList<ASTNode>();
            list.add(compilationUnit.getPackage());
            if (!compilationUnit.types().isEmpty()) {
                for (Object o : compilationUnit.types()) {
                    list.add((ASTNode)o);
                }
            }
            return list;
        }

        /** @see com.google.gwt.view.client.AbstractDataProvider#updateRowData(int, java.util.List) */
        @Override
        public void updateRowData(int start, List<ASTNode> values) {
            super.updateRowData(start, values);
            if (display != null) {
                display.setRowCount(values.size());
            }

        }

        /**
         *
         */
        public void addAllNodes() {
            updateRowData(0, getNodes());
        }
    }

    public class AstNodeDataProvider extends AbstractDataProvider<ASTNode> {

        private final ASTNode value;

        /** @param value */
        public AstNodeDataProvider(ASTNode value) {
            this.value = value;
        }

        /** @see com.google.gwt.view.client.AbstractDataProvider#onRangeChanged(com.google.gwt.view.client.HasData) */
        @SuppressWarnings("unchecked")
        @Override
        protected void onRangeChanged(HasData<ASTNode> display) {
            final List<ASTNode> list = new ArrayList<ASTNode>();
            if (value instanceof TypeDeclaration) {
                final TypeDeclaration t = (TypeDeclaration)value;
                Object property = t.getProperty(CHOLDREN_LIST_EXO);
                if (property != null && filter != null)
                    list.addAll((List<ASTNode>)property);
                else {
                    t.accept(new TypeChildrenVisitor(list, t));
                }
            } else if (value instanceof EnumDeclaration) {
                EnumDeclaration e = (EnumDeclaration)value;
                Object property = e.getProperty(CHOLDREN_LIST_EXO);
                if (property != null && filter != null)
                    list.addAll((List<ASTNode>)property);
                else {
                    list.addAll(e.enumConstants());
                    list.addAll(e.bodyDeclarations());
                }
            } else if (value instanceof AnnotationTypeDeclaration) {
                AnnotationTypeDeclaration a = (AnnotationTypeDeclaration)value;
                Object property = a.getProperty(CHOLDREN_LIST_EXO);
                if (property != null && filter != null)
                    list.addAll((List<ASTNode>)property);
                else
                    list.addAll(a.bodyDeclarations());
            } else if (value instanceof AnonymousClassDeclaration) {
                AnonymousClassDeclaration ann = (AnonymousClassDeclaration)value;
                Object property = ann.getProperty(CHOLDREN_LIST_EXO);
                if (property != null && filter != null) {
                    list.addAll((List<ASTNode>)property);
                } else
                    list.addAll(ann.bodyDeclarations());
            }
            display.setRowData(0, list);
        }
    }

    /** Custom cell for displaying Outline nodes. */
    public static class AstCell extends AbstractCell<ASTNode> {
        /** Visitor to create widgets. */
        private CreateWidgetVisitor createWidgetVisitor = new CreateWidgetVisitor();

        /**
         * @see com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client.Cell.Context, java.lang.Object,
         *      com.google.gwt.safehtml.shared.SafeHtmlBuilder)
         */
        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, ASTNode value, SafeHtmlBuilder sb) {
            getNodeType(value, sb);
        }

        /**
         * Append widget to cell of the specified AST node.
         *
         * @param node
         * @param buf
         */
        private void getNodeType(ASTNode node, SafeHtmlBuilder buf) {
            if (node instanceof PackageDeclaration) {
                createWidgetVisitor.visit((PackageDeclaration)node);
            } else if (node instanceof ImportDeclaration) {
                createWidgetVisitor.visit((ImportDeclaration)node);
            } else if (node instanceof TypeDeclaration) {
                createWidgetVisitor.visit((TypeDeclaration)node);
            } else if (node instanceof MethodDeclaration) {
                createWidgetVisitor.visit((MethodDeclaration)node);
            } else if (node instanceof FieldDeclaration) {
                createWidgetVisitor.visit((FieldDeclaration)node);
            } else if (node instanceof EnumDeclaration) {
                createWidgetVisitor.visit((EnumDeclaration)node);
            } else if (node instanceof EnumConstantDeclaration) {
                createWidgetVisitor.visit((EnumConstantDeclaration)node);
            } else if (node instanceof AnnotationTypeDeclaration) {
                createWidgetVisitor.visit((AnnotationTypeDeclaration)node);
            } else if (node instanceof AnnotationTypeMemberDeclaration) {
                createWidgetVisitor.visit((AnnotationTypeMemberDeclaration)node);
            } else if (node instanceof AnonymousClassDeclaration) {
                createWidgetVisitor.visit((AnonymousClassDeclaration)node);
            } else if (node instanceof VariableDeclarationFragment) {
                createWidgetVisitor.visit((VariableDeclarationFragment)node);
            }

            buf.append(createWidgetVisitor.getHTML().toSafeHtml());
        }
    }

    private static final String PROPERTY_NAME = "AnonymousClass";

    /**
     *
     */
    private static final String CHOLDREN_LIST_EXO = "CHOLDREN_LIST_EXO";

    private final CompilationUnit unit;

    private final SingleSelectionModel<ASTNode> selectionModel;

    private RootDataProvider rootDataProvider;

    private String filter;

    /**
     * @param unit
     * @param selectionModel
     */
    public QuickOutlineViewModel(CompilationUnit unit, SingleSelectionModel<ASTNode> selectionModel) {
        this.unit = unit;
        this.selectionModel = selectionModel;
    }

    /** @see com.google.gwt.view.client.TreeViewModel#getNodeInfo(java.lang.Object) */
    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {
        if (value == null) {
            rootDataProvider = new RootDataProvider(unit);
            return new DefaultNodeInfo<ASTNode>(rootDataProvider, new AstCell(), selectionModel, null);
        }

        if (value instanceof AbstractTypeDeclaration)
            return new DefaultNodeInfo(new AstNodeDataProvider((ASTNode)value), new AstCell(), selectionModel, null);

        if (value instanceof AnonymousClassDeclaration)
            return new DefaultNodeInfo(new AstNodeDataProvider((ASTNode)value), new AstCell(), selectionModel, null);

        if (value instanceof FieldDeclaration || value instanceof MethodDeclaration) {
            ASTNode f = (ASTNode)value;
            if (f.getProperty(PROPERTY_NAME) != null) {
                return new DefaultNodeInfo(new ListDataProvider<AnonymousClassDeclaration>(
                        (List<AnonymousClassDeclaration>)f.getProperty(PROPERTY_NAME)), new AstCell(), selectionModel, null);
            }
        }

        return null;
    }

    /** @see com.google.gwt.view.client.TreeViewModel#isLeaf(java.lang.Object) */
    @Override
    public boolean isLeaf(Object value) {
        if (value == null)
            return false;

        if (value instanceof AnnotationTypeDeclaration) {
            return ((AnnotationTypeDeclaration)value).bodyDeclarations().isEmpty();
        }

        if (value instanceof AbstractTypeDeclaration)
            return false;

        if (value instanceof AnonymousClassDeclaration)
            return false;

        if (value instanceof MethodDeclaration) {
            AnnonymousClassFinder classFinder = new AnnonymousClassFinder();
            ((MethodDeclaration)value).accept(classFinder);
            ((MethodDeclaration)value).setProperty(PROPERTY_NAME, classFinder.anonymousClassDeclaration);
            return classFinder.anonymousClassDeclaration.isEmpty();
        }

        if (value instanceof FieldDeclaration) {
            AnnonymousClassFinder classFinder = new AnnonymousClassFinder();
            ((FieldDeclaration)value).accept(classFinder);
            ((ASTNode)value).setProperty(PROPERTY_NAME, classFinder.anonymousClassDeclaration);
            return classFinder.anonymousClassDeclaration.isEmpty();
        }
        return true;
    }

    private static class AnnonymousClassFinder extends ASTVisitor {

        private List<AnonymousClassDeclaration> anonymousClassDeclaration = new ArrayList<AnonymousClassDeclaration>();

        /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.AnonymousClassDeclaration) */
        @Override
        public boolean visit(AnonymousClassDeclaration node) {
            anonymousClassDeclaration.add(node);
            return true;
        }

    }

    /**
     *
     */
    public void removeFilter() {
        if (filter == null)
            return;
        filter = null;
        rootDataProvider.addAllNodes();
    }

    /** @param value */
    public void filter(String value) {
        filter = value.toLowerCase();
        List<ASTNode> list = new ArrayList<ASTNode>();
        if (match(rootDataProvider.compilationUnit.getPackage().getName().getFullyQualifiedName())) {
            list.add(rootDataProvider.compilationUnit.getPackage());
        }
        if (!rootDataProvider.compilationUnit.types().isEmpty()) {
            AbstractTypeDeclaration type = (AbstractTypeDeclaration)rootDataProvider.compilationUnit.types().get(0);
            if (match(type.getName().getFullyQualifiedName()))
                list.add(type);
            else {
                FilterNodeVisitor v = new FilterNodeVisitor(type);
                type.accept(v);
                if (!v.childrens.isEmpty()) {
                    type.setProperty(CHOLDREN_LIST_EXO, v.childrens);
                    list.add(type);
                }
            }
        }
        rootDataProvider.updateRowData(0, list);
    }

    boolean match(String value) {
        if (filter == null)
            return true;
        return value.toLowerCase().startsWith(filter);
    }

    private class FilterNodeVisitor extends GenericVisitor {
        public List<ASTNode> childrens = new ArrayList<ASTNode>();

        private final ASTNode parent;

        /** @param parent */
        public FilterNodeVisitor(ASTNode parent) {
            super();
            this.parent = parent;
        }

        /** @see org.eclipse.jdt.client.internal.corext.dom.GenericVisitor#visit(org.eclipse.jdt.client.core.dom.FieldDeclaration) */
        @Override
        public boolean visit(FieldDeclaration node) {
            if (node.equals(parent))
                return true;
            // Get field's name and type:
            Iterator iterator = node.fragments().iterator();
            while (iterator.hasNext()) {
                String name = ((VariableDeclarationFragment)iterator.next()).getName().getIdentifier();
                if (match(name)) {
                    childrens.add(node);
                    break;
                }
            }
            findChildrens(node);
            return false;
        }

        /** @see org.eclipse.jdt.client.internal.corext.dom.GenericVisitor#visit(org.eclipse.jdt.client.core.dom.MethodDeclaration) */
        @Override
        public boolean visit(MethodDeclaration node) {
            if (node.equals(parent))
                return true;
            if (match(node.getName().getFullyQualifiedName())) {
                childrens.add(node);
                node.setProperty(CHOLDREN_LIST_EXO, null);
            } else
                findChildrens(node);
            return false;
        }

        /** @see org.eclipse.jdt.client.internal.corext.dom.GenericVisitor#visit(org.eclipse.jdt.client.core.dom
         * .AnonymousClassDeclaration) */
        @Override
        public boolean visit(AnonymousClassDeclaration node) {
            if (node.equals(parent))
                return true;
            ASTNode parent = node.getParent();
            if (parent instanceof ClassInstanceCreation) {
                Type type = ((ClassInstanceCreation)parent).getType();
                String name = "new " + ASTNodes.getTypeName(type);
                if (match(name)) {
                    childrens.add(node);
                    node.setProperty(CHOLDREN_LIST_EXO, null);
                } else
                    findChildrens(node);
            }
            return false;
        }

        /** @param node */
        private void findChildrens(ASTNode node) {
            FilterNodeVisitor visitor = new FilterNodeVisitor(node);
            node.accept(visitor);
            if (!visitor.childrens.isEmpty()) {
                childrens.add(node);
                node.setProperty(CHOLDREN_LIST_EXO, visitor.childrens);
            }
        }

        /** @see org.eclipse.jdt.client.internal.corext.dom.GenericVisitor#visit(org.eclipse.jdt.client.core.dom.TypeDeclaration) */
        @Override
        public boolean visit(TypeDeclaration node) {
            if (node.equals(parent))
                return true;
            if (match(node.getName().getFullyQualifiedName())) {
                childrens.add(node);
                node.setProperty(CHOLDREN_LIST_EXO, null);
            } else
                findChildrens(node);
            return false;
        }

        /** @see org.eclipse.jdt.client.internal.corext.dom.GenericVisitor#visit(org.eclipse.jdt.client.core.dom
         * .AnnotationTypeDeclaration) */
        @Override
        public boolean visit(AnnotationTypeDeclaration node) {
            if (node.equals(parent))
                return true;
            if (match(node.getName().getFullyQualifiedName())) {
                childrens.add(node);
                node.setProperty(CHOLDREN_LIST_EXO, null);
            } else
                findChildrens(node);
            return false;
        }

        /** @see org.eclipse.jdt.client.internal.corext.dom.GenericVisitor#visit(org.eclipse.jdt.client.core.dom.EnumDeclaration) */
        @Override
        public boolean visit(EnumDeclaration node) {
            if (node.equals(parent))
                return true;
            if (match(node.getName().getFullyQualifiedName())) {
                childrens.add(node);
                node.setProperty(CHOLDREN_LIST_EXO, null);
            } else
                findChildrens(node);
            return false;
        }

        /** @see org.eclipse.jdt.client.internal.corext.dom.GenericVisitor#visit(org.eclipse.jdt.client.core.dom.EnumConstantDeclaration) */
        @Override
        public boolean visit(EnumConstantDeclaration node) {
            if (node.equals(parent))
                return true;

            if (match(node.getName().getFullyQualifiedName())) {
                childrens.add(node);
                node.setProperty(CHOLDREN_LIST_EXO, null);
            } else
                findChildrens(node);
            return false;
        }
    }
}
