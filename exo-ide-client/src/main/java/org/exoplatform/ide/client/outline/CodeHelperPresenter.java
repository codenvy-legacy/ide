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
package org.exoplatform.ide.client.outline;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;

/**
 * Presenter for CodeHelper panel, that displays in
 * right side of IDE.
 * 
 * Handles events, that open and close panels in tabs.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeHelperPresenter
{
   interface Display
   {
   }

   private HandlerManager eventBus;

   private Handlers handlers;

   private Display display;

   public CodeHelperPresenter(HandlerManager bus)
   {
      eventBus = bus;
      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      display = d;
   }

}
