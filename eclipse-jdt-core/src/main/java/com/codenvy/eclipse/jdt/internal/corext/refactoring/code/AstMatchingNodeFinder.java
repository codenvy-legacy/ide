/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.corext.refactoring.code;

import com.codenvy.eclipse.jdt.core.dom.ASTMatcher;
import com.codenvy.eclipse.jdt.core.dom.ASTNode;
import com.codenvy.eclipse.jdt.core.dom.ASTVisitor;
import com.codenvy.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import com.codenvy.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import com.codenvy.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import com.codenvy.eclipse.jdt.core.dom.ArrayAccess;
import com.codenvy.eclipse.jdt.core.dom.ArrayCreation;
import com.codenvy.eclipse.jdt.core.dom.ArrayInitializer;
import com.codenvy.eclipse.jdt.core.dom.ArrayType;
import com.codenvy.eclipse.jdt.core.dom.AssertStatement;
import com.codenvy.eclipse.jdt.core.dom.Assignment;
import com.codenvy.eclipse.jdt.core.dom.Block;
import com.codenvy.eclipse.jdt.core.dom.BlockComment;
import com.codenvy.eclipse.jdt.core.dom.BooleanLiteral;
import com.codenvy.eclipse.jdt.core.dom.BreakStatement;
import com.codenvy.eclipse.jdt.core.dom.CastExpression;
import com.codenvy.eclipse.jdt.core.dom.CatchClause;
import com.codenvy.eclipse.jdt.core.dom.CharacterLiteral;
import com.codenvy.eclipse.jdt.core.dom.ClassInstanceCreation;
import com.codenvy.eclipse.jdt.core.dom.CompilationUnit;
import com.codenvy.eclipse.jdt.core.dom.ConditionalExpression;
import com.codenvy.eclipse.jdt.core.dom.ConstructorInvocation;
import com.codenvy.eclipse.jdt.core.dom.ContinueStatement;
import com.codenvy.eclipse.jdt.core.dom.DoStatement;
import com.codenvy.eclipse.jdt.core.dom.EmptyStatement;
import com.codenvy.eclipse.jdt.core.dom.EnhancedForStatement;
import com.codenvy.eclipse.jdt.core.dom.EnumConstantDeclaration;
import com.codenvy.eclipse.jdt.core.dom.EnumDeclaration;
import com.codenvy.eclipse.jdt.core.dom.ExpressionStatement;
import com.codenvy.eclipse.jdt.core.dom.FieldAccess;
import com.codenvy.eclipse.jdt.core.dom.FieldDeclaration;
import com.codenvy.eclipse.jdt.core.dom.ForStatement;
import com.codenvy.eclipse.jdt.core.dom.IfStatement;
import com.codenvy.eclipse.jdt.core.dom.ImportDeclaration;
import com.codenvy.eclipse.jdt.core.dom.InfixExpression;
import com.codenvy.eclipse.jdt.core.dom.Initializer;
import com.codenvy.eclipse.jdt.core.dom.InstanceofExpression;
import com.codenvy.eclipse.jdt.core.dom.Javadoc;
import com.codenvy.eclipse.jdt.core.dom.LabeledStatement;
import com.codenvy.eclipse.jdt.core.dom.LineComment;
import com.codenvy.eclipse.jdt.core.dom.MarkerAnnotation;
import com.codenvy.eclipse.jdt.core.dom.MemberRef;
import com.codenvy.eclipse.jdt.core.dom.MemberValuePair;
import com.codenvy.eclipse.jdt.core.dom.MethodDeclaration;
import com.codenvy.eclipse.jdt.core.dom.MethodInvocation;
import com.codenvy.eclipse.jdt.core.dom.MethodRef;
import com.codenvy.eclipse.jdt.core.dom.MethodRefParameter;
import com.codenvy.eclipse.jdt.core.dom.Modifier;
import com.codenvy.eclipse.jdt.core.dom.NormalAnnotation;
import com.codenvy.eclipse.jdt.core.dom.NullLiteral;
import com.codenvy.eclipse.jdt.core.dom.NumberLiteral;
import com.codenvy.eclipse.jdt.core.dom.PackageDeclaration;
import com.codenvy.eclipse.jdt.core.dom.ParameterizedType;
import com.codenvy.eclipse.jdt.core.dom.ParenthesizedExpression;
import com.codenvy.eclipse.jdt.core.dom.PostfixExpression;
import com.codenvy.eclipse.jdt.core.dom.PrefixExpression;
import com.codenvy.eclipse.jdt.core.dom.PrimitiveType;
import com.codenvy.eclipse.jdt.core.dom.QualifiedName;
import com.codenvy.eclipse.jdt.core.dom.QualifiedType;
import com.codenvy.eclipse.jdt.core.dom.ReturnStatement;
import com.codenvy.eclipse.jdt.core.dom.SimpleName;
import com.codenvy.eclipse.jdt.core.dom.SimpleType;
import com.codenvy.eclipse.jdt.core.dom.SingleMemberAnnotation;
import com.codenvy.eclipse.jdt.core.dom.SingleVariableDeclaration;
import com.codenvy.eclipse.jdt.core.dom.StringLiteral;
import com.codenvy.eclipse.jdt.core.dom.SuperConstructorInvocation;
import com.codenvy.eclipse.jdt.core.dom.SuperFieldAccess;
import com.codenvy.eclipse.jdt.core.dom.SuperMethodInvocation;
import com.codenvy.eclipse.jdt.core.dom.SwitchCase;
import com.codenvy.eclipse.jdt.core.dom.SwitchStatement;
import com.codenvy.eclipse.jdt.core.dom.SynchronizedStatement;
import com.codenvy.eclipse.jdt.core.dom.TagElement;
import com.codenvy.eclipse.jdt.core.dom.TextElement;
import com.codenvy.eclipse.jdt.core.dom.ThisExpression;
import com.codenvy.eclipse.jdt.core.dom.ThrowStatement;
import com.codenvy.eclipse.jdt.core.dom.TryStatement;
import com.codenvy.eclipse.jdt.core.dom.TypeDeclaration;
import com.codenvy.eclipse.jdt.core.dom.TypeDeclarationStatement;
import com.codenvy.eclipse.jdt.core.dom.TypeLiteral;
import com.codenvy.eclipse.jdt.core.dom.TypeParameter;
import com.codenvy.eclipse.jdt.core.dom.VariableDeclarationExpression;
import com.codenvy.eclipse.jdt.core.dom.VariableDeclarationFragment;
import com.codenvy.eclipse.jdt.core.dom.VariableDeclarationStatement;
import com.codenvy.eclipse.jdt.core.dom.WhileStatement;
import com.codenvy.eclipse.jdt.core.dom.WildcardType;
import com.codenvy.eclipse.jdt.internal.corext.dom.JdtASTMatcher;

import java.util.ArrayList;
import java.util.Collection;

class AstMatchingNodeFinder {

    private AstMatchingNodeFinder() {
    }

    public static ASTNode[] findMatchingNodes(ASTNode scope, ASTNode node) {
        Visitor visitor = new Visitor(node);
        scope.accept(visitor);
        return visitor.getMatchingNodes();
    }

    private static class Visitor extends ASTVisitor {

        Collection<ASTNode> fFound;

        ASTMatcher fMatcher;

        ASTNode fNodeToMatch;

        Visitor(ASTNode nodeToMatch) {
            fNodeToMatch = nodeToMatch;
            fFound = new ArrayList<ASTNode>();
            fMatcher = new JdtASTMatcher();
        }

        ASTNode[] getMatchingNodes() {
            return fFound.toArray(new ASTNode[fFound.size()]);
        }

        private boolean matches(ASTNode node) {
            fFound.add(node);
            return false;
        }

        @Override
        public boolean visit(AnonymousClassDeclaration node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ArrayAccess node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ArrayCreation node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ArrayInitializer node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ArrayType node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(AssertStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(Assignment node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(Block node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(BooleanLiteral node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(BreakStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(CastExpression node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(CatchClause node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(CharacterLiteral node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ClassInstanceCreation node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(CompilationUnit node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ConditionalExpression node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ConstructorInvocation node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ContinueStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(DoStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(EmptyStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ExpressionStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(FieldAccess node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(FieldDeclaration node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ForStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(IfStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ImportDeclaration node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(InfixExpression node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(Initializer node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(InstanceofExpression node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(Javadoc node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(LabeledStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(MethodDeclaration node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(MethodInvocation node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(NullLiteral node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(NumberLiteral node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(PackageDeclaration node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ParenthesizedExpression node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(PostfixExpression node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(PrefixExpression node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(PrimitiveType node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(QualifiedName node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ReturnStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(SimpleName node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(SimpleType node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(SingleVariableDeclaration node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(StringLiteral node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(SuperConstructorInvocation node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(SuperFieldAccess node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(SuperMethodInvocation node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(SwitchCase node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(SwitchStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(SynchronizedStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ThisExpression node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ThrowStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(TryStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(TypeDeclaration node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(TypeDeclarationStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(TypeLiteral node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(VariableDeclarationExpression node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(VariableDeclarationFragment node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(VariableDeclarationStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(WhileStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(AnnotationTypeDeclaration node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(AnnotationTypeMemberDeclaration node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(BlockComment node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(EnhancedForStatement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(EnumConstantDeclaration node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(EnumDeclaration node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(LineComment node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(MarkerAnnotation node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(MemberRef node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(MemberValuePair node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(MethodRef node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(MethodRefParameter node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(Modifier node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(NormalAnnotation node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(ParameterizedType node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(QualifiedType node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(SingleMemberAnnotation node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(TagElement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(TextElement node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(TypeParameter node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(WildcardType node) {
            if (node.subtreeMatch(fMatcher, fNodeToMatch)) {
                return matches(node);
            }
            return super.visit(node);
        }
    }
}
