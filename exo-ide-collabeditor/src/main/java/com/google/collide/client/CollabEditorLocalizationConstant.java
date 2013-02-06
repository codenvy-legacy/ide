/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.google.collide.client;

/**
 * Interface to represent the constants contained in resource bundle: 'CollabEditorLocalizationConstant.properties'.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CollabEditorLocalizationConstant.java Feb 6, 2013 3:04:53 PM azatsarynnyy $
 *
 */
public interface CollabEditorLocalizationConstant extends com.google.gwt.i18n.client.Messages
{

   // Controls
   @Key("control.collaborators.id")
   String collaboratorsControlId();

   @Key("control.collaborators.title")
   String collaboratorsControlTitle();

   @Key("control.collaborators.prompt.show")
   String collaboratorsControlPromptShow();

   @Key("control.collaborators.prompt.hide")
   String collaboratorsControlPromptHide();

}
