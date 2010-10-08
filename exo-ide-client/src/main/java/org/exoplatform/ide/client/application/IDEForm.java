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
package org.exoplatform.ide.client.application;

import org.exoplatform.gwtframework.ui.client.event.WindowResizedEvent;
import org.exoplatform.ide.client.application.perspective.DefaultPerspective;
import org.exoplatform.ide.client.component.ClearFocusForm;
import org.exoplatform.ide.client.download.DownloadForm;
import org.exoplatform.ide.client.model.ApplicationContext;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.layout.Layout;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class IDEForm extends Layout implements IDEPresenter.Display
{

   private IDEPresenter presenter;

   private HandlerManager eventBus;

   private IDEPresenter.Display display;
   
   private ApplicationContext context;
   
   private ControlsRegistration controlsRegistration;

   public IDEForm(final HandlerManager eventBus, ApplicationContext context, final ControlsRegistration controlsRegistration)
   {
      this.eventBus = eventBus;
      this.context = context;
      this.controlsRegistration = controlsRegistration;
      display = this;

      setWidth100();
      setHeight100();
      setOverflow(Overflow.HIDDEN);
      
      draw();

      new ClearFocusForm(eventBus);
      new DownloadForm(eventBus);

      Window.addResizeHandler(new ResizeHandler()
      {
         public void onResize(ResizeEvent event)
         {
            eventBus.fireEvent(new WindowResizedEvent());
         }
      });

      new Timer()
      {
         @Override
         public void run()
         {
            presenter = new IDEPresenter(eventBus, controlsRegistration);
            presenter.bindDisplay(display);
         }
      }.schedule(200);
   }

   public void showDefaultPerspective()
   {
      addMember(new DefaultPerspective(eventBus, context));         
   }

}
