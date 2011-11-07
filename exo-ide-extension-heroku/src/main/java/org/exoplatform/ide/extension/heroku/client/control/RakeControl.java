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
import org.exoplatform.ide.extension.heroku.client.rake.RakeCommandEvent;

/**
 * Control is used for executing rake commands,
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 17, 2011 10:43:13 AM anya $
 *
 */
public class RakeControl extends AbstractHerokuControl
{

   public RakeControl()
   {
      super(HerokuExtension.LOCALIZATION_CONSTANT.rakeControlId());
      setTitle(HerokuExtension.LOCALIZATION_CONSTANT.rakeControlTitle());
      setPrompt(HerokuExtension.LOCALIZATION_CONSTANT.rakeControlPrompt());
      setEvent(new RakeCommandEvent());
      setImages(HerokuClientBundle.INSTANCE.rake(), HerokuClientBundle.INSTANCE.rakeDisabled());
      setDelimiterBefore(true);
   }

}
