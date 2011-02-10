/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.netvibes.client.model;

import java.util.LinkedHashMap;

/**
 * The list of supported languages taken from http://eco.netvibes.com/lang scheme.
 * Usage a non-supported code will result in a submission error.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 30, 2010 $
 *
 */
public class Languages
{
   /**
    * Map with language code and value.
    */
   private static LinkedHashMap<String, String> languagesMap = new LinkedHashMap<String, String>();

   static
   {
      languagesMap.put("ar_SA", "Arabic");
      languagesMap.put("bg_BG", "Bulgarian");
      languagesMap.put("br_FR", "Breton");
      languagesMap.put("bs_BA", "Bosnian");
      languagesMap.put("ca_ES", "Catalan-Valencian");
      languagesMap.put("cs_CZ", "Czech");
      languagesMap.put("da_DK", "Danish");
      languagesMap.put("de_DE", "German");
      languagesMap.put("el_GR", "Greek");
      languagesMap.put("en_US", "English");
      languagesMap.put("eo_EO", "Esperanto");
      languagesMap.put("es_AR", "Spanish (Argentina)");
      languagesMap.put("es_CL", "Spanish (Chile)");
      languagesMap.put("es_ES", "Spanish");

      languagesMap.put("es_MX", "Spanish (Mexico)");
      languagesMap.put("fa_IR", "Persian");
      languagesMap.put("fr_FR", "French");
      languagesMap.put("fy_NL", "Frisian");
      languagesMap.put("gl_ES", "Galician");
      languagesMap.put("gu_IN", "Gujarati");
      languagesMap.put("he_IL", "Hebrew");
      languagesMap.put("hr_HR", "Croatian");
      languagesMap.put("hu_HU", "Hungarian");
      languagesMap.put("it_IT", "Italian");
      languagesMap.put("ja_JP", "Japanese");
      languagesMap.put("ko_KR", "Korean");
      languagesMap.put("lt_LT", "Lithuanian");
      languagesMap.put("nl_NL", "Dutch");

      languagesMap.put("nn_NO", "Norwegian Nynorsk");
      languagesMap.put("no_NO", "Norwegian Bokmal");
      languagesMap.put("pl_PL", "Polish");
      languagesMap.put("pt_BR", "Portuguese");
      languagesMap.put("pt_PT", "Portuguese (Portugal)");
      languagesMap.put("ro_RO", "Romanian");
      languagesMap.put("ru_RU", "Russian");
      languagesMap.put("sv_SE", "Swedish");
      languagesMap.put("tr_TR", "Turkish");
      languagesMap.put("vi_VN", "Vietnamese");
      languagesMap.put("zh_CN", "Simplified Chinese");
      languagesMap.put("zh_TW", "Chinese (Traditional)");
   }

   /**
    * Get the map of supported languages.
    * 
    * @return {@link LinkedHashMap}
    */
   public static LinkedHashMap<String, String> getLanguagesMap()
   {
      return languagesMap;
   }
}
