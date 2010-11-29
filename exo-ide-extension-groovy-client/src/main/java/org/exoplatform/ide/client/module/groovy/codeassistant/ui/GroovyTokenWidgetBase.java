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
package org.exoplatform.ide.client.module.groovy.codeassistant.ui;

import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenWidget;
import org.exoplatform.ide.client.module.groovy.GroovyPluginImageBundle;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 29, 2010 9:42:07 AM evgen $
 *
 */
public abstract class GroovyTokenWidgetBase extends TokenWidget<TokenExt>
{
   
   /**
    * @param token
    */
   public GroovyTokenWidgetBase(TokenExt token)
   {
      super(token);
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenName()
    */
   @Override
   public String getTokenName()
   {
      return getToken().getName();
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setSelectedStyle()
    */
   @Override
   public void setSelectedStyle()
   {
      setStyleName(GroovyPluginImageBundle.INSTANCE.css().selectedItem());
   }



   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setOveredStyle()
    */
   @Override
   public void setOveredStyle()
   {
      setStyleName(GroovyPluginImageBundle.INSTANCE.css().overedItem());
   }



   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setDefaultStyle()
    */
   @Override
   public void setDefaultStyle()
   {
      setStyleName(GroovyPluginImageBundle.INSTANCE.css().item());
   }
   
}
