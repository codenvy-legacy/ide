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
package org.exoplatform.ide.client.framework.outline.ui;

import org.exoplatform.ide.editor.api.codeassitant.Token;

import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * This interface is used to create code outline item widget from OutlineTreeGrid class of exo.ide.client library. 
 * Function getOutlineItemWidget(Token token) is realized in the OutlineItemCreatorImpl class and specific {FileType}OutlineItemCreator classes of exo-ide-editor-extension-{FileType} libraries.
 * Each {FileType}OutlineItemCreator classes are loaded like editors in the IDE at the start by using method org.exoplatform.ide.client.IDE->addOutlineItemCreator().      
 * 
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $
 */

public interface OutlineItemCreator
{
   Widget getOutlineItemWidget(Token token);
}
