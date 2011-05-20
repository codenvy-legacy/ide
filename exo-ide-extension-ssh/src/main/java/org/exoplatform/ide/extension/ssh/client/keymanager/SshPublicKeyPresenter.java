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
package org.exoplatform.ide.extension.ssh.client.keymanager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.ssh.client.SshService;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: SshPublicKeyPresenter May 19, 2011 12:32:14 PM evgen $
 *
 */
public class SshPublicKeyPresenter implements ViewClosedHandler
{

   public interface Display extends IsView
   {
      String ID = "ideSshPublicKeyView";

      HasClickHandlers getCloseButton();

      HasValue<String> getKeyField();
      
   }

   private KeyItem keyItem;

   private HandlerRegistration viewClosedHandler;

   private Display display;

   /**
    * 
    */
   public SshPublicKeyPresenter(KeyItem keyItem)
   {
      this.keyItem = keyItem;
      viewClosedHandler = IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);

      display = GWT.create(Display.class);

      bind();
      
      IDE.getInstance().openView(display.asView());

      showPublicKey();
   }

   /**
    * 
    */
   private void showPublicKey()
   {
      SshService.get().getPublicKey(keyItem, new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            display.getKeyField().setValue(result);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            IDE.EVENT_BUS.fireEvent(new ExceptionThrownEvent(exception));
         }
      });
   }

   /**
    * 
    */
   private void bind()
   {
      display.getCloseButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(Display.ID);
         }
      });

   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
         viewClosedHandler.removeHandler();
      }
   }

}
