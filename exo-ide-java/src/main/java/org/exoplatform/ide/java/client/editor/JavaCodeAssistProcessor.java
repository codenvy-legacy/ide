/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.java.client.editor;

import org.exoplatform.ide.java.client.JavaExtension;
import org.exoplatform.ide.java.client.TypeInfoStorage;
import org.exoplatform.ide.java.client.codeassistant.AbstractJavaCompletionProposal;
import org.exoplatform.ide.java.client.codeassistant.CompletionProposalCollector;
import org.exoplatform.ide.java.client.codeassistant.FillArgumentNamesCompletionProposalCollector;
import org.exoplatform.ide.java.client.codeassistant.JavaContentAssistInvocationContext;
import org.exoplatform.ide.java.client.codeassistant.LazyGenericTypeProposal;
import org.exoplatform.ide.java.client.codeassistant.TemplateCompletionProposalComputer;
import org.exoplatform.ide.java.client.codeassistant.api.JavaCompletionProposal;
import org.exoplatform.ide.java.client.core.IJavaElement;
import org.exoplatform.ide.java.client.core.IType;
import org.exoplatform.ide.java.client.core.JavaCore;
import org.exoplatform.ide.java.client.core.Signature;
import org.exoplatform.ide.java.client.core.dom.CompilationUnit;
import org.exoplatform.ide.java.client.editor.AstProvider.AstListener;
import org.exoplatform.ide.java.client.internal.codeassist.CompletionEngine;
import org.exoplatform.ide.java.client.internal.compiler.env.INameEnvironment;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.runtime.AssertionFailedException;
import org.exoplatform.ide.texteditor.api.TextEditorPartDisplay;
import org.exoplatform.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal;
import org.exoplatform.ide.util.loging.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaCodeAssistProcessor implements CodeAssistProcessor
{

   private Comparator<JavaCompletionProposal> comparator = new Comparator<JavaCompletionProposal>()
   {

      @Override
      public int compare(JavaCompletionProposal o1, JavaCompletionProposal o2)
      {

         if (o1.getRelevance() > o2.getRelevance())
            return -1;
         else if (o1.getRelevance() < o2.getRelevance())
            return 1;
         else
            return 0;
      }
   };

   class InternalAstListener implements AstListener
   {

      /**
       * {@inheritDoc}
       */
      @Override
      public void onCompilationUnitChanged(CompilationUnit cUnit)
      {
         currentFile = astProvider.getFile();
         nameEnvironment = astProvider.getNameEnvironment();
         unit = cUnit;
      }
      
   }
   private String docContext;

   private CompilationUnit unit;

   private File currentFile;

   private INameEnvironment nameEnvironment;

   private TemplateCompletionProposalComputer templateCompletionProposalComputer =
      new TemplateCompletionProposalComputer();

   private final AstProvider astProvider;

   /**
    * @param projectId
    * @param docContext
    */
   public JavaCodeAssistProcessor(String docContext, AstProvider astProvider)
   {
      this.docContext = docContext;
      this.astProvider = astProvider;
      astProvider.addAstListener(new InternalAstListener());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CompletionProposal[] computeCompletionProposals(TextEditorPartDisplay display, int offset)
   {
      if (unit == null)
      {
         return null;
      }
      String projectId = currentFile.getProject().getId();
      CompletionProposalCollector collector =
      //TODO receive vfs id
         new FillArgumentNamesCompletionProposalCollector(unit, display.getDocument(), offset, projectId, docContext,
            "dev-monit");
      CompletionEngine e = new CompletionEngine(nameEnvironment, collector, JavaCore.getOptions());
      try
      {
         e.complete(new org.exoplatform.ide.java.client.compiler.batch.CompilationUnit(display.getDocument().get()
            .toCharArray(), currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.')), "UTF-8"),
            offset, 0);

         JavaCompletionProposal[] javaCompletionProposals = collector.getJavaCompletionProposals();
         List<JavaCompletionProposal> types =
            new ArrayList<JavaCompletionProposal>(Arrays.asList(javaCompletionProposals));
         if (types.size() > 0 && collector.getInvocationContext().computeIdentifierPrefix().length() == 0)
         {
            IType expectedType = collector.getInvocationContext().getExpectedType();
            if (expectedType != null)
            {
               // empty prefix completion - insert LRU types if known, but prune if they already occur in the core list

               // compute minmimum relevance and already proposed list
               int relevance = Integer.MAX_VALUE;
               Set<String> proposed = new HashSet<String>();
               for (Iterator<JavaCompletionProposal> it = types.iterator(); it.hasNext();)
               {
                  AbstractJavaCompletionProposal p = (AbstractJavaCompletionProposal)it.next();
                  IJavaElement element = p.getJavaElement();
                  if (element instanceof IType)
                     proposed.add(((IType)element).getFullyQualifiedName());
                  relevance = Math.min(relevance, p.getRelevance());
               }

               // insert history types
               List<String> history =
                  JavaExtension.get().getContentAssistHistory().getHistory(expectedType.getFullyQualifiedName())
                     .getTypes();
               relevance -= history.size() + 1;
               for (Iterator<String> it = history.iterator(); it.hasNext();)
               {
                  String type = it.next();
                  if (proposed.contains(type))
                     continue;

                  JavaCompletionProposal proposal =
                     createTypeProposal(relevance, type, collector.getInvocationContext());

                  if (proposal != null)
                     types.add(proposal);
                  relevance++;
               }
            }
         }

         List<JavaCompletionProposal> templateProposals =
            templateCompletionProposalComputer.computeCompletionProposals(collector.getInvocationContext());
         JavaCompletionProposal[] array =
            templateProposals.toArray(new JavaCompletionProposal[templateProposals.size()]);
         javaCompletionProposals = types.toArray(new JavaCompletionProposal[0]);
         JavaCompletionProposal[] proposals = new JavaCompletionProposal[javaCompletionProposals.length + array.length];
         System.arraycopy(javaCompletionProposals, 0, proposals, 0, javaCompletionProposals.length);
         System.arraycopy(array, 0, proposals, javaCompletionProposals.length, array.length);

         Arrays.sort(proposals, comparator);
         return proposals;
      }
      catch (AssertionFailedException ex)
      {
         Log.error(getClass(), ex);

      }
      catch (Exception ex)
      {
         Log.error(getClass(), ex);
      }
      return new JavaCompletionProposal[0];
   }

   private JavaCompletionProposal createTypeProposal(int relevance, String fullyQualifiedType,
      JavaContentAssistInvocationContext context)
   {
      IType type = TypeInfoStorage.get().getTypeByFqn(fullyQualifiedType);

      if (type == null)
         return null;

      org.exoplatform.ide.java.client.core.CompletionProposal proposal =
         org.exoplatform.ide.java.client.core.CompletionProposal.create(
            org.exoplatform.ide.java.client.core.CompletionProposal.TYPE_REF, context.getInvocationOffset());
      proposal.setCompletion(fullyQualifiedType.toCharArray());
      proposal.setDeclarationSignature(Signature.getQualifier(type.getFullyQualifiedName().toCharArray()));
      proposal.setFlags(type.getFlags());
      proposal.setRelevance(relevance);
      proposal.setReplaceRange(context.getInvocationOffset(), context.getInvocationOffset());
      proposal.setSignature(Signature.createTypeSignature(fullyQualifiedType, true).toCharArray());

      return new LazyGenericTypeProposal(proposal, context);

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public char[] getCompletionProposalAutoActivationCharacters()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getErrorMessage()
   {
      return null;
   }

}
