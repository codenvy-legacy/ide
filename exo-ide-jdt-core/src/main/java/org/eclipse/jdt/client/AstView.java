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
package org.eclipse.jdt.client;

import com.google.gwt.user.client.ui.Label;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.ScrollPanel;

import org.eclipse.jdt.client.AstPesenter.Display;
import org.eclipse.jdt.client.astview.ASTTreeViewModel;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Jan 20, 2012 1:33:45 PM evgen $
 *
 */
public class AstView extends ViewImpl implements Display
{

   
   private ScrollPanel scrollPanel;
   /**
    * 
    */
   public AstView()
   {
      super(id, ViewType.INFORMATION, "AST");
      scrollPanel = new ScrollPanel(new Label("Parsing File..."));
      add(scrollPanel);
   }
   /**
    * @see org.eclipse.jdt.client.AstPesenter.Display#drawAst(org.eclipse.jdt.client.core.dom.CompilationUnit)
    */
   @Override
   public void drawAst(CompilationUnit cUnit)
   {
      CellTree.Resources res = GWT.create(CellTree.BasicResources.class);
      CellTree cellTree = new CellTree(new ASTTreeViewModel(cUnit), null, res);
      scrollPanel.clear();
      scrollPanel.add(cellTree);
   }

}
