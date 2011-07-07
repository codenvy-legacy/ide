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
package org.exoplatform.ide.extension.ruby.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public interface RubyLocalizationConstants extends Messages
{
   /*
    * Buttons
    */
   @Key("ruby.button.create")
   String createButton();
   
   @Key("ruby.button.cancel")
   String cancelButton();

   /*
    * Controls.
    */
   @Key("control.create.rubyProject.id")
   String createRubyProjectControlId();
   
   @Key("control.create.rubyProject.title")
   String createRubyProjectControlTitle();
   
   @Key("control.create.rubyProject.prompt")
   String createRubyProjectControlPrompt();
   
   /*
    * CreateApplicationView
    */
   @Key("rubyProject.view.title")
   String createRubyProjectViewTitle();
   
   @Key("rubyProject.view.nameField.title")
   String createRubyProjectNameFieldTitle();
   
   @Key("rubyProject.view.nameField.defaultName")
   String createRubyProjectDefaultName();
   
   @Key("rubyProject.created.message")
   String projectCreated(String name);
}
