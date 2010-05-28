/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.autocompletion;

import java.util.LinkedHashMap;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledEvent;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteCalledHandler;
import org.exoplatform.gwtframework.editor.event.EditorAutoCompleteEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class JavaScriptAutoCompletionHandler implements EditorAutoCompleteCalledHandler
{
   
   private Handlers handlers;
   private HandlerManager eventBus;
   private String editorId;
   private String context;
   DynamicForm form;
   
   public JavaScriptAutoCompletionHandler(HandlerManager eventBus, ApplicationContext context) {
      this.eventBus = eventBus;
      
      handlers = new Handlers(eventBus);
      handlers.addHandler(EditorAutoCompleteCalledEvent.TYPE, this);      
   }

   public void onEditorAutoCompleteCalled(final EditorAutoCompleteCalledEvent event)
   {
      this.editorId = event.getEditorId();
      this.context = event.getContext();
      
      if (! event.getMimeType().equals(MimeType.APPLICATION_JAVASCRIPT)
           && ! event.getMimeType().equals(MimeType.TEXT_JAVASCRIPT)
           && ! event.getMimeType().equals(MimeType.APPLICATION_X_JAVASCRIPT)) 
         return;
      
      int cursorOffsetX = event.getCursorOffsetX();
      int cursorOffsetY = event.getCursorOffsetY();
     
      // create and draw completion list
      form = new DynamicForm();  
      form.setWidth(500);
      form.setTop(cursorOffsetY);
      form.setLeft(cursorOffsetX);      
      SelectItem autoCompleteList = new SelectItem();
      autoCompleteList.setShowTitle(false);
      LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
      valueMap.put("function ()\n{\n}", "insert FUNCTION-block");
      valueMap.put("for ()\n{\n}", "insert FOR-block");      
      valueMap.put("if ()\n{\n} else {\n}", "insert IF-ELSE-block");      
      autoCompleteList.setValueMap(valueMap);

      autoCompleteList.addChangedHandler(new ChangedHandler() {  
         public void onChanged(ChangedEvent event)
         {
            onCompletionSelected((String) event.getValue());
         }
      });    
      
      form.setItems(autoCompleteList);
      form.draw();      
   }

   protected void onCompletionSelected(String completion)
   {
      form.destroy();
      
      this.eventBus.fireEvent(new EditorAutoCompleteEvent(
            this.editorId, 
            completion
         )
      );
   }

}
