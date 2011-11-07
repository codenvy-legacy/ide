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
import org.exoplatform.ide.extension.heroku.client.info.ShowApplicationInfoEvent;

/**
 * Control for displaying application information.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 1, 2011 11:18:56 AM anya $
 *
 */
public class ShowApplicationInfoControl extends AbstractHerokuControl
{

   public ShowApplicationInfoControl()
   {
      super(HerokuExtension.LOCALIZATION_CONSTANT.showApplicationInfoControlId());
      setTitle(HerokuExtension.LOCALIZATION_CONSTANT.showApplicationInfoControlTitle());
      setPrompt(HerokuExtension.LOCALIZATION_CONSTANT.showApplicationInfoControlPrompt());
      setEvent(new ShowApplicationInfoEvent());
      setImages(HerokuClientBundle.INSTANCE.applicationInfo(), HerokuClientBundle.INSTANCE.applicationInfoDisabled());
   }

}
