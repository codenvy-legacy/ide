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
import org.crsh.command.ScriptException;
import org.crsh.cmdline.annotations.Man
import org.crsh.cmdline.annotations.Command
import org.crsh.cmdline.annotations.Usage
import org.crsh.jcr.command.Path
import org.crsh.cmdline.annotations.Argument;

public class commit extends org.crsh.jcr.command.JCRCommand {

  @Usage("saves changes")
  @Command
  @Man("""Saves the changes done to the current session. A node can be provided to save the state of the
this nodes and its descendants only.""")
  public void main(
    @Argument
    @Usage("the path to commit")
    @Man("The path of the node to commit") Path path) throws ScriptException {
    assertConnected();
    def node = findNodeByPath(path);
    node.save();
  }
}
