/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.editor.api.codeassitant.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 19, 2010 4:41:26 PM evgen $
 * 
 */
public class AssistantImportDeclarationPanel extends FlowPanel
{

   private List<TokenWidget> entries = new ArrayList<TokenWidget>();

   /**
    * Add a new child widget to the panel.
    * 
    * @param w the widget to be added
    */
   public void addWidget(TokenWidget w)
   {
      entries.add(w);
      super.add(w);
   }

   /**
    * @see com.google.gwt.user.client.ui.FlowPanel#clear()
    */
   @Override
   public void clear()
   {
      entries.clear();
      super.clear();
   }

   public List<TokenWidget> getEntries()
   {
      return entries;
   }
}
