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
package com.codenvy.ide.ext.java.client.editor.outline;

import com.codenvy.ide.ext.java.client.core.dom.ASTNode;
import com.codenvy.ide.ext.java.client.core.dom.ASTVisitor;
import com.codenvy.ide.ext.java.client.core.dom.AbstractTypeDeclaration;
import com.codenvy.ide.ext.java.client.core.dom.AnnotationTypeDeclaration;
import com.codenvy.ide.ext.java.client.core.dom.AnonymousClassDeclaration;
import com.codenvy.ide.ext.java.client.core.dom.ClassInstanceCreation;
import com.codenvy.ide.ext.java.client.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.client.core.dom.EnumDeclaration;
import com.codenvy.ide.ext.java.client.core.dom.FieldDeclaration;
import com.codenvy.ide.ext.java.client.core.dom.ImportDeclaration;
import com.codenvy.ide.ext.java.client.core.dom.MethodDeclaration;
import com.codenvy.ide.ext.java.client.core.dom.PackageDeclaration;
import com.codenvy.ide.ext.java.client.core.dom.SingleVariableDeclaration;
import com.codenvy.ide.ext.java.client.core.dom.Type;
import com.codenvy.ide.ext.java.client.core.dom.TypeDeclaration;
import com.codenvy.ide.ext.java.client.core.dom.VariableDeclarationFragment;
import com.codenvy.ide.ext.java.client.editor.AstProvider;
import com.codenvy.ide.ext.java.client.editor.AstProvider.AstListener;
import com.codenvy.ide.ext.java.client.internal.corext.dom.ASTNodes;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.texteditor.api.outline.CodeBlock;
import com.codenvy.ide.texteditor.api.outline.OutlineModel;

import java.util.Iterator;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class OutlineModelUpdater implements AstListener {

    class OutlineAstVisitor extends ASTVisitor {
        CodeBlock parent;

        JsonArray<CodeBlock> childrens = JsonCollections.createArray();

        private JavaCodeBlock imports;

        private final ASTNode astParent;

        /** @param parent */
        public OutlineAstVisitor(CodeBlock parent, ASTNode astParent) {
            super();
            this.parent = parent;
            this.astParent = astParent;
        }

        /** {@inheritDoc} */
        @Override
        public boolean visit(PackageDeclaration node) {
            JavaCodeBlock i =
                    new JavaCodeBlock(parent, BlockTypes.PACKAGE.getType(), node.getStartPosition(), node.getLength());
            i.setName(node.getName().getFullyQualifiedName());
            childrens.add(i);
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public boolean visit(ImportDeclaration node) {
            if (imports == null) {
                imports = new JavaCodeBlock();
                imports.setChildren(JsonCollections.<CodeBlock>createArray());
                imports.setType(BlockTypes.IMPORTS.getType());
                imports.setName("import declarations");
                imports.setParent(parent);
                childrens.add(imports);
            }
            JavaCodeBlock c =
                    new JavaCodeBlock(parent, BlockTypes.IMPORT.getType(), node.getStartPosition(), node.getLength());
            c.setName(node.getName().getFullyQualifiedName());
            imports.getChildren().add(c);
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public boolean visit(TypeDeclaration node) {
            if (node == astParent)
                return true;
            JavaCodeBlock type = addJavaType(node, node.isInterface() ? BlockTypes.INTERFACE : BlockTypes.CLASS);
            addChildrens(node, type);
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public boolean visit(AnnotationTypeDeclaration node) {
            if (node == astParent)
                return true;
            JavaCodeBlock type = addJavaType(node, BlockTypes.ANNOTATION);
            addChildrens(node, type);

            return false;
        }

        private void addChildrens(ASTNode node, JavaCodeBlock type) {
            OutlineAstVisitor typeVisitor = new OutlineAstVisitor(type, node);
            node.accept(typeVisitor);
            type.setChildren(typeVisitor.childrens);
        }

        /** {@inheritDoc} */
        @Override
        public boolean visit(EnumDeclaration node) {
            if (node == astParent)
                return true;
            JavaCodeBlock type = addJavaType(node, BlockTypes.ENUM);
            addChildrens(node, type);
            return false;
        }

        private JavaCodeBlock addJavaType(AbstractTypeDeclaration node, BlockTypes type) {
            JavaCodeBlock t = new JavaCodeBlock(parent, type.getType(), node.getStartPosition(), node.getLength());
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
                JavaCodeBlock f =
                        new JavaCodeBlock(parent, BlockTypes.FIELD.getType(), fragment.getStartPosition(), fragment.getLength());
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
            JavaCodeBlock m =
                    new JavaCodeBlock(parent, BlockTypes.METHOD.getType(), node.getStartPosition(), node.getLength());
            m.setModifiers(node.getModifiers());
            m.setName(node.getName().getFullyQualifiedName() + getMethodParams(node));
            m.setJavaType(node.getReturnType2().toString());
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
            JavaCodeBlock type =
                    new JavaCodeBlock(this.parent, BlockTypes.CLASS.getType(), node.getStartPosition(), node.getLength());
            type.setName(name);
            childrens.add(type);
            addChildrens(node, type);
            return false;
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

    private OutlineModel outlineModel;

    private JavaCodeBlock root;

    /** @param outlineModel */
    public OutlineModelUpdater(OutlineModel outlineModel, AstProvider provider) {
        super();
        this.outlineModel = outlineModel;
        provider.addAstListener(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onCompilationUnitChanged(CompilationUnit cUnit) {
        if (this.root == null) {
            root = new JavaCodeBlock();
            root.setType(CodeBlock.ROOT_TYPE);
            root.setChildren(JsonCollections.<CodeBlock>createArray());
            outlineModel.updateRoot(root);

        }
        OutlineAstVisitor v = new OutlineAstVisitor(root, cUnit);
        cUnit.accept(v);
        outlineModel.setRootChildren(v.childrens);
    }
}
