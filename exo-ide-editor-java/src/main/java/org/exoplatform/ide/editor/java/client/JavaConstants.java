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
package org.exoplatform.ide.editor.java.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Dec 1, 2011 5:04:44 PM evgen $
 *
 */
public interface JavaConstants extends Messages
{
   @Key("create.java.class.view.title")
   String createJavaClassTitle();

   @Key("create.java.class.view.label.main")
   String createJCLabelMain();

   @Key("create.java.class.view.label.desciption")
   String createJCLabelDesciption();

   @Key("create.java.class.view.label.source")
   String createJCLabelSource();

   @Key("create.java.class.view.label.package")
   String createJCLabelPackage();

   @Key("create.java.class.view.label.name")
   String createJCLabelName();

   @Key("create.java.class.view.label.modifiers")
   String createJCLabelModifiers();

   @Key("create.java.class.view.label.superclass")
   String createJCLabelSuperclass();

   @Key("create.java.class.view.label.interfaces")
   String createJCLabelInterfaces();

   @Key("create.java.class.view.label.subs")
   String createJCLabelStubs();

   @Key("create.java.class.view.label.subs.constructors")
   String createJCLabelConstructors();

   @Key("create.java.class.view.label.subs.methods")
   String createJCLabelMethods();

   @Key("create.java.class.view.button.browse")
   String createJCButtonBrowse();

   @Key("create.java.class.view.button.add")
   String createJCButtonAdd();

   @Key("create.java.class.view.button.create")
   String createJCButtonCreate();

   @Key("create.java.class.view.button.cancel")
   String createJCButtonCancel();

   @Key("create.java.class.view.button.remove")
   String createJCButtonRemove();

   @Key("search.type.button.search")
   String searchTypeButtonSearch();

   @Key("search.type.superclass")
   String searchTypeSuperClass();

   @Key("search.type.interaces")
   String searchTypeInterfaces();

   @Key("search.type.input.text")
   String searchTypeInputText();

   @Key("search.type.result")
   String searchTypeResult();

   @Key("search.type.button.ok")
   String searchTypeButtonOk();

   @Key("create.java.class.view.error.final.abstract")
   String abstractFinalError();

   @Key("create.java.class.view.error.name")
   String nameError();

   @Key("create.java.class.view.error.package")
   String packageError();

}
