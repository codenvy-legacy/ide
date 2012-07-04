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