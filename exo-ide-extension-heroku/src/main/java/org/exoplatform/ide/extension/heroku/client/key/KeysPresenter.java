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
package org.exoplatform.ide.extension.heroku.client.key;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;

/**
 * Presenter for actions with keys (add, clear).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 31, 2011 10:27:33 AM anya $
 *
 */
public class KeysPresenter implements AddKeyHandler, ClearKeysHandler, LoggedInHandler
{

   /**
    * Shows what action user tried to do before log in method.
    */
   private boolean clearKeys = false;

   /**
    *
    */
   public KeysPresenter()
   {
      IDE.addHandler(AddKeyEvent.TYPE, this);
      IDE.addHandler(ClearKeysEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.key.AddKeyHandler#onAddKey(org.exoplatform.ide.extension.heroku.client.key.AddKeyEvent)
    */
   @Override
   public void onAddKey(AddKeyEvent event)
   {
      addKeys();
   }

   /**
    * Perform adding keys on Heroku.
    */
   protected void addKeys()
   {
      clearKeys = false;
      HerokuClientService.getInstance().addKey(new HerokuAsyncRequestCallback(IDE.eventBus(), this)
      {
         @Override
         protected void onSuccess(List<Property> result)
         {
            IDE.fireEvent(new OutputEvent(HerokuExtension.LOCALIZATION_CONSTANT.addKeysSuccess(), Type.INFO));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.key.ClearKeysHandler#onClearKeys(org.exoplatform.ide.extension.heroku.client.key.ClearKeysEvent)
    */
   @Override
   public void onClearKeys(ClearKeysEvent event)
   {
      Dialogs.getInstance().ask(HerokuExtension.LOCALIZATION_CONSTANT.removeKeysTitle(),
         HerokuExtension.LOCALIZATION_CONSTANT.askRemoveKeys(), new BooleanValueReceivedHandler()
         {

            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value != null && value)
               {
                  clearKeys();
               }
            }
         });
   }

   /**
    * Perform removing keys from Heroku.
    */
   protected void clearKeys()
   {
      clearKeys = true;
      HerokuClientService.getInstance().clearKeys(new HerokuAsyncRequestCallback(IDE.eventBus(), this)
      {
         @Override
         protected void onSuccess(List<Property> result)
         {
            IDE.fireEvent(new OutputEvent(HerokuExtension.LOCALIZATION_CONSTANT.clearKeysSuccess(), Type.INFO));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      IDE.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         if (clearKeys)
         {
            clearKeys();
         }
         else
         {
            addKeys();
         }
      }
   }

}
