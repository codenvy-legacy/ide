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
package org.exoplatform.ide.extension.heroku.client.control;

import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.heroku.client.HerokuClientBundle;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.key.AddKeyEvent;

/**
 * Control for adding keys on Heroku.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 31, 2011 9:32:28 AM anya $
 *
 */
public class AddKeyControl extends AbstractHerokuControl
{

   public AddKeyControl()
   {
      super(HerokuExtension.LOCALIZATION_CONSTANT.addKeyControlId());
      setTitle(HerokuExtension.LOCALIZATION_CONSTANT.addKeyControlTitle());
      setPrompt(HerokuExtension.LOCALIZATION_CONSTANT.addKeyControlPrompt());
      setEvent(new AddKeyEvent());
      setImages(HerokuClientBundle.INSTANCE.addKeys(), HerokuClientBundle.INSTANCE.addKeysDisabled());
      setDelimiterBefore(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);

      setVisible(true);
   }

   /**
    * 
    */
   protected void refresh()
   {
      setEnabled(vfsInfo != null);
   }

}
