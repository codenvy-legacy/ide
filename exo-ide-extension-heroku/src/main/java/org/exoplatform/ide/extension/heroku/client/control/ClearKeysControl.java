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

import org.exoplatform.ide.extension.heroku.client.HerokuClientBundle;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.key.ClearKeysEvent;

/**
 * Control for clearing(removing) keys from Heroku.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 31, 2011 9:32:28 AM anya $
 *
 */
public class ClearKeysControl extends AbstractHerokuControl
{

   public ClearKeysControl()
   {
      super(HerokuExtension.LOCALIZATION_CONSTANT.clearKeysId());
      setTitle(HerokuExtension.LOCALIZATION_CONSTANT.clearKeysTitle());
      setPrompt(HerokuExtension.LOCALIZATION_CONSTANT.clearKeysPrompt());
      setImages(HerokuClientBundle.INSTANCE.clearKeys(), HerokuClientBundle.INSTANCE.clearKeysDisabled());
      setEvent(new ClearKeysEvent());
   }

}
