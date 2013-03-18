/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.editor.python.client;

import com.google.collide.client.code.autocomplete.AutocompleteProposals;
import com.google.collide.client.code.autocomplete.AutocompleteProposals.ProposalWithContext;
import com.google.collide.client.code.autocomplete.AutocompleteResult;
import com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter;
import com.google.collide.client.code.autocomplete.SignalEventEssence;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.codemirror2.SyntaxType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class PyAutocompliter extends LanguageSpecificAutocompleter
{

   private static final PyExplicitAutocompleter EXPLICIT_AUTOCOMPLETER = new PyExplicitAutocompleter();

   protected PyAutocompliter()
   {
      super(SyntaxType.PY);
   }

   @Override
   protected ExplicitAction getExplicitAction(SelectionModel selectionModel, SignalEventEssence signal,
      boolean popupIsShown)
   {
      return EXPLICIT_AUTOCOMPLETER.getExplicitAction(selectionModel, signal, popupIsShown, getParser());
   }

   @Override
   public AutocompleteResult computeAutocompletionResult(ProposalWithContext proposal)
   {
      return null;
   }

   @Override
   public AutocompleteProposals findAutocompletions(SelectionModel selection, SignalEventEssence trigger)
   {
      return null;
   }

   @Override
   public void cleanup()
   {
   }
}
