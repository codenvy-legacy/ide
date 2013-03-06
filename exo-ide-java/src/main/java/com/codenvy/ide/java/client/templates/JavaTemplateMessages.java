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
package com.codenvy.ide.java.client.templates;

import com.google.gwt.core.client.GWT;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 4:52:06 PM 34360 2009-07-22 23:58:59Z evgen $
 * 
 */
public interface JavaTemplateMessages extends Messages
{
   JavaTemplateMessages INSTANCE = GWT.create(JavaTemplateMessages.class);

   @Key("CodeTemplateContextType_variable_description_tags")
   String CodeTemplateContextType_variable_description_tags();

   @Key("CodeTemplateContextType_variable_description_todo")
   String CodeTemplateContextType_variable_description_todo();

   @Key("CodeTemplateContextType_variable_description_enclosingtype")
   String CodeTemplateContextType_variable_description_enclosingtype();

   @Key("CodeTemplateContextType_variable_description_enclosingmethod")
   String CodeTemplateContextType_variable_description_enclosingmethod();

   @Key("CodeTemplateContextType_variable_description_exceptiontype")
   String CodeTemplateContextType_variable_description_exceptiontype();

   @Key("CodeTemplateContextType_variable_description_exceptionvar")
   String CodeTemplateContextType_variable_description_exceptionvar();

   @Key("CodeTemplateContextType_variable_description_bodystatement")
   String CodeTemplateContextType_variable_description_bodystatement();

   @Key("CodeTemplateContextType_variable_description_getterfieldname")
   String CodeTemplateContextType_variable_description_getterfieldname();

   @Key("CodeTemplateContextType_variable_description_param")
   String CodeTemplateContextType_variable_description_param();

   @Key("CodeTemplateContextType_variable_description_typename")
   String CodeTemplateContextType_variable_description_typename();

   /**
    * @return
    */
   @Key("CodeTemplateContextType_variable_description_packdeclaration")
   String CodeTemplateContextType_variable_description_packdeclaration();

   @Key("CodeTemplateContextType_variable_description_typedeclaration")
   String CodeTemplateContextType_variable_description_typedeclaration();

   @Key("CodeTemplateContextType_variable_description_typecomment")
   String CodeTemplateContextType_variable_description_typecomment();

   @Key("CodeTemplateContextType_variable_description_filecomment")
   String CodeTemplateContextType_variable_description_filecomment();

   @Key("CodeTemplateContextType_variable_description_fieldtype")
   String CodeTemplateContextType_variable_description_fieldtype();

   @Key("CodeTemplateContextType_variable_description_fieldname")
   String CodeTemplateContextType_variable_description_fieldname();

   @Key("CodeTemplateContextType_variable_description_returntype")
   String CodeTemplateContextType_variable_description_returntype();

   @Key("CodeTemplateContextType_variable_description_see_overridden_tag")
   String CodeTemplateContextType_variable_description_see_overridden_tag();

   @Key("CodeTemplateContextType_variable_description_see_target_tag")
   String CodeTemplateContextType_variable_description_see_target_tag();

   @Key("CodeTemplateContextType_variable_description_getterfieldtype")
   String CodeTemplateContextType_variable_description_getterfieldtype();

   @Key("CodeTemplateContextType_variable_description_barefieldname")
   String CodeTemplateContextType_variable_description_barefieldname();

   @Key("CodeTemplateContextType_variable_description_filename")
   String CodeTemplateContextType_variable_description_filename();

   @Key("CodeTemplateContextType_variable_description_packagename")
   String CodeTemplateContextType_variable_description_packagename();

   @Key("CodeTemplateContextType_variable_description_projectname")
   String CodeTemplateContextType_variable_description_projectname();

   @Key("CodeTemplateContextType_validate_unknownvariable")
   String CodeTemplateContextType_validate_unknownvariable(String unknown);

   @Key("CodeTemplateContextType_validate_missingvariable")
   String CodeTemplateContextType_validate_missingvariable(String missing);

   @Key("CodeTemplateContextType_validate_invalidcomment")
   String CodeTemplateContextType_validate_invalidcomment();

   /**
    * @return
    */
   @Key("CompilationUnitContextType_variable_description_enclosing_type")
   String CompilationUnitContextType_variable_description_enclosing_type();

}
