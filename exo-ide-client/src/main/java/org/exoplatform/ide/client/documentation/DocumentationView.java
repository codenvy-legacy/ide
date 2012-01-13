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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: DocumentationForm Jan 21, 2011 12:23:13 PM evgen $
 * 
 */
public class DocumentationView extends ViewImpl implements DocumentationPresenter.Display
{

   private static final String ID = "ideDocumentationView";

   private static final String FRAME_ID = "ideDocumentationFrame";

   private static final Image DOCUMENTATION_TAB_ICON = new Image(IDEImageBundle.INSTANCE.documentation());

   private Frame iFrame;

   public DocumentationView()
   {
      super(ID, ViewType.INFORMATION, IDE.IDE_LOCALIZATION_CONSTANT.documentationTitle());
      setIcon(DOCUMENTATION_TAB_ICON);

      iFrame = new Frame();
      DOM.setElementAttribute(iFrame.getElement(), "scrolling", "no");
      DOM.setElementAttribute(iFrame.getElement(), "frameborder", "0");
      DOM.setElementAttribute(iFrame.getElement(), "style", "overflow:visible");
      iFrame.setStyleName("");
      iFrame.setWidth("100%");
      iFrame.setHeight("100%");
      iFrame.ensureDebugId(FRAME_ID);
      add(iFrame);
      setWidth("100%");
   }

   /**
    * @see org.exoplatform.ide.client.documentation.DocumentationPresenter.Display#setDocumentationURL(java.lang.String)
    */
   @Override
   public void setDocumentationURL(String url)
   {
      iFrame.setUrl(url);
   }
}
