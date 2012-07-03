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

