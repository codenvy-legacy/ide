/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.collaboration.chat.client;

import com.codenvy.ide.client.util.Elements;
import com.codenvy.ide.collaboration.dto.UserDetails;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import elemental.html.DivElement;
import elemental.html.SpanElement;

import org.exoplatform.ide.client.framework.module.IDE;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ChatNotificationWidget extends Composite
{
   private HTML html;

   public ChatNotificationWidget(UserDetails user, String message)
   {
      html = new HTML();
      Image i = new Image(ChatExtension.resources.chat());
      i.getElement().getStyle().setPosition(Position.RELATIVE);
      i.getElement().getStyle().setTop(-15, Unit.PX);
      i.getElement().getStyle().setFloat(Style.Float.LEFT);

      html.getElement().appendChild(i.getElement());

      SpanElement nameElement = Elements.createSpanElement(ChatExtension.resources.chatCss().notificationName());
      nameElement.setInnerHTML(user.getDisplayName() +":");
      html.getElement().appendChild((Node)nameElement);

      DivElement messageDiv = Elements.createDivElement(ChatExtension.resources.chatCss().notificationMessage());
      messageDiv.setInnerHTML("&nbsp;" + message);
      html.getElement().appendChild((Node)messageDiv);

      initWidget(html);
      html.addDomHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.fireEvent(new ShowHideChatEvent(true));
         }
      }, ClickEvent.getType());
   }
}
