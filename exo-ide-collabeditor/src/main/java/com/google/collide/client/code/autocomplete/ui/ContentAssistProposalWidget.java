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
package com.google.collide.client.code.autocomplete.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;

import org.exoplatform.ide.editor.api.contentassist.CompletionProposal;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: AutocompleteProposalWidget.java Sep 11, 2012 3:17:38 PM azatsarynnyy $
 *
 */
public class ContentAssistProposalWidget extends Composite
{
   private CompletionProposal completionProposal;

   private Grid grid;

   public ContentAssistProposalWidget(CompletionProposal completionProposal)
   {
      this.completionProposal = completionProposal;
      grid = new Grid(1, 2);
      grid.setWidget(0, 0, completionProposal.getImage());
      initWidget(grid);
   }

}
