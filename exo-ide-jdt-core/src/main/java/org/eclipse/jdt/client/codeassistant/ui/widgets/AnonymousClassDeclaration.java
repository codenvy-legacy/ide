package org.eclipse.jdt.client.codeassistant.ui.widgets;

import com.google.gwt.resources.client.ImageResource;

import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.codeassistant.ui.ProposalWidget;
import org.eclipse.jdt.client.core.CompletionProposal;
import org.eclipse.jdt.client.text.BadLocationException;
import org.eclipse.jdt.client.text.IDocument;
import org.eclipse.jdt.client.text.edits.InsertEdit;
import org.eclipse.jdt.client.text.edits.MalformedTreeException;
import org.eclipse.jdt.client.text.edits.TextEdit;

/**
 * Completion is a declaration of an anonymous class. This kind of completion might occur in a context like
 * <code>"new List(^;"</code> and complete it to <code>"new List() {}"</code>.
 * <p>
 * The following additional context information is available for this kind of completion proposal at little extra cost:
 * <ul>
 * <li>{@link #getDeclarationSignature()} - the type signature of the type being implemented or subclassed</li>
 * <li>{@link #getDeclarationKey()} - the type unique key of the type being implemented or subclassed</li>
 * <li>{@link #getSignature()} - the method signature of the constructor that is referenced</li>
 * <li>{@link #getKey()} - the method unique key of the constructor that is referenced if the declaring type is not an interface</li>
 * <li>{@link #getFlags()} - the modifiers flags of the constructor that is referenced</li>
 * </ul>
 * </p>
 */
// TODO
public class AnonymousClassDeclaration extends ProposalWidget
{

   public AnonymousClassDeclaration(CompletionProposal proposal)
   {
      super(proposal);

   }

   @Override
   public String getName()
   {
      return String.valueOf(proposal.getName());
   }

   @Override
   public Widget getDecription()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /** @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getImage(int) */
   @Override
   protected ImageResource getImage(int flags)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /** @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getTypeSignature() */
   @Override
   protected String getTypeSignature()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /** @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#getClassSignature() */
   @Override
   protected String getClassSignature()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @throws BadLocationException 
    * @throws MalformedTreeException 
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalWidget#apply(org.eclipse.jdt.client.text.IDocument)
    */
   @Override
   public void apply(IDocument document)
   {
      TextEdit edit = new InsertEdit(proposal.getReplaceStart(), String.valueOf(proposal.getCompletion()));
      try
      {
         edit.apply(document);
      }
      catch (MalformedTreeException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (BadLocationException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
}
