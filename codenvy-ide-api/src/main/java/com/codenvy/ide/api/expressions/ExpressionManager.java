/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.api.expressions;

import com.codenvy.ide.api.event.ExpressionsChangedEvent;

/**
 * Centralized Expression handler, that notifies Expressions and handles their value 
 * changes producing a curresponding {@link ExpressionsChangedEvent}
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public interface ExpressionManager
{

   /**
    * Register new {@link Expression} in {@link ExpressionManagerImpl}
    * 
    * @param expression
    */
   public void registerExpression(Expression expression);

   /**
    * Removes {@link Expression} from {@link ExpressionManagerImpl}
    * 
    * @param expression
    */
   public void unRegisterExpression(Expression expression);

}