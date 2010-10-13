/**
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
 *
 */

package org.exoplatform.ide.client.versioning;


import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorConfiguration;
import org.exoplatform.gwtframework.editor.api.EditorFactory;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.gwtframework.editor.api.GWTTextEditor;
import org.exoplatform.gwtframework.ui.client.smartgwteditor.SmartGWTTextEditor;
import org.exoplatform.ide.client.module.vfs.api.Version;
import org.exoplatform.ide.client.panel.SimpleTabPanel;
import org.exoplatform.ide.client.panel.event.PanelOpenedEvent;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class VersionContentForm extends SimpleTabPanel implements VersionContentPresenter.Display
{
   public static final String ID = "ideVersionContentForm";

   private HandlerManager eventBus;

   private SmartGWTTextEditor smartGWTTextEditor;
   
   private VersionContentPresenter presenter;

   public VersionContentForm(HandlerManager eventBus)
   {
      super(ID);

      this.eventBus = eventBus;
      presenter = new VersionContentPresenter(eventBus);
      presenter.bindDisplay(this);
   }

   /**
    * @see com.smartgwt.client.widgets.BaseWidget#onDraw()
    */
   @Override
   protected void onDraw()
   {
      eventBus.fireEvent(new PanelOpenedEvent(ID));   
      super.onDraw();
   }

   /**
    * @see com.smartgwt.client.widgets.BaseWidget#onDestroy()
    */
   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }

   /**
    * @see org.exoplatform.ide.client.versioning.VersionContentPresenter.Display#showVersion(org.exoplatform.ide.client.module.vfs.api.Version)
    */
   public void showVersion(Version version)
   {
      if (smartGWTTextEditor != null){
         removeMember(smartGWTTextEditor);
      }
      
      Editor editor = null;
      try
      {
         editor = EditorFactory.getDefaultEditor(version.getContentType());
      }
      catch (EditorNotFoundException e)
      {
         e.printStackTrace();
      }

      EditorConfiguration configuration = new EditorConfiguration(version.getContentType());
      configuration.setLineNumbers(true);
      configuration.setReadOnly(true);

      GWTTextEditor textEditor = editor.createTextEditor(eventBus, configuration);
      smartGWTTextEditor = new SmartGWTTextEditor(eventBus, textEditor);
      addMember(smartGWTTextEditor);
   }

   /**
    * @see org.exoplatform.ide.client.versioning.VersionContentPresenter.Display#getEditorId()
    */
   public String getEditorId()
   {
      return smartGWTTextEditor.getEditorId();
   }

   /**
    * @see org.exoplatform.ide.client.versioning.VersionContentPresenter.Display#setVersionContent(java.lang.String)
    */
   public void setVersionContent(String content)
   {
      try
      {
         smartGWTTextEditor.setText(content);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @see org.exoplatform.ide.client.versioning.VersionContentPresenter.Display#closeForm()
    */
   public void closeForm()
   {
      destroy();
   }
}
