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
package org.exoplatform.ide.editor.api.codeassitant.ui;

import org.exoplatform.ide.editor.api.codeassitant.Token;

/**
 * This interface describes factory that builds 
 * {@link TokenWidget} by specific token.<br>
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 25, 2010 3:59:50 PM evgen $
 *
 */
public interface TokenWidgetFactory
{
   
   /**
    * Create new {@link TokenWidget} for token.
    * @param token
    * @return {@link TokenWidget} that represent token
    */
   TokenWidget buildTokenWidget(Token token);

}
