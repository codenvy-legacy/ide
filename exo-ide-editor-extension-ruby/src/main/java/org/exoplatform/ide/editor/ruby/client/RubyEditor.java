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

package org.exoplatform.ide.editor.ruby.client;

import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.framework.module.Extension;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * 
 * Provides a text editing area along with UI for executing text commands on the.<br> 
 * Support syntax coloration for Ruby language (http://www.ruby-lang.org/en/)   
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Revision$
 */
public class RubyEditor extends Extension
{
   
   interface DefaultContent extends ClientBundle
   {
      @Source("hello.rb")
      TextResource getSource();
   }

   /**
    * Localizable constants and messages
    */
   private final Messages messages = GWT.create(Messages.class);

   public final static DefaultContent DEFAULT_CONTENT = GWT.create(DefaultContent.class);
   
   public final String ICON = UIHelper.getGadgetImagesURL() + "ruby.png";

   @Override
   public void initialize()
   {

   }
   
}
