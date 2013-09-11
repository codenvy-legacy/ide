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
import org.crsh.cmdline.annotations.Usage
import org.crsh.cmdline.annotations.Command
import org.crsh.cmdline.annotations.Man
import org.crsh.cmdline.annotations.Argument
import org.crsh.jcr.command.Path;

public class cd extends org.crsh.jcr.command.JCRCommand {

  @Usage("changes the current node")
  @Command
  @Man("""\
The cd command changes the current node path. The command used with no argument changes to the root
node. A relative or absolute path argument can be provided to specify a new current node path.

[/]% cd /gadgets
[/gadgets]% cd /gadgets
[/gadgets]% cd
[/]%""")
  public Object main(
    @Argument
    @Usage("the new path")
    @Man("The new path that will change the current node navigation") Path path) throws ScriptException {
    assertConnected();
    def node = findNodeByPath(path);
    setCurrentNode(node);
  }
}

