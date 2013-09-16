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
import org.crsh.command.InvocationContext
import org.crsh.cmdline.annotations.Command
import org.crsh.cmdline.annotations.Usage
import org.crsh.cmdline.annotations.Man;

public class pwd extends org.crsh.command.CRaSHCommand {

  @Usage("print the current node path")
  @Command
  @Man("""The pwd command prints the current node path, the current node is produced by this command.

[/gadgets]% pwd
/gadgets""")
  public void main(InvocationContext<Node, Void> context) throws ScriptException {
    context.produce(getCurrentNode());
    context.writer <<= currentPath.string;
  }
}