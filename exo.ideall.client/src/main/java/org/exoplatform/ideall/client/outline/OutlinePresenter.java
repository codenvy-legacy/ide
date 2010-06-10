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
package org.exoplatform.ideall.client.outline;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ideall.client.editor.event.EditorGoToLineEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.outline.event.RefreshOutlineEvent;
import org.exoplatform.ideall.client.outline.event.RefreshFunctionsHandler;
import org.exoplatform.ideall.client.util.SimpleParser;
import org.exoplatform.ideall.client.util.Token;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OutlinePresenter implements RefreshFunctionsHandler
{
   interface Display
   {
      OutlineTreeGrid<Token> getBrowserTree();
      
      void selectTreeItem(int number);
   }
   
   private HandlerManager eventBus;
   
   private ApplicationContext context;
   
   private Handlers handlers;
   
   private Display display;
   
   private Map<String, Integer> functions = new LinkedHashMap<String, Integer>();
   
   public OutlinePresenter(HandlerManager bus, ApplicationContext applicationContext)
   {
      eventBus = bus;
      context = applicationContext;
      
      handlers = new Handlers(eventBus);
      
      handlers.addHandler(RefreshOutlineEvent.TYPE, this);
      
   }
   
   public void bindDisplay(Display d)
   {
      display = d;
      
      display.getBrowserTree().addSelectionHandler(new SelectionHandler<Token>()
      {
         public void onSelection(SelectionEvent<Token> event)
         {
            String name = event.getSelectedItem().getName();
            if (functions.containsKey(name))
            {
               int line = functions.get(name);
               int maxLineNumber = context.getActiveFile().getContent().split("\n").length;
               eventBus.fireEvent(new EditorGoToLineEvent(
                  line < maxLineNumber ? line : maxLineNumber));
            }
         }
      });
   }
   
   public void onRefreshFunctions(RefreshOutlineEvent event)
   {
      refreshFunctions();
   }
   
   private void refreshFunctions()
   {
      if (context.getActiveFile() == null || context.getActiveFile().getContent() == null)
      {
         return;
      }
      
      functions.clear();
      
      
      String text = context.getActiveFile().getContent();
      
      Token rootItem = new Token("", null, -1);
      
      List<Token> tokens = SimpleParser.parse(text);
      
      for (Token token : tokens)
      {
         rootItem.getTokens().add(token);
      }
      
//      Iterator<Entry<String, Integer>> it = functions.entrySet().iterator();
//      while (it.hasNext())
//      {
//         Entry<String, Integer> entry = it.next();
//      }
      
      display.getBrowserTree().setValue(rootItem);
   }

}
