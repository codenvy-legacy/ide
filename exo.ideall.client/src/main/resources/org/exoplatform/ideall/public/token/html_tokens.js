var html_tokens = [
		{
			"name" : "!DOCTYPE",
			"type" : "TAG",
			"code" : "<!DOCTYPE>",
			"fullDescription" : "Defines the document type"
		},
		{
			"name" : "a",
			"type" : "TAG",
			"code" : "<a></a>",
			"fullDescription" : "Defines an anchor"
		},
		{
			"name" : "abbr",
			"type" : "TAG",
			"code" : "<abbr></abbr>",
			"fullDescription" : "Defines an abbreviation"
		},
		{
			"name" : "acronym",
			"type" : "TAG",
			"code" : "<acronym></acronym>",
			"fullDescription" : "Defines an acronym"
		},
		{
			"name" : "address",
			"type" : "TAG",
			"code" : "<address></address>",
			"fullDescription" : "Defines contact information for the author/owner of a document"
		},
		{
			"name" : "area",
			"type" : "TAG",
			"code" : "<area />",
			"fullDescription" : "Defines an area inside an image-map"
		},
		{
			"name" : "b",
			"type" : "TAG",
			"code" : "<b></b>",
			"fullDescription" : "Defines bold text"
		},
		{
			"name" : "base",
			"type" : "TAG",
			"code" : "<base />",
			"fullDescription" : "Defines a default address or a default target for all links on a page"
		},
		{
			"name" : "bdo",
			"type" : "TAG",
			"code" : "<bdo></bdo>",
			"fullDescription" : "Defines the text direction"
		},
		{
			"name" : "big",
			"type" : "TAG",
			"code" : "<big></big>",
			"fullDescription" : "Defines big text"
		},
		{
			"name" : "blockquote",
			"type" : "TAG",
			"code" : "<blockquote></blockquote>",
			"fullDescription" : "Defines a long quotation"
		},
		{
			"name" : "body",
			"type" : "TAG",
			"code" : "<body></body>",
			"fullDescription" : "Defines the document's body"
		},
		{
			"name" : "br",
			"type" : "TAG",
			"code" : "<br />",
			"fullDescription" : "Defines a single line break"
		},
		{
			"name" : "button",
			"type" : "TAG",
			"code" : "<button></button>",
			"fullDescription" : "Defines a push button"
		},
		{
			"name" : "caption",
			"type" : "TAG",
			"code" : "<caption></caption>",
			"fullDescription" : "Defines a table caption"
		},
		{
			"name" : "cite",
			"type" : "TAG",
			"code" : "<cite></cite>",
			"fullDescription" : "Defines a citation"
		},
		{
			"name" : "code",
			"type" : "TAG",
			"code" : "<code></code>",
			"fullDescription" : "Defines computer code text"
		},
		{
			"name" : "col",
			"type" : "TAG",
			"code" : "<col />",
			"fullDescription" : "Defines attribute values for one or more columns in a table"
		},
		{
			"name" : "colgroup",
			"type" : "TAG",
			"code" : "<colgroup></colgroup>",
			"fullDescription" : "Defines a group of columns in a table for formatting"
		},
		{
			"name" : "dd",
			"type" : "TAG",
			"code" : "<dd></dd>",
			"fullDescription" : "Defines a description of a term in a definition list"
		},
		{
			"name" : "del",
			"type" : "TAG",
			"code" : "<del></del>",
			"fullDescription" : "Defines deleted text"
		},
		{
			"name" : "dfn",
			"type" : "TAG",
			"code" : "<dfn></dfn>",
			"fullDescription" : "Defines a definition term"
		},
		{
			"name" : "div",
			"type" : "TAG",
			"code" : "<div></div>",
			"fullDescription" : "Defines a section in a document"
		},
		{
			"name" : "dl",
			"type" : "TAG",
			"code" : "<dl></dl>",
			"fullDescription" : "Defines a definition list"
		},
		{
			"name" : "dt",
			"type" : "TAG",
			"code" : "<dt></dt>",
			"fullDescription" : "Defines a term (an item) in a definition list"
		},
		{
			"name" : "em",
			"type" : "TAG",
			"code" : "<em></em>",
			"fullDescription" : "Defines emphasized text"
		},
		{
			"name" : "fieldset",
			"type" : "TAG",
			"code" : "<fieldset></fieldset>",
			"fullDescription" : "Defines a border around elements in a form"
		},
		{
			"name" : "form",
			"type" : "TAG",
			"code" : "<form></form>",
			"fullDescription" : "Defines an HTML form for user input"
		},
		{
			"name" : "frame",
			"type" : "TAG",
			"code" : "<frame />",
			"fullDescription" : "Defines a window (a frame) in a frameset"
		},
		{
			"name" : "frameset",
			"type" : "TAG",
			"code" : "<frameset></frameset>",
			"fullDescription" : "Defines a set of frames"
		},
		{
			"name" : "h1",
			"type" : "TAG",
			"code" : "<h1></h1>",
			"fullDescription" : "Defines HTML headings"
		},
		{
			"name" : "h2",
			"type" : "TAG",
			"code" : "<h2></h2>",
			"fullDescription" : "Defines HTML headings"
		},
		{
			"name" : "h3",
			"type" : "TAG",
			"code" : "<h3></h3>",
			"fullDescription" : "Defines HTML headings"
		},
		{
			"name" : "h4",
			"type" : "TAG",
			"code" : "<h4></h4>",
			"fullDescription" : "Defines HTML headings"
		},
		{
			"name" : "h5",
			"type" : "TAG",
			"code" : "<h5></h5>",
			"fullDescription" : "Defines HTML headings"
		},
		{
			"name" : "h6",
			"type" : "TAG",
			"code" : "<h6></h6>",
			"fullDescription" : "Defines HTML headings"
		},
		{
			"name" : "head",
			"type" : "TAG",
			"code" : "<head></head>",
			"fullDescription" : "Defines information about the document"
		},
		{
			"name" : "hr",
			"type" : "TAG",
			"code" : "<hr />",
			"fullDescription" : "Defines a horizontal line"
		},
		{
			"name" : "html",
			"type" : "TAG",
			"code" : "<html></html>",
			"fullDescription" : "Defines an HTML document"
		},
		{
			"name" : "i",
			"type" : "TAG",
			"code" : "<i></i>",
			"fullDescription" : "Defines italic text"
		},
		{
			"name" : "iframe",
			"type" : "TAG",
			"code" : "<iframe></iframe>",
			"fullDescription" : "Defines an inline frame"
		},
		{
			"name" : "img",
			"type" : "TAG",
			"code" : "<img />",
			"fullDescription" : "Defines an image"
		},
		{
			"name" : "input",
			"type" : "TAG",
			"code" : "<input />",
			"fullDescription" : "Defines an input control"
		},
		{
			"name" : "ins",
			"type" : "TAG",
			"code" : "<ins></ins>",
			"fullDescription" : "Defines inserted text"
		},
		{
			"name" : "kbd",
			"type" : "TAG",
			"code" : "<kbd></kbd>",
			"fullDescription" : "Defines keyboard text"
		},
		{
			"name" : "label",
			"type" : "TAG",
			"code" : "<label></label>",
			"fullDescription" : "Defines a label for an input element"
		},
		{
			"name" : "legend",
			"type" : "TAG",
			"code" : "<legend></legend>",
			"fullDescription" : "Defines a caption for a fieldset element"
		},
		{
			"name" : "li",
			"type" : "TAG",
			"code" : "<li></li>",
			"fullDescription" : "Defines a list item"
		},
		{
			"name" : "link",
			"type" : "TAG",
			"code" : "<link />",
			"fullDescription" : "Defines the relationship between a document and an external resource"
		},
		{
			"name" : "map",
			"type" : "TAG",
			"code" : "<map></map>",
			"fullDescription" : "Defines an image-map"
		},
		{
			"name" : "meta",
			"type" : "TAG",
			"code" : "<meta />",
			"fullDescription" : "Defines metadata about an HTML document"
		},
		{
			"name" : "noframes",
			"type" : "TAG",
			"code" : "<noframes></noframes>",
			"fullDescription" : "Defines an alternate content for users that do not support frames"
		},
		{
			"name" : "noscript",
			"type" : "TAG",
			"code" : "<noscript></noscript>",
			"fullDescription" : "Defines an alternate content for users that do not support client-side scripts"
		},
		{
			"name" : "object",
			"type" : "TAG",
			"code" : "<object></object>",
			"fullDescription" : "Defines an embedded object"
		},
		{
			"name" : "ol",
			"type" : "TAG",
			"code" : "<ol></ol>",
			"fullDescription" : "Defines an ordered list"
		},
		{
			"name" : "optgroup",
			"type" : "TAG",
			"code" : "<optgroup></optgroup>",
			"fullDescription" : "Defines a group of related options in a select list"
		}, {
			"name" : "optin",
			"type" : "TAG",
			"code" : "<option></option>",
			"fullDescription" : "Defines an option in a select list"
		}, {
			"name" : "p",
			"type" : "TAG",
			"code" : "<p></p>",
			"fullDescription" : "Defines a paragraph"
		}, {
			"name" : "param",
			"type" : "TAG",
			"code" : "<param />",
			"fullDescription" : "Defines a parameter for an object"
		}, {
			"name" : "pre",
			"type" : "TAG",
			"code" : "<pre></pre>",
			"fullDescription" : "Defines preformatted text"
		}, {
			"name" : "q",
			"type" : "TAG",
			"code" : "<q></q>",
			"fullDescription" : "Defines a short quotation"
		}, {
			"name" : "samp",
			"type" : "TAG",
			"code" : "<samp></samp>",
			"fullDescription" : "Defines sample computer code"
		}, {
			"name" : "script",
			"type" : "TAG",
			"code" : "<script>/<script>",
			"fullDescription" : "Defines a client-side script"
		}, {
			"name" : "select",
			"type" : "TAG",
			"code" : "<select></select>",
			"fullDescription" : "Defines a select list (drop-down list)"
		}, {
			"name" : "small",
			"type" : "TAG",
			"code" : "<small></small>",
			"fullDescription" : "Defines small text"
		}, {
			"name" : "span",
			"type" : "TAG",
			"code" : "<span></span>",
			"fullDescription" : "Defines a section in a document"
		}, {
			"name" : "strong",
			"type" : "TAG",
			"code" : "<strong></strong>",
			"fullDescription" : "Defines strong text"
		}, {
			"name" : "style",
			"type" : "TAG",
			"code" : "<style></style>",
			"fullDescription" : "Defines style information for a document"
		}, {
			"name" : "sub",
			"type" : "TAG",
			"code" : "<sub></sub>",
			"fullDescription" : "Defines subscripted text"
		}, {
			"name" : "sup",
			"type" : "TAG",
			"code" : "<sup></sup>",
			"fullDescription" : "Defines superscripted text"
		}, {
			"name" : "table",
			"type" : "TAG",
			"code" : "<table></table>",
			"fullDescription" : "Defines a table"
		}, {
			"name" : "tbody",
			"type" : "TAG",
			"code" : "<tbody></tbody>",
			"fullDescription" : "Groups the body content in a table"
		}, {
			"name" : "td",
			"type" : "TAG",
			"code" : "<td></td>",
			"fullDescription" : "Defines a cell in a table"
		}, {
			"name" : "textarea",
			"type" : "TAG",
			"code" : "<textarea></textarea>",
			"fullDescription" : "Defines a multi-line text input control"
		}, {
			"name" : "tfoot",
			"type" : "TAG",
			"code" : "<tfoot></tfoot>",
			"fullDescription" : "Groups the footer content in a table"
		}, {
			"name" : "th",
			"type" : "TAG",
			"code" : "<th></th>",
			"fullDescription" : "Defines a header cell in a table"
		}, {
			"name" : "thead",
			"type" : "TAG",
			"code" : "<thead></thead>",
			"fullDescription" : "Groups the header content in a table"
		}, {
			"name" : "title",
			"type" : "TAG",
			"code" : "<title></title>",
			"fullDescription" : "Defines the title of a document"
		}, {
			"name" : "tr",
			"type" : "TAG",
			"code" : "<tr></tr>",
			"fullDescription" : "Defines a row in a table"
		}, {
			"name" : "tt",
			"type" : "TAG",
			"code" : "<tt></tt>",
			"fullDescription" : "Defines teletype text"
		}, {
			"name" : "ul",
			"type" : "TAG",
			"code" : "<ul></ul>",
			"fullDescription" : "Defines an unordered list"
		}, {
			"name" : "var",
			"type" : "TAG",
			"code" : "<var></var>",
			"fullDescription" : "Defines a variable part of a text"
		}, {
			"name" : "abbr",
			"type" : "ATTRIBUTE",
			"code" : "abbr=\"\""
		}, {
			"name" : "accept-charset",
			"type" : "ATTRIBUTE",
			"code" : "accept-charset=\"\""
		}, {
			"name" : "accept",
			"type" : "ATTRIBUTE",
			"code" : "accept=\"\""
		}, {
			"name" : "accesskey",
			"type" : "ATTRIBUTE",
			"code" : "accesskey=\"\""
		}, {
			"name" : "action",
			"type" : "ATTRIBUTE",
			"code" : "action=\"\""
		}, {
			"name" : "align",
			"type" : "ATTRIBUTE",
			"code" : "align=\"\""
		}, {
			"name" : "alink",
			"type" : "ATTRIBUTE",
			"code" : "alink=\"\""
		}, {
			"name" : "alt",
			"type" : "ATTRIBUTE",
			"code" : "alt=\"\""
		}, {
			"name" : "archive",
			"type" : "ATTRIBUTE",
			"code" : "archive=\"\""
		}, {
			"name" : "axis",
			"type" : "ATTRIBUTE",
			"code" : "axis=\"\""
		}, {
			"name" : "background",
			"type" : "ATTRIBUTE",
			"code" : "background=\"\""
		}, {
			"name" : "bgcolor",
			"type" : "ATTRIBUTE",
			"code" : "bgcolor=\"\""
		}, {
			"name" : "border",
			"type" : "ATTRIBUTE",
			"code" : "border=\"\""
		}, {
			"name" : "cellpadding",
			"type" : "ATTRIBUTE",
			"code" : "cellpadding=\"\""
		}, {
			"name" : "char",
			"type" : "ATTRIBUTE",
			"code" : "char=\"\""
		}, {
			"name" : "charoff",
			"type" : "ATTRIBUTE",
			"code" : "charoff=\"\""
		}, {
			"name" : "charset",
			"type" : "ATTRIBUTE",
			"code" : "charset=\"\""
		}, {
			"name" : "checked",
			"type" : "ATTRIBUTE",
			"code" : "checked=\"\""
		}, {
			"name" : "cite",
			"type" : "ATTRIBUTE",
			"code" : "cite=\"\""
		}, {
			"name" : "class",
			"type" : "ATTRIBUTE",
			"code" : "class=\"\""
		}, {
			"name" : "classid",
			"type" : "ATTRIBUTE",
			"code" : "classid=\"\""
		}, {
			"name" : "clear",
			"type" : "ATTRIBUTE",
			"code" : "clear=\"\""
		}, {
			"name" : "code",
			"type" : "ATTRIBUTE",
			"code" : "code=\"\""
		}, {
			"name" : "codebase",
			"type" : "ATTRIBUTE",
			"code" : "codebase=\"\""
		}, {
			"name" : "codetype",
			"type" : "ATTRIBUTE",
			"code" : "codetype=\"\""
		}, {
			"name" : "color",
			"type" : "ATTRIBUTE",
			"code" : "color=\"\""
		}, {
			"name" : "cols",
			"type" : "ATTRIBUTE",
			"code" : "cols=\"\""
		}, {
			"name" : "colspan",
			"type" : "ATTRIBUTE",
			"code" : "colspan=\"\""
		}, {
			"name" : "compact",
			"type" : "ATTRIBUTE",
			"code" : "compact=\"\""
		}, {
			"name" : "content",
			"type" : "ATTRIBUTE",
			"code" : "content=\"\""
		}, {
			"name" : "coords",
			"type" : "ATTRIBUTE",
			"code" : "coords=\"\""
		}, {
			"name" : "data",
			"type" : "ATTRIBUTE",
			"code" : "data=\"\""
		}, {
			"name" : "datetime",
			"type" : "ATTRIBUTE",
			"code" : "datetime=\"\""
		}, {
			"name" : "declare",
			"type" : "ATTRIBUTE",
			"code" : "declare=\"\""
		}, {
			"name" : "defer",
			"type" : "ATTRIBUTE",
			"code" : "defer=\"\""
		}, {
			"name" : "dir",
			"type" : "ATTRIBUTE",
			"code" : "dir=\"\""
		}, {
			"name" : "disabled",
			"type" : "ATTRIBUTE",
			"code" : "disabled=\"\""
		}, {
			"name" : "enctype",
			"type" : "ATTRIBUTE",
			"code" : "enctype=\"\""
		}, {
			"name" : "face",
			"type" : "ATTRIBUTE",
			"code" : "face=\"\""
		}, {
			"name" : "for",
			"type" : "ATTRIBUTE",
			"code" : "for=\"\""
		}, {
			"name" : "frame",
			"type" : "ATTRIBUTE",
			"code" : "frame=\"\""
		}, {
			"name" : "frameborder",
			"type" : "ATTRIBUTE",
			"code" : "frameborder=\"\""
		}, {
			"name" : "headers",
			"type" : "ATTRIBUTE",
			"code" : "headers=\"\""
		}, {
			"name" : "height",
			"type" : "ATTRIBUTE",
			"code" : "height=\"\""
		}, {
			"name" : "href",
			"type" : "ATTRIBUTE",
			"code" : "href=\"\""
		}, {
			"name" : "hreflang",
			"type" : "ATTRIBUTE",
			"code" : "hreflang=\"\""
		}, {
			"name" : "hspace",
			"type" : "ATTRIBUTE",
			"code" : "hspace=\"\""
		}, {
			"name" : "http-equiv",
			"type" : "ATTRIBUTE",
			"code" : "http-equiv=\"\""
		}, {
			"name" : "id",
			"type" : "ATTRIBUTE",
			"code" : "id=\"\""
		}, {
			"name" : "ismap",
			"type" : "ATTRIBUTE",
			"code" : "ismap=\"\""
		}, {
			"name" : "label",
			"type" : "ATTRIBUTE",
			"code" : "label=\"\""
		}, {
			"name" : "lang",
			"type" : "ATTRIBUTE",
			"code" : "lang=\"\""
		}, {
			"name" : "language",
			"type" : "ATTRIBUTE",
			"code" : "language=\"\""
		}, {
			"name" : "link",
			"type" : "ATTRIBUTE",
			"code" : "link=\"\""
		}, {
			"name" : "longdesc",
			"type" : "ATTRIBUTE",
			"code" : "longdesc=\"\""
		}, {
			"name" : "marginheight",
			"type" : "ATTRIBUTE",
			"code" : "marginheight=\"\""
		}, {
			"name" : "marginwidth",
			"type" : "ATTRIBUTE",
			"code" : "marginwidth=\"\""
		}, {
			"name" : "maxlength",
			"type" : "ATTRIBUTE",
			"code" : "maxlength=\"\""
		}, {
			"name" : "media",
			"type" : "ATTRIBUTE",
			"code" : "media=\"\""
		}, {
			"name" : "method",
			"type" : "ATTRIBUTE",
			"code" : "method=\"\""
		}, {
			"name" : "multiple",
			"type" : "ATTRIBUTE",
			"code" : "multiple=\"\""
		}, {
			"name" : "name",
			"type" : "ATTRIBUTE",
			"code" : "name=\"\""
		}, {
			"name" : "nohref",
			"type" : "ATTRIBUTE",
			"code" : "nohref=\"\""
		}, {
			"name" : "noresize",
			"type" : "ATTRIBUTE",
			"code" : "noresize=\"\""
		}, {
			"name" : "noshade",
			"type" : "ATTRIBUTE",
			"code" : "noshade=\"\""
		}, {
			"name" : "nowrap",
			"type" : "ATTRIBUTE",
			"code" : "nowrap=\"\""
		}, {
			"name" : "object",
			"type" : "ATTRIBUTE",
			"code" : "object=\"\""
		}, {
			"name" : "onblur",
			"type" : "ATTRIBUTE",
			"code" : "onblur=\"\""
		}, {
			"name" : "onchange",
			"type" : "ATTRIBUTE",
			"code" : "onchange=\"\""
		}, {
			"name" : "onclick",
			"type" : "ATTRIBUTE",
			"code" : "onclick=\"\""
		}, {
			"name" : "ondblclick",
			"type" : "ATTRIBUTE",
			"code" : "ondblclick=\"\""
		}, {
			"name" : "onfocus",
			"type" : "ATTRIBUTE",
			"code" : "onfocus=\"\""
		}, {
			"name" : "onkeydown",
			"type" : "ATTRIBUTE",
			"code" : "onkeydown=\"\""
		}, {
			"name" : "onkeypress",
			"type" : "ATTRIBUTE",
			"code" : "onkeypress=\"\""
		}, {
			"name" : "onkeyup",
			"type" : "ATTRIBUTE",
			"code" : "onkeyup=\"\""
		}, {
			"name" : "onload",
			"type" : "ATTRIBUTE",
			"code" : "onload=\"\""
		}, {
			"name" : "onmousedown",
			"type" : "ATTRIBUTE",
			"code" : "onmousedown=\"\""
		}, {
			"name" : "onmousemove",
			"type" : "ATTRIBUTE",
			"code" : "onmousemove=\"\""
		}, {
			"name" : "onmouseout",
			"type" : "ATTRIBUTE",
			"code" : "onmouseout=\"\""
		}, {
			"name" : "onmouseover",
			"type" : "ATTRIBUTE",
			"code" : "onmouseover=\"\""
		}, {
			"name" : "onmouseup",
			"type" : "ATTRIBUTE",
			"code" : "onmouseup=\"\""
		}, {
			"name" : "onreset",
			"type" : "ATTRIBUTE",
			"code" : "onreset=\"\""
		}, {
			"name" : "onselect",
			"type" : "ATTRIBUTE",
			"code" : "onselect=\"\""
		}, {
			"name" : "onsubmit",
			"type" : "ATTRIBUTE",
			"code" : "onsubmit=\"\""
		}, {
			"name" : "onunload",
			"type" : "ATTRIBUTE",
			"code" : "onunload=\"\""
		}, {
			"name" : "profile",
			"type" : "ATTRIBUTE",
			"code" : "profile=\"\""
		}, {
			"name" : "prompt",
			"type" : "ATTRIBUTE",
			"code" : "prompt=\"\""
		}, {
			"name" : "readonly",
			"type" : "ATTRIBUTE",
			"code" : "readonly=\"\""
		}, {
			"name" : "rel",
			"type" : "ATTRIBUTE",
			"code" : "rel=\"\""
		}, {
			"name" : "rev",
			"type" : "ATTRIBUTE",
			"code" : "rev=\"\""
		}, {
			"name" : "rows",
			"type" : "ATTRIBUTE",
			"code" : "rows=\"\""
		}, {
			"name" : "rowspan",
			"type" : "ATTRIBUTE",
			"code" : "rowspan=\"\""
		}, {
			"name" : "rules",
			"type" : "ATTRIBUTE",
			"code" : "rules=\"\""
		}, {
			"name" : "scheme",
			"type" : "ATTRIBUTE",
			"code" : "scheme=\"\""
		}, {
			"name" : "scope",
			"type" : "ATTRIBUTE",
			"code" : "scope=\"\""
		}, {
			"name" : "scrolling",
			"type" : "ATTRIBUTE",
			"code" : "scrolling=\"\""
		}, {
			"name" : "selected",
			"type" : "ATTRIBUTE",
			"code" : "selected=\"\""
		}, {
			"name" : "shape",
			"type" : "ATTRIBUTE",
			"code" : "shape=\"\""
		}, {
			"name" : "shape",
			"type" : "ATTRIBUTE",
			"code" : "shape=\"\""
		}, {
			"name" : "size",
			"type" : "ATTRIBUTE",
			"code" : "size=\"\""
		}, {
			"name" : "span",
			"type" : "ATTRIBUTE",
			"code" : "span=\"\""
		}, {
			"name" : "src",
			"type" : "ATTRIBUTE",
			"code" : "src=\"\""
		}, {
			"name" : "standby",
			"type" : "ATTRIBUTE",
			"code" : "standby=\"\""
		}, {
			"name" : "start",
			"type" : "ATTRIBUTE",
			"code" : "start=\"\""
		}, {
			"name" : "style",
			"type" : "ATTRIBUTE",
			"code" : "style=\"\""
		}, {
			"name" : "summary",
			"type" : "ATTRIBUTE",
			"code" : "summary=\"\""
		}, {
			"name" : "tabindex",
			"type" : "ATTRIBUTE",
			"code" : "tabindex=\"\""
		}, {
			"name" : "target",
			"type" : "ATTRIBUTE",
			"code" : "target=\"\""
		}, {
			"name" : "text",
			"type" : "ATTRIBUTE",
			"code" : "text=\"\""
		}, {
			"name" : "title",
			"type" : "ATTRIBUTE",
			"code" : "title=\"\""
		}, {
			"name" : "type",
			"type" : "ATTRIBUTE",
			"code" : "type=\"\""
		}, {
			"name" : "usemap",
			"type" : "ATTRIBUTE",
			"code" : "usemap=\"\""
		}, {
			"name" : "valign",
			"type" : "ATTRIBUTE",
			"code" : "valign=\"\""
		}, {
			"name" : "value",
			"type" : "ATTRIBUTE",
			"code" : "value=\"\""
		}, {
			"name" : "valuetype",
			"type" : "ATTRIBUTE",
			"code" : "valuetype=\"\""
		}, {
			"name" : "version",
			"type" : "ATTRIBUTE",
			"code" : "version=\"\""
		}, {
			"name" : "vlink",
			"type" : "ATTRIBUTE",
			"code" : "vlink=\"\""
		}, {
			"name" : "vspace",
			"type" : "ATTRIBUTE",
			"code" : "vspace=\"\""
		}, {
			"name" : "width",
			"type" : "ATTRIBUTE",
			"code" : "width=\"\""
		} ]
