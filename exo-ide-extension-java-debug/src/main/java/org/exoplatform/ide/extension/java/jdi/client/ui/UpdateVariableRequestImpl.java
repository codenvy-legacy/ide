/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.client.ui;

import org.exoplatform.ide.extension.java.jdi.shared.UpdateVariableRequest;
import org.exoplatform.ide.extension.java.jdi.shared.VariablePath;

/**
 * Implementation of {@link UpdateVariableRequest} interface.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: UpdateVarImpl.java Apr 27, 2012 5:30:48 PM azatsarynnyy $
 *
 */
public class UpdateVariableRequestImpl implements UpdateVariableRequest
{

   private String expression;

   private VariablePath variablePath;

   public UpdateVariableRequestImpl(VariablePath variablePath, String expression)
   {
      this.variablePath = variablePath;
      this.expression = expression;
   }

   public UpdateVariableRequestImpl()
   {
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.shared.UpdateVariableRequest#getVariablePath()
    */
   @Override
   public VariablePath getVariablePath()
   {
      return variablePath;
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.shared.UpdateVariableRequest#setVariablePath(org.exoplatform.ide.extension.java.jdi.shared.VariablePath)
    */
   @Override
   public void setVariablePath(VariablePath variablePath)
   {
      this.variablePath = variablePath;
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.shared.UpdateVariableRequest#getExpression()
    */
   @Override
   public String getExpression()
   {
      return expression;
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.shared.UpdateVariableRequest#setExpression(java.lang.String)
    */
   @Override
   public void setExpression(String expression)
   {
      this.expression = expression;
   }

}
