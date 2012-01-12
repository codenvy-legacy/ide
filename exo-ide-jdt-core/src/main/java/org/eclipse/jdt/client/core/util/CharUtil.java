/*
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
package org.eclipse.jdt.client.core.util;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Jan 10, 2012 5:55:02 PM evgen $
 *
 */
public class CharUtil
{
   private static final int[] type = new int[0x10500];
   static
   {
      decode(")$'$&$($*$+$,$.$-$%$/$9$1$0$6$7$8$5$3$2$4$C$W$:$B$\\$D$;$<$A$E$>$H$?$G"
         + "$@$F$=$n$e$`$M$Y$Z$[$X$i$I$#&o$T$v$Q$#-#/g$a$f$L$m$e%b$K$J$p$S$u$R$h$k"
         + "$c$&%U$r$P$s$]$j$$yV$q$O$t$^$l$d$_$N$",
         ",%$&#'()+*-0/6574123.:>?HBDF@;8=AGEC|,|.|0|2|4|6|8|:|<|>|@|B|D|$%S{|$|"
            + "&|(|*|V|%|'|)*|[|X|^|`|]|*|#|%4|c|Y|_|f|a|Z|l|d*|o|j-<J|i|b|r|x|W|p|w|"
            + "n|{|j|}*X}#%|y|*-.8L9PMNO<I|33}#%}#8}#)|k}##|b|m}#;|\\|jYj}#<}#*|-9<KJ"
            + "}#:|F}#GK_QI}#K|-|/|1}#6.}#R|G}#U|J}#X}#T|I|5,}#S|H}#V}#K}#%-}#K}#d}#K"
            + "}#,|l}#K|7|9|;|=|?|A|CC}#KRba]LztmWfdUkr9}#e}#a}#Z|5}#m|M}#p|P}#s|F}#]"
            + "}#V}$.}#o|O}#r|D}#i}#-}#/}#1}#3ovyK[`ixJ\\ZgQphw^ITcslnueV%'-}$)}#:q", new int[]{-2, -1, 0, 1, 2, 3, 4,
            5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
            33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 60, 70, 80, 90, 100, 200, 300, 400,
            500, 600, 700, 800, 900, 1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 20000, 30000, 40000,
            50000, 60000, 70000, 80000, 90000}, type);
   }

   public static int getNumericValue(int ch)
   {
      if (ch < 0x10500)
      {
         return type[ch];
      }
      else if (ch >= 0x1D7CE && ch <= 0x1D7FF)
      {
         return (ch - 0x1D7CE) % 10;
      }
      return -2;
   }

   public static boolean isWhitespace(int cp)
   {
      switch (cp)
      {
         case 9 :
         case 10 :
         case 11 :
         case 12 :
         case 13 :
         case 0x1C :
         case 0x1D :
         case 0x1E :
         case 0x1F :
            return true;
         case 0x00A0 :
         case 0x2007 :
         case 0x202f :
            return false;
         default :
            return isSpaceChar(cp);
      }
   }

   @SuppressWarnings("deprecation")
   public static boolean isSpaceChar(int cp)
   {
      return Character.isSpace((char)cp);
   }

   static int[] decode(String key, String data, int[] types, int[] result)
   {
      int[] pairTypes = new int[256];
      int[] pairCounts = new int[256];
      Decoder d = new Decoder(key);
      int max = 0;
      while (true)
      {
         if ((pairTypes[max] = d.next()) < 0)
         {
            break;
         }
         pairCounts[max++] = d.next();
      }
      String decoded = deLZW(new Decoder(data), max);
      for (int i = 0, idx = 0;;)
      {
         int n = decoded.charAt(idx++);
         if (n > 300)
         {
            break;
         }
         for (int j = 0; j < pairCounts[n]; j++)
         {
            result[i++] = types == null ? pairTypes[n] : types[pairTypes[n]];
         }
      }
      return result;
   }

   private static native String deLZW(Decoder d, int max) /*-{
                                                          var dict = {}, f = String.fromCharCode;
                                                          var current = f(d.@org.eclipse.jdt.client.core.util.CharUtil.Decoder::next()());
                                                          var oldPhrase = current, seen = max, phrase, out = [ current ], code;
                                                          for (;;) {
                                                          if ((code = d.@org.eclipse.jdt.client.core.util.CharUtil.Decoder::next()()) < 0) {
                                                          break;
                                                          } else if (code < max) {
                                                          phrase = f(code);
                                                          } else {
                                                          phrase = dict[code] ? dict[code] : oldPhrase + current;
                                                          }
                                                          out.push(phrase);
                                                          current = phrase.charAt(0);
                                                          dict[seen++] = oldPhrase + current;
                                                          oldPhrase = phrase;
                                                          }
                                                          return out.join("") + f(301);
                                                          }-*/;

   private static class Decoder
   {
      private String s;

      private int idx;

      public Decoder(String s)
      {
         this.s = s;
         this.idx = 0;
      }

      public int next()
      {
         if (idx >= s.length())
         {
            return -1;
         }
         char c = s.charAt(idx++);
         if (c == '|')
         {
            return 89 + s.charAt(idx++) - '#';
         }
         else if (c == '}')
         {
            return 89 + 91 + 1 + (s.charAt(idx++) - '#') * 91 + s.charAt(idx++) - '#';
         }
         else if (c == '~')
         {
            return 89 + 91 + 1 + 91 * 91 + (s.charAt(idx++) - '#') * 91 * 91 + (s.charAt(idx++) - '#') * 91
               + s.charAt(idx++) - '#';
         }
         else
         {
            return c - '#';
         }
      }
   }
}
