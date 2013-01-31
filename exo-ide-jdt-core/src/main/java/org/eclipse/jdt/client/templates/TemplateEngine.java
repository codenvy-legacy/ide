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
package org.eclipse.jdt.client.templates;

import com.google.gwt.user.client.ui.Image;

import org.eclipse.jdt.client.JdtExtension;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.templates.api.GlobalTemplateVariables;
import org.eclipse.jdt.client.templates.api.Template;
import org.eclipse.jdt.client.templates.api.TemplateContextType;
import org.exoplatform.ide.editor.client.api.contentassist.Point;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;
import org.exoplatform.ide.editor.shared.runtime.Assert;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.editor.shared.text.Position;
import org.exoplatform.ide.editor.shared.text.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TemplateEngine
{

   private static final String $_LINE_SELECTION = "${" + GlobalTemplateVariables.LineSelection.NAME + "}"; //$NON-NLS-1$ //$NON-NLS-2$

   private static final String $_WORD_SELECTION = "${" + GlobalTemplateVariables.WordSelection.NAME + "}"; //$NON-NLS-1$ //$NON-NLS-2$

   /** The context type. */
   private TemplateContextType fContextType;

   /** The result proposals. */
   private ArrayList<TemplateProposal> fProposals = new ArrayList<TemplateProposal>();

   /** Positions created on the key documents to remove in reset. */
   private final Map<IDocument, Position> fPositions = new HashMap<IDocument, Position>();

   /**
    * Creates the template engine for the given <code>contextType</code>.
    * <p>
    * The <code>JavaPlugin.getDefault().getTemplateContextRegistry()</code> defines the supported context types.
    * </p>
    * 
    * @param contextType the context type
    */
   public TemplateEngine(TemplateContextType contextType)
   {
      Assert.isNotNull(contextType);
      fContextType = contextType;
   }

   /**
    * Empties the collector.
    */
   public void reset()
   {
      fProposals.clear();
      for (Iterator<Entry<IDocument, Position>> it = fPositions.entrySet().iterator(); it.hasNext();)
      {
         Entry<IDocument, Position> entry = it.next();
         IDocument doc = entry.getKey();
         Position position = entry.getValue();
         doc.removePosition(position);
      }
      fPositions.clear();
   }

   /**
    * Returns the array of matching templates.
    * 
    * @return the template proposals
    */
   public TemplateProposal[] getResults()
   {
      return fProposals.toArray(new TemplateProposal[fProposals.size()]);
   }

   /**
    * Inspects the context of the compilation unit around <code>completionPosition</code> and feeds the collector with proposals.
    * 
    * @param viewer the text viewer
    * @param completionPosition the context position in the document of the text viewer
    * @param compilationUnit the compilation unit (may be <code>null</code>)
    */
   public void complete(IDocument document, int completionPosition, CompilationUnit compilationUnit)
   {

      if (!(fContextType instanceof CompilationUnitContextType))
         return;

      // TODO
      Point selection = new Point(0, 0);
      Position position = new Position(completionPosition, selection.y);

      // remember selected text
      String selectedText = null;
      if (selection.y != 0)
      {
         try
         {
            selectedText = document.get(selection.x, selection.y);
            document.addPosition(position);
            fPositions.put(document, position);
         }
         catch (BadLocationException e)
         {
         }
      }

      CompilationUnitContext context =
         ((CompilationUnitContextType)fContextType).createContext(document, position, compilationUnit);
      context.setVariable("selection", selectedText); //$NON-NLS-1$
      int start = context.getStart();
      int end = context.getEnd();
      IRegion region = new Region(start, end - start);

      Template[] templates = JdtExtension.get().getTemplateStore().getTemplates();

      if (selection.y == 0)
      {
         for (int i = 0; i != templates.length; i++)
         {
            Template template = templates[i];
            if (context.canEvaluate(template))
            {
               fProposals.add(new TemplateProposal(template, context, region, getImage()));
            }
         }
      }
      else
      {

         if (context.getKey().length() == 0)
            context.setForceEvaluation(true);

         boolean multipleLinesSelected = false;// areMultipleLinesSelected(viewer);

         for (int i = 0; i != templates.length; i++)
         {
            Template template = templates[i];
            if (context.canEvaluate(template)
               && (!multipleLinesSelected && template.getPattern().indexOf($_WORD_SELECTION) != -1 || (multipleLinesSelected && template
                  .getPattern().indexOf($_LINE_SELECTION) != -1)))
            {
               fProposals.add(new TemplateProposal(templates[i], context, region, getImage()));
            }
         }
      }
   }

   private Image getImage()
   {
      return new Image(JavaClientBundle.INSTANCE.template());
   }

   // /**
   // * Returns <code>true</code> if one line is completely selected or if multiple lines are selected.
   // * Being completely selected means that all characters except the new line characters are
   // * selected.
   // *
   // * @param viewer the text viewer
   // * @return <code>true</code> if one or multiple lines are selected
   // * @since 2.1
   // */
   // private boolean areMultipleLinesSelected(ITextViewer viewer) {
   // if (viewer == null)
   // return false;
   //
   // Point s= viewer.getSelectedRange();
   // if (s.y == 0)
   // return false;
   //
   // try {
   //
   // IDocument document= viewer.getDocument();
   // int startLine= document.getLineOfOffset(s.x);
   // int endLine= document.getLineOfOffset(s.x + s.y);
   // IRegion line= document.getLineInformation(startLine);
   // return startLine != endLine || (s.x == line.getOffset() && s.y == line.getLength());
   //
   // } catch (BadLocationException x) {
   // return false;
   // }
   // }
}
