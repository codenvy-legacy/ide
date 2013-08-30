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
import org.crsh.jcr.command.Path
import org.crsh.cmdline.annotations.Usage
import org.crsh.cmdline.annotations.Command
import org.crsh.cmdline.annotations.Argument
import org.crsh.cmdline.annotations.Man

@Usage("versioning commands")
@Man("Versionning commands")
public class version extends org.crsh.jcr.command.JCRCommand {
  // The path of the node to checkin
  @Usage("checkin a node")
  @Man("Perform a node checkin")
  @Command
  public void checkin(@Argument @Usage("the path to checkin") @Man("The node path to checkin") Path path) throws ScriptException {
    assertConnected();
    def node = findNodeByPath(path);
    node.checkin();
  }

  // The path of the node to checkout
  @Usage("checkout a node")
  @Man("Perform a node checkout")
  @Command
  public void checkout(@Argument @Usage("the path to checkout") @Man("The node path to checkout") Path path) throws ScriptException {
    assertConnected();
    def node = findNodeByPath(path);
    node.checkout();
  }
}
