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
package org.exoplatform.ide.extension.chromattic.client.ui;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.module.EditorNotFoundException;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.extension.chromattic.client.ChromatticClientBundle;
import org.exoplatform.ide.extension.chromattic.client.ChromatticExtension;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

/**
 * View for preview the generated node type definition.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 17, 2010 $
 * 
 */
public class GeneratedNodeTypePreviewForm extends ViewImpl implements GeneratedNodeTypePreviewPresenter.Display
{

   static final String ID = "ideGeneratedTypePreviewView";

   /**
    * Editor to display the content of node type definition.
    */
   private org.exoplatform.ide.editor.api.Editor editor;

   /**
    * @param eventBus handler manager
    */
   public GeneratedNodeTypePreviewForm()
   {
      super(ID, "operation", ChromatticExtension.LOCALIZATION_CONSTANT.generateNodeTypePreviewFormTitle(), new Image(
         ChromatticClientBundle.INSTANCE.previewNodeTypeControl()));

      try
      {
         createEditor();
      }
      catch (EditorNotFoundException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e.getMessage()));
      }
   }

   /**
    * Create editor to display the content of node type definition.
    * @throws EditorNotFoundException 
    */
   private void createEditor() throws EditorNotFoundException
   {
      final Editor editor = IDE.getInstance().getFileTypeRegistry().getEditor(MimeType.APPLICATION_XML);
      editor.setReadOnly(true);
      editor.showLineNumbers(true);
      add(editor);

      new Timer()
      {

         @Override
         public void run()
         {
            Element editorWraper = Document.get().getElementById(editor.getId());

            NodeList<Element> iframes = editorWraper.getElementsByTagName("iframe");
            if (iframes != null && iframes.getLength() > 0)
            {

               Element iFrameElement = iframes.getItem(0);
               setHandler(iFrameElement);
            }
         }
      }.schedule(2000);

   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.GeneratedNodeTypePreviewPresenter.Display#setContent()
    */
   @Override
   public void setContent(String content)
   {
      editor.setText(content);
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.GeneratedNodeTypePreviewPresenter.Display#getEditor()
    */
   @Override
   public Editor getEditor()
   {
      return editor;
   }

   private native void setHandler(Element e)
   /*-{
      var type = "mousedown";
      var instance = this;     

      if(typeof e.contentDocument != "undefined")
      {
         e.contentDocument.addEventListener(
            type,
            function()
            {
               instance.@org.exoplatform.ide.extension.chromattic.client.ui.GeneratedNodeTypePreviewForm::activate()();
            },
            false);
      }
      else
      {
         e.contentWindow.document.attachEvent(
            "on" + type,
            function()
            {
               instance.@org.exoplatform.ide.extension.chromattic.client.ui.GeneratedNodeTypePreviewForm::activate()();
            });
      }
   }-*/;

}
