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
package com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals;

import com.codenvy.ide.ext.java.jdt.Images;
import com.codenvy.ide.ext.java.jdt.core.dom.AST;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.Block;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.Expression;
import com.codenvy.ide.ext.java.jdt.core.dom.ExpressionStatement;
import com.codenvy.ide.ext.java.jdt.core.dom.IBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.IMethodBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.IVariableBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.MethodDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.Modifier;
import com.codenvy.ide.ext.java.jdt.core.dom.ReturnStatement;
import com.codenvy.ide.ext.java.jdt.core.dom.Statement;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodeFactory;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ScopeAnalyzer;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.CorrectionMessages;
import com.codenvy.ide.text.Document;

import java.util.List;

public class MissingReturnTypeCorrectionProposal extends LinkedCorrectionProposal {

    //   private static final String RETURN_EXPRESSION_KEY = "value"; //$NON-NLS-1$

    private MethodDeclaration fMethodDecl;

    private ReturnStatement fExistingReturn;

    public MissingReturnTypeCorrectionProposal(MethodDeclaration decl, ReturnStatement existingReturn, int relevance,
                                               Document document) {
        super("", null, relevance, document, Images.correction_change); //$NON-NLS-1$
        fMethodDecl = decl;
        fExistingReturn = existingReturn;
    }

    @Override
    public String getName() {
        if (fExistingReturn != null) {
            return CorrectionMessages.INSTANCE.MissingReturnTypeCorrectionProposal_changereturnstatement_description();
        } else {
            return CorrectionMessages.INSTANCE.MissingReturnTypeCorrectionProposal_addreturnstatement_description();
        }
    }

    /*(non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.ASTRewriteCorrectionProposal#getRewrite()
     */
    @Override
    protected ASTRewrite getRewrite() {
        AST ast = fMethodDecl.getAST();

        ITypeBinding returnBinding = getReturnTypeBinding();

        if (fExistingReturn != null) {
            ASTRewrite rewrite = ASTRewrite.create(ast);

            Expression expression = evaluateReturnExpressions(ast, returnBinding, fExistingReturn.getStartPosition());
            if (expression != null) {
                rewrite.set(fExistingReturn, ReturnStatement.EXPRESSION_PROPERTY, expression, null);

                //            addLinkedPosition(rewrite.track(expression), true, RETURN_EXPRESSION_KEY);
            }
            return rewrite;
        } else {
            ASTRewrite rewrite = ASTRewrite.create(ast);

            Block block = fMethodDecl.getBody();

            List<Statement> statements = block.statements();
            int nStatements = statements.size();
            ASTNode lastStatement = null;
            if (nStatements > 0) {
                lastStatement = statements.get(nStatements - 1);
            }

            if (returnBinding != null && lastStatement instanceof ExpressionStatement
                && lastStatement.getNodeType() != ASTNode.ASSIGNMENT) {
                Expression expression = ((ExpressionStatement)lastStatement).getExpression();
                ITypeBinding binding = expression.resolveTypeBinding();
                if (binding != null && binding.isAssignmentCompatible(returnBinding)) {
                    Expression placeHolder = (Expression)rewrite.createMoveTarget(expression);

                    ReturnStatement returnStatement = ast.newReturnStatement();
                    returnStatement.setExpression(placeHolder);

                    rewrite.replace(lastStatement, returnStatement, null);
                    return rewrite;
                }
            }

            int offset;
            if (lastStatement == null) {
                offset = block.getStartPosition() + 1;
            } else {
                offset = lastStatement.getStartPosition() + lastStatement.getLength();
            }
            ReturnStatement returnStatement = ast.newReturnStatement();
            Expression expression = evaluateReturnExpressions(ast, returnBinding, offset);

            returnStatement.setExpression(expression);

            rewrite.getListRewrite(block, Block.STATEMENTS_PROPERTY).insertLast(returnStatement, null);

            //         addLinkedPosition(rewrite.track(returnStatement.getExpression()), true, RETURN_EXPRESSION_KEY);
            return rewrite;
        }
    }

    private ITypeBinding getReturnTypeBinding() {
        IMethodBinding methodBinding = fMethodDecl.resolveBinding();
        if (methodBinding != null && methodBinding.getReturnType() != null) {
            return methodBinding.getReturnType();
        }
        return null;
    }

    /*
     * Evaluates possible return expressions. The favourite expression is returned.
     */
    private Expression evaluateReturnExpressions(AST ast, ITypeBinding returnBinding, int returnOffset) {
        CompilationUnit root = (CompilationUnit)fMethodDecl.getRoot();

        Expression result = null;
        if (returnBinding != null) {
            ScopeAnalyzer analyzer = new ScopeAnalyzer(root);
            IBinding[] bindings =
                    analyzer.getDeclarationsInScope(returnOffset, ScopeAnalyzer.VARIABLES | ScopeAnalyzer.CHECK_VISIBILITY);
            for (int i = 0; i < bindings.length; i++) {
                IVariableBinding curr = (IVariableBinding)bindings[i];
                ITypeBinding type = curr.getType();
                if (type != null && type.isAssignmentCompatible(returnBinding) && testModifier(curr)) {
                    if (result == null) {
                        result = ast.newSimpleName(curr.getName());
                    }
                    //               addLinkedPositionProposal(RETURN_EXPRESSION_KEY, curr.getName(), null);
                }
            }
        }
        Expression defaultExpression =
                ASTNodeFactory.newDefaultExpression(ast, fMethodDecl.getReturnType2(), fMethodDecl.getExtraDimensions());
        //      addLinkedPositionProposal(RETURN_EXPRESSION_KEY, ASTNodes.asString(defaultExpression), null);
        if (result == null) {
            return defaultExpression;
        }
        return result;
    }

    private boolean testModifier(IVariableBinding curr) {
        int modifiers = curr.getModifiers();
        int staticFinal = Modifier.STATIC | Modifier.FINAL;
        if ((modifiers & staticFinal) == staticFinal) {
            return false;
        }
        if (Modifier.isStatic(modifiers) && !Modifier.isStatic(fMethodDecl.getModifiers())) {
            return false;
        }
        return true;
    }
}
