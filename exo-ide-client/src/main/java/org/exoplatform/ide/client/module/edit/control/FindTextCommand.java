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
package org.exoplatform.ideall.client.module.edit.control;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ideall.client.IDEImageBundle;
import org.exoplatform.ideall.client.framework.control.IDEControl;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.framework.form.FormClosedEvent;
import org.exoplatform.ideall.client.framework.form.FormClosedHandler;
import org.exoplatform.ideall.client.framework.form.FormOpenedEvent;
import org.exoplatform.ideall.client.framework.form.FormOpenedHandler;
import org.exoplatform.ideall.client.module.edit.event.FindTextEvent;
import org.exoplatform.ideall.client.search.Search;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class FindTextCommand extends IDEControl implements EditorActiveFileChangedHandler, FormOpenedHandler, FormClosedHandler
{
   //   public static final String ID = "Edit/Find&#47Replace...";
   public static final String ID = "Edit/Find-Replace...";

   private static final String TITLE = "Find/Replace...";
   
   private List<String> openedForms = new ArrayList<String>();
   
   private boolean findTextFormOpened = false;

   public FindTextCommand(HandlerManager eventBus)
   {
      super(ID, eventBus);
      setTitle(TITLE);
      setPrompt(TITLE);
      setDelimiterBefore(true);
      setImages(IDEImageBundle.INSTANCE.findText(), IDEImageBundle.INSTANCE.findTextDisabled());
      setEvent(new FindTextEvent());
   }

   /**
    * @see org.exoplatform.ideall.client.application.component.IDECommand#onRegisterHandlers()
    */
   @Override
   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
      addHandler(FormOpenedEvent.TYPE, this);
      addHandler(FormClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || event.getEditor() == null)
      {
         setVisible(false);
         setEnabled(false);
         return;
      } else {
         setVisible(true);
      }
      
      boolean canFindReplace = event.getEditor().canFindAndReplace();
      //boolean isOpened = openedForms.contains(FindTextForm.ID); 
      boolean enableSearch = canFindReplace && !findTextFormOpened;
      setEnabled(enableSearch);
   }
   
   public void onFormOpened(FormOpenedEvent event)
   {
      if (Search.FORM_ID.equals(event.getFormId())) {
         findTextFormOpened = true;
         setEnabled(false);
      }
   }

   public void onFormClosed(FormClosedEvent event)
   {
      if (Search.FORM_ID.equals(event.getFormId())) {
         findTextFormOpened = false;
         setEnabled(true);
      }
   }
   
}
