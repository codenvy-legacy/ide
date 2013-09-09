/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.shell.client;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 15, 2011 evgen $
 */
public interface Messages extends com.google.gwt.i18n.client.Messages {
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

    @Key("jobs.help")
    String jobsHelp();

    @Key("jobs.error")
    String jobsError();

    @Key("kill.help")
    String killHelp();

    @Key("kill.usage")
    String killUsage();

    @Key("kill.error")
    String killError();

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

    @Key("mvn.build.help")
    String mvnBuildHelp();

    @Key("mvn.build.success")
    String mvnBuildSuccess(String url);

    // Errors
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

    @Key("kill.success")
    String killJobSuccess();

    @Key("welcome.message")
    String welcomeMessage();
}