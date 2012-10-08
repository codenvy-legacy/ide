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
package org.exoplatform.ide.texteditor.api.codeassistant;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.Region;

/**
 *  The interface of completion proposals generated by content assist processors. A completion proposal contains information used
 * to present the proposed completion to the user, to insert the completion should the user select it, and to present context
 * information for the chosen completion once it has been inserted.
 * <p>
 * This interface can be implemented by clients
 * </p>
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface CompletionProposal
{
   /**
    * Inserts the proposed completion into the given document.
    * 
    * @param document the document into which to insert the proposed completion
    */
   void apply(Document document);

   /**
    * Returns the new selection after the proposal has been applied to the given document in absolute document coordinates. If it
    * returns <code>null</code>, no new selection is set.
    * 
    * A document change can trigger other document changes, which have to be taken into account when calculating the new
    * selection. Typically, this would be done by installing a document listener or by using a document position during
    * {@link #apply(Document)}.
    * 
    * @param document the document into which the proposed completion has been inserted
    * @return the new selection in absolute document coordinates
    */
   Region getSelection(Document document);

   /**
    * Returns optional additional information about the proposal. The additional information will be presented to assist the user
    * in deciding if the selected proposal is the desired choice.
    * 
    * @return the additional information or <code>null</code>
    */
   Widget getAdditionalProposalInfo();

   /**
    * Returns the string to be displayed in the list of completion proposals.
    * 
    * @return the string to be displayed
    * 
    * @see ICompletionProposalExtension6#getStyledDisplayString()
    */
   String getDisplayString();

   /**
    * Returns the image to be displayed in the list of completion proposals. The image would typically be shown to the left of the
    * display string.
    * 
    * @return the image to be shown or <code>null</code> if no image is desired
    */
   Image getImage();

   /**
    * Applies the proposed completion to the given document. The insertion has been triggered by entering the given character at
    * the given offset. This method assumes that {@link #isValidFor(Document, int)} returns <code>true</code> if called for
    * <code>offset</code>.
    * 
    * @param document the document into which to insert the proposed completion
    * @param trigger the trigger to apply the completion
    * @param offset the offset at which the trigger has been activated
    */
   void apply(Document document, char trigger, int offset);

   /**
    * Returns whether this completion proposal is valid for the given position in the given document.
    * 
    * @param document the document for which the proposal is tested
    * @param offset the offset for which the proposal is tested
    * @return <code>true</code> iff valid
    */
   boolean isValidFor(Document document, int offset);

   /**
    * Returns the characters which trigger the application of this completion proposal.
    * 
    * @return the completion characters for this completion proposal or <code>null</code> if no completion other than the new line
    *         character is possible
    */
   char[] getTriggerCharacters();

   /**
    * Returns <code>true</code> if the proposal may be automatically inserted, <code>false</code> otherwise. Automatic insertion
    * can happen if the proposal is the only one being proposed, in which case the content assistant may decide to not prompt the
    * user with a list of proposals, but simply insert the single proposal. A proposal may veto this behavior by returning
    * <code>false</code> to a call to this method.
    * 
    * @return <code>true</code> if the proposal may be inserted automatically, <code>false</code> if not
    */
   boolean isAutoInsertable();
}
