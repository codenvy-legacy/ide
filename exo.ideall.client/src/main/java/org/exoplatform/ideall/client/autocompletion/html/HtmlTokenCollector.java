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
package org.exoplatform.ideall.client.autocompletion.html;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.ideall.client.autocompletion.TokenCollector;
import org.exoplatform.ideall.client.autocompletion.TokensCollectedCallback;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class HtmlTokenCollector implements TokenCollector
{

   private static List<Token> tags = new ArrayList<Token>();

   private static List<String> noEndTag = new ArrayList<String>();

   private static List<Token> attributes = new ArrayList<Token>();

   static
   {
      tags.add(new Token("!DOCTYPE", TokenType.TAG, "", "<!DOCTYPE>", "Defines the document type"));
      tags.add(new Token("a", TokenType.TAG, "", "<a></a>", "Defines an anchor"));
      tags.add(new Token("abbr", TokenType.TAG, "", "<abbr></abbr>", "Defines an abbreviation"));
      tags.add(new Token("acronym", TokenType.TAG, "", "<acronym></acronym>", "Defines an acronym"));
      tags.add(new Token("address", TokenType.TAG, "", "<address></address>",
         "Defines contact information for the author/owner of a document"));
      tags.add(new Token("area", TokenType.TAG, "", "<area />", "Defines an area inside an image-map"));
      tags.add(new Token("b", TokenType.TAG, "", "<b></b>", "Defines bold text"));
      tags.add(new Token("base", TokenType.TAG, "", "<base />",
         "Defines a default address or a default target for all links on a page"));
      tags.add(new Token("bdo", TokenType.TAG, "", "<bdo></bdo>", "Defines the text direction"));
      tags.add(new Token("big", TokenType.TAG, "", "<big></big>", "Defines big text"));
      tags.add(new Token("blockquote", TokenType.TAG, "", "<blockquote></blockquote>", "Defines a long quotation"));
      tags.add(new Token("body", TokenType.TAG, "", "<body></body>", "Defines the document's body"));
      tags.add(new Token("br", TokenType.TAG, "", "<br />", "Defines a single line break"));
      tags.add(new Token("button", TokenType.TAG, "", "<button></button>", "Defines a push button"));
      tags.add(new Token("caption", TokenType.TAG, "", "<caption></caption>", "Defines a table caption"));
      tags.add(new Token("cite", TokenType.TAG, "", "<cite></cite>", "Defines a citation"));
      tags.add(new Token("code", TokenType.TAG, "", "<code></code>", "Defines computer code text"));
      tags.add(new Token("col", TokenType.TAG, "", "<col />",
         "Defines attribute values for one or more columns in a table"));
      tags.add(new Token("colgroup", TokenType.TAG, "", "<colgroup></colgroup>",
         "Defines a group of columns in a table for formatting"));
      tags.add(new Token("dd", TokenType.TAG, "", "<dd></dd>", "Defines a description of a term in a definition list"));
      tags.add(new Token("del", TokenType.TAG, "", "<del></del>", "Defines deleted text"));
      tags.add(new Token("dfn", TokenType.TAG, "", "<dfn></dfn>", "Defines a definition term"));
      tags.add(new Token("div", TokenType.TAG, "", "<div></div>", "Defines a section in a document"));
      tags.add(new Token("dl", TokenType.TAG, "", "<dl></dl>", "Defines a definition list"));
      tags.add(new Token("dt", TokenType.TAG, "", "<dt></dt>", "Defines a term (an item) in a definition list"));
      tags.add(new Token("em", TokenType.TAG, "", "<em></em>", "Defines emphasized text"));
      tags.add(new Token("fieldset", TokenType.TAG, "", "<fieldset></fieldset>",
         "Defines a border around elements in a form"));
      tags.add(new Token("form", TokenType.TAG, "", "<form></form>", "Defines an HTML form for user input"));
      tags.add(new Token("frame", TokenType.TAG, "", "<frame />", "Defines a window (a frame) in a frameset"));
      tags.add(new Token("frameset", TokenType.TAG, "", "<frameset></frameset>", "Defines a set of frames"));
      tags.add(new Token("h1", TokenType.TAG, "", "<h1></h1>", "Defines HTML headings"));
      tags.add(new Token("h2", TokenType.TAG, "", "<h2></h2>", "Defines HTML headings"));
      tags.add(new Token("h3", TokenType.TAG, "", "<h3></h3>", "Defines HTML headings"));
      tags.add(new Token("h4", TokenType.TAG, "", "<h4></h4>", "Defines HTML headings"));
      tags.add(new Token("h5", TokenType.TAG, "", "<h5></h5>", "Defines HTML headings"));
      tags.add(new Token("h6", TokenType.TAG, "", "<h6></h6>", "Defines HTML headings"));
      tags.add(new Token("head", TokenType.TAG, "", "<head></head>", "Defines information about the document"));
      tags.add(new Token("hr", TokenType.TAG, "", "<hr />", "Defines a horizontal line"));
      tags.add(new Token("html", TokenType.TAG, "", "<html></html>", "Defines an HTML document"));
      tags.add(new Token("i", TokenType.TAG, "", "<i></i>", "Defines italic text"));
      tags.add(new Token("iframe", TokenType.TAG, "", "<iframe></iframe>", "Defines an inline frame"));
      tags.add(new Token("img", TokenType.TAG, "", "<img />", "Defines an image"));
      tags.add(new Token("input", TokenType.TAG, "", "<input />", "Defines an input control"));
      tags.add(new Token("ins", TokenType.TAG, "", "<ins></ins>", "Defines inserted text"));
      tags.add(new Token("kbd", TokenType.TAG, "", "<kbd></kbd>", "Defines keyboard text"));
      tags.add(new Token("label", TokenType.TAG, "", "<label></label>", "Defines a label for an input element"));
      tags.add(new Token("legend", TokenType.TAG, "", "<legend></legend>", "Defines a caption for a fieldset element"));
      tags.add(new Token("li", TokenType.TAG, "", "<li></li>", "Defines a list item"));
      tags.add(new Token("link", TokenType.TAG, "", "<link />",
         "Defines the relationship between a document and an external resource"));
      tags.add(new Token("map", TokenType.TAG, "", "<map></map>", "Defines an image-map"));
      tags.add(new Token("meta", TokenType.TAG, "", "<meta />", "Defines metadata about an HTML document"));
      tags.add(new Token("noframes", TokenType.TAG, "", "<noframes></noframes>",
         "Defines an alternate content for users that do not support frames"));
      tags.add(new Token("noscript", TokenType.TAG, "", "<noscript></noscript>",
         "Defines an alternate content for users that do not support client-side scripts"));
      tags.add(new Token("object", TokenType.TAG, "", "<object></object>", "Defines an embedded object"));
      tags.add(new Token("ol", TokenType.TAG, "", "<ol></ol>", "Defines an ordered list"));
      tags.add(new Token("optgroup", TokenType.TAG, "", "<optgroup></optgroup>",
         "Defines a group of related options in a select list"));
      tags.add(new Token("optin", TokenType.TAG, "", "<option></option>", "Defines an option in a select list"));
      tags.add(new Token("p", TokenType.TAG, "", "<p></p>", "Defines a paragraph"));
      tags.add(new Token("param", TokenType.TAG, "", "<param />", "Defines a parameter for an object"));
      tags.add(new Token("pre", TokenType.TAG, "", "<pre></pre>", "Defines preformatted text"));
      tags.add(new Token("q", TokenType.TAG, "", "<q></q>", "Defines a short quotation"));
      tags.add(new Token("samp", TokenType.TAG, "", "<samp></samp>", "Defines sample computer code"));
      tags.add(new Token("script", TokenType.TAG, "", "<script>/<script>", "Defines a client-side script"));
      tags.add(new Token("select", TokenType.TAG, "", "<select></select>", "Defines a select list (drop-down list)"));
      tags.add(new Token("small", TokenType.TAG, "", "<small></small>", "Defines small text"));
      tags.add(new Token("span", TokenType.TAG, "", "<span></span>", "Defines a section in a document"));
      tags.add(new Token("strong", TokenType.TAG, "", "<strong></strong>", "Defines strong text"));
      tags.add(new Token("style", TokenType.TAG, "", "<style></style>", "Defines style information for a document"));
      tags.add(new Token("sub", TokenType.TAG, "", "<sub></sub>", "Defines subscripted text"));
      tags.add(new Token("sup", TokenType.TAG, "", "<sup></sup>", "Defines superscripted text"));
      tags.add(new Token("table", TokenType.TAG, "", "<table></table>", "Defines a table"));
      tags.add(new Token("tbody", TokenType.TAG, "", "<tbody></tbody>", "Groups the body content in a table"));
      tags.add(new Token("td", TokenType.TAG, "", "<td></td>", "Defines a cell in a table"));
      tags.add(new Token("textarea", TokenType.TAG, "", "<textarea></textarea>",
         "Defines a multi-line text input control"));
      tags.add(new Token("tfoot", TokenType.TAG, "", "<tfoot></tfoot>", "Groups the footer content in a table"));
      tags.add(new Token("th", TokenType.TAG, "", "<th></th>", "Defines a header cell in a table"));
      tags.add(new Token("thead", TokenType.TAG, "", "<thead></thead>", "Groups the header content in a table"));
      tags.add(new Token("title", TokenType.TAG, "", "<title></title>", "Defines the title of a document"));
      tags.add(new Token("tr", TokenType.TAG, "", "<tr></tr>", "Defines a row in a table"));
      tags.add(new Token("tt", TokenType.TAG, "", "<tt></tt>", "Defines teletype text"));
      tags.add(new Token("ul", TokenType.TAG, "", "<ul></ul>", "Defines an unordered list"));
      tags.add(new Token("var", TokenType.TAG, "", "<var></var>", "Defines a variable part of a text"));

      noEndTag.add("area");
      noEndTag.add("base");
      noEndTag.add("br");
      noEndTag.add("col");
      noEndTag.add("frame");
      noEndTag.add("hr");
      noEndTag.add("img");
      noEndTag.add("input");
      noEndTag.add("link");
      noEndTag.add("meta");
      noEndTag.add("param");

      attributes.add(new Token("abbr", TokenType.ATTRIBUTE, null, "abbr=\"\"", null));
      attributes.add(new Token("accept-charset", TokenType.ATTRIBUTE, null, "accept-charset=\"\"", null));
      attributes.add(new Token("accept", TokenType.ATTRIBUTE, null, "accept=\"\"", null));
      attributes.add(new Token("accesskey", TokenType.ATTRIBUTE, null, "accesskey=\"\"", null));
      attributes.add(new Token("action", TokenType.ATTRIBUTE, null, "action=\"\"", null));
      attributes.add(new Token("align", TokenType.ATTRIBUTE, null, "align=\"\"", null));
      attributes.add(new Token("alink", TokenType.ATTRIBUTE, null, "alink=\"\"", null));
      attributes.add(new Token("alt", TokenType.ATTRIBUTE, null, "alt=\"\"", null));
      attributes.add(new Token("archive", TokenType.ATTRIBUTE, null, "archive=\"\"", null));
      attributes.add(new Token("axis", TokenType.ATTRIBUTE, null, "axis=\"\"", null));
      attributes.add(new Token("background", TokenType.ATTRIBUTE, null, "background=\"\"", null));
      attributes.add(new Token("bgcolor", TokenType.ATTRIBUTE, null, "bgcolor=\"\"", null));
      attributes.add(new Token("border", TokenType.ATTRIBUTE, null, "border=\"\"", null));
      attributes.add(new Token("cellpadding", TokenType.ATTRIBUTE, null, "cellpadding=\"\"", null));
      attributes.add(new Token("char", TokenType.ATTRIBUTE, null, "char=\"\"", null));
      attributes.add(new Token("charoff", TokenType.ATTRIBUTE, null, "charoff=\"\"", null));
      attributes.add(new Token("charset", TokenType.ATTRIBUTE, null, "charset=\"\"", null));
      attributes.add(new Token("checked", TokenType.ATTRIBUTE, null, "checked=\"\"", null));
      attributes.add(new Token("cite", TokenType.ATTRIBUTE, null, "cite=\"\"", null));
      attributes.add(new Token("class", TokenType.ATTRIBUTE, null, "class=\"\"", null));
      attributes.add(new Token("classid", TokenType.ATTRIBUTE, null, "classid=\"\"", null));
      attributes.add(new Token("clear", TokenType.ATTRIBUTE, null, "clear=\"\"", null));
      attributes.add(new Token("code", TokenType.ATTRIBUTE, null, "code=\"\"", null));
      attributes.add(new Token("codebase", TokenType.ATTRIBUTE, null, "codebase=\"\"", null));
      attributes.add(new Token("codetype", TokenType.ATTRIBUTE, null, "codetype=\"\"", null));
      attributes.add(new Token("color", TokenType.ATTRIBUTE, null, "color=\"\"", null));
      attributes.add(new Token("cols", TokenType.ATTRIBUTE, null, "cols=\"\"", null));
      attributes.add(new Token("colspan", TokenType.ATTRIBUTE, null, "colspan=\"\"", null));
      attributes.add(new Token("compact", TokenType.ATTRIBUTE, null, "compact=\"\"", null));
      attributes.add(new Token("content", TokenType.ATTRIBUTE, null, "content=\"\"", null));
      attributes.add(new Token("coords", TokenType.ATTRIBUTE, null, "coords=\"\"", null));
      attributes.add(new Token("data", TokenType.ATTRIBUTE, null, "data=\"\"", null));
      attributes.add(new Token("datetime", TokenType.ATTRIBUTE, null, "datetime=\"\"", null));
      attributes.add(new Token("declare", TokenType.ATTRIBUTE, null, "declare=\"\"", null));
      attributes.add(new Token("defer", TokenType.ATTRIBUTE, null, "defer=\"\"", null));
      attributes.add(new Token("dir", TokenType.ATTRIBUTE, null, "dir=\"\"", null));
      attributes.add(new Token("disabled", TokenType.ATTRIBUTE, null, "disabled=\"\"", null));
      attributes.add(new Token("enctype", TokenType.ATTRIBUTE, null, "enctype=\"\"", null));
      attributes.add(new Token("face", TokenType.ATTRIBUTE, null, "face=\"\"", null));
      attributes.add(new Token("for", TokenType.ATTRIBUTE, null, "for=\"\"", null));
      attributes.add(new Token("frame", TokenType.ATTRIBUTE, null, "frame=\"\"", null));
      attributes.add(new Token("frameborder", TokenType.ATTRIBUTE, null, "frameborder=\"\"", null));
      attributes.add(new Token("headers", TokenType.ATTRIBUTE, null, "headers=\"\"", null));
      attributes.add(new Token("height", TokenType.ATTRIBUTE, null, "height=\"\"", null));
      attributes.add(new Token("href", TokenType.ATTRIBUTE, null, "href=\"\"", null));
      attributes.add(new Token("hreflang", TokenType.ATTRIBUTE, null, "hreflang=\"\"", null));
      attributes.add(new Token("hspace", TokenType.ATTRIBUTE, null, "hspace=\"\"", null));
      attributes.add(new Token("http-equiv", TokenType.ATTRIBUTE, null, "http-equiv=\"\"", null));
      attributes.add(new Token("id", TokenType.ATTRIBUTE, null, "id=\"\"", null));
      attributes.add(new Token("ismap", TokenType.ATTRIBUTE, null, "ismap=\"\"", null));
      attributes.add(new Token("label", TokenType.ATTRIBUTE, null, "label=\"\"", null));
      attributes.add(new Token("lang", TokenType.ATTRIBUTE, null, "lang=\"\"", null));
      attributes.add(new Token("language", TokenType.ATTRIBUTE, null, "language=\"\"", null));
      attributes.add(new Token("link", TokenType.ATTRIBUTE, null, "link=\"\"", null));
      attributes.add(new Token("longdesc", TokenType.ATTRIBUTE, null, "longdesc=\"\"", null));
      attributes.add(new Token("marginheight", TokenType.ATTRIBUTE, null, "marginheight=\"\"", null));
      attributes.add(new Token("marginwidth", TokenType.ATTRIBUTE, null, "marginwidth=\"\"", null));
      attributes.add(new Token("maxlength", TokenType.ATTRIBUTE, null, "maxlength=\"\"", null));
      attributes.add(new Token("media", TokenType.ATTRIBUTE, null, "media=\"\"", null));
      attributes.add(new Token("method", TokenType.ATTRIBUTE, null, "method=\"\"", null));
      attributes.add(new Token("multiple", TokenType.ATTRIBUTE, null, "multiple=\"\"", null));
      attributes.add(new Token("name", TokenType.ATTRIBUTE, null, "name=\"\"", null));
      attributes.add(new Token("nohref", TokenType.ATTRIBUTE, null, "nohref=\"\"", null));
      attributes.add(new Token("noresize", TokenType.ATTRIBUTE, null, "noresize=\"\"", null));
      attributes.add(new Token("noshade", TokenType.ATTRIBUTE, null, "noshade=\"\"", null));
      attributes.add(new Token("nowrap", TokenType.ATTRIBUTE, null, "nowrap=\"\"", null));
      attributes.add(new Token("object", TokenType.ATTRIBUTE, null, "object=\"\"", null));
      attributes.add(new Token("onblur", TokenType.ATTRIBUTE, null, "onblur=\"\"", null));
      attributes.add(new Token("onchange", TokenType.ATTRIBUTE, null, "onchange=\"\"", null));
      attributes.add(new Token("onclick", TokenType.ATTRIBUTE, null, "onclick=\"\"", null));
      attributes.add(new Token("ondblclick", TokenType.ATTRIBUTE, null, "ondblclick=\"\"", null));
      attributes.add(new Token("onfocus", TokenType.ATTRIBUTE, null, "onfocus=\"\"", null));
      attributes.add(new Token("onkeydown", TokenType.ATTRIBUTE, null, "onkeydown=\"\"", null));
      attributes.add(new Token("onkeypress", TokenType.ATTRIBUTE, null, "onkeypress=\"\"", null));
      attributes.add(new Token("onkeyup", TokenType.ATTRIBUTE, null, "onkeyup=\"\"", null));
      attributes.add(new Token("onload", TokenType.ATTRIBUTE, null, "onload=\"\"", null));
      attributes.add(new Token("onmousedown", TokenType.ATTRIBUTE, null, "onmousedown=\"\"", null));
      attributes.add(new Token("onmousemove", TokenType.ATTRIBUTE, null, "onmousemove=\"\"", null));
      attributes.add(new Token("onmouseout", TokenType.ATTRIBUTE, null, "onmouseout=\"\"", null));
      attributes.add(new Token("onmouseover", TokenType.ATTRIBUTE, null, "onmouseover=\"\"", null));
      attributes.add(new Token("onmouseup", TokenType.ATTRIBUTE, null, "onmouseup=\"\"", null));
      attributes.add(new Token("onreset", TokenType.ATTRIBUTE, null, "onreset=\"\"", null));
      attributes.add(new Token("onselect", TokenType.ATTRIBUTE, null, "onselect=\"\"", null));
      attributes.add(new Token("onsubmit", TokenType.ATTRIBUTE, null, "onsubmit=\"\"", null));
      attributes.add(new Token("onunload", TokenType.ATTRIBUTE, null, "onunload=\"\"", null));
      attributes.add(new Token("profile", TokenType.ATTRIBUTE, null, "profile=\"\"", null));
      attributes.add(new Token("prompt", TokenType.ATTRIBUTE, null, "prompt=\"\"", null));
      attributes.add(new Token("readonly", TokenType.ATTRIBUTE, null, "readonly=\"\"", null));
      attributes.add(new Token("rel", TokenType.ATTRIBUTE, null, "rel=\"\"", null));
      attributes.add(new Token("rev", TokenType.ATTRIBUTE, null, "rev=\"\"", null));
      attributes.add(new Token("rows", TokenType.ATTRIBUTE, null, "rows=\"\"", null));
      attributes.add(new Token("rowspan", TokenType.ATTRIBUTE, null, "rowspan=\"\"", null));
      attributes.add(new Token("rules", TokenType.ATTRIBUTE, null, "rules=\"\"", null));
      attributes.add(new Token("scheme", TokenType.ATTRIBUTE, null, "scheme=\"\"", null));
      attributes.add(new Token("scope", TokenType.ATTRIBUTE, null, "scope=\"\"", null));
      attributes.add(new Token("scrolling", TokenType.ATTRIBUTE, null, "scrolling=\"\"", null));
      attributes.add(new Token("selected", TokenType.ATTRIBUTE, null, "selected=\"\"", null));
      attributes.add(new Token("shape", TokenType.ATTRIBUTE, null, "shape=\"\"", null));
      attributes.add(new Token("shape", TokenType.ATTRIBUTE, null, "shape=\"\"", null));
      attributes.add(new Token("size", TokenType.ATTRIBUTE, null, "size=\"\"", null));
      attributes.add(new Token("span", TokenType.ATTRIBUTE, null, "span=\"\"", null));
      attributes.add(new Token("src", TokenType.ATTRIBUTE, null, "src=\"\"", null));
      attributes.add(new Token("standby", TokenType.ATTRIBUTE, null, "standby=\"\"", null));
      attributes.add(new Token("start", TokenType.ATTRIBUTE, null, "start=\"\"", null));
      attributes.add(new Token("style", TokenType.ATTRIBUTE, null, "style=\"\"", null));
      attributes.add(new Token("summary", TokenType.ATTRIBUTE, null, "summary=\"\"", null));
      attributes.add(new Token("tabindex", TokenType.ATTRIBUTE, null, "tabindex=\"\"", null));
      attributes.add(new Token("target", TokenType.ATTRIBUTE, null, "target=\"\"", null));
      attributes.add(new Token("text", TokenType.ATTRIBUTE, null, "text=\"\"", null));
      attributes.add(new Token("title", TokenType.ATTRIBUTE, null, "title=\"\"", null));
      attributes.add(new Token("type", TokenType.ATTRIBUTE, null, "type=\"\"", null));
      attributes.add(new Token("usemap", TokenType.ATTRIBUTE, null, "usemap=\"\"", null));
      attributes.add(new Token("valign", TokenType.ATTRIBUTE, null, "valign=\"\"", null));
      attributes.add(new Token("value", TokenType.ATTRIBUTE, null, "value=\"\"", null));
      attributes.add(new Token("valuetype", TokenType.ATTRIBUTE, null, "valuetype=\"\"", null));
      attributes.add(new Token("version", TokenType.ATTRIBUTE, null, "version=\"\"", null));
      attributes.add(new Token("vlink", TokenType.ATTRIBUTE, null, "vlink=\"\"", null));
      attributes.add(new Token("vspace", TokenType.ATTRIBUTE, null, "vspace=\"\"", null));
      attributes.add(new Token("width", TokenType.ATTRIBUTE, null, "width=\"\"", null));
   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private TokensCollectedCallback tokensCollectedCallback;

   private String beforeToken;

   private String afterToken;

   private String tokenToComplete;

   private boolean isTag = false;

   public HtmlTokenCollector(HandlerManager eventBus, ApplicationContext context,
      TokensCollectedCallback tokensCollectedCallback)
   {
      this.context = context;
      this.eventBus = eventBus;
      this.tokensCollectedCallback = tokensCollectedCallback;
   }

   /**
    * @see org.exoplatform.ideall.client.autocompletion.TokenCollector#getTokens(java.lang.String, int, int, java.util.List)
    */
   public void getTokens(String line, int lineNum, int cursorPos, List<Token> tokenFromParser)
   {
      List<Token> token = new ArrayList<Token>();

      parseTokenLine(line, cursorPos);

      if (!isTag)
      {
         token.addAll(tags);
         tokensCollectedCallback.onTokensCollected(token, beforeToken, tokenToComplete, afterToken);
         return;
      }

      if (tokenToComplete.endsWith(" ") || tokenToComplete.endsWith("\""))
      { //add attributes
         String tag = "";

         tag = tokenToComplete.substring(0, tokenToComplete.indexOf(" "));

         if (noEndTag.contains(tag))
         {
            token.add(new Token(tag, TokenType.TAG, "close tag with '/>'", " />", null));
         }
         else
         {
            token.add(new Token(tag, TokenType.TAG, "close tag with '></" + tag + ">'", "></" + tag + ">", null));
            token.add(new Token(tag, TokenType.TAG, "close tag with '>'", ">", null));
         }
         beforeToken += tokenToComplete;
         tokenToComplete = "";
         token.addAll(attributes);

      }
      else
      {
         token.addAll(tags);
      }

      tokensCollectedCallback.onTokensCollected(token, beforeToken, tokenToComplete, afterToken);
   }

   /**
    * @param line
    */
   private void parseTokenLine(String line, int cursorPos)
   {
      String tokenLine = "";
      afterToken = line.substring(cursorPos - 1, line.length());
      tokenLine = line.substring(0, cursorPos - 1);
      if (tokenLine.contains("<"))
      {
         beforeToken = tokenLine.substring(0, tokenLine.lastIndexOf("<") + 1);
         tokenLine = tokenLine.substring(tokenLine.lastIndexOf("<") + 1, tokenLine.length());
         if (tokenLine.contains(">"))
         {
            isTag = false;
            tokenToComplete = "";
            beforeToken = line.substring(0, cursorPos - 1);
         }
         else
         {
            isTag = true;
            tokenToComplete = tokenLine;
         }
      }
      else
      {
         beforeToken = tokenLine;
         tokenToComplete = "";
      }

   }

}
