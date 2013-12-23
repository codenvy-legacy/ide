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
package com.codenvy.ide.ext.java.worker;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTVisitor;
import com.codenvy.ide.ext.java.jdt.core.dom.AbstractTypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.AnnotationTypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.AnonymousClassDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.ClassInstanceCreation;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.EnumDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.FieldDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.ImportDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.MethodDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.PackageDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.SingleVariableDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.Type;
import com.codenvy.ide.ext.java.jdt.core.dom.TypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.VariableDeclarationFragment;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodes;
import com.codenvy.ide.ext.java.messages.BlockTypes;
import com.codenvy.ide.ext.java.messages.impl.OutlineUpdateMessage;
import com.codenvy.ide.ext.java.messages.impl.WorkerCodeBlock;

import java.util.Iterator;

/**
 * Worker Outline updater
 *
 * @author Evgen Vidolob
 */
public class WorkerOutlineModelUpdater {

    private WorkerCodeBlock  root;
    private JavaParserWorker worker;

    public WorkerOutlineModelUpdater(JavaParserWorker worker) {
        this.worker = worker;
    }

    public void onCompilationUnitChanged(CompilationUnit cUnit, String id) {
        if (this.root == null) {
            root = WorkerCodeBlock.make();
//            root.setType(CodeBlock.ROOT_TYPE);
            root.setChildren(JsoArray.<WorkerCodeBlock>create());
        }

        OutlineAstVisitor v = new OutlineAstVisitor(root, cUnit);
        cUnit.accept(v);
        OutlineUpdateMessage message = OutlineUpdateMessage.make();
        message.setFileId(id).setBlocks(v.childrens);
        worker.sendMessage(message.serialize());
    }

    class OutlineAstVisitor extends ASTVisitor {
        private final ASTNode astParent;
        WorkerCodeBlock parent;
        Array<WorkerCodeBlock> childrens = JsoArray.create();
        private WorkerCodeBlock imports;

        /** @param parent */
        public OutlineAstVisitor(WorkerCodeBlock parent, ASTNode astParent) {
            super();
            this.parent = parent;
            this.astParent = astParent;
        }

        /** {@inheritDoc} */
        @Override
        public boolean visit(PackageDeclaration node) {
            WorkerCodeBlock i = createCodeBlock(BlockTypes.PACKAGE.getType(), node.getStartPosition(), node.getLength());
            i.setName(node.getName().getFullyQualifiedName());
            childrens.add(i);
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public boolean visit(ImportDeclaration node) {
            if (imports == null) {
                imports = WorkerCodeBlock.make();
                imports.setChildren(JsoArray.<WorkerCodeBlock>create());
                imports.setType(BlockTypes.IMPORTS.getType());
                imports.setName("import declarations");
                childrens.add(imports);
            }
            WorkerCodeBlock c = createCodeBlock(BlockTypes.IMPORT.getType(), node.getStartPosition(), node.getLength());
            c.setName(node.getName().getFullyQualifiedName());
            imports.getChildren().add(c);
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public boolean visit(TypeDeclaration node) {
            if (node == astParent)
                return true;
            WorkerCodeBlock type = addJavaType(node, node.isInterface() ? BlockTypes.INTERFACE : BlockTypes.CLASS);
            addChildrens(node, type);
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public boolean visit(AnnotationTypeDeclaration node) {
            if (node == astParent)
                return true;
            WorkerCodeBlock type = addJavaType(node, BlockTypes.ANNOTATION);
            addChildrens(node, type);

            return false;
        }

        private void addChildrens(ASTNode node, WorkerCodeBlock type) {
            OutlineAstVisitor typeVisitor = new OutlineAstVisitor(type, node);
            node.accept(typeVisitor);
            type.setChildren(typeVisitor.childrens);
        }

        /** {@inheritDoc} */
        @Override
        public boolean visit(EnumDeclaration node) {
            if (node == astParent)
                return true;
            WorkerCodeBlock type = addJavaType(node, BlockTypes.ENUM);
            addChildrens(node, type);
            return false;
        }

        private WorkerCodeBlock addJavaType(AbstractTypeDeclaration node, BlockTypes type) {
            WorkerCodeBlock t = createCodeBlock(type.getType(), node.getStartPosition(), node.getLength());
            t.setModifiers(node.getModifiers());
            t.setName(node.getName().getFullyQualifiedName());
            childrens.add(t);
            return t;
        }

        /** {@inheritDoc} */
        @Override
        public boolean visit(FieldDeclaration node) {
            if (node == astParent)
                return true;
            for (VariableDeclarationFragment fragment : node.fragments()) {
                WorkerCodeBlock f = createCodeBlock(BlockTypes.FIELD.getType(), fragment.getStartPosition(), fragment.getLength());
                f.setName(fragment.getName().getFullyQualifiedName());
                f.setModifiers(node.getModifiers());
                f.setJavaType(node.getType().toString());
                childrens.add(f);
            }
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public boolean visit(MethodDeclaration node) {
            if (node == astParent)
                return true;
            WorkerCodeBlock m = createCodeBlock(BlockTypes.METHOD.getType(), node.getStartPosition(), node.getLength());
            m.setModifiers(node.getModifiers());
            m.setName(node.getName().getFullyQualifiedName() + getMethodParams(node));
            Type returnType = node.getReturnType2();
            m.setJavaType(returnType != null ? returnType.toString() : null);
            childrens.add(m);
            addChildrens(node, m);
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public boolean visit(AnonymousClassDeclaration node) {
            if (node == astParent)
                return true;
            String name = "";
            ASTNode parent = node.getParent();
            if (parent instanceof ClassInstanceCreation) {
                Type type = ((ClassInstanceCreation)parent).getType();
                name = ASTNodes.getTypeName(type);
            }
            WorkerCodeBlock type = createCodeBlock(BlockTypes.CLASS.getType(), node.getStartPosition(), node.getLength());
            type.setName(name);
            childrens.add(type);
            addChildrens(node, type);
            return false;
        }

        private WorkerCodeBlock createCodeBlock(String type, int offset, int length) {
            return WorkerCodeBlock.make().setType(type).setOffset(offset).setLength(length).setChildren(JsoArray.<WorkerCodeBlock>create());
        }

        /**
         * Returns the string presentation of method's parameters.
         *
         * @param method
         * @return {@link String} method's parameters comma separated
         */
        @SuppressWarnings("unchecked")
        protected String getMethodParams(MethodDeclaration method) {
            if (method.parameters().isEmpty()) {
                return "()";
            } else {
                Iterator<SingleVariableDeclaration> paramsIterator = method.parameters().iterator();
                StringBuffer params = new StringBuffer("(");
                while (paramsIterator.hasNext()) {
                    SingleVariableDeclaration variable = paramsIterator.next();
                    params.append(variable.getType().toString());
                    if (paramsIterator.hasNext()) {
                        params.append(", ");
                    }
                }
                params.append(")");
                return params.toString();
            }
        }

    }
}
