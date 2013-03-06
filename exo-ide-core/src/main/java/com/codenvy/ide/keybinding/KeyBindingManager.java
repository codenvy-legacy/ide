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
package com.codenvy.ide.keybinding;

import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.ui.keybinding.Scheme;

import com.codenvy.ide.util.browser.UserAgent;
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.util.input.SignalEvent;
import com.codenvy.ide.util.input.SignalEventUtils;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.Element;

/**
 * Implementation of the {@link KeyBindingAgent}.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class KeyBindingManager implements KeyBindingAgent
{

   private SchemeImpl globalScheme;

   private SchemeImpl activeScheme;

   private SchemeImpl eclipseScheme;

   private final EventListener downListener = new EventListener()
   {
      @Override
      public void handleEvent(Event event)
      {
         SignalEvent signalEvent = SignalEventUtils.create(event, false);
         if (signalEvent == null)
         {
            return;
         }
         //handle event in active scheme
         if (activeScheme.handleKeyEvent(signalEvent))
         {
            event.preventDefault();
            event.stopPropagation();
         }
         //else handle event in global scheme
         else if (globalScheme.handleKeyEvent(signalEvent))
         {
            event.preventDefault();
            event.stopPropagation();
         }
         //default, lets this event handle other part of the IDE
      }
   };

   public KeyBindingManager()
   {
      globalScheme = new SchemeImpl("ide.ui.keyBinding.global", "Global");
      eclipseScheme = new SchemeImpl("ide.ui.keyBinding.eclipse", "Eclipse Scheme");
      activeScheme = eclipseScheme;

      // Attach the listeners.
      final Element documentElement = Elements.getDocument().getDocumentElement();
      if (UserAgent.isFirefox())
      {
         // firefox fiers keypress events
         documentElement.addEventListener(Event.KEYPRESS, downListener, true);
      }
      else
      {
         //webkit browsers fiers keydown events
         documentElement.addEventListener(Event.KEYDOWN, downListener, true);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Scheme getGlobal()
   {
      return activeScheme;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Scheme getEclipse()
   {
      return eclipseScheme;
   }
}
