/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.editor;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.editor.event.EditorActivityEvent;
import org.exoplatform.gwtframework.editor.event.EditorActivityHandler;
import org.exoplatform.gwtframework.ui.client.smartgwteditor.SmartGWTTextEditor;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.Utils;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.ui.ViewHighlightManager;
import org.exoplatform.ide.client.framework.vfs.File;

import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.tab.Tab;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorTab extends Tab implements EditorActivityHandler
{

   private View viewPane;

   private SmartGWTTextEditor textEditor;

   private File file;

   private boolean readOnly = false;

   private BodyElement body;

   private static int ID;

   private Handlers handlers;

   public EditorTab(File file, HandlerManager eventBus)
   {
      this.file = file;

      handlers = new Handlers(eventBus);
      handlers.addHandler(EditorActivityEvent.TYPE, this);

      setTitle(getTabTitle());
      viewPane = new View("ideEditorTab-" + ID++, eventBus);
      setPane(viewPane);
   }

   public void showReadOnlyStatus()
   {
      readOnly = true;
      setTitle(getTabTitle());
   }

   public void hideReadOnlyStatus()
   {
      readOnly = false;
      setTitle(getTabTitle());
   }

   public SmartGWTTextEditor getTextEditor()
   {
      return textEditor;
   }

   //   private List<Element> getChildren(Element element)
   //   {
   //      List<Element> elements = new ArrayList<Element>();
   //
   //      for (int i = 0; i < DOM.getChildCount(element); i++)
   //      {
   //         Element child = DOM.getChild(element, i);
   //         elements.add(child);
   //      }
   //
   //      return elements;
   //   }
   //   
   //   private Element getIFrameElement(Element element) {
   //      for (Element child1 : getChildren(element))
   //      {
   //         if (child1.getAttribute("class") != null && "CodeMirror-wrapping".equals(child1.getAttribute("class")))
   //         {
   //            System.out.println("codemirror > " + child1);
   //
   //            for (Element child2 : getChildren(child1))
   //            {
   //               if ("iframe".equalsIgnoreCase(child2.getNodeName()))
   //               {
   //                  return child2;
   //                  
   ////                  IFrameElement iframe = IFrameElement.as(child2);
   ////                  Document doc = getIFrameDocument(iframe);
   ////                  BodyElement body = doc.getBody();
   ////                  setHandler(body);
   //               }
   //
   //            }
   //
   //         }
   //      }
   //
   //      return null;
   //   }

   public void setTextEditor(final SmartGWTTextEditor textEditor)
   {
      if (this.textEditor != null)
      {
         viewPane.removeMember(this.textEditor);
      }

      this.textEditor = textEditor;

      viewPane.addMember(textEditor);

      //      new Timer()
      //      {
      //
      //         @Override
      //         public void run()
      //         {
      //
      //            Element editorWraper = Document.get().getElementById(textEditor.getTextEditor().getEditorWrapperID());
      //
      //            NodeList<Element> iframes = editorWraper.getElementsByTagName("iframe");
      //            if (iframes != null && iframes.getLength() > 0)
      //            {
      //
      //               Element iFrameElement = iframes.getItem(0);
      //               Document doc = getIFrameDocument(IFrameElement.as(iFrameElement));
      //               body = doc.getBody();
      //               setHandler(body);
      //            }
      //         }
      //      }.schedule(1000);

   }

   public File getFile()
   {
      return file;
   }

   public void setFile(File file)
   {
      this.file = file;
   }

   public String getTabTitle()
   {
      boolean fileChanged = file.isContentChanged() || file.isPropertiesChanged();

      String fileName = Utils.unescape(fileChanged ? file.getName() + "&nbsp;*" : file.getName());

      String mainHint = file.getHref();

      String readonlyImage =
         (readOnly)
            ? "<img id=\"fileReadonly\"  style=\"margin-left:-4px; margin-bottom: -4px;\" border=\"0\" suppress=\"true\" src=\""
               + Images.Editor.READONLY_FILE + "\" />" : "";

      mainHint = (readOnly) ? "File opened in read only mode. Use SaveAs command." : mainHint;
      String title =
         "<span title=\"" + mainHint + "\">" + Canvas.imgHTML(file.getIcon()) + readonlyImage + "&nbsp;" + fileName
            + "</span>";

      return title;
   }

   private native void setHandler(Element e)/*-{
      var type = "mousedown";
      var instance = this;     
      if(typeof e.addEventListener != "undefined")
      {
      e.addEventListener(type,function(){instance.@org.exoplatform.ide.client.editor.EditorTab::onMouseDown()();},false);
      }
      else
      {
      e.attachEvent("on" + type,function(){instance.@org.exoplatform.ide.client.editor.EditorTab::onMouseDown()();});
      }
   }-*/;

   private native Document getIFrameDocument(IFrameElement iframe)/*-{
      return iframe.contentDocument || iframe.contentWindow.document;
   }-*/;

   private void onMouseDown()
   {
      ViewHighlightManager.getInstance().selectView(viewPane);
   }

   /**
    * @see org.exoplatform.gwtframework.editor.event.EditorActivityHandler#onEditorActivity(org.exoplatform.gwtframework.editor.event.EditorActivityEvent)
    */
   public void onEditorActivity(EditorActivityEvent event)
   {
      if (textEditor.getEditorId().equals(event.getEditorId()))
      {
         onMouseDown();
      }
   }

}
