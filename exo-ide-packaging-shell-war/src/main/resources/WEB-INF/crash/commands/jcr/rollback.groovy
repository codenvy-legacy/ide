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
import org.crsh.command.ScriptException;
import org.crsh.cmdline.annotations.Usage
import org.crsh.jcr.command.PathArg
import org.crsh.cmdline.annotations.Man
import org.crsh.cmdline.annotations.Command
import org.crsh.cmdline.annotations.Argument
import org.crsh.jcr.command.Path;

public class rollback extends org.crsh.jcr.command.JCRCommand {

  @Usage("rollback changes")
  @Command
  @Man("""Rollbacks the changes of the current session. A node can be provided to rollback the state of the
this nodes and its descendants only.""")
  public void main(@Argument @Man("the path to rollback") @Usage("The path of the node to rollback") Path path) throws ScriptException {
    assertConnected();
    def node = findNodeByPath(path);
    node.refresh(false);
  }
}
