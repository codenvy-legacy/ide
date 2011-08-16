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
package org.exoplatform.cloudshell.client;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 15, 2011 evgen $
 *
 */
public interface Messages extends com.google.gwt.i18n.client.Messages
{
   @Key("cat.help")
   String catHelp();

   @Key("cat.usage")
   String catUsage(String command);

   @Key("cat.file.content.error")
   String catGetFileContentError();

   @Key("cat.folder.error")
   String catFolderError(String folder);

   @Key("cat.not.found.error")
   String catFileNotFound(String file);

   @Key("cd.help")
   String cdHelp();

   @Key("cd.usage")
   String cdUsage();

   @Key("cd.error")
   String cdError();

   @Key("cd.error.not.folder")
   String cdErrorFolder(String name);
   
   @Key("clear.help")
   String clearHelp();
   
   @Key("help.help")
   String helpHelp();
   
   @Key("ls.help")
   String lsHelp();
   
   @Key("ls.usage")
   String lsUsage();
   
   @Key("ls.header")
   String lsHeader();
   
   @Key("ls.error")
   String lsError(String folderName);
   
   @Key("mkdir.help")
   String mkdirHelp();
   
   @Key("mkdir.usage")
   String mkdirUsage();
   
   @Key("mkdir.header")
   String mkdirHeader();
   
   @Key("mkdir.error")
   String mkdirError();
   
   @Key("pwd.help")
   String pwdHelp();
   
   @Key("pwd.usage")
   String pwdUsage();
   
   @Key("pwd.header")
   String pwdHeader();
   
   @Key("rm.help")
   String rmHelp();
   
   @Key("rm.usage")
   String rmUsage();
   
   @Key("rm.header")
   String rmHeader();
   
   //Errors
   @Key("commands.unmarshaller.error")
   String commandsUnmarshallerError();
   
   @Key("no.appropriate.command")
   String noAppropriateCommandError(String command);
   
   @Key("syntax.error")
   String syntaxtError(String command);
   
   @Key("required.argument.not.found")
   String requiredArgumentNotFound(String command);
   
   @Key("required.option.not.found")
   String requiredOptionNotFound(String command);
   
   @Key("required.property.not.set")
   String requiredPropertyNotSet(String property);
}