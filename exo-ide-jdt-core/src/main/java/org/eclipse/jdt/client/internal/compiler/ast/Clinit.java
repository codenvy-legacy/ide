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
package org.eclipse.jdt.client.internal.compiler.ast;

import org.eclipse.jdt.client.internal.compiler.ASTVisitor;
import org.eclipse.jdt.client.internal.compiler.CompilationResult;
import org.eclipse.jdt.client.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.client.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.client.internal.compiler.flow.InitializationFlowContext;
import org.eclipse.jdt.client.internal.compiler.lookup.Binding;
import org.eclipse.jdt.client.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.client.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.client.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.client.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.client.internal.compiler.parser.Parser;
import org.eclipse.jdt.client.internal.compiler.problem.AbortMethod;

public class Clinit extends AbstractMethodDeclaration
{

   public Clinit(CompilationResult compilationResult)
   {
      super(compilationResult);
      this.modifiers = 0;
      this.selector = TypeConstants.CLINIT;
   }

   public void analyseCode(ClassScope classScope, InitializationFlowContext staticInitializerFlowContext,
      FlowInfo flowInfo)
   {

      if (this.ignoreFurtherInvestigation)
         return;
      try
      {
         ExceptionHandlingFlowContext clinitContext =
            new ExceptionHandlingFlowContext(staticInitializerFlowContext.parent, this, Binding.NO_EXCEPTIONS,
               staticInitializerFlowContext, this.scope, FlowInfo.DEAD_END);

         // check for missing returning path
         if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0)
         {
            this.bits |= ASTNode.NeedFreeReturn;
         }

         // check missing blank final field initializations
         flowInfo = flowInfo.mergedWith(staticInitializerFlowContext.initsOnReturn);
         FieldBinding[] fields = this.scope.enclosingSourceType().fields();
         for (int i = 0, count = fields.length; i < count; i++)
         {
            FieldBinding field;
            if ((field = fields[i]).isStatic() && field.isFinal() && (!flowInfo.isDefinitelyAssigned(fields[i])))
            {
               this.scope.problemReporter().uninitializedBlankFinalField(field,
                  this.scope.referenceType().declarationOf(field.original()));
               // can complain against the field decl, since only one <clinit>
            }
         }
         // check static initializers thrown exceptions
         staticInitializerFlowContext.checkInitializerExceptions(this.scope, clinitContext, flowInfo);
      }
      catch (AbortMethod e)
      {
         this.ignoreFurtherInvestigation = true;
      }
   }

   public boolean isClinit()
   {

      return true;
   }

   public boolean isInitializationMethod()
   {

      return true;
   }

   public boolean isStatic()
   {

      return true;
   }

   public void parseStatements(Parser parser, CompilationUnitDeclaration unit)
   {
      // the clinit is filled by hand ....
   }

   public StringBuffer print(int tab, StringBuffer output)
   {

      printIndent(tab, output).append("<clinit>()"); //$NON-NLS-1$
      printBody(tab + 1, output);
      return output;
   }

   public void resolve(ClassScope classScope)
   {

      this.scope = new MethodScope(classScope, classScope.referenceContext, true);
   }

   public void traverse(ASTVisitor visitor, ClassScope classScope)
   {

      visitor.visit(this, classScope);
      visitor.endVisit(this, classScope);
   }

   public void setAssertionSupport(FieldBinding assertionSyntheticFieldBinding, boolean needClassLiteralField)
   {

//      this.assertionSyntheticFieldBinding = assertionSyntheticFieldBinding;
//
//      // we need to add the field right now, because the field infos are generated before the methods
//      if (needClassLiteralField)
//      {
//         SourceTypeBinding sourceType = this.scope.outerMostClassScope().enclosingSourceType();
//         // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=22334
//         if (!sourceType.isInterface() && !sourceType.isBaseType())
//         {
//            this.classLiteralSyntheticField = sourceType.addSyntheticFieldForClassLiteral(sourceType, this.scope);
//         }
//      }
   }

}
