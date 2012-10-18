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
package org.exoplatform.ide.java.client.core;

import com.google.gwt.core.client.GWT;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 * 
 */
public interface Messages extends com.google.gwt.i18n.client.Messages
{

   Messages INSTANCE = GWT.create(Messages.class);

   /**
    * @return
    */
   String convention_package_nullName();

   /**
    * @return
    */
   String convention_package_emptyName();

   /**
    * @return
    */
   String convention_package_dotName();

   /**
    * @return
    */
   String convention_package_nameWithBlanks();

   /**
    * @return
    */
   String convention_package_consecutiveDotsName();

   /**
    * @param typeName
    * @return
    */
   String convention_illegalIdentifier(String typeName);

   /**
    * @return
    */
   String convention_package_uppercaseName();

   String GetterSetterCompletionProposal_getter_label(String str);

   String GetterSetterCompletionProposal_setter_label(String str);

   String MethodCompletionProposal_constructor_label();

   String MethodCompletionProposal_method_label();

   @Key("formatter.title")
   String formatterTitle();
}
