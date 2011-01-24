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
package org.exoplatform.ide.client.documentation;

import org.exoplatform.gwtframework.ui.client.toolbar.Toolbar;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: DocumentationForm Jan 21, 2011 11:48:17 AM evgen $
 *
 */
public class DocumentationBrowser extends Composite
{
   private VerticalPanel basePanel;

   private Frame contentPanel;
   
   private Toolbar toolBar;

   public DocumentationBrowser()
   {
      basePanel = new VerticalPanel();
      toolBar = new Toolbar();
      toolBar.setHeight("28px;");
 
//      IconButton back =
//         new IconButton(ImageHelper.getImageHTML(IDEImageBundle.INSTANCE.navigateBack()),
//            ImageHelper.getImageHTML(IDEImageBundle.INSTANCE.navigateBack_Disabled()));
//      
//      back.setTitle("Back");
//      
//      
//      
//      
//      IconButton forward =
//         new IconButton(ImageHelper.getImageHTML(IDEImageBundle.INSTANCE.navigateForward()),
//            ImageHelper.getImageHTML(IDEImageBundle.INSTANCE.navigateForward_Disabled()));
//      forward.setTitle("Forward");
//      
//      IconButton refresh = new IconButton(ImageHelper.getImageHTML(IDEImageBundle.INSTANCE.navigateRefresh()),
//         ImageHelper.getImageHTML(IDEImageBundle.INSTANCE.navigateRrefresh_Disabled()));
//
//      IconButton home = new IconButton(ImageHelper.getImageHTML(IDEImageBundle.INSTANCE.navigateHome()),
//         ImageHelper.getImageHTML(IDEImageBundle.INSTANCE.navigateHome_Disabled()));

      
//      toolBar.addDelimiter();
//      toolBar.addItem(back);
//      toolBar.addItem(forward);
//      toolBar.addItem(refresh);
//      toolBar.addItem(home);
//
//      contentPanel = new Frame();
//      DOM.setElementAttribute(contentPanel.getElement(), "scrolling", "no");
//      DOM.setElementAttribute(contentPanel.getElement(), "frameborder", "0");
//      DOM.setElementAttribute(contentPanel.getElement(), "style", "overflow:visible");
//      contentPanel.setStyleName("");
//      contentPanel.setWidth("100%");
//      contentPanel.setHeight("100%");
//
//      basePanel.add(toolBar);
//      basePanel.add(contentPanel);
//      basePanel.setCellHeight(toolBar, "28px;");
//      basePanel.setSpacing(0);
//      basePanel.setStyleName("");
//
//      initWidget(basePanel);
//      setWidth("100%");
//      setHeight("100%");
//      History h = getHistoryForIframe(contentPanel.getElement());

   }

   /**
    * @param element
    * @return
    */
   private native History getHistoryForIframe(Element e)/*-{
//      return element.window.history;
      if(typeof e.contentDocument != "undefined")
      {
        return e.contentDocument.history;
      } 
      else
      {
         return element.contentWindow.history
      }
      
//      return element.contentDocument.history || element.contentWindow.history
   }-*/;


   public void setDocumentationURL(String url)
   {
      contentPanel.setUrl(url);
//      History iWindow = getHistoryForIframe(contentPanel.getElement());
     
//      
//      iWindow.addValueChangeHandler(new ValueChangeHandler<String>()
//      {
//         
//         @Override
//         public void onValueChange(ValueChangeEvent<String> event)
//         {
//            System.out.println(event.getValue());            
//         }
//      });
   }

}
