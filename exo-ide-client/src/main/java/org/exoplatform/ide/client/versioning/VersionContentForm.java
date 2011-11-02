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
package org.exoplatform.ide.client.versioning;

import java.util.ArrayList;
import java.util.HashMap;

import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.editor.api.EditorParameters;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Timer;

/**
 * 
 * Form with version content, that displayed in Version Tab.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class VersionContentForm extends ViewImpl implements VersionContentPresenter.Display
{

   private static final String ID = "ideVersionContentView";
   
   private org.exoplatform.ide.editor.api.Editor editor;

   private VersionContentPresenter presenter;

   public VersionContentForm(Version version)
   {
      super(ID, "information", "");
      createEditor(version);
      presenter = new VersionContentPresenter();
      presenter.bindDisplay(this);
   }

   private void createEditor(Version version)
   {
      if (editor != null)
      {
         remove(editor);
      }
      
      final HashMap<String, Object> params = new HashMap<String, Object>();
      params.put(EditorParameters.IS_READ_ONLY, true);
      params.put(EditorParameters.IS_SHOW_LINE_NUMER, true);
      params.put(EditorParameters.HOT_KEY_LIST, new ArrayList<String>());

      try
      {
         editor = IDE.getInstance().getEditor(version.getContentType()).createEditor(version.getContent(), IDE.eventBus(), params);
      }
      catch (EditorNotFoundException e)
      {
         e.printStackTrace();
      }
      
      add(editor);

      new Timer()
      {

         @Override
         public void run()
         {
            Element editorWraper =
               Document.get().getElementById(editor.getEditorId());

            NodeList<Element> iframes = editorWraper.getElementsByTagName("iframe");
            if (iframes != null && iframes.getLength() > 0)
            {
               Element iFrameElement = iframes.getItem(0);
               setHandler(iFrameElement);
            }
         }
      }.schedule(1000);
   }

   /**
    * @see org.exoplatform.ide.client.versioning.VersionContentPresenter.Display#getEditorId()
    */
   public String getEditorId()
   {
      return editor.getEditorId();
   }

   /**
    * @see org.exoplatform.ide.client.versioning.VersionContentPresenter.Display#setVersionContent(java.lang.String)
    */
   public void setVersionContent(String content)
   {
      try
      {
         editor.setText(content);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private native void setHandler(Element e)/*-{
      var type = "mousedown";
      var instance = this;     

       if(typeof e.contentDocument != "undefined")
      {
         e.contentDocument.addEventListener(type,function(){instance.@org.exoplatform.ide.client.versioning.VersionContentForm::activate()();},false);
      }
      else
      {
         e.contentWindow.document.attachEvent("on" + type,function(){instance.@org.exoplatform.ide.client.versioning.VersionContentForm::activate()();});
      }

   }-*/;

   private native Document getIFrameDocument(IFrameElement iframe)/*-{
      return iframe.contentDocument || iframe.contentWindow.document;
   }-*/;
   
}
