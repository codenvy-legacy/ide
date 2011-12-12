/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.heroku.client.stack;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.shared.Stack;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;

/**
 * Grid for displaying application's stacks.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 29, 2011 11:33:38 AM anya $
 *
 */
public class StackGrid extends ListGrid<Stack>
{
   private final String ID = "ideStackGrid";

   private final String STACK = HerokuExtension.LOCALIZATION_CONSTANT.changeStackViewStackField();

   private final String BETA = HerokuExtension.LOCALIZATION_CONSTANT.changeStackViewBetaField();

   public StackGrid()
   {
      super();

      setID(ID);

      Column<Stack, SafeHtml> nameColumn = new Column<Stack, SafeHtml>(new SafeHtmlCell())
      {

         @Override
         public SafeHtml getValue(final Stack stack)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  return (stack.isCurrent()) ? "<b>" + stack.getName() + "</b>" : stack.getName();
               }
            };
            return html;
         }
      };

      Column<Stack, SafeHtml> betaColumn = new Column<Stack, SafeHtml>(new SafeHtmlCell())
      {
         @Override
         public SafeHtml getValue(final Stack stack)
         {
            SafeHtml html = new SafeHtml()
            {
               private static final long serialVersionUID = 1L;

               @Override
               public String asString()
               {
                  if (stack.isBeta())
                    return (stack.isCurrent()) ? "<b>beta</b>" : "beta";
                  return "";
               }
            };
            return html;
         }
      };

      getCellTable().addColumn(nameColumn, STACK);
      getCellTable().setColumnWidth(nameColumn, "70%");
      getCellTable().addColumn(betaColumn, BETA);
      getCellTable().setColumnWidth(betaColumn, "30%");
   }

}
