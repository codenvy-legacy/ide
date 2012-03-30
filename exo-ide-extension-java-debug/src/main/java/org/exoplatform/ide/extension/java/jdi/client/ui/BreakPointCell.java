/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.client.ui;

import org.exoplatform.ide.extension.java.jdi.client.DebuggerClientBundle;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class BreakPointCell extends AbstractCell<BreakPoint>
{
      @Override
      public void render(Context context, BreakPoint breakpoint, SafeHtmlBuilder sb)
      {
         if (breakpoint == null)
            return;
         Image image = getImage(DebuggerClientBundle.INSTANCE.breakpoint());
         sb.appendHtmlConstant(image.toString());
         sb.appendHtmlConstant(getClassName(breakpoint.getLocation().getClassName()).getString());
         sb.appendHtmlConstant(getLineNumber(breakpoint.getLocation().getLineNumber()).toString());
   }
      
      protected Image getImage(ImageResource imageResource)
      {
         Image image = new Image(imageResource);
         DOM.setStyleAttribute(image.getElement(), "cssFloat", "left");
         DOM.setStyleAttribute(image.getElement(), "marginRight", "5px");
         return image;
      }
      
      protected Element getClassName(String className)
      {
         Element span = DOM.createSpan();
         span.setInnerHTML(className);
         return span;
      }
      
      protected String getLineNumber(int line)
      {
         Element span = DOM.createSpan();
         span.setInnerText(" - [line : " + line + "]");
         span.getStyle().setColor("#644A17");
         return span.getString();
      }

}
