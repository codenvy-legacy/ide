/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.core.util.Util;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;


/**
 * The {@link SharedASTProvider} provides access to the {@link org.eclipse.jdt.core.dom.CompilationUnit AST root} used by
 * the current active Java editor.
 *
 * <p>For performance reasons, not more than one AST should be kept in memory at a time. Therefore, clients must
 * not keep any references to the shared AST or its nodes or bindings.
 * </p>
 * <p>Clients can make the following assumptions about the AST:
 * <dl>
 * <li>the AST has a {@link org.eclipse.jdt.core.ITypeRoot} as source: {@link org.eclipse.jdt.core.dom.CompilationUnit#getTypeRoot()} is not null.</li>
 * <li>the {@link org.eclipse.jdt.core.dom.AST#apiLevel() AST API level} is {@link org.eclipse.jdt.core.dom.AST#JLS4 API level 4} or higher</li>
 * <li>the AST has bindings resolved ({@link org.eclipse.jdt.core.dom.AST#hasResolvedBindings()})</li>
 * <li>{@link org.eclipse.jdt.core.dom.AST#hasStatementsRecovery() statement} and {@link org.eclipse.jdt.core.dom.AST#hasBindingsRecovery() bindings}
 * recovery are enabled
 * </li>
 * </dl>
 * It is possible that in the future a higher API level is used, or that future options will be enabled.
 * </p>
 * <p>
 * The returned AST is shared. It is marked as {@link org.eclipse.jdt.core.dom.ASTNode#PROTECT} and must not be modified. Clients are advised to use
 * the non-modifying {@link org.eclipse.jdt.core.dom.rewrite.ASTRewrite} to get update scripts.
 * </p>
 *
 * <p>
 * This class is not intended to be subclassed or instantiated by clients.
 * </p>
 *
 * @since 3.4
 */
public final class SharedASTProvider
{

   /**
    * Wait flag class.
    */
   public static final class WAIT_FLAG
   {

      private String fName;

      private WAIT_FLAG(String name)
      {
         fName = name;
      }

      /*
       * @see java.lang.Object#toString()
       */
      @Override
      public String toString()
      {
         return fName;
      }
   }

   /**
    * Wait flag indicating that a client requesting an AST
    * wants to wait until an AST is ready.
    * <p>
    * An AST will be created by this AST provider if the shared
    * AST is not for the given Java element.
    * </p>
    */
   public static final WAIT_FLAG WAIT_YES = new WAIT_FLAG("wait yes"); //$NON-NLS-1$

   /**
    * Wait flag indicating that a client requesting an AST
    * only wants to wait for the shared AST of the active editor.
    * <p>
    * No AST will be created by the AST provider.
    * </p>
    */
   public static final WAIT_FLAG WAIT_ACTIVE_ONLY = new WAIT_FLAG("wait active only"); //$NON-NLS-1$

   /**
    * Wait flag indicating that a client requesting an AST
    * only wants the already available shared AST.
    * <p>
    * No AST will be created by the AST provider.
    * </p>
    */
   public static final WAIT_FLAG WAIT_NO = new WAIT_FLAG("don't wait"); //$NON-NLS-1$

   /**
    * Returns a compilation unit AST for the given Java element. If the element is the input of the
    * active Java editor, the AST is the shared AST.
    * <p>
    * Clients are not allowed to modify the AST and must not keep any references.
    * </p>
    *
    * @param element         the {@link org.eclipse.jdt.core.ITypeRoot}, must not be <code>null</code>
    * @param waitFlag        {@link #WAIT_YES}, {@link #WAIT_NO} or {@link #WAIT_ACTIVE_ONLY}
    * @param progressMonitor the progress monitor or <code>null</code>
    * @return the AST or <code>null</code>.
    *         <dl>
    *         <li>If {@link #WAIT_NO} has been specified <code>null</code> is returned if the
    *         element is not input of the current Java editor or no AST is available</li>
    *         <li>If {@link #WAIT_ACTIVE_ONLY} has been specified <code>null</code> is returned if
    *         the element is not input of the current Java editor</li>
    *         <li>If {@link #WAIT_YES} has been specified either the shared AST is returned or a
    *         new AST is created.</li>
    *         <li><code>null</code> will be returned if the operation gets canceled.</li>
    *         </dl>
    */
   public static CompilationUnit getAST(ITypeRoot element, WAIT_FLAG waitFlag, IProgressMonitor progressMonitor)
   {
      return createAST(element, progressMonitor);
   }

   /**
    * Checks whether the given Java element has accessible source.
    *
    * @param je the Java element to test
    * @return <code>true</code> if the element has source
    * @since 3.2
    */
   private static boolean hasSource(ITypeRoot je)
   {
      if (je == null || !je.exists())
      {
         return false;
      }

      try
      {
         return je.getBuffer() != null;
      }
      catch (JavaModelException ex)
      {
         IStatus status = new Status(IStatus.ERROR, "IDE", IStatus.OK, "Error in JDT Core during AST creation",
            ex);  //$NON-NLS-1$
         Util.log(status);
      }
      return false;
   }

   /**
    * Creates a new compilation unit AST.
    *
    * @param input           the Java element for which to create the AST
    * @param progressMonitor the progress monitor
    * @return AST
    */
   private static CompilationUnit createAST(final ITypeRoot input, final IProgressMonitor progressMonitor)
   {
      if (!hasSource(input))
      {
         return null;
      }

      if (progressMonitor != null && progressMonitor.isCanceled())
      {
         return null;
      }

      final ASTParser parser = ASTParser.newParser(AST.JLS4);
      parser.setResolveBindings(true);
      parser.setStatementsRecovery(true);
      parser.setBindingsRecovery(true);
      parser.setSource(input);

      if (progressMonitor != null && progressMonitor.isCanceled())
      {
         return null;
      }

      final CompilationUnit root[] = new CompilationUnit[1];

      SafeRunner.run(new ISafeRunnable()
      {
         public void run()
         {
            try
            {
               if (progressMonitor != null && progressMonitor.isCanceled())
               {
                  return;
               }
               root[0] = (CompilationUnit)parser.createAST(progressMonitor);

               //mark as unmodifiable
               ASTNodes.setFlagsToAST(root[0], ASTNode.PROTECT);
            }
            catch (OperationCanceledException ex)
            {
               return;
            }
         }

         public void handleException(Throwable ex)
         {
            IStatus status = new Status(IStatus.ERROR, "IDE", IStatus.OK, "Error in JDT Core during AST creation",
               ex);  //$NON-NLS-1$
            Util.log(status);
         }
      });
      return root[0];
   }

   private SharedASTProvider()
   {
      // Prevent instantiation.
   }

}
