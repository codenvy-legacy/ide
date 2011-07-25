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
package org.exoplatform.ide.editor.extension.ruby.client.codemirror;

import org.exoplatform.ide.client.framework.outline.ui.OutlineItemCreatorImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.extension.ruby.client.RubyClientBundle;


/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class RubyOutlineItemCreator extends OutlineItemCreatorImpl
{  
   @Override
   public String getTokenIcon(TokenBeenImpl token)
   {
      switch (token.getType())
      {
         case LOCAL_VARIABLE :
            return RubyClientBundle.INSTANCE.variable().getURL();

         case GLOBAL_VARIABLE :
            return RubyClientBundle.INSTANCE.rubyGlobalVariable().getURL();

         case CLASS_VARIABLE :
            return RubyClientBundle.INSTANCE.rubyClassVariable().getURL();

         case INSTANCE_VARIABLE :
            return RubyClientBundle.INSTANCE.rubyObjectVariable().getURL();

         case CONSTANT :
            return RubyClientBundle.INSTANCE.rubyConstant().getURL();

         case MODULE :
            return RubyClientBundle.INSTANCE.module().getURL();
            
         case CLASS :
            return RubyClientBundle.INSTANCE.classItem().getURL();

         case METHOD :
            return RubyClientBundle.INSTANCE.publicMethod().getURL();       
            
         default :
            return "";
      }
   }
   
   @Override
   public String getTokenDisplayTitle(TokenBeenImpl token)
   {
      String label = token.getName();
      
      // Add "()" to the end of method's label 
      if (TokenType.METHOD.equals(token.getType()))
      {
         label += "()"; 
      }
      
      if (token.getElementType() != null)
      {
         label += getElementType(token);
      }
      
      return label;
   }
}
