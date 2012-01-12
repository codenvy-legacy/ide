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
package org.exoplatform.ide.client.framework.util;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class Utils
{

   /**
    * Emulate String.format(String, String)
    * 
    * @param format format string with <i>format specifiers</i> '%s'
    * @param substitute string by which will be replaced one '%s' format specifier in the format parameter
    * @return format string with one time replaced '%s' by substitute string
    */
   public static String stringFormat(String format, String substitute)
   {
      return Utils.javaScriptDecodeURI(format.replaceFirst("%s", substitute));
   }

   public static String stringFormat(String format)
   {
      return Utils.javaScriptDecodeURI(format);
   }

   public static native String getGadgetURLFromUtil() /*-{
                                                      // gathering the gadget's URL from the properties url of document.URL
                                                      if ($wnd.gadgets != null) {
                                                      return $wnd.gadgets.util.getUrlParameters().url.match(/(.*)\//)[1]
                                                      + "/";
                                                      } else {
                                                      return "";
                                                      }
                                                      }-*/;

   public static native void expandGadgetWidth() /*-{
                                                 // set width of gadget to 100%
                                                 if ($wnd.frameElement == null) {
                                                 return;
                                                 }
                                                 $wnd.frameElement.style.width = '100%';
                                                 }-*/;

   public static native void expandGadgetHeight() /*-{
                                                  // set width of gadget to 100%
                                                  if ($wnd.frameElement == null) {
                                                  return;
                                                  }
                                                  $wnd.frameElement.style.width = '100%';
                                                  }-*/;

   // return if result of JavaScript function string.match(new RegExp(pattern, modifiers)) is not null
   public static native boolean match(String string, String pattern, String modifiers) /*-{
                                                                                       return (string.match(new RegExp(pattern, modifiers)) !== null);
                                                                                       }-*/;

   // emulate java.net.URLDecoder.decode(string,"UTF-8"): before calling decodeURIComponent we replacing "+" on "%20", and "%2F" on "/".
   public static native String urlDecode_decode(String string) /*-{
                                                               string = string.replace(/[+]/g, "%20"); // replace "+" on "%20"
                                                               string = string.replace("%2F", "/"); // replace "%2F" on "/"      
                                                               return decodeURIComponent(string);
                                                               }-*/;

   /**
    * @param url
    * @return result of javaScript function <code>unescape(url)</code>
    */
   public static native String unescape(String text) /*-{
                                                     return unescape(text);
                                                     }-*/;

   /**
    * @param url
    * @return result of javaScript function <code>escape(url)</code>
    */
   public static native String escape(String text) /*-{
                                                   return escape(text);
                                                   }-*/;

   /**
    * @param url
    * @return result of javaScript function <code>decodeURI(url)</code>
    */
   public static native String javaScriptDecodeURI(String url) /*-{
                                                               return decodeURI(url);
                                                               }-*/;

   /**
    * @param url
    * @return result of javaScript function <code>encodeURI(url)</code>
    */
   public static native String encodeURI(String url) /*-{
                                                     return encodeURI(url);
                                                     }-*/;

   public static native String getUserAgent() /*-{
                                              return navigator.userAgent.toLowerCase();
                                              }-*/;

   public static native String md5(String string) /*-{
                                                  function RotateLeft(lValue, iShiftBits) {
                                                  return (lValue << iShiftBits) | (lValue >>> (32 - iShiftBits));
                                                  }
                                                  function AddUnsigned(lX, lY) {
                                                  var lX4, lY4, lX8, lY8, lResult;
                                                  lX8 = (lX & 0x80000000);
                                                  lY8 = (lY & 0x80000000);
                                                  lX4 = (lX & 0x40000000);
                                                  lY4 = (lY & 0x40000000);
                                                  lResult = (lX & 0x3FFFFFFF) + (lY & 0x3FFFFFFF);
                                                  if (lX4 & lY4) {
                                                  return (lResult ^ 0x80000000 ^ lX8 ^ lY8);
                                                  }
                                                  if (lX4 | lY4) {
                                                  if (lResult & 0x40000000) {
                                                  return (lResult ^ 0xC0000000 ^ lX8 ^ lY8);
                                                  } else {
                                                  return (lResult ^ 0x40000000 ^ lX8 ^ lY8);
                                                  }
                                                  } else {
                                                  return (lResult ^ lX8 ^ lY8);
                                                  }
                                                  }
                                                  function F(x, y, z) {
                                                  return (x & y) | ((~x) & z);
                                                  }
                                                  function G(x, y, z) {
                                                  return (x & z) | (y & (~z));
                                                  }
                                                  function H(x, y, z) {
                                                  return (x ^ y ^ z);
                                                  }
                                                  function I(x, y, z) {
                                                  return (y ^ (x | (~z)));
                                                  }
                                                  function FF(a, b, c, d, x, s, ac) {
                                                  a = AddUnsigned(a, AddUnsigned(AddUnsigned(F(b, c, d), x), ac));
                                                  return AddUnsigned(RotateLeft(a, s), b);
                                                  }
                                                  ;
                                                  function GG(a, b, c, d, x, s, ac) {
                                                  a = AddUnsigned(a, AddUnsigned(AddUnsigned(G(b, c, d), x), ac));
                                                  return AddUnsigned(RotateLeft(a, s), b);
                                                  }
                                                  ;
                                                  function HH(a, b, c, d, x, s, ac) {
                                                  a = AddUnsigned(a, AddUnsigned(AddUnsigned(H(b, c, d), x), ac));
                                                  return AddUnsigned(RotateLeft(a, s), b);
                                                  }
                                                  ;
                                                  function II(a, b, c, d, x, s, ac) {
                                                  a = AddUnsigned(a, AddUnsigned(AddUnsigned(I(b, c, d), x), ac));
                                                  return AddUnsigned(RotateLeft(a, s), b);
                                                  }
                                                  ;
                                                  function ConvertToWordArray(string) {
                                                  var lWordCount;
                                                  var lMessageLength = string.length;
                                                  var lNumberOfWords_temp1 = lMessageLength + 8;
                                                  var lNumberOfWords_temp2 = (lNumberOfWords_temp1 - (lNumberOfWords_temp1 % 64)) / 64;
                                                  var lNumberOfWords = (lNumberOfWords_temp2 + 1) * 16;
                                                  var lWordArray = Array(lNumberOfWords - 1);
                                                  var lBytePosition = 0;
                                                  var lByteCount = 0;
                                                  while (lByteCount < lMessageLength) {
                                                  lWordCount = (lByteCount - (lByteCount % 4)) / 4;
                                                  lBytePosition = (lByteCount % 4) * 8;
                                                  lWordArray[lWordCount] = (lWordArray[lWordCount] | (string
                                                  .charCodeAt(lByteCount) << lBytePosition));
                                                  lByteCount++;
                                                  }
                                                  lWordCount = (lByteCount - (lByteCount % 4)) / 4;
                                                  lBytePosition = (lByteCount % 4) * 8;
                                                  lWordArray[lWordCount] = lWordArray[lWordCount]
                                                  | (0x80 << lBytePosition);
                                                  lWordArray[lNumberOfWords - 2] = lMessageLength << 3;
                                                  lWordArray[lNumberOfWords - 1] = lMessageLength >>> 29;
                                                  return lWordArray;
                                                  }
                                                  ;
                                                  function WordToHex(lValue) {
                                                  var WordToHexValue = "", WordToHexValue_temp = "", lByte, lCount;
                                                  for (lCount = 0; lCount <= 3; lCount++) {
                                                  lByte = (lValue >>> (lCount * 8)) & 255;
                                                  WordToHexValue_temp = "0" + lByte.toString(16);
                                                  WordToHexValue = WordToHexValue
                                                  + WordToHexValue_temp.substr(
                                                  WordToHexValue_temp.length - 2, 2);
                                                  }
                                                  return WordToHexValue;
                                                  }
                                                  ;
                                                  function Utf8Encode(string) {
                                                  string = string.replace(/\r\n/g, "\n");
                                                  var utftext = "";
                                                  for ( var n = 0; n < string.length; n++) {
                                                  var c = string.charCodeAt(n);
                                                  if (c < 128) {
                                                  utftext += String.fromCharCode(c);
                                                  } else if ((c > 127) && (c < 2048)) {
                                                  utftext += String.fromCharCode((c >> 6) | 192);
                                                  utftext += String.fromCharCode((c & 63) | 128);
                                                  } else {
                                                  utftext += String.fromCharCode((c >> 12) | 224);
                                                  utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                                                  utftext += String.fromCharCode((c & 63) | 128);
                                                  }
                                                  }
                                                  return utftext;
                                                  }
                                                  ;
                                                  var x = Array();
                                                  var k, AA, BB, CC, DD, a, b, c, d;
                                                  var S11 = 7, S12 = 12, S13 = 17, S14 = 22;
                                                  var S21 = 5, S22 = 9, S23 = 14, S24 = 20;
                                                  var S31 = 4, S32 = 11, S33 = 16, S34 = 23;
                                                  var S41 = 6, S42 = 10, S43 = 15, S44 = 21;
                                                  string = Utf8Encode(string);
                                                  x = ConvertToWordArray(string);
                                                  a = 0x67452301;
                                                  b = 0xEFCDAB89;
                                                  c = 0x98BADCFE;
                                                  d = 0x10325476;
                                                  for (k = 0; k < x.length; k += 16) {
                                                  AA = a;
                                                  BB = b;
                                                  CC = c;
                                                  DD = d;
                                                  a = FF(a, b, c, d, x[k + 0], S11, 0xD76AA478);
                                                  d = FF(d, a, b, c, x[k + 1], S12, 0xE8C7B756);
                                                  c = FF(c, d, a, b, x[k + 2], S13, 0x242070DB);
                                                  b = FF(b, c, d, a, x[k + 3], S14, 0xC1BDCEEE);
                                                  a = FF(a, b, c, d, x[k + 4], S11, 0xF57C0FAF);
                                                  d = FF(d, a, b, c, x[k + 5], S12, 0x4787C62A);
                                                  c = FF(c, d, a, b, x[k + 6], S13, 0xA8304613);
                                                  b = FF(b, c, d, a, x[k + 7], S14, 0xFD469501);
                                                  a = FF(a, b, c, d, x[k + 8], S11, 0x698098D8);
                                                  d = FF(d, a, b, c, x[k + 9], S12, 0x8B44F7AF);
                                                  c = FF(c, d, a, b, x[k + 10], S13, 0xFFFF5BB1);
                                                  b = FF(b, c, d, a, x[k + 11], S14, 0x895CD7BE);
                                                  a = FF(a, b, c, d, x[k + 12], S11, 0x6B901122);
                                                  d = FF(d, a, b, c, x[k + 13], S12, 0xFD987193);
                                                  c = FF(c, d, a, b, x[k + 14], S13, 0xA679438E);
                                                  b = FF(b, c, d, a, x[k + 15], S14, 0x49B40821);
                                                  a = GG(a, b, c, d, x[k + 1], S21, 0xF61E2562);
                                                  d = GG(d, a, b, c, x[k + 6], S22, 0xC040B340);
                                                  c = GG(c, d, a, b, x[k + 11], S23, 0x265E5A51);
                                                  b = GG(b, c, d, a, x[k + 0], S24, 0xE9B6C7AA);
                                                  a = GG(a, b, c, d, x[k + 5], S21, 0xD62F105D);
                                                  d = GG(d, a, b, c, x[k + 10], S22, 0x2441453);
                                                  c = GG(c, d, a, b, x[k + 15], S23, 0xD8A1E681);
                                                  b = GG(b, c, d, a, x[k + 4], S24, 0xE7D3FBC8);
                                                  a = GG(a, b, c, d, x[k + 9], S21, 0x21E1CDE6);
                                                  d = GG(d, a, b, c, x[k + 14], S22, 0xC33707D6);
                                                  c = GG(c, d, a, b, x[k + 3], S23, 0xF4D50D87);
                                                  b = GG(b, c, d, a, x[k + 8], S24, 0x455A14ED);
                                                  a = GG(a, b, c, d, x[k + 13], S21, 0xA9E3E905);
                                                  d = GG(d, a, b, c, x[k + 2], S22, 0xFCEFA3F8);
                                                  c = GG(c, d, a, b, x[k + 7], S23, 0x676F02D9);
                                                  b = GG(b, c, d, a, x[k + 12], S24, 0x8D2A4C8A);
                                                  a = HH(a, b, c, d, x[k + 5], S31, 0xFFFA3942);
                                                  d = HH(d, a, b, c, x[k + 8], S32, 0x8771F681);
                                                  c = HH(c, d, a, b, x[k + 11], S33, 0x6D9D6122);
                                                  b = HH(b, c, d, a, x[k + 14], S34, 0xFDE5380C);
                                                  a = HH(a, b, c, d, x[k + 1], S31, 0xA4BEEA44);
                                                  d = HH(d, a, b, c, x[k + 4], S32, 0x4BDECFA9);
                                                  c = HH(c, d, a, b, x[k + 7], S33, 0xF6BB4B60);
                                                  b = HH(b, c, d, a, x[k + 10], S34, 0xBEBFBC70);
                                                  a = HH(a, b, c, d, x[k + 13], S31, 0x289B7EC6);
                                                  d = HH(d, a, b, c, x[k + 0], S32, 0xEAA127FA);
                                                  c = HH(c, d, a, b, x[k + 3], S33, 0xD4EF3085);
                                                  b = HH(b, c, d, a, x[k + 6], S34, 0x4881D05);
                                                  a = HH(a, b, c, d, x[k + 9], S31, 0xD9D4D039);
                                                  d = HH(d, a, b, c, x[k + 12], S32, 0xE6DB99E5);
                                                  c = HH(c, d, a, b, x[k + 15], S33, 0x1FA27CF8);
                                                  b = HH(b, c, d, a, x[k + 2], S34, 0xC4AC5665);
                                                  a = II(a, b, c, d, x[k + 0], S41, 0xF4292244);
                                                  d = II(d, a, b, c, x[k + 7], S42, 0x432AFF97);
                                                  c = II(c, d, a, b, x[k + 14], S43, 0xAB9423A7);
                                                  b = II(b, c, d, a, x[k + 5], S44, 0xFC93A039);
                                                  a = II(a, b, c, d, x[k + 12], S41, 0x655B59C3);
                                                  d = II(d, a, b, c, x[k + 3], S42, 0x8F0CCC92);
                                                  c = II(c, d, a, b, x[k + 10], S43, 0xFFEFF47D);
                                                  b = II(b, c, d, a, x[k + 1], S44, 0x85845DD1);
                                                  a = II(a, b, c, d, x[k + 8], S41, 0x6FA87E4F);
                                                  d = II(d, a, b, c, x[k + 15], S42, 0xFE2CE6E0);
                                                  c = II(c, d, a, b, x[k + 6], S43, 0xA3014314);
                                                  b = II(b, c, d, a, x[k + 13], S44, 0x4E0811A1);
                                                  a = II(a, b, c, d, x[k + 4], S41, 0xF7537E82);
                                                  d = II(d, a, b, c, x[k + 11], S42, 0xBD3AF235);
                                                  c = II(c, d, a, b, x[k + 2], S43, 0x2AD7D2BB);
                                                  b = II(b, c, d, a, x[k + 9], S44, 0xEB86D391);
                                                  a = AddUnsigned(a, AA);
                                                  b = AddUnsigned(b, BB);
                                                  c = AddUnsigned(c, CC);
                                                  d = AddUnsigned(d, DD);
                                                  }
                                                  var temp = WordToHex(a) + WordToHex(b) + WordToHex(c) + WordToHex(d);
                                                  return temp.toLowerCase();
                                                  }-*/;

   public static native String crc32(String str)/*-{
                                                function Utf8Encode(string) {
                                                string = string.replace(/\r\n/g, "\n");
                                                var utftext = "";

                                                for ( var n = 0; n < string.length; n++) {

                                                var c = string.charCodeAt(n);

                                                if (c < 128) {
                                                utftext += String.fromCharCode(c);
                                                } else if ((c > 127) && (c < 2048)) {
                                                utftext += String.fromCharCode((c >> 6) | 192);
                                                utftext += String.fromCharCode((c & 63) | 128);
                                                } else {
                                                utftext += String.fromCharCode((c >> 12) | 224);
                                                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                                                utftext += String.fromCharCode((c & 63) | 128);
                                                }

                                                }

                                                return utftext;
                                                };
                                                

                                                str = Utf8Encode(str);

                                                var table = "00000000 77073096 EE0E612C 990951BA 076DC419 706AF48F E963A535 9E6495A3 0EDB8832 79DCB8A4 E0D5E91E 97D2D988 09B64C2B 7EB17CBD E7B82D07 90BF1D91 1DB71064 6AB020F2 F3B97148 84BE41DE 1ADAD47D 6DDDE4EB F4D4B551 83D385C7 136C9856 646BA8C0 FD62F97A 8A65C9EC 14015C4F 63066CD9 FA0F3D63 8D080DF5 3B6E20C8 4C69105E D56041E4 A2677172 3C03E4D1 4B04D447 D20D85FD A50AB56B 35B5A8FA 42B2986C DBBBC9D6 ACBCF940 32D86CE3 45DF5C75 DCD60DCF ABD13D59 26D930AC 51DE003A C8D75180 BFD06116 21B4F4B5 56B3C423 CFBA9599 B8BDA50F 2802B89E 5F058808 C60CD9B2 B10BE924 2F6F7C87 58684C11 C1611DAB B6662D3D 76DC4190 01DB7106 98D220BC EFD5102A 71B18589 06B6B51F 9FBFE4A5 E8B8D433 7807C9A2 0F00F934 9609A88E E10E9818 7F6A0DBB 086D3D2D 91646C97 E6635C01 6B6B51F4 1C6C6162 856530D8 F262004E 6C0695ED 1B01A57B 8208F4C1 F50FC457 65B0D9C6 12B7E950 8BBEB8EA FCB9887C 62DD1DDF 15DA2D49 8CD37CF3 FBD44C65 4DB26158 3AB551CE A3BC0074 D4BB30E2 4ADFA541 3DD895D7 A4D1C46D D3D6F4FB 4369E96A 346ED9FC AD678846 DA60B8D0 44042D73 33031DE5 AA0A4C5F DD0D7CC9 5005713C 270241AA BE0B1010 C90C2086 5768B525 206F85B3 B966D409 CE61E49F 5EDEF90E 29D9C998 B0D09822 C7D7A8B4 59B33D17 2EB40D81 B7BD5C3B C0BA6CAD EDB88320 9ABFB3B6 03B6E20C 74B1D29A EAD54739 9DD277AF 04DB2615 73DC1683 E3630B12 94643B84 0D6D6A3E 7A6A5AA8 E40ECF0B 9309FF9D 0A00AE27 7D079EB1 F00F9344 8708A3D2 1E01F268 6906C2FE F762575D 806567CB 196C3671 6E6B06E7 FED41B76 89D32BE0 10DA7A5A 67DD4ACC F9B9DF6F 8EBEEFF9 17B7BE43 60B08ED5 D6D6A3E8 A1D1937E 38D8C2C4 4FDFF252 D1BB67F1 A6BC5767 3FB506DD 48B2364B D80D2BDA AF0A1B4C 36034AF6 41047A60 DF60EFC3 A867DF55 316E8EEF 4669BE79 CB61B38C BC66831A 256FD2A0 5268E236 CC0C7795 BB0B4703 220216B9 5505262F C5BA3BBE B2BD0B28 2BB45A92 5CB36A04 C2D7FFA7 B5D0CF31 2CD99E8B 5BDEAE1D 9B64C2B0 EC63F226 756AA39C 026D930A 9C0906A9 EB0E363F 72076785 05005713 95BF4A82 E2B87A14 7BB12BAE 0CB61B38 92D28E9B E5D5BE0D 7CDCEFB7 0BDBDF21 86D3D2D4 F1D4E242 68DDB3F8 1FDA836E 81BE16CD F6B9265B 6FB077E1 18B74777 88085AE6 FF0F6A70 66063BCA 11010B5C 8F659EFF F862AE69 616BFFD3 166CCF45 A00AE278 D70DD2EE 4E048354 3903B3C2 A7672661 D06016F7 4969474D 3E6E77DB AED16A4A D9D65ADC 40DF0B66 37D83BF0 A9BCAE53 DEBB9EC5 47B2CF7F 30B5FFE9 BDBDF21C CABAC28A 53B39330 24B4A3A6 BAD03605 CDD70693 54DE5729 23D967BF B3667A2E C4614AB8 5D681B02 2A6F2B94 B40BBE37 C30C8EA1 5A05DF1B 2D02EF8D";

                                                if (typeof (crc) == "undefined") {
                                                crc = 0;
                                                }
                                                var x = 0;
                                                var y = 0;

                                                crc = crc ^ (-1);
                                                for ( var i = 0, iTop = str.length; i < iTop; i++) {
                                                y = (crc ^ str.charCodeAt(i)) & 0xFF;
                                                x = "0x" + table.substr(y * 9, 8);
                                                crc = (crc >>> 8) ^ x;
                                                }

                                                return crc ^ (-1);
                                                }-*/;

}
