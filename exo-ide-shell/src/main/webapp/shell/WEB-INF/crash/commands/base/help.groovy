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
import org.crsh.command.DescriptionMode
import org.crsh.command.CRaSHCommand
import org.crsh.cmdline.annotations.Usage
import org.crsh.cmdline.annotations.Command;

class help extends CRaSHCommand
{

  /** . */
  private static final String TAB = "  ";

  @Usage("provides basic help")
  @Command
  Object main() {
    def names = [];
    def descs = [];
    int len = 0;
    shellContext.listResourceId(org.crsh.plugin.ResourceKind.SCRIPT).each() {
      String name ->
      try {
        def cmd = shell.getCommand(name);
        if (cmd != null) {
          def desc = cmd.describe(name, DescriptionMode.DESCRIBE) ?: "";
          names.add(name);
          descs.add(desc);
          len = Math.max(len, name.length());
        }
      } catch (org.crsh.shell.impl.CreateCommandException ignore) {
        //
      }
    }

    //
    def ret = "Try one of these commands with the -h or --help switch:\n\n";
    for (int i = 0;i < names.size();i++) {
      def name = names[i];
      char[] chars = new char[TAB.length() + len - name.length()];
      Arrays.fill(chars, (char)' ');
      def space = new String(chars);
      ret += "$TAB$name$space${descs[i]}\n";
    }
    ret += "\n";
    return ret;
  }
}