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
package org.exoplatform.ide.client.module.chromattic.ui;

import com.google.gwt.dom.client.IFrameElement;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Timer;

import com.google.gwt.user.client.ui.Image;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorConfiguration;
import org.exoplatform.gwtframework.editor.api.EditorFactory;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.gwtframework.editor.api.GWTTextEditor;
import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.gwtframework.ui.client.smartgwteditor.SmartGWTTextEditor;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.ui.ViewType;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent;
import org.exoplatform.ide.client.module.chromattic.Images;


/**
 * View for preview the generated node type definition.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 17, 2010 $
 *
 */
public class GeneratedNodeTypePreviewForm extends View implements GeneratedNodeTypePreviewPresenter.Display
{
   public static final String ID = "ideGeneratedTypePreviewPanel";

   public static final String FORM_ID = "ideGeneratedTypePreviewForm";

   public static final String TITLE = "Node Type Preview";

   /**
    * Handlers manager.
    */
   private HandlerManager eventBus;

   /**
    * Editor to display the content of node type definition.
    */
   private SmartGWTTextEditor smartGWTTextEditor;

   /**
    * @param eventBus handler manager
    */
   public GeneratedNodeTypePreviewForm(HandlerManager eventBus)
   {
      super(ID, eventBus);
      this.eventBus = eventBus;

      setID(FORM_ID);
      setType(ViewType.PREVIEW);
      setTitle(TITLE);
      setImage(new Image(Images.Controls.PREVIEW_NODE_TYPE));

      createEditor();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.View#onDestroy()
    */
   @Override
   protected void onDestroy()
   {
      eventBus.fireEvent(new ViewClosedEvent(ID));
      super.onDestroy();
   }

   /**
    * Create editor to display the content of node type definition.
    */
   private void createEditor()
   {
      Editor editor = null;
      try
      {
         editor = EditorFactory.getDefaultEditor(MimeType.APPLICATION_XML);
      }
      catch (EditorNotFoundException e)
      {
         e.printStackTrace();
      }

      EditorConfiguration configuration = new EditorConfiguration(MimeType.APPLICATION_XML);
      configuration.setLineNumbers(true);
      configuration.setReadOnly(true);

      GWTTextEditor textEditor = editor.createTextEditor(eventBus, configuration);
      smartGWTTextEditor = new SmartGWTTextEditor(eventBus, textEditor);
      addMember(smartGWTTextEditor);

      new Timer()
      {

         @Override
         public void run()
         {
            Element editorWraper =
               Document.get().getElementById(smartGWTTextEditor.getTextEditor().getEditorWrapperID());

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
    * @see org.exoplatform.ide.client.module.chromattic.ui.GeneratedNodeTypePreviewPresenter.Display#closeView()
    */
   @Override
   public void closeView()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.GeneratedNodeTypePreviewPresenter.Display#setContent()
    */
   @Override
   public void setContent(String content)
   {
      smartGWTTextEditor.setText(content);
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.GeneratedNodeTypePreviewPresenter.Display#getEditor()
    */
   @Override
   public TextEditor getEditor()
   {
      return smartGWTTextEditor;
   }

   private native void setHandler(Element e)/*-{
      var type = "mousedown";
      var instance = this;     

      if(typeof e.contentDocument != "undefined")
      {
      e.contentDocument.addEventListener(type,function(){instance.@org.exoplatform.ide.client.module.chromattic.ui.GeneratedNodeTypePreviewForm::activateView()();},false);
      }
      else
      {
      e.contentWindow.document.attachEvent("on" + type,function(){instance.@org.exoplatform.ide.client.module.chromattic.ui.GeneratedNodeTypePreviewForm::activateView()();});
      }
   }-*/;

   /**
    * Get editor's iframe content.
    * 
    * @param iframe
    * @return {@link Document}
    */
   private native Document getIFrameDocument(IFrameElement iframe)/*-{
      return iframe.contentDocument || iframe.contentWindow.document;
   }-*/;

}
