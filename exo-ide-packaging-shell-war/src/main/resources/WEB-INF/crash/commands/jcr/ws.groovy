/**
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
import javax.jcr.SimpleCredentials;

import org.crsh.jcr.JCR;
import org.crsh.jcr.command.ContainerOpt;
import org.crsh.jcr.command.UserNameOpt;
import org.crsh.jcr.command.PasswordOpt
import org.crsh.cmdline.annotations.Man
import org.crsh.cmdline.annotations.Usage
import org.crsh.cmdline.annotations.Command
import org.crsh.cmdline.annotations.Argument
import org.crsh.cmdline.annotations.Required;

@Usage("workspace commands")
@Man("""The ws command provides a set of commands interacting with JCR workspace.""")
class ws extends org.crsh.jcr.command.JCRCommand
{

  @Usage("login to a workspace")
  @Man("""
This command login to a JCR workspace and establish a session with the repository.
When you are connected the shell maintain a JCR session and allows you to interact with the session in a shell
oriented fashion. The repository name must be specified and optionally you can specify a user name and password to
have more privileges.

% ws login -c portal portal-system
Connected to workspace portal-system

% ws login -c portal -u root -p gtn portal-system
Connected to workspace portal-system

""")
  @Command
  public Object login(
    @UserNameOpt String userName,
    @PasswordOpt String password,
    @ContainerOpt String containerName,
    @Argument
    @Required
    @Usage("the workspace name")
    @Man("The name of the workspace to connect to")
    String workspaceName) throws ScriptException {

    //
    if (userName != null && password == null) {
      password = readLine("password:", false);
    }

    //
    def properties = containerName == null ? [:] : ["exo.container.name":containerName];

    //
    def repo = JCR.getRepository(properties);

    //
    if (repo == null) {
      return "Could not locate repository";
    }

    //
    if (userName != null && password != null) {
      def credentials = new SimpleCredentials(userName, password.toCharArray());
      session = repo.login(credentials, workspaceName);
    } else {
      session = repo.login(workspaceName);
    }
    def root = session.getRootNode();
    setCurrentNode(root);
    return """Connected to workspace $workspaceName""";
  }

  @Usage("logout from a workspace")
  @Man("""This command logout from the currently connected JCR workspace""")
  @Command
  public Object logout() throws ScriptException {
    assertConnected();
    session.logout();
    session = null;
    currentPath = null;
    return "Disconnected from workspace";
  }
}