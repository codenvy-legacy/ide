/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Renaud Waldura &lt;renaud+eclipse@waldura.com&gt; - Access to static proposal
 *     Benjamin Muskalla <bmuskalla@innoopract.com> - [quick fix] Shouldn't offer "Add throws declaration" quickfix for overriding
 *     signature if result would conflict with overridden signature
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.text.correction;

import com.codenvy.ide.ext.java.jdt.Images;
import com.codenvy.ide.ext.java.jdt.JavaPreferencesSettings;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.IProblemLocation;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.core.dom.*;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ListRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ASTResolving;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.StubUtility;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.StubUtility2;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodeFactory;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodes;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.Bindings;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.BodyDeclarationRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.NecessaryParenthesesChecker;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.Selection;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.TypeRules;
import com.codenvy.ide.ext.java.jdt.internal.corext.fix.CleanUpConstants;
import com.codenvy.ide.ext.java.jdt.internal.corext.fix.CleanUpOptions;
import com.codenvy.ide.ext.java.jdt.internal.corext.fix.CodeStyleCleanUp;
import com.codenvy.ide.ext.java.jdt.internal.corext.fix.CodeStyleFix;
import com.codenvy.ide.ext.java.jdt.internal.corext.fix.ICleanUp;
import com.codenvy.ide.ext.java.jdt.internal.corext.fix.IProposableFix;
import com.codenvy.ide.ext.java.jdt.internal.corext.fix.UnimplementedCodeCleanUp;
import com.codenvy.ide.ext.java.jdt.internal.corext.fix.UnimplementedCodeFix;
import com.codenvy.ide.ext.java.jdt.internal.corext.fix.UnnecessaryCodeCleanUp;
import com.codenvy.ide.ext.java.jdt.internal.corext.fix.UnusedCodeFix;
import com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.code.CompilationUnitChange;
import com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.code.Invocations;
import com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.sorround.ExceptionAnalyzer;
import com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.sorround.SurroundWithTryCatchRefactoring;
import com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.util.NoCommentSourceRangeComputer;
import com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.util.TightSourceRangeComputer;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ASTRewriteCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.CUCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ChangeCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ChangeMethodSignatureProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ChangeMethodSignatureProposal.ChangeDescription;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ChangeMethodSignatureProposal.InsertDescription;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ChangeMethodSignatureProposal.RemoveDescription;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ConstructorFromSuperclassProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.FixCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.LinkedCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.MissingAnnotationAttributesProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.NewVariableCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ReplaceCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.quickassist.api.InvocationContext;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.edits.InsertEdit;
import com.codenvy.ide.text.edits.TextEdit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 */
public class LocalCorrectionsSubProcessor {

    private static final String RAW_TYPE_REFERENCE_ID = "org.eclipse.jdt.ui.correction.rawTypeReference"; //$NON-NLS-1$

    private static final String ADD_EXCEPTION_TO_THROWS_ID = "org.eclipse.jdt.ui.correction.addThrowsDecl"; //$NON-NLS-1$

    private static final String ADD_NON_NLS_ID = "org.eclipse.jdt.ui.correction.addNonNLS"; //$NON-NLS-1$

    private static final String ADD_FIELD_QUALIFICATION_ID = "org.eclipse.jdt.ui.correction.qualifyField"; //$NON-NLS-1$

    private static final String ADD_STATIC_ACCESS_ID = "org.eclipse.jdt.ui.correction.changeToStatic"; //$NON-NLS-1$

    private static final String REMOVE_UNNECESSARY_NLS_TAG_ID = "org.eclipse.jdt.ui.correction.removeNlsTag"; //$NON-NLS-1$

    public static void addUncaughtExceptionProposals(InvocationContext context, IProblemLocation problem,
                                                     Collection<ICommandAccess> proposals) throws CoreException {

        CompilationUnit astRoot = context.getASTRoot();
        ASTNode selectedNode = problem.getCoveringNode(astRoot);
        if (selectedNode == null) {
            return;
        }
        while (selectedNode != null && !(selectedNode instanceof Statement)
               && !(selectedNode instanceof VariableDeclarationExpression)) {
            selectedNode = selectedNode.getParent();
        }
        if (selectedNode == null) {
            return;
        }

        int offset = selectedNode.getStartPosition();
        int length = selectedNode.getLength();
        int selectionEnd = context.getSelectionOffset() + context.getSelectionLength();
        if (selectionEnd > offset + length) {
            // extend the selection if more than one statement is selected (bug 72149)
            length = selectionEnd - offset;
        }

        //Surround with proposals
        SurroundWithTryCatchRefactoring refactoring =
                SurroundWithTryCatchRefactoring.create(context.getDocument(), offset, length);
        if (refactoring == null)
            return;

        refactoring.setLeaveDirty(true);
        if (refactoring.checkActivationBasics(context.getDocument(), astRoot).isOK()) {
            String label =
                    CorrectionMessages.INSTANCE.INSTANCE.LocalCorrectionsSubProcessor_surroundwith_trycatch_description();
            Images image = Images.exceptionProp;
            CUCorrectionProposal proposal =
                    new CUCorrectionProposal(label, (CompilationUnitChange)refactoring.createChange(), 6,
                                             context.getDocument(), image);
            //         proposal.setLinkedProposalModel(refactoring.getLinkedProposalModel());
            proposals.add(proposal);
        }
        //TODO Java7
        //      if (JavaModelUtil.is17OrHigher(cu.getJavaProject()))
        //      {
        //         refactoring = SurroundWithTryCatchRefactoring.create(cu, offset, length, true);
        //         if (refactoring == null)
        //            return;
        //
        //         refactoring.setLeaveDirty(true);
        //         if (refactoring.checkActivationBasics(astRoot).isOK())
        //         {
        //            String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_surroundwith_trymulticatch_description;
        //            Image image = JavaPluginImages.get(JavaPluginImages.IMG_OBJS_EXCEPTION);
        //            CUCorrectionProposal proposal =
        //               new CUCorrectionProposal(label, cu, (CompilationUnitChange)refactoring.createChange(null), 7, image);
        //            proposal.setLinkedProposalModel(refactoring.getLinkedProposalModel());
        //            proposals.add(proposal);
        //         }
        //      }

        //Catch exception
        BodyDeclaration decl = ASTResolving.findParentBodyDeclaration(selectedNode);
        if (decl == null) {
            return;
        }

        ITypeBinding[] uncaughtExceptions =
                ExceptionAnalyzer.perform(decl, Selection.createFromStartLength(offset, length));
        if (uncaughtExceptions.length == 0) {
            return;
        }

        TryStatement surroundingTry = ASTResolving.findParentTryStatement(selectedNode);
        AST ast = astRoot.getAST();
        if (surroundingTry != null
            && (ASTNodes.isParent(selectedNode, surroundingTry.getBody()) ||
                selectedNode.getLocationInParent() == TryStatement.RESOURCES_PROPERTY)) {
            {
                ASTRewrite rewrite = ASTRewrite.create(surroundingTry.getAST());

                String label =
                        CorrectionMessages.INSTANCE.INSTANCE.LocalCorrectionsSubProcessor_addadditionalcatch_description();
                Images image = Images.exceptionProp;
                LinkedCorrectionProposal proposal =
                        new LinkedCorrectionProposal(label, rewrite, 7, context.getDocument(), image);

                ImportRewrite imports = proposal.createImportRewrite(context.getASTRoot());
                ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(decl, imports);

                ListRewrite clausesRewrite = rewrite.getListRewrite(surroundingTry, TryStatement.CATCH_CLAUSES_PROPERTY);
                for (int i = 0; i < uncaughtExceptions.length; i++) {
                    ITypeBinding excBinding = uncaughtExceptions[i];
                    String varName = StubUtility.getExceptionVariableName();
                    SingleVariableDeclaration var = ast.newSingleVariableDeclaration();
                    var.setName(ast.newSimpleName(varName));
                    var.setType(imports.addImport(excBinding, ast, importRewriteContext));
                    CatchClause newClause = ast.newCatchClause();
                    newClause.setException(var);
                    String catchBody =
                            StubUtility.getCatchBodyContent(excBinding.getName(), varName, selectedNode, String.valueOf('\n'));
                    if (catchBody != null) {
                        ASTNode node = rewrite.createStringPlaceholder(catchBody, ASTNode.RETURN_STATEMENT);
                        newClause.getBody().statements().add(node);
                    }
                    clausesRewrite.insertLast(newClause, null);

                    String typeKey = "type" + i; //$NON-NLS-1$
                    String nameKey = "name" + i; //$NON-NLS-1$
                    //               proposal.addLinkedPosition(rewrite.track(var.getType()), false, typeKey);
                    //               proposal.addLinkedPosition(rewrite.track(var.getName()), false, nameKey);
                    addExceptionTypeLinkProposals(proposal, excBinding, typeKey);
                }
                proposals.add(proposal);
            }
            //TODO Java7
            //         if (JavaModelUtil.is17OrHigher(cu.getJavaProject()))
            //         {
            //            List<CatchClause> catchClauses = surroundingTry.catchClauses();
            //
            //            if (catchClauses != null && catchClauses.size() == 1)
            //            {
            //               String label =
            //                  uncaughtExceptions.length > 1
            //                     ? CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_addexceptionstoexistingcatch_description
            //                     : CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_addexceptiontoexistingcatch_description;
            //               Image image = JavaPluginImages.get(JavaPluginImages.IMG_OBJS_EXCEPTION);
            //               ASTRewrite rewrite = ASTRewrite.create(ast);
            //               LinkedCorrectionProposal proposal = new LinkedCorrectionProposal(label, cu, rewrite, 7, image);
            //               ImportRewrite imports = proposal.createImportRewrite(context.getASTRoot());
            //               ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(decl, imports);
            //
            //               CatchClause catchClause = catchClauses.get(0);
            //               Type type = catchClause.getException().getType();
            //               if (type instanceof UnionType)
            //               {
            //                  UnionType unionType = (UnionType)type;
            //                  ListRewrite listRewrite = rewrite.getListRewrite(unionType, UnionType.TYPES_PROPERTY);
            //                  for (int i = 0; i < uncaughtExceptions.length; i++)
            //                  {
            //                     ITypeBinding excBinding = uncaughtExceptions[i];
            //                     Type type2 = imports.addImport(excBinding, ast, importRewriteContext);
            //                     listRewrite.insertLast(type2, null);
            //
            //                     String typeKey = "type" + i; //$NON-NLS-1$
            //                     proposal.addLinkedPosition(rewrite.track(type2), false, typeKey);
            //                     addExceptionTypeLinkProposals(proposal, excBinding, typeKey);
            //                  }
            //               }
            //               else
            //               {
            //                  UnionType newUnionType = ast.newUnionType();
            //                  List<Type> types = newUnionType.types();
            //
            //                  types.add((Type)rewrite.createCopyTarget(type));
            //                  for (int i = 0; i < uncaughtExceptions.length; i++)
            //                  {
            //                     ITypeBinding excBinding = uncaughtExceptions[i];
            //                     Type type2 = imports.addImport(excBinding, ast, importRewriteContext);
            //                     types.add(type2);
            //
            //                     String typeKey = "type" + i; //$NON-NLS-1$
            //                     proposal.addLinkedPosition(rewrite.track(type2), false, typeKey);
            //                     addExceptionTypeLinkProposals(proposal, excBinding, typeKey);
            //                  }
            //                  rewrite.replace(type, newUnionType, null);
            //               }
            //               proposals.add(proposal);
            //            }
            //            else if (catchClauses != null && catchClauses.size() == 0 && uncaughtExceptions.length > 1)
            //            {
            //               String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_addadditionalmulticatch_description;
            //               Image image = JavaPluginImages.get(JavaPluginImages.IMG_OBJS_EXCEPTION);
            //               ASTRewrite rewrite = ASTRewrite.create(ast);
            //               LinkedCorrectionProposal proposal = new LinkedCorrectionProposal(label, cu, rewrite, 7, image);
            //               ImportRewrite imports = proposal.createImportRewrite(context.getASTRoot());
            //               ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(decl, imports);
            //
            //               CatchClause newCatchClause = ast.newCatchClause();
            //               String varName = StubUtility.getExceptionVariableName(cu.getJavaProject());
            //               SingleVariableDeclaration var = ast.newSingleVariableDeclaration();
            //               var.setName(ast.newSimpleName(varName));
            //
            //               UnionType newUnionType = ast.newUnionType();
            //               List<Type> types = newUnionType.types();
            //
            //               for (int i = 0; i < uncaughtExceptions.length; i++)
            //               {
            //                  ITypeBinding excBinding = uncaughtExceptions[i];
            //                  Type type2 = imports.addImport(excBinding, ast, importRewriteContext);
            //                  types.add(type2);
            //
            //                  String typeKey = "type" + i; //$NON-NLS-1$
            //                  proposal.addLinkedPosition(rewrite.track(type2), false, typeKey);
            //                  addExceptionTypeLinkProposals(proposal, excBinding, typeKey);
            //               }
            //               String nameKey = "name"; //$NON-NLS-1$
            //               proposal.addLinkedPosition(rewrite.track(var.getName()), false, nameKey);
            //               var.setType(newUnionType);
            //               newCatchClause.setException(var);
            //               String catchBody =
            //                  StubUtility.getCatchBodyContent(cu, "Exception", varName, selectedNode, String.valueOf('\n')); //$NON-NLS-1$
            //               if (catchBody != null)
            //               {
            //                  ASTNode node = rewrite.createStringPlaceholder(catchBody, ASTNode.RETURN_STATEMENT);
            //                  newCatchClause.getBody().statements().add(node);
            //               }
            //               ListRewrite listRewrite = rewrite.getListRewrite(surroundingTry, TryStatement.CATCH_CLAUSES_PROPERTY);
            //               listRewrite.insertFirst(newCatchClause, null);
            //               proposals.add(proposal);
            //            }
            //         }
        }

        //Add throws declaration
        if (decl instanceof MethodDeclaration) {
            MethodDeclaration methodDecl = (MethodDeclaration)decl;
            IMethodBinding binding = methodDecl.resolveBinding();
            boolean isApplicable = (binding != null);
            if (isApplicable) {
                IMethodBinding overriddenMethod = Bindings.findOverriddenMethod(binding, true);
                if (overriddenMethod != null) {
                    isApplicable = overriddenMethod.getDeclaringClass().isFromSource();
                }
            }
            if (isApplicable) {
                ITypeBinding[] methodExceptions = binding.getExceptionTypes();
                ArrayList<ITypeBinding> unhandledExceptions = new ArrayList<ITypeBinding>(uncaughtExceptions.length);
                for (int i = 0; i < uncaughtExceptions.length; i++) {
                    ITypeBinding curr = uncaughtExceptions[i];
                    if (!canRemoveException(curr, methodExceptions)) {
                        unhandledExceptions.add(curr);
                    }
                }
                uncaughtExceptions = unhandledExceptions.toArray(new ITypeBinding[unhandledExceptions.size()]);

                List<Name> exceptions = methodDecl.thrownExceptions();
                int nExistingExceptions = exceptions.size();
                ChangeDescription[] desc = new ChangeDescription[nExistingExceptions + uncaughtExceptions.length];
                for (int i = 0; i < exceptions.size(); i++) {
                    Name elem = exceptions.get(i);
                    if (canRemoveException(elem.resolveTypeBinding(), uncaughtExceptions)) {
                        desc[i] = new RemoveDescription();
                    }
                }
                for (int i = 0; i < uncaughtExceptions.length; i++) {
                    desc[i + nExistingExceptions] = new InsertDescription(uncaughtExceptions[i], ""); //$NON-NLS-1$
                }

                String label = CorrectionMessages.INSTANCE.INSTANCE.LocalCorrectionsSubProcessor_addthrows_description();
                Images image = Images.exceptionProp;

                ChangeMethodSignatureProposal proposal =
                        new ChangeMethodSignatureProposal(label, astRoot, binding, null, desc, 8, context.getDocument(), image);
                for (int i = 0; i < uncaughtExceptions.length; i++) {
                    addExceptionTypeLinkProposals(proposal, uncaughtExceptions[i],
                                                  proposal.getExceptionTypeGroupId(i + nExistingExceptions));
                }
                proposal.setCommandId(ADD_EXCEPTION_TO_THROWS_ID);
                proposals.add(proposal);
            }
        }
    }

    private static void addExceptionTypeLinkProposals(LinkedCorrectionProposal proposal, ITypeBinding exc, String key) {
        //      // all super classes except Object
        //      while (exc != null && !"java.lang.Object".equals(exc.getQualifiedName())) { //$NON-NLS-1$
        //         proposal.addLinkedPositionProposal(key, exc);
        //         exc = exc.getSuperclass();
        //      }
    }

    private static boolean canRemoveException(ITypeBinding curr, ITypeBinding[] addedExceptions) {
        while (curr != null) {
            for (int i = 0; i < addedExceptions.length; i++) {
                if (curr == addedExceptions[i]) {
                    return true;
                }
            }
            curr = curr.getSuperclass();
        }
        return false;
    }

    public static void addUnreachableCatchProposals(InvocationContext context, IProblemLocation problem,
                                                    Collection<ICommandAccess> proposals) {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (selectedNode == null) {
            return;
        }

        QuickAssistProcessorImpl.getCatchClauseToThrowsProposals(context, selectedNode, proposals);
    }

    //   public static void addNLSProposals(IInvocationContext context, IProblemLocation problem,
    //      Collection<ICommandAccess> proposals) throws CoreException
    //   {
    //      String name = CorrectionMessages.INSTANCE.INSTANCE.LocalCorrectionsSubProcessor_externalizestrings_description();
    //
    //      ChangeCorrectionProposal proposal =
    //         new ChangeCorrectionProposal(name, null, 4, JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE))
    //         {
    //            @Override
    //            public void apply(IDocument document)
    //            {
    //               ExternalizeWizard.open(cu, JavaPlugin.getActiveWorkbenchShell());
    //            }
    //
    //            @Override
    //            public Object getAdditionalProposalInfo(IProgressMonitor monitor)
    //            {
    //               return CorrectionMessages.INSTANCE.INSTANCE.LocalCorrectionsSubProcessor_externalizestrings_additional_info();
    //            }
    //
    //         };
    //      proposals.add(proposal);
    //
    //      IProposableFix fix = StringFix.createFix(context.getASTRoot(), problem, false, true);
    //      if (fix != null)
    //      {
    //         Image image = JavaPluginImages.get(JavaPluginImages.IMG_OBJS_NLS_NEVER_TRANSLATE);
    //         Map<String, String> options = new HashMap<String, String>();
    //         options.put(CleanUpConstants.ADD_MISSING_NLS_TAGS, CleanUpOptions.TRUE);
    //         FixCorrectionProposal addNLS = new FixCorrectionProposal(fix, new StringCleanUp(options), 5, image, context);
    //         addNLS.setCommandId(ADD_NON_NLS_ID);
    //         proposals.add(addNLS);
    //      }
    //   }
    //
    //   public static void getUnnecessaryNLSTagProposals(IInvocationContext context, IProblemLocation problem,
    //      Collection<ICommandAccess> proposals) throws CoreException
    //   {
    //      IProposableFix fix = StringFix.createFix(context.getASTRoot(), problem, true, false);
    //      if (fix != null)
    //      {
    //         Image image = new Image(JavaClientBundle.INSTANCE.delete_obj());
    //         Map<String, String> options = new HashMap<String, String>();
    //         options.put(CleanUpConstants.REMOVE_UNNECESSARY_NLS_TAGS, CleanUpOptions.TRUE);
    //         FixCorrectionProposal proposal = new FixCorrectionProposal(fix, new StringCleanUp(options), 6, image, context);
    //         proposal.setCommandId(REMOVE_UNNECESSARY_NLS_TAG_ID);
    //         proposals.add(proposal);
    //      }
    //   }

    /*
     * Fix instance accesses and indirect (static) accesses to static fields/methods
     */
    public static void addCorrectAccessToStaticProposals(InvocationContext context, IProblemLocation problem,
                                                         Collection<ICommandAccess> proposals) throws CoreException {
        IProposableFix fix =
                CodeStyleFix.createIndirectAccessToStaticFix(context.getASTRoot(), problem, context.getDocument());
        if (fix != null) {
            Images image = Images.correction_change;
            Map<String, String> options = new HashMap<String, String>();
            options.put(CleanUpConstants.MEMBER_ACCESSES_STATIC_QUALIFY_WITH_DECLARING_CLASS, CleanUpOptions.TRUE);
            options.put(CleanUpConstants.MEMBER_ACCESSES_STATIC_QUALIFY_WITH_DECLARING_CLASS_SUBTYPE_ACCESS,
                        CleanUpOptions.TRUE);
            FixCorrectionProposal proposal =
                    new FixCorrectionProposal(fix, new CodeStyleCleanUp(context.getDocument(), options), 6, image, context);
            proposal.setCommandId(ADD_STATIC_ACCESS_ID);
            proposals.add(proposal);
            return;
        }

        IProposableFix[] fixes =
                CodeStyleFix.createNonStaticAccessFixes(context.getASTRoot(), problem, context.getDocument());
        if (fixes != null) {
            IProposableFix fix1 = fixes[0];
            Images image = Images.correction_change;
            Map<String, String> options = new HashMap<String, String>();
            options.put(CleanUpConstants.MEMBER_ACCESSES_STATIC_QUALIFY_WITH_DECLARING_CLASS, CleanUpOptions.TRUE);
            options.put(CleanUpConstants.MEMBER_ACCESSES_STATIC_QUALIFY_WITH_DECLARING_CLASS_INSTANCE_ACCESS,
                        CleanUpOptions.TRUE);
            FixCorrectionProposal proposal =
                    new FixCorrectionProposal(fix1, new CodeStyleCleanUp(context.getDocument(), options), 6, image, context);
            proposal.setCommandId(ADD_STATIC_ACCESS_ID);
            proposals.add(proposal);

            if (fixes.length > 1) {
                Map<String, String> options1 = new HashMap<String, String>();
                options1.put(CleanUpConstants.MEMBER_ACCESSES_STATIC_QUALIFY_WITH_DECLARING_CLASS, CleanUpOptions.TRUE);
                options1.put(CleanUpConstants.MEMBER_ACCESSES_STATIC_QUALIFY_WITH_DECLARING_CLASS_SUBTYPE_ACCESS,
                             CleanUpOptions.TRUE);
                options1.put(CleanUpConstants.MEMBER_ACCESSES_STATIC_QUALIFY_WITH_DECLARING_CLASS_INSTANCE_ACCESS,
                             CleanUpOptions.TRUE);
                IProposableFix fix2 = fixes[1];
                proposal =
                        new FixCorrectionProposal(fix2, new CodeStyleCleanUp(context.getDocument(), options), 5, image, context);
                proposals.add(proposal);
            }
        }
        ModifierCorrectionSubProcessor.addNonAccessibleReferenceProposal(context, problem, proposals,
                                                                         ModifierCorrectionSubProcessor.TO_NON_STATIC, 4);
    }

    public static void addUnimplementedMethodsProposals(InvocationContext context, IProblemLocation problem,
                                                        Collection<ICommandAccess> proposals) {
        IProposableFix addMethodFix =
                UnimplementedCodeFix.createAddUnimplementedMethodsFix(context.getASTRoot(), problem, context.getDocument());
        if (addMethodFix != null) {
            Images image = Images.correction_change;

            Map<String, String> settings = new HashMap<String, String>();
            settings.put(CleanUpConstants.ADD_MISSING_METHODES, CleanUpOptions.TRUE);
            ICleanUp cleanUp = new UnimplementedCodeCleanUp(settings);

            proposals.add(new FixCorrectionProposal(addMethodFix, cleanUp, 10, image, context));
        }

        IProposableFix makeAbstractFix =
                UnimplementedCodeFix.createMakeTypeAbstractFix(context.getASTRoot(), problem, context.getDocument());
        if (makeAbstractFix != null) {
            Images image = Images.correction_change;

            Map<String, String> settings = new HashMap<String, String>();
            settings.put(UnimplementedCodeCleanUp.MAKE_TYPE_ABSTRACT, CleanUpOptions.TRUE);
            ICleanUp cleanUp = new UnimplementedCodeCleanUp(settings);

            proposals.add(new FixCorrectionProposal(makeAbstractFix, cleanUp, 5, image, context));
        }
    }

    public static void addUninitializedLocalVariableProposal(InvocationContext context, IProblemLocation problem,
                                                             Collection<ICommandAccess> proposals) {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (!(selectedNode instanceof Name)) {
            return;
        }
        Name name = (Name)selectedNode;
        IBinding binding = name.resolveBinding();
        if (!(binding instanceof IVariableBinding)) {
            return;
        }
        IVariableBinding varBinding = (IVariableBinding)binding;

        CompilationUnit astRoot = context.getASTRoot();
        ASTNode node = astRoot.findDeclaringNode(binding);
        if (node instanceof VariableDeclarationFragment) {
            ASTRewrite rewrite = ASTRewrite.create(node.getAST());

            VariableDeclarationFragment fragment = (VariableDeclarationFragment)node;
            if (fragment.getInitializer() != null) {
                return;
            }
            Expression expression = ASTNodeFactory.newDefaultExpression(astRoot.getAST(), varBinding.getType());
            if (expression == null) {
                return;
            }
            rewrite.set(fragment, VariableDeclarationFragment.INITIALIZER_PROPERTY, expression, null);

            String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_uninitializedvariable_description();
            Images image = Images.correction_change;

            LinkedCorrectionProposal proposal =
                    new LinkedCorrectionProposal(label, rewrite, 6, context.getDocument(), image);
            //         proposal.addLinkedPosition(rewrite.track(expression), false, "initializer"); //$NON-NLS-1$
            proposals.add(proposal);
        }
    }

    public static void addConstructorFromSuperclassProposal(InvocationContext context, IProblemLocation problem,
                                                            Collection<ICommandAccess> proposals) throws CoreException {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (selectedNode == null) {
            return;
        }

        TypeDeclaration typeDeclaration = null;
        if (selectedNode.getLocationInParent() == TypeDeclaration.NAME_PROPERTY) {
            typeDeclaration = (TypeDeclaration)selectedNode.getParent();
        } else {
            BodyDeclaration declaration = ASTResolving.findParentBodyDeclaration(selectedNode);
            if (declaration instanceof Initializer
                && problem.getProblemId() == IProblem.UnhandledExceptionInDefaultConstructor) {
                addUncaughtExceptionProposals(context, problem, proposals);
            }
            return;
        }

        ITypeBinding binding = typeDeclaration.resolveBinding();
        if (binding == null || binding.getSuperclass() == null) {
            return;
        }
        IMethodBinding[] methods = binding.getSuperclass().getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            IMethodBinding curr = methods[i];
            if (curr.isConstructor() && !Modifier.isPrivate(curr.getModifiers())) {
                proposals.add(new ConstructorFromSuperclassProposal(typeDeclaration, curr, 5, context.getDocument()));
            }
        }
    }

    public static void addUnusedMemberProposal(InvocationContext context, IProblemLocation problem,
                                               Collection<ICommandAccess> proposals) {
        int problemId = problem.getProblemId();
        UnusedCodeFix fix =
                UnusedCodeFix.createUnusedMemberFix(context.getASTRoot(), problem, false, context.getDocument());
        if (fix != null) {
            addProposal(context, proposals, fix);
        }

        if (problemId == IProblem.LocalVariableIsNeverUsed) {
            fix = UnusedCodeFix.createUnusedMemberFix(context.getASTRoot(), problem, true, context.getDocument());
            addProposal(context, proposals, fix);
        }

        if (problemId == IProblem.ArgumentIsNeverUsed) {
            JavadocTagsSubProcessor.getUnusedAndUndocumentedParameterOrExceptionProposals(context, problem, proposals);
        }

        if (problemId == IProblem.UnusedPrivateField) {
            GetterSetterCorrectionSubProcessor.addGetterSetterProposal(context, problem, proposals, 8);
        }

    }

    public static void addRedundantSuperInterfaceProposal(InvocationContext context, IProblemLocation problem,
                                                          Collection<ICommandAccess> proposals) {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (!(selectedNode instanceof Name)) {
            return;
        }
        ASTNode node = ASTNodes.getNormalizedNode(selectedNode);

        ASTRewrite rewrite = ASTRewrite.create(node.getAST());
        rewrite.remove(node, null);

        String label =
                CorrectionMessages.INSTANCE.INSTANCE.LocalCorrectionsSubProcessor_remove_redundant_superinterface();
        Images image = Images.delete_obj;

        ASTRewriteCorrectionProposal proposal =
                new ASTRewriteCorrectionProposal(label, rewrite, 6, context.getDocument(), image);
        proposals.add(proposal);

    }

    private static void addProposal(InvocationContext context, Collection<ICommandAccess> proposals,
                                    final UnusedCodeFix fix) {
        if (fix != null) {
            Images image = Images.delete_obj;
            FixCorrectionProposal proposal = new FixCorrectionProposal(fix, fix.getCleanUp(), 10, image, context);
            proposals.add(proposal);
        }
    }

    public static void addSuperfluousSemicolonProposal(InvocationContext context, IProblemLocation problem,
                                                       Collection<ICommandAccess> proposals) {
        String label = CorrectionMessages.INSTANCE.INSTANCE.LocalCorrectionsSubProcessor_removesemicolon_description();
        ReplaceCorrectionProposal proposal =
                new ReplaceCorrectionProposal(label, problem.getOffset(), problem.getLength(), "", 6, context.getDocument()); //$NON-NLS-1$
        proposals.add(proposal);
    }

    public static void addUnnecessaryCastProposal(InvocationContext context, IProblemLocation problem,
                                                  Collection<ICommandAccess> proposals) {
        IProposableFix fix =
                UnusedCodeFix.createRemoveUnusedCastFix(context.getASTRoot(), problem, context.getDocument());
        if (fix != null) {
            Images image = Images.correction_change;
            Map<String, String> options = new HashMap<String, String>();
            options.put(CleanUpConstants.REMOVE_UNNECESSARY_CASTS, CleanUpOptions.TRUE);
            FixCorrectionProposal proposal =
                    new FixCorrectionProposal(fix, new UnnecessaryCodeCleanUp(options), 10, image, context);
            proposals.add(proposal);
        }
    }

    public static void addUnnecessaryInstanceofProposal(InvocationContext context, IProblemLocation problem,
                                                        Collection<ICommandAccess> proposals) {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());

        ASTNode curr = selectedNode;
        while (curr instanceof ParenthesizedExpression) {
            curr = ((ParenthesizedExpression)curr).getExpression();
        }

        if (curr instanceof InstanceofExpression) {
            AST ast = curr.getAST();

            ASTRewrite rewrite = ASTRewrite.create(ast);

            InstanceofExpression inst = (InstanceofExpression)curr;

            InfixExpression expression = ast.newInfixExpression();
            expression.setLeftOperand((Expression)rewrite.createCopyTarget(inst.getLeftOperand()));
            expression.setOperator(InfixExpression.Operator.NOT_EQUALS);
            expression.setRightOperand(ast.newNullLiteral());

            rewrite.replace(inst, expression, null);

            String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_unnecessaryinstanceof_description();
            Images image = Images.correction_change;
            ASTRewriteCorrectionProposal proposal =
                    new ASTRewriteCorrectionProposal(label, rewrite, 10, context.getDocument(), image);
            proposals.add(proposal);
        }

    }

    public static void addUnnecessaryThrownExceptionProposal(InvocationContext context, IProblemLocation problem,
                                                             Collection<ICommandAccess> proposals) {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (selectedNode == null || !(selectedNode.getParent() instanceof MethodDeclaration)) {
            return;
        }
        MethodDeclaration decl = (MethodDeclaration)selectedNode.getParent();
        IMethodBinding binding = decl.resolveBinding();
        if (binding != null) {
            List<Name> thrownExceptions = decl.thrownExceptions();
            int index = thrownExceptions.indexOf(selectedNode);
            if (index == -1) {
                return;
            }
            ChangeDescription[] desc = new ChangeDescription[thrownExceptions.size()];
            desc[index] = new RemoveDescription();

            String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_unnecessarythrow_description();
            Images image = Images.exceptionProp;

            proposals.add(new ChangeMethodSignatureProposal(label, selectedNode, binding, null, desc, 5, context
                    .getDocument(), image));
        }

        JavadocTagsSubProcessor.getUnusedAndUndocumentedParameterOrExceptionProposals(context, problem, proposals);
    }

    public static void addUnqualifiedFieldAccessProposal(InvocationContext context, IProblemLocation problem,
                                                         Collection<ICommandAccess> proposals) {
        IProposableFix fix =
                CodeStyleFix.createAddFieldQualifierFix(context.getASTRoot(), context.getDocument(), problem);
        if (fix != null) {
            Images image = Images.correction_change;
            Map<String, String> options = new HashMap<String, String>();
            options.put(CleanUpConstants.MEMBER_ACCESSES_NON_STATIC_FIELD_USE_THIS, CleanUpOptions.TRUE);
            options.put(CleanUpConstants.MEMBER_ACCESSES_NON_STATIC_FIELD_USE_THIS_ALWAYS, CleanUpOptions.TRUE);
            FixCorrectionProposal proposal =
                    new FixCorrectionProposal(fix, new CodeStyleCleanUp(context.getDocument(), options), 8, image, context);
            proposal.setCommandId(ADD_FIELD_QUALIFICATION_ID);
            proposals.add(proposal);
        }
    }

    public static void addInvalidVariableNameProposals(InvocationContext context, IProblemLocation problem,
                                                       Collection<ICommandAccess> proposals) {
        // hiding, redefined or future keyword

        CompilationUnit root = context.getASTRoot();
        ASTNode selectedNode = problem.getCoveringNode(root);
        if (selectedNode instanceof MethodDeclaration) {
            selectedNode = ((MethodDeclaration)selectedNode).getName();
        }
        if (!(selectedNode instanceof SimpleName)) {
            return;
        }
        SimpleName nameNode = (SimpleName)selectedNode;
        String valueSuggestion = null;

        String name;
        switch (problem.getProblemId()) {
            case IProblem.LocalVariableHidingLocalVariable:
            case IProblem.LocalVariableHidingField:
                name =
                        CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_hiding_local_label(nameNode.getIdentifier());
                break;
            case IProblem.FieldHidingLocalVariable:
            case IProblem.FieldHidingField:
            case IProblem.DuplicateField:
                name =
                        CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_hiding_field_label(nameNode.getIdentifier());
                break;
            case IProblem.ArgumentHidingLocalVariable:
            case IProblem.ArgumentHidingField:
                name =
                        CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_hiding_argument_label(nameNode.getIdentifier());
                break;
            case IProblem.DuplicateMethod:
                name =
                        CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_renaming_duplicate_method(nameNode
                                                                                                                   .getIdentifier());
                break;

            default:
                name = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_rename_var_label(nameNode.getIdentifier());
        }

        if (problem.getProblemId() == IProblem.UseEnumAsAnIdentifier) {
            valueSuggestion = "enumeration"; //$NON-NLS-1$
        } else {
            valueSuggestion = nameNode.getIdentifier() + '1';
        }

        //TODO
//            LinkedNamesAssistProposal proposal = new LinkedNamesAssistProposal(name, context, nameNode, valueSuggestion);
        //      proposals.add(proposal);
    }

    public static void getInvalidOperatorProposals(InvocationContext context, IProblemLocation problem,
                                                   Collection<ICommandAccess> proposals) {
        CompilationUnit root = context.getASTRoot();
        AST ast = root.getAST();

        ASTNode selectedNode = problem.getCoveringNode(root);

        while (selectedNode instanceof ParenthesizedExpression) {
            selectedNode = ((ParenthesizedExpression)selectedNode).getExpression();
        }

        if (selectedNode instanceof PrefixExpression) {
            // !x instanceof X -> !(x instanceof X)

            PrefixExpression expression = (PrefixExpression)selectedNode;
            if (expression.getOperator() == PrefixExpression.Operator.NOT) {
                ASTNode parent = expression.getParent();

                String label = null;
                switch (parent.getNodeType()) {
                    case ASTNode.INSTANCEOF_EXPRESSION:
                        label =
                                CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_setparenteses_instanceof_description();
                        break;
                    case ASTNode.INFIX_EXPRESSION:
                        InfixExpression infixExpression = (InfixExpression)parent;
                        label =

                                CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_setparenteses_description(infixExpression
                                                                                                                           .getOperator()
                                                                                                                           .toString());
                        break;
                }

                if (label != null) {
                    ASTRewrite rewrite = ASTRewrite.create(ast);
                    rewrite.replace(selectedNode, rewrite.createMoveTarget(expression.getOperand()), null);

                    ParenthesizedExpression newParentExpr = ast.newParenthesizedExpression();
                    newParentExpr.setExpression((Expression)rewrite.createMoveTarget(parent));
                    PrefixExpression newPrefixExpr = ast.newPrefixExpression();
                    newPrefixExpr.setOperand(newParentExpr);
                    newPrefixExpr.setOperator(PrefixExpression.Operator.NOT);

                    rewrite.replace(parent, newPrefixExpr, null);

                    Images image = Images.correction_cast;
                    ASTRewriteCorrectionProposal proposal =
                            new ASTRewriteCorrectionProposal(label, rewrite, 5, context.getDocument(), image);
                    proposals.add(proposal);
                }
            }
        } else if (selectedNode instanceof InfixExpression
                   && isBitOperation((((InfixExpression)selectedNode).getOperator()))) {
            // a & b == c -> (a & b) == c
            final CompareInBitWiseOpFinder opFinder = new CompareInBitWiseOpFinder(selectedNode);
            if (opFinder.getCompareExpression() != null) { // compare operation inside bit operations: set parents
                String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_setparenteses_bitop_description();
                Images image = Images.correction_cast;
                CUCorrectionProposal proposal = new CUCorrectionProposal(label, 5, context.getDocument(), image) {
                    @Override
                    protected void addEdits(Document document, TextEdit edit) throws CoreException {
                        InfixExpression compareExpression = opFinder.getCompareExpression();
                        InfixExpression expression = opFinder.getParentInfixExpression();
                        ASTNode left = compareExpression.getLeftOperand();
                        if (expression.getStartPosition() < left.getStartPosition()) {
                            edit.addChild(new InsertEdit(expression.getStartPosition(), String.valueOf('(')));
                            edit.addChild(new InsertEdit(ASTNodes.getExclusiveEnd(left), String.valueOf(')')));
                        }
                        ASTNode rigth = compareExpression.getRightOperand();
                        int selEnd = ASTNodes.getExclusiveEnd(expression);
                        if (selEnd > ASTNodes.getExclusiveEnd(rigth)) {
                            edit.addChild(new InsertEdit(rigth.getStartPosition(), String.valueOf('(')));
                            edit.addChild(new InsertEdit(selEnd, String.valueOf(')')));
                        }
                    }
                };
                proposals.add(proposal);
            }
        }
    }

    private static boolean isBitOperation(InfixExpression.Operator op) {
        return op == InfixExpression.Operator.AND || op == InfixExpression.Operator.OR
               || op == InfixExpression.Operator.XOR;
    }

    private static class CompareInBitWiseOpFinder extends ASTVisitor {

        private InfixExpression fCompareExpression = null;

        private final ASTNode fSelectedNode;

        public CompareInBitWiseOpFinder(ASTNode selectedNode) {
            fSelectedNode = selectedNode;
            selectedNode.accept(this);
        }

        @Override
        public boolean visit(InfixExpression e) {
            InfixExpression.Operator op = e.getOperator();
            if (isBitOperation(op)) {
                return true;
            } else if (op == InfixExpression.Operator.EQUALS || op == InfixExpression.Operator.NOT_EQUALS) {
                fCompareExpression = e;
                return false;
            }
            return false;
        }

        public InfixExpression getCompareExpression() {
            return fCompareExpression;
        }

        public InfixExpression getParentInfixExpression() {
            ASTNode expr = fSelectedNode;
            ASTNode parent = expr.getParent(); // include all parents
            while (parent instanceof InfixExpression && isBitOperation(((InfixExpression)parent).getOperator())) {
                expr = parent;
                parent = expr.getParent();
            }
            return (InfixExpression)expr;
        }
    }

    public static void getUnnecessaryElseProposals(InvocationContext context, IProblemLocation problem,
                                                   Collection<ICommandAccess> proposals) {
        CompilationUnit root = context.getASTRoot();
        ASTNode selectedNode = problem.getCoveringNode(root);
        if (selectedNode == null) {
            return;
        }
        ASTNode parent = selectedNode.getParent();
        if (parent instanceof ExpressionStatement) {
            parent = parent.getParent();
        }
        if (!(parent instanceof IfStatement)) {
            return;
        }
        IfStatement ifStatement = (IfStatement)parent;
        ASTNode ifParent = ifStatement.getParent();
        if (!(ifParent instanceof Block) && !(ifParent instanceof SwitchStatement)
            && !ASTNodes.isControlStatementBody(ifStatement.getLocationInParent())) {
            return;
        }

        ASTRewrite rewrite = ASTRewrite.create(root.getAST());
        ASTNode placeholder = QuickAssistProcessorImpl.getCopyOfInner(rewrite, ifStatement.getElseStatement(), false);
        if (placeholder == null) {
            return;
        }
        rewrite.remove(ifStatement.getElseStatement(), null);

        if (ifParent instanceof Block) {
            ListRewrite listRewrite = rewrite.getListRewrite(ifParent, Block.STATEMENTS_PROPERTY);
            listRewrite.insertAfter(placeholder, ifStatement, null);
        } else if (ifParent instanceof SwitchStatement) {
            ListRewrite listRewrite = rewrite.getListRewrite(ifParent, SwitchStatement.STATEMENTS_PROPERTY);
            listRewrite.insertAfter(placeholder, ifStatement, null);
        } else {
            Block block = root.getAST().newBlock();
            rewrite.replace(ifStatement, block, null);
            block.statements().add(rewrite.createCopyTarget(ifStatement));
            block.statements().add(placeholder);
        }

        String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_removeelse_description();
        Images image = Images.correction_change;
        ASTRewriteCorrectionProposal proposal =
                new ASTRewriteCorrectionProposal(label, rewrite, 10, context.getDocument(), image);
        proposals.add(proposal);
    }

    public static void getInterfaceExtendsClassProposals(InvocationContext context, IProblemLocation problem,
                                                         Collection<ICommandAccess> proposals) {
        CompilationUnit root = context.getASTRoot();
        ASTNode selectedNode = problem.getCoveringNode(root);
        if (selectedNode == null) {
            return;
        }
        while (selectedNode.getParent() instanceof Type) {
            selectedNode = selectedNode.getParent();
        }

        StructuralPropertyDescriptor locationInParent = selectedNode.getLocationInParent();
        if (locationInParent != TypeDeclaration.SUPERCLASS_TYPE_PROPERTY) {
            return;
        }

        TypeDeclaration typeDecl = (TypeDeclaration)selectedNode.getParent();
        {
            ASTRewrite rewrite = ASTRewrite.create(root.getAST());
            ASTNode placeHolder = rewrite.createMoveTarget(selectedNode);
            ListRewrite interfaces = rewrite.getListRewrite(typeDecl, TypeDeclaration.SUPER_INTERFACE_TYPES_PROPERTY);
            interfaces.insertFirst(placeHolder, null);

            String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_extendstoimplements_description();
            Images image = Images.correction_change;
            ASTRewriteCorrectionProposal proposal =
                    new ASTRewriteCorrectionProposal(label, rewrite, 6, context.getDocument(), image);
            proposals.add(proposal);
        }
        {
            ASTRewrite rewrite = ASTRewrite.create(root.getAST());

            rewrite.set(typeDecl, TypeDeclaration.INTERFACE_PROPERTY, Boolean.TRUE, null);

            String typeName = typeDecl.getName().getIdentifier();
            String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_classtointerface_description(typeName);
            Images image = Images.correction_change;
            ASTRewriteCorrectionProposal proposal =
                    new ASTRewriteCorrectionProposal(label, rewrite, 3, context.getDocument(), image);
            proposals.add(proposal);
        }
    }

    public static void getUnreachableCodeProposals(InvocationContext context, IProblemLocation problem,
                                                   Collection<ICommandAccess> proposals) {
        CompilationUnit root = context.getASTRoot();
        ASTNode selectedNode = problem.getCoveringNode(root);
        if (selectedNode == null) {
            return;
        }

        ASTNode parent = selectedNode.getParent();
        while (parent instanceof ExpressionStatement) {
            selectedNode = parent;
            parent = selectedNode.getParent();
        }

        if (parent instanceof WhileStatement) {
            addRemoveIncludingConditionProposal(context, parent, null, proposals);

        } else if (selectedNode.getLocationInParent() == IfStatement.THEN_STATEMENT_PROPERTY) {
            Statement elseStatement = ((IfStatement)parent).getElseStatement();
            addRemoveIncludingConditionProposal(context, parent, elseStatement, proposals);

        } else if (selectedNode.getLocationInParent() == IfStatement.ELSE_STATEMENT_PROPERTY) {
            Statement thenStatement = ((IfStatement)parent).getThenStatement();
            addRemoveIncludingConditionProposal(context, parent, thenStatement, proposals);

        } else if (selectedNode.getLocationInParent() == ForStatement.BODY_PROPERTY) {
            Statement body = ((ForStatement)parent).getBody();
            addRemoveIncludingConditionProposal(context, parent, body, proposals);

        } else if (selectedNode.getLocationInParent() == ConditionalExpression.THEN_EXPRESSION_PROPERTY) {
            Expression elseExpression = ((ConditionalExpression)parent).getElseExpression();
            addRemoveIncludingConditionProposal(context, parent, elseExpression, proposals);

        } else if (selectedNode.getLocationInParent() == ConditionalExpression.ELSE_EXPRESSION_PROPERTY) {
            Expression thenExpression = ((ConditionalExpression)parent).getThenExpression();
            addRemoveIncludingConditionProposal(context, parent, thenExpression, proposals);

        } else if (selectedNode.getLocationInParent() == InfixExpression.RIGHT_OPERAND_PROPERTY) {
            // also offer split && / || condition proposals:
            InfixExpression infixExpression = (InfixExpression)parent;
            Expression leftOperand = infixExpression.getLeftOperand();
            List<Expression> extendedOperands = infixExpression.extendedOperands();

            ASTRewrite rewrite = ASTRewrite.create(parent.getAST());
            if (extendedOperands.size() == 0) {
                rewrite.replace(infixExpression, rewrite.createMoveTarget(leftOperand), null);
            } else {
                ASTNode firstExtendedOp = rewrite.createMoveTarget(extendedOperands.get(0));
                rewrite.set(infixExpression, InfixExpression.RIGHT_OPERAND_PROPERTY, firstExtendedOp, null);
                rewrite.remove(leftOperand, null);
            }
            String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_removeunreachablecode_description();
            addRemoveProposal(context, rewrite, label, proposals);

            AssistContext assistContext =
                    new AssistContext(context.getDocument(), infixExpression.getRightOperand().getStartPosition() - 1, 0);
            assistContext.setASTRoot(root);
            AdvancedQuickAssistProcessor.getSplitAndConditionProposals(assistContext, infixExpression, proposals);
            AdvancedQuickAssistProcessor.getSplitOrConditionProposals(assistContext, infixExpression, proposals);

        } else if (selectedNode instanceof Statement && selectedNode.getLocationInParent().isChildListProperty()) {
            // remove all statements following the unreachable:
            List<Statement> statements =
                    (List<Statement>)selectedNode.getParent().getStructuralProperty(selectedNode.getLocationInParent());
            int idx = statements.indexOf(selectedNode);

            ASTRewrite rewrite = ASTRewrite.create(selectedNode.getAST());
            String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_removeunreachablecode_description();

            if (idx > 0) {
                Object prevStatement = statements.get(idx - 1);
                if (prevStatement instanceof IfStatement) {
                    IfStatement ifStatement = (IfStatement)prevStatement;
                    if (ifStatement.getElseStatement() == null) {
                        // remove if (true), see https://bugs.eclipse.org/bugs/show_bug.cgi?id=261519
                        rewrite.replace(ifStatement, rewrite.createMoveTarget(ifStatement.getThenStatement()), null);
                        label =
                                CorrectionMessages.INSTANCE
                                                  .LocalCorrectionsSubProcessor_removeunreachablecode_including_condition_description();
                    }
                }
            }

            for (int i = idx; i < statements.size(); i++) {
                ASTNode statement = statements.get(i);
                if (statement instanceof SwitchCase)
                    break; // stop at case *: and default:
                rewrite.remove(statement, null);
            }

            addRemoveProposal(context, rewrite, label, proposals);

        } else {
            // no special case, just remove the node:
            addRemoveProposal(context, selectedNode, proposals);
        }
    }

    private static void addRemoveProposal(InvocationContext context, ASTNode selectedNode,
                                          Collection<ICommandAccess> proposals) {
        ASTRewrite rewrite = ASTRewrite.create(selectedNode.getAST());
        rewrite.remove(selectedNode, null);

        String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_removeunreachablecode_description();
        addRemoveProposal(context, rewrite, label, proposals);
    }

    private static void addRemoveIncludingConditionProposal(InvocationContext context, ASTNode toRemove,
                                                            ASTNode replacement, Collection<ICommandAccess> proposals) {
        Images image = Images.delete_obj;
        String label =
                CorrectionMessages.INSTANCE
                                  .LocalCorrectionsSubProcessor_removeunreachablecode_including_condition_description();
        AST ast = toRemove.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);
        ASTRewriteCorrectionProposal proposal =
                new ASTRewriteCorrectionProposal(label, rewrite, 10, context.getDocument(), image);

        if (replacement == null || replacement instanceof EmptyStatement || replacement instanceof Block
                                                                            && ((Block)replacement).statements().size() == 0) {
            if (ASTNodes.isControlStatementBody(toRemove.getLocationInParent())) {
                rewrite.replace(toRemove, toRemove.getAST().newBlock(), null);
            } else {
                rewrite.remove(toRemove, null);
            }

        } else if (toRemove instanceof Expression && replacement instanceof Expression) {
            Expression moved = (Expression)rewrite.createMoveTarget(replacement);
            Expression toRemoveExpression = (Expression)toRemove;
            Expression replacementExpression = (Expression)replacement;
            ITypeBinding explicitCast = ASTNodes.getExplicitCast(replacementExpression, toRemoveExpression);
            if (explicitCast != null) {
                CastExpression cast = ast.newCastExpression();
                if (NecessaryParenthesesChecker.needsParentheses(replacementExpression, cast,
                                                                 CastExpression.EXPRESSION_PROPERTY)) {
                    ParenthesizedExpression parenthesized = ast.newParenthesizedExpression();
                    parenthesized.setExpression(moved);
                    moved = parenthesized;
                }
                cast.setExpression(moved);
                ImportRewrite imports = proposal.createImportRewrite(context.getASTRoot());
                ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(toRemove, imports);
                cast.setType(imports.addImport(explicitCast, ast, importRewriteContext));
                moved = cast;
            }
            rewrite.replace(toRemove, moved, null);

        } else {
            ASTNode parent = toRemove.getParent();
            ASTNode moveTarget;
            if ((parent instanceof Block || parent instanceof SwitchStatement) && replacement instanceof Block) {
                ListRewrite listRewrite = rewrite.getListRewrite(replacement, Block.STATEMENTS_PROPERTY);
                List<Statement> list = ((Block)replacement).statements();
                int lastIndex = list.size() - 1;
                moveTarget = listRewrite.createMoveTarget(list.get(0), list.get(lastIndex));
            } else {
                moveTarget = rewrite.createMoveTarget(replacement);
            }

            rewrite.replace(toRemove, moveTarget, null);
        }

        proposals.add(proposal);
    }

    private static void addRemoveProposal(InvocationContext context, ASTRewrite rewrite, String label,
                                          Collection<ICommandAccess> proposals) {
        Images image = Images.delete_obj;
        ASTRewriteCorrectionProposal proposal =
                new ASTRewriteCorrectionProposal(label, rewrite, 10, context.getDocument(), image);
        proposals.add(proposal);
    }

    public static void getUnusedObjectAllocationProposals(InvocationContext context, IProblemLocation problem,
                                                          Collection<ICommandAccess> proposals) {
        CompilationUnit root = context.getASTRoot();
        AST ast = root.getAST();
        ASTNode selectedNode = problem.getCoveringNode(root);
        if (selectedNode == null) {
            return;
        }

        ASTNode parent = selectedNode.getParent();

        if (parent instanceof ExpressionStatement) {
            ExpressionStatement expressionStatement = (ExpressionStatement)parent;
            Expression expr = expressionStatement.getExpression();
            ITypeBinding exprType = expr.resolveTypeBinding();

            if (exprType != null && Bindings.isSuperType(ast.resolveWellKnownType("java.lang.Throwable"), exprType)) { //$NON-NLS-1$
                ASTRewrite rewrite = ASTRewrite.create(ast);
                TightSourceRangeComputer sourceRangeComputer = new TightSourceRangeComputer();
                rewrite.setTargetSourceRangeComputer(sourceRangeComputer);

                ThrowStatement throwStatement = ast.newThrowStatement();
                throwStatement.setExpression((Expression)rewrite.createMoveTarget(expr));
                sourceRangeComputer.addTightSourceNode(expressionStatement);
                rewrite.replace(expressionStatement, throwStatement, null);

                String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_throw_allocated_description();
                Images image = Images.correction_change;
                LinkedCorrectionProposal proposal =
                        new LinkedCorrectionProposal(label, rewrite, 8, context.getDocument(), image);
                //            proposal.setEndPosition(rewrite.track(throwStatement));
                proposals.add(proposal);
            }

            MethodDeclaration method = ASTResolving.findParentMethodDeclaration(selectedNode);
            if (method != null) {
                ASTRewrite rewrite = ASTRewrite.create(ast);
                TightSourceRangeComputer sourceRangeComputer = new TightSourceRangeComputer();
                rewrite.setTargetSourceRangeComputer(sourceRangeComputer);

                ReturnStatement returnStatement = ast.newReturnStatement();
                returnStatement.setExpression((Expression)rewrite.createMoveTarget(expr));
                sourceRangeComputer.addTightSourceNode(expressionStatement);
                rewrite.replace(expressionStatement, returnStatement, null);

                String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_return_allocated_description();
                Images image = Images.correction_change;
                int relevance;
                ITypeBinding returnTypeBinding = method.getReturnType2().resolveBinding();
                if (returnTypeBinding != null && exprType != null && exprType.isAssignmentCompatible(returnTypeBinding)) {
                    relevance = 7;
                } else if (method.getReturnType2() instanceof PrimitiveType
                           && ((PrimitiveType)method.getReturnType2()).getPrimitiveTypeCode() == PrimitiveType.VOID) {
                    relevance = 1;
                } else {
                    relevance = 2;
                }
                LinkedCorrectionProposal proposal =
                        new LinkedCorrectionProposal(label, rewrite, relevance, context.getDocument(), image);
                //            proposal.setEndPosition(rewrite.track(returnStatement));
                proposals.add(proposal);
            }

            {
                ASTRewrite rewrite = ASTRewrite.create(ast);
                rewrite.remove(parent, null);

                String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_remove_allocated_description();
                Images image = Images.delete_obj;
                ASTRewriteCorrectionProposal proposal =
                        new ASTRewriteCorrectionProposal(label, rewrite, 1, context.getDocument(), image);
                proposals.add(proposal);
            }

        }

        QuickAssistProcessorImpl.getAssignToVariableProposals(context, selectedNode, null, proposals);
    }

    public static void getAssignmentHasNoEffectProposals(InvocationContext context, IProblemLocation problem,
                                                         Collection<ICommandAccess> proposals) {
        CompilationUnit root = context.getASTRoot();
        ASTNode selectedNode = problem.getCoveringNode(root);
        if (!(selectedNode instanceof Assignment)) {
            return;
        }
        ASTNode assignedNode = ((Assignment)selectedNode).getLeftHandSide();
        ASTNode assignExpression = ((Assignment)selectedNode).getRightHandSide();
        if (!(assignedNode instanceof SimpleName) && !(assignExpression instanceof SimpleName)) {
            return;
        }

        IBinding binding =
                (assignedNode instanceof SimpleName) ? ((SimpleName)assignedNode).resolveBinding()
                                                     : ((SimpleName)assignExpression).resolveBinding();
        if (!(binding instanceof IVariableBinding)) {
            return;
        }
        ITypeBinding typeBinding = Bindings.getBindingOfParentType(selectedNode);
        if (typeBinding == null) {
            return;
        }
        IVariableBinding fieldBinding = Bindings.findFieldInHierarchy(typeBinding, binding.getName());
        if (fieldBinding == null || fieldBinding.getDeclaringClass() != typeBinding
                                    && Modifier.isPrivate(fieldBinding.getModifiers())) {
            return;
        }

        if (binding != fieldBinding) {
            if (assignedNode instanceof SimpleName) {
                String label =
                        CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_qualify_left_hand_side_description();
                proposals.add(createNoSideEffectProposal(context, (SimpleName)assignedNode, fieldBinding, label, 6));
            }
            if (assignExpression instanceof SimpleName) {
                String label =
                        CorrectionMessages.INSTANCE
                                          .LocalCorrectionsSubProcessor_LocalCorrectionsSubProcessor_qualify_right_hand_side_description();
                proposals.add(createNoSideEffectProposal(context, (SimpleName)assignExpression, fieldBinding, label, 5));
            }
        }

        if (binding == fieldBinding && ASTResolving.findParentBodyDeclaration(selectedNode) instanceof MethodDeclaration) {
            SimpleName simpleName = (SimpleName)((assignedNode instanceof SimpleName) ? assignedNode : assignExpression);
            String label =
                    CorrectionMessages.INSTANCE.UnresolvedElementsSubProcessor_createparameter_description(simpleName
                                                                                                                   .getIdentifier());
            Images image = Images.local_var;
            proposals.add(new NewVariableCorrectionProposal(label, NewVariableCorrectionProposal.PARAM, simpleName, null,
                                                            5, context.getDocument(), image));
        }

    }

    private static ASTRewriteCorrectionProposal createNoSideEffectProposal(InvocationContext context,
                                                                           SimpleName nodeToQualify, IVariableBinding fieldBinding,
                                                                           String label, int relevance) {
        AST ast = nodeToQualify.getAST();

        Expression qualifier;
        if (Modifier.isStatic(fieldBinding.getModifiers())) {
            ITypeBinding declaringClass = fieldBinding.getDeclaringClass();
            qualifier = ast.newSimpleName(declaringClass.getTypeDeclaration().getName());
        } else {
            qualifier = ast.newThisExpression();
        }

        ASTRewrite rewrite = ASTRewrite.create(ast);
        FieldAccess access = ast.newFieldAccess();
        access.setName((SimpleName)rewrite.createCopyTarget(nodeToQualify));
        access.setExpression(qualifier);
        rewrite.replace(nodeToQualify, access, null);

        Images image = Images.correction_change;
        return new ASTRewriteCorrectionProposal(label, rewrite, relevance, context.getDocument(), image);
    }

    public static void addValueForAnnotationProposals(InvocationContext context, IProblemLocation problem,
                                                      Collection<ICommandAccess> proposals) {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (selectedNode instanceof Annotation) {
            Annotation annotation = (Annotation)selectedNode;
            if (annotation.resolveTypeBinding() == null) {
                return;
            }
            MissingAnnotationAttributesProposal proposal =
                    new MissingAnnotationAttributesProposal(annotation, 10, context.getDocument());
            proposals.add(proposal);
        }
    }

    public static void addTypePrametersToRawTypeReference(InvocationContext context, IProblemLocation problem,
                                                          Collection<ICommandAccess> proposals) {
        //      IProposableFix fix = Java50Fix.createRawTypeReferenceFix(context.getASTRoot(), problem);
        //      if (fix != null)
        //      {
        //         for (Iterator<ICommandAccess> iter = proposals.iterator(); iter.hasNext();)
        //         {
        //            Object element = iter.next();
        //            if (element instanceof FixCorrectionProposal)
        //            {
        //               FixCorrectionProposal fixProp = (FixCorrectionProposal)element;
        //               if (RAW_TYPE_REFERENCE_ID.equals(fixProp.getCommandId()))
        //               {
        //                  return;
        //               }
        //            }
        //         }
        //         Image image = new Image(JavaClientBundle.INSTANCE.correction_change());
        //         Map<String, String> options = new HashMap<String, String>();
        //         options.put(CleanUpConstants.VARIABLE_DECLARATION_USE_TYPE_ARGUMENTS_FOR_RAW_TYPE_REFERENCES,
        //            CleanUpOptions.TRUE);
        //         FixCorrectionProposal proposal = new FixCorrectionProposal(fix, new Java50CleanUp(options), 6, image, context);
        //         proposal.setCommandId(RAW_TYPE_REFERENCE_ID);
        //         proposals.add(proposal);
        //      }
        //
        //      //Infer Generic Type Arguments... proposal
        //      boolean hasInferTypeArgumentsProposal = false;
        //      for (Iterator<ICommandAccess> iterator = proposals.iterator(); iterator.hasNext();)
        //      {
        //         Object completionProposal = iterator.next();
        //         if (completionProposal instanceof ChangeCorrectionProposal)
        //         {
        //            if (IJavaEditorActionDefinitionIds.INFER_TYPE_ARGUMENTS_ACTION
        //               .equals(((ChangeCorrectionProposal)completionProposal).getCommandId()))
        //            {
        //               hasInferTypeArgumentsProposal = true;
        //               break;
        //            }
        //         }
        //      }
        //      if (!hasInferTypeArgumentsProposal)
        //      {
        //         final ICompilationUnit cu = context.getCompilationUnit();
        //         ChangeCorrectionProposal proposal =
        //            new ChangeCorrectionProposal(
        //               CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_InferGenericTypeArguments, null, 5,
        //               JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE))
        //            {
        //               @Override
        //               public void apply(IDocument document)
        //               {
        //                  IEditorInput input = new FileEditorInput((IFile)cu.getResource());
        //                  IWorkbenchPage p = JavaPlugin.getActivePage();
        //                  if (p == null)
        //                     return;
        //
        //                  IEditorPart part = p.findEditor(input);
        //                  if (!(part instanceof JavaEditor))
        //                     return;
        //
        //                  IEditorSite site = ((JavaEditor)part).getEditorSite();
        //                  InferTypeArgumentsAction action = new InferTypeArgumentsAction(site);
        //                  action.run(new StructuredSelection(cu));
        //               }
        //
        //               /**
        //                * {@inheritDoc}
        //                */
        //               @Override
        //               public Object getAdditionalProposalInfo(IProgressMonitor monitor)
        //               {
        //                  return CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_InferGenericTypeArguments_description;
        //               }
        //            };
        //         proposal.setCommandId(IJavaEditorActionDefinitionIds.INFER_TYPE_ARGUMENTS_ACTION);
        //         proposals.add(proposal);
        //      }
        System.out.println("LocalCorrectionsSubProcessor.addTypePrametersToRawTypeReference()");
        //TODO
        addTypeArgumentsFromContext(context, problem, proposals);
    }

    private static void addTypeArgumentsFromContext(InvocationContext context, IProblemLocation problem,
                                                    Collection<ICommandAccess> proposals) {
        // similar to UnresolvedElementsSubProcessor.getTypeProposals(context, problem, proposals);

        CompilationUnit root = context.getASTRoot();
        ASTNode selectedNode = problem.getCoveringNode(root);
        if (selectedNode == null) {
            return;
        }

        while (selectedNode.getLocationInParent() == QualifiedName.NAME_PROPERTY) {
            selectedNode = selectedNode.getParent();
        }

        Name node = null;
        if (selectedNode instanceof SimpleType) {
            node = ((SimpleType)selectedNode).getName();
        } else if (selectedNode instanceof ArrayType) {
            Type elementType = ((ArrayType)selectedNode).getElementType();
            if (elementType.isSimpleType()) {
                node = ((SimpleType)elementType).getName();
            } else {
                return;
            }
        } else if (selectedNode instanceof Name) {
            node = (Name)selectedNode;
        } else {
            return;
        }

        // try to resolve type in context
        ITypeBinding binding = ASTResolving.guessBindingForTypeReference(node);
        if (binding != null) {
            ASTNode parent = node.getParent();
            if (parent instanceof Type && parent.getLocationInParent() == ClassInstanceCreation.TYPE_PROPERTY
                && binding.isInterface()) { //bug 351853
                return;
            }
            ITypeBinding simpleBinding = binding;
            if (simpleBinding.isArray()) {
                simpleBinding = simpleBinding.getElementType();
            }
            simpleBinding = simpleBinding.getTypeDeclaration();

            if (!simpleBinding.isRecovered()) {
                if (binding.isParameterizedType() && node.getParent() instanceof SimpleType
                    && !(node.getParent().getParent() instanceof Type)) {
                    proposals.add(UnresolvedElementsSubProcessor.createTypeRefChangeFullProposal(context.getDocument(),
                                                                                                 binding, node, 2));
                }
            }
        } else {
            ASTNode normalizedNode = ASTNodes.getNormalizedNode(node);
            if (!(normalizedNode.getParent() instanceof Type) && node.getParent() != normalizedNode) {
                ITypeBinding normBinding = ASTResolving.guessBindingForTypeReference(normalizedNode);
                if (normBinding != null && !normBinding.isRecovered()) {
                    proposals.add(UnresolvedElementsSubProcessor.createTypeRefChangeFullProposal(context.getDocument(),
                                                                                                 normBinding, normalizedNode, 2));
                }
            }
        }
    }

    public static void addRemoveRedundantTypeArgumentsProposals(InvocationContext context, IProblemLocation problem,
                                                                Collection<ICommandAccess> proposals) {
        CompilationUnit astRoot = context.getASTRoot();
        ASTNode selectedNode = problem.getCoveringNode(astRoot);
        if (selectedNode == null)
            return;

        while (!(selectedNode instanceof ParameterizedType) && !(selectedNode instanceof Statement)) {
            selectedNode = selectedNode.getParent();
        }
        if (!(selectedNode instanceof ParameterizedType)) {
            return;
        }
        ParameterizedType parameterizedType = (ParameterizedType)selectedNode;

        AST ast = astRoot.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);
        ListRewrite listRewrite = rewrite.getListRewrite(parameterizedType, ParameterizedType.TYPE_ARGUMENTS_PROPERTY);

        List<Type> typeArguments = parameterizedType.typeArguments();
        for (Iterator<Type> iterator = typeArguments.iterator(); iterator.hasNext(); ) {
            listRewrite.remove(iterator.next(), null);
        }

        Images image = Images.delete_obj;
        String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_remove_type_arguments();
        ASTRewriteCorrectionProposal proposal =
                new ASTRewriteCorrectionProposal(label, rewrite, 6, context.getDocument(), image);
        proposals.add(proposal);
    }

    public static void addFallThroughProposals(InvocationContext context, IProblemLocation problem,
                                               Collection<ICommandAccess> proposals) {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (selectedNode instanceof SwitchCase
            && selectedNode.getLocationInParent() == SwitchStatement.STATEMENTS_PROPERTY) {
            AST ast = selectedNode.getAST();
            ASTNode parent = selectedNode.getParent();

            // insert break:
            ASTRewrite rewrite = ASTRewrite.create(ast);
            ListRewrite listRewrite = rewrite.getListRewrite(parent, SwitchStatement.STATEMENTS_PROPERTY);
            listRewrite.insertBefore(ast.newBreakStatement(), selectedNode, null);

            String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_insert_break_statement();
            Images image = Images.correction_change;
            ASTRewriteCorrectionProposal proposal =
                    new ASTRewriteCorrectionProposal(label, rewrite, 5, context.getDocument(), image);
            proposals.add(proposal);

            // insert //$FALL-THROUGH$:
            rewrite = ASTRewrite.create(ast);
            rewrite.setTargetSourceRangeComputer(new NoCommentSourceRangeComputer());
            listRewrite = rewrite.getListRewrite(parent, SwitchStatement.STATEMENTS_PROPERTY);
            ASTNode fallThroughComment = rewrite.createStringPlaceholder("//$FALL-THROUGH$", ASTNode.EMPTY_STATEMENT); //$NON-NLS-1$
            listRewrite.insertBefore(fallThroughComment, selectedNode, null);

            label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_insert_fall_through();
            image = Images.correction_change;
            proposal = new ASTRewriteCorrectionProposal(label, rewrite, 4, context.getDocument(), image);
            proposals.add(proposal);
        }
    }

    public static void addDeprecatedFieldsToMethodsProposals(InvocationContext context, IProblemLocation problem,
                                                             Collection<ICommandAccess> proposals) {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (selectedNode instanceof Name) {
            IBinding binding = ((Name)selectedNode).resolveBinding();
            if (binding instanceof IVariableBinding) {
                IVariableBinding variableBinding = (IVariableBinding)binding;
                if (variableBinding.isField()) {
                    String qualifiedName = variableBinding.getDeclaringClass().getTypeDeclaration().getQualifiedName();
                    String fieldName = variableBinding.getName();
                    String[] methodName = getMethod(qualifiedName + "." + fieldName);
                    if (methodName != null) {
                        AST ast = selectedNode.getAST();
                        ASTRewrite astRewrite = ASTRewrite.create(ast);
                        ImportRewrite importRewrite =
                                StubUtility.createImportRewrite(context.getDocument(), context.getASTRoot(), true);

                        MethodInvocation method = ast.newMethodInvocation();
                        String qfn = importRewrite.addImport(methodName[0]);
                        method.setExpression(ast.newName(qfn));
                        method.setName(ast.newSimpleName(methodName[1]));
                        ASTNode parent = selectedNode.getParent();
                        // add explicit type arguments if necessary:
                        if (Invocations.isInvocationWithArguments(parent)) {
                            IMethodBinding methodBinding = Invocations.resolveBinding(parent);
                            if (methodBinding != null) {
                                ITypeBinding[] parameterTypes = methodBinding.getParameterTypes();
                                int i = Invocations.getArguments(parent).indexOf(selectedNode);
                                if (parameterTypes.length >= i && parameterTypes[i].isParameterizedType()) {
                                    ITypeBinding[] typeArguments = parameterTypes[i].getTypeArguments();
                                    for (int j = 0; j < typeArguments.length; j++) {
                                        ITypeBinding typeArgument = typeArguments[j];
                                        if (!TypeRules.isJavaLangObject(typeArgument)) {
                                            List<Type> typeArgumentsList = method.typeArguments();
                                            for (int k = 0; k < typeArguments.length; k++) {
                                                typeArgument = typeArguments[k];
                                                typeArgumentsList.add(importRewrite.addImport(typeArgument, ast));
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        astRewrite.replace(selectedNode, method, null);

                        String label =

                                CorrectionMessages.INSTANCE
                                                  .LocalCorrectionsSubProcessor_replacefieldaccesswithmethod_description(ASTNodes
                                                                                                                                 .asString(
                                                                                                                                         method));
                        Images image = Images.correction_change;
                        ASTRewriteCorrectionProposal proposal =
                                new ASTRewriteCorrectionProposal(label, astRewrite, 10, context.getDocument(), image);
                        proposal.setImportRewrite(importRewrite);
                        proposals.add(proposal);
                    }
                }
            }
        }
    }

    private static Map<String, String[]> resolveMap;

    private static String[] getMethod(String fieldName) {
        if (resolveMap == null) {
            resolveMap = new HashMap<String, String[]>();
            resolveMap.put("java.util.Collections.EMPTY_MAP",
                           new String[]{"java.util.Collections", "emptyMap"}); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            resolveMap.put("java.util.Collections.EMPTY_SET",
                           new String[]{"java.util.Collections", "emptySet"}); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            resolveMap.put("java.util.Collections.EMPTY_LIST",
                           new String[]{"java.util.Collections", "emptyList"});//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        }
        return resolveMap.get(fieldName);
    }

    public static void getMissingEnumConstantCaseProposals(InvocationContext context, IProblemLocation problem,
                                                           Collection<ICommandAccess> proposals) {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (selectedNode instanceof Name && selectedNode.getParent() instanceof SwitchStatement) {
            SwitchStatement statement = (SwitchStatement)selectedNode.getParent();
            ITypeBinding binding = statement.getExpression().resolveTypeBinding();
            if (binding == null || !binding.isEnum()) {
                return;
            }

            String[] missingEnumCases = evaluateMissingEnumConstantCases(binding, statement.statements());
            if (missingEnumCases.length == 0)
                return;

            proposals.add(createMissingEnumConstantCaseProposals(context, statement, missingEnumCases));
        }
    }

    public static String[] evaluateMissingEnumConstantCases(ITypeBinding enumBindings, List<Statement> switchStatements) {
        ArrayList<String> enumConstNames = new ArrayList<String>();
        IVariableBinding[] fields = enumBindings.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isEnumConstant()) {
                enumConstNames.add(fields[i].getName());
            }
        }

        List<Statement> statements = switchStatements;
        for (int i = 0; i < statements.size(); i++) {
            Statement curr = statements.get(i);
            if (curr instanceof SwitchCase) {
                Expression expression = ((SwitchCase)curr).getExpression();
                if (expression instanceof SimpleName) {
                    enumConstNames.remove(((SimpleName)expression).getFullyQualifiedName());
                }
            }
        }
        return enumConstNames.toArray(new String[enumConstNames.size()]);

    }

    public static ASTRewriteCorrectionProposal createMissingEnumConstantCaseProposals(InvocationContext context,
                                                                                      SwitchStatement switchStatement,
                                                                                      String[] enumConstNames) {
        List<Statement> statements = switchStatement.statements();
        int defaultIndex = statements.size();
        for (int i = 0; i < statements.size(); i++) {
            Statement curr = statements.get(i);
            if (curr instanceof SwitchCase && ((SwitchCase)curr).getExpression() == null) {
                defaultIndex = i;
                break;
            }
        }
        AST ast = switchStatement.getAST();
        ASTRewrite astRewrite = ASTRewrite.create(ast);

        boolean hasDefault = defaultIndex < statements.size();

        ListRewrite listRewrite = astRewrite.getListRewrite(switchStatement, SwitchStatement.STATEMENTS_PROPERTY);
        for (int i = 0; i < enumConstNames.length; i++) {
            SwitchCase newSwitchCase = ast.newSwitchCase();
            newSwitchCase.setExpression(ast.newName(enumConstNames[i]));
            listRewrite.insertAt(newSwitchCase, defaultIndex, null);
            defaultIndex++;
            if (!hasDefault) {
                listRewrite.insertAt(ast.newBreakStatement(), defaultIndex, null);
                defaultIndex++;
            }
        }
        String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_add_missing_cases_description();
        Images image = Images.correction_change;
        return new ASTRewriteCorrectionProposal(label, astRewrite, 10, context.getDocument(), image);
    }

    public static void addMissingHashCodeProposals(InvocationContext context, IProblemLocation problem,
                                                   Collection<ICommandAccess> proposals) {

        CompilationUnit astRoot = context.getASTRoot();
        ASTNode selectedNode = problem.getCoveringNode(astRoot);
        if (!(selectedNode instanceof Name)) {
            return;
        }

        AbstractTypeDeclaration typeDeclaration = null;
        StructuralPropertyDescriptor locationInParent = selectedNode.getLocationInParent();
        if (locationInParent != TypeDeclaration.NAME_PROPERTY && locationInParent != EnumDeclaration.NAME_PROPERTY) {
            return;
        }

        typeDeclaration = (AbstractTypeDeclaration)selectedNode.getParent();

        ITypeBinding binding = typeDeclaration.resolveBinding();
        if (binding == null || binding.getSuperclass() == null) {
            return;
        }
        //      final IType type = (IType)binding.getJavaElement();

        boolean hasInstanceFields = false;
        IVariableBinding[] declaredFields = binding.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {
            if (!Modifier.isStatic(declaredFields[i].getModifiers())) {
                hasInstanceFields = true;
                break;
            }
        }
        if (hasInstanceFields) {
            //Generate hashCode() and equals()... proposal
            String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_generate_hashCode_equals_description();
            Images image = Images.correction_change;
            ChangeCorrectionProposal proposal = new ChangeCorrectionProposal(label, null, 3, image) {
                @Override
                public void apply(Document document) {
                    System.out
                          .println(
                                  "LocalCorrectionsSubProcessor.addMissingHashCodeProposals(...).new ChangeCorrectionProposal() {...}.apply()");
                    //TODO
                    //               IEditorInput input = new FileEditorInput((IFile)cu.getResource());
                    //               IWorkbenchPage p = JavaPlugin.getActivePage();
                    //               if (p == null)
                    //                  return;
                    //
                    //               IEditorPart part = p.findEditor(input);
                    //               if (!(part instanceof JavaEditor))
                    //                  return;
                    //
                    //               IEditorSite site = ((JavaEditor)part).getEditorSite();
                    //               GenerateHashCodeEqualsAction action = new GenerateHashCodeEqualsAction(site);
                    //               action.run(new StructuredSelection(type));
                }

                @Override
                public Object getAdditionalInfo() {
                    return CorrectionMessages.INSTANCE
                                             .LocalCorrectionsSubProcessor_generate_hashCode_equals_additional_info();
                }
            };
            proposals.add(proposal);
        }

        //Override hashCode() proposal
        IMethodBinding superHashCode = Bindings.findMethodInHierarchy(binding, "hashCode", new ITypeBinding[0]); //$NON-NLS-1$
        if (superHashCode == null) {
            return;
        }

        String label = CorrectionMessages.INSTANCE.LocalCorrectionsSubProcessor_override_hashCode_description();
        Images image = Images.publicMethod;

        ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());
        LinkedCorrectionProposal proposal2 =
                new LinkedCorrectionProposal(label, rewrite, 5, context.getDocument(), image);
        ImportRewrite importRewrite = proposal2.createImportRewrite(astRoot);

        String typeQualifiedName = binding.getQualifiedName();
        final CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings();

        try {
            ImportRewriteContext importContext =
                    new ContextSensitiveImportRewriteContext(astRoot, problem.getOffset(), importRewrite);
            MethodDeclaration hashCode =
                    StubUtility2.createImplementationStub(rewrite, importRewrite, importContext, superHashCode,
                                                          typeQualifiedName, settings, false);
            BodyDeclarationRewrite.create(rewrite, typeDeclaration).insert(hashCode, null);

            //         proposal2.setEndPosition(rewrite.track(hashCode));

        } catch (CoreException e) {
            //TODO log error
//            Log.error(LocalCorrectionsSubProcessor.class, e);
        }

        proposals.add(proposal2);
    }

}
