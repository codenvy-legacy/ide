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
package org.eclipse.jdt.client.outline;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.exoplatform.gwtframework.ui.client.CellTreeResource;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * View for Java Outline tree.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 4:29:29 PM anya $
 * 
 */
public class OutlineView extends ViewImpl implements OutlinePresenter.Display
{
   /**
    * Scroll panel, which contains tree.
    */
   private ScrollPanel scrollPanel;

   private CellTree.Resources res = GWT.create(CellTreeResource.class);

   private CellTree cellTree;

   public OutlineView()
   {
      super("OutlineViewId", ViewType.INFORMATION, "Java Outline");
      scrollPanel = new ScrollPanel(new Label("Parsing File..."));
      add(scrollPanel);
   }

   /**
    * @see org.eclipse.jdt.client.outline.OutlinePresenter.Display#updateOutline(org.eclipse.jdt.client.core.dom.CompilationUnit)
    */
   @Override
   public void updateOutline(CompilationUnit cUnit)
   {
      try
      {
         cellTree = new CellTree(new OutlineTreeViewModel(cUnit), null, res);
         cellTree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
         scrollPanel.clear();
         scrollPanel.add(cellTree);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
