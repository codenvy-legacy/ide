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
