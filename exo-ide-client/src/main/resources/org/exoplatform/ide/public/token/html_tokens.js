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
			"fullDescription" : "Defines an anchor",
			"subTokenList" : [
					{
						"name" : "charset",
						"type" : "ATTRIBUTE",
						"code" : "charset=\"\"",
						"fullDescription" : "Specifies the character-set of a linked document"
					},
					{
						"name" : "coords",
						"type" : "ATTRIBUTE",
						"code" : "coords=\"\"",
						"fullDescription" : "Specifies the coordinates of a link"
					},
					{
						"name" : "href",
						"type" : "ATTRIBUTE",
						"code" : "href=\"\"",
						"fullDescription" : "Specifies the destination of a link"
					},
					{
						"name" : "hreflang",
						"type" : "ATTRIBUTE",
						"code" : "hreflang=\"\"",
						"fullDescription" : "Specifies the language of a linked document"
					},
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "Specifies the name of an anchor"
					},
					{
						"name" : "rel",
						"type" : "ATTRIBUTE",
						"code" : "rel=\"\"",
						"fullDescription" : "Specifies the relationship between the current document and the linked document"
					},
					{
						"name" : "rev",
						"type" : "ATTRIBUTE",
						"code" : "rev=\"\"",
						"fullDescription" : "Specifies the relationship between the linked document and the current document"
					},
					{
						"name" : "shape",
						"type" : "ATTRIBUTE",
						"code" : "shape=\"\"",
						"fullDescription" : "Specifies the shape of a link"
					},
					{
						"name" : "target",
						"type" : "ATTRIBUTE",
						"code" : "target=\"\"",
						"fullDescription" : "Specifies where to open the linked document"
					},
					{
						"name" : "onblur",
						"type" : "ATTRIBUTE",
						"code" : "onblur=\"\"",
						"fullDescription" : "Script to be run when an element loses focus"
					},
					{
						"name" : "onfocus",
						"type" : "ATTRIBUTE",
						"code" : "onfocus=\"\"",
						"fullDescription" : "Script to be run when an element gets focus"
					} ]
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
			"fullDescription" : "Defines an area inside an image-map",
			"subTokenList" : [
					{
						"name" : "alt",
						"type" : "ATTRIBUTE",
						"code" : "alt=\"\"",
						"fullDescription" : "Specifies an alternate text for an area"
					},
					{
						"name" : "coords",
						"type" : "ATTRIBUTE",
						"code" : "coords=\"\"",
						"fullDescription" : "Specifies the coordinates of an area"
					},
					{
						"name" : "href",
						"type" : "ATTRIBUTE",
						"code" : "href=\"\"",
						"fullDescription" : "Specifies the destination of a link in an area"
					},
					{
						"name" : "nohref",
						"type" : "ATTRIBUTE",
						"code" : "nohref=\"\"",
						"fullDescription" : "Specifies that an area has no associated link"
					},
					{
						"name" : "shape",
						"type" : "ATTRIBUTE",
						"code" : "shape=\"\"",
						"fullDescription" : "Specifies the shape of an area"
					},
					{
						"name" : "target",
						"type" : "ATTRIBUTE",
						"code" : "target=\"\"",
						"fullDescription" : "Specifies where to open the linked page specified in the href attribute"
					} ]
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
			"fullDescription" : "Defines a default address or a default target for all links on a page",
			"subTokenList" : [
					{
						"name" : "href",
						"type" : "ATTRIBUTE",
						"code" : "href=\"\"",
						"fullDescription" : "Specifies a base URL for all relative URLs on a page.<br /><b>Note:</b> The base URL must be an absolute URL!"
					},
					{
						"name" : "target",
						"type" : "ATTRIBUTE",
						"code" : "target=\"\"",
						"fullDescription" : "Specifies where to open all the links on a page"
					} ]
		},
		{
			"name" : "bdo",
			"type" : "TAG",
			"code" : "<bdo></bdo>",
			"fullDescription" : "Defines the text direction",
			"subTokenList" : [ {
				"name" : "target",
				"type" : "ATTRIBUTE",
				"code" : "dir=\"\"",
				"fullDescription" : "Specifies the text direction of the text inside a bdo element"
			} ]
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
			"fullDescription" : "Defines a long quotation",
			"subTokenList" : [ {
				"name" : "cite",
				"type" : "ATTRIBUTE",
				"code" : "cite=\"\"",
				"fullDescription" : "Specifies the source of a quotation"
			} ]
		},
		{
			"name" : "body",
			"type" : "TAG",
			"code" : "<body></body>",
			"fullDescription" : "Defines the document's body",
			"subTokenList" : [ {
				"name" : "onload",
				"type" : "ATTRIBUTE",
				"code" : "onload=\"\"",
				"fullDescription" : "Script to be run when a document load"
			}, {
				"name" : "onunload",
				"type" : "ATTRIBUTE",
				"code" : "onunload=\"\"",
				"fullDescription" : "Script to be run when a document unload"
			} ]
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
			"fullDescription" : "Defines a push button",
			"subTokenList" : [
					{
						"name" : "disabled",
						"type" : "ATTRIBUTE",
						"code" : "disabled=\"\"",
						"fullDescription" : "Specifies that a button should be disabled"
					},
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "Specifies the name for a button"
					},
					{
						"name" : "type",
						"type" : "ATTRIBUTE",
						"code" : "type=\"\"",
						"fullDescription" : "Specifies the type of a button"
					},
					{
						"name" : "value",
						"type" : "ATTRIBUTE",
						"code" : "value=\"\"",
						"fullDescription" : "Specifies the underlying value of a button"
					},
					{
						"name" : "accesskey",
						"type" : "ATTRIBUTE",
						"code" : "accesskey=\"\"",
						"fullDescription" : "Specifies a keyboard shortcut to access an element"
					},
					{
						"name" : "tabindex",
						"type" : "ATTRIBUTE",
						"code" : "tabindex=\"\"",
						"fullDescription" : "Specifies the tab order of an element"
					},
					{
						"name" : "onblur",
						"type" : "ATTRIBUTE",
						"code" : "onblur=\"\"",
						"fullDescription" : "Script to be run when an element loses focus"
					},
					{
						"name" : "onfocus",
						"type" : "ATTRIBUTE",
						"code" : "onunload=\"\"",
						"fullDescription" : "Script to be run when an element gets focus"
					} ]
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
			"fullDescription" : "Defines attribute values for one or more columns in a table",
			"subTokenList" : [
					{
						"name" : "align",
						"type" : "ATTRIBUTE",
						"code" : "align=\"\"",
						"fullDescription" : "Specifies the alignment of the content related to a col element"
					},
					{
						"name" : "char",
						"type" : "ATTRIBUTE",
						"code" : "char=\"\"",
						"fullDescription" : "Specifies the alignment of the content related to a col element to a character"
					},
					{
						"name" : "charoff",
						"type" : "ATTRIBUTE",
						"code" : "charoff=\"\"",
						"fullDescription" : "Specifies the number of characters the content will be aligned from the character specified by the char attribute"
					},
					{
						"name" : "span",
						"type" : "ATTRIBUTE",
						"code" : "span=\"\"",
						"fullDescription" : "Specifies the number of columns a col element should span"
					},
					{
						"name" : "valign",
						"type" : "ATTRIBUTE",
						"code" : "valign=\"\"",
						"fullDescription" : "Specifies the vertical alignment of the content related to a col element"
					},
					{
						"name" : "width",
						"type" : "ATTRIBUTE",
						"code" : "width=\"\"",
						"fullDescription" : "Specifies the width of a col element"
					} ]
		},
		{
			"name" : "colgroup",
			"type" : "TAG",
			"code" : "<colgroup></colgroup>",
			"fullDescription" : "Defines a group of columns in a table for formatting",
			"subTokenList" : [
					{
						"name" : "align",
						"type" : "ATTRIBUTE",
						"code" : "align=\"\"",
						"fullDescription" : "Aligns the content in a column group"
					},
					{
						"name" : "char",
						"type" : "ATTRIBUTE",
						"code" : "char=\"\"",
						"fullDescription" : "Aligns the content in a column group to a character"
					},
					{
						"name" : "charoff",
						"type" : "ATTRIBUTE",
						"code" : "charoff=\"\"",
						"fullDescription" : "Sets the number of characters the content will be aligned from the character specified by the char attribute"
					},
					{
						"name" : "span",
						"type" : "ATTRIBUTE",
						"code" : "span=\"\"",
						"fullDescription" : "Specifies the number of columns a column group should span"
					},
					{
						"name" : "valign",
						"type" : "ATTRIBUTE",
						"code" : "valign=\"\"",
						"fullDescription" : "Vertical aligns the content in a column group"
					},
					{
						"name" : "width",
						"type" : "ATTRIBUTE",
						"code" : "width=\"\"",
						"fullDescription" : "Specifies the width of a column group"
					} ]
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
			"fullDescription" : "Defines deleted text",
			"subTokenList" : [
					{
						"name" : "cite",
						"type" : "ATTRIBUTE",
						"code" : "cite=\"\"",
						"fullDescription" : "Specifies a URL to a document which explains why the text was deleted"
					},
					{
						"name" : "datetime",
						"type" : "ATTRIBUTE",
						"code" : "datetime=\"\"",
						"fullDescription" : "Specifies the date and time when the text was deleted"
					} ]
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
			"fullDescription" : "Defines an HTML form for user input",
			"subTokenList" : [
					{
						"name" : "action",
						"type" : "ATTRIBUTE",
						"code" : "action=\"\"",
						"fullDescription" : "Specifies where to send the form-data when a form is submitted"
					},
					{
						"name" : "accept",
						"type" : "ATTRIBUTE",
						"code" : "accept=\"\"",
						"fullDescription" : "Specifies the types of files that can be submitted through a file upload"
					},
					{
						"name" : "accept-charset",
						"type" : "ATTRIBUTE",
						"code" : "accept-charset=\"\"",
						"fullDescription" : "Specifies the character-sets the server can handle for form-data"
					},
					{
						"name" : "enctype",
						"type" : "ATTRIBUTE",
						"code" : "enctype=\"\"",
						"fullDescription" : "Specifies how form-data should be encoded before sending it to a server"
					},
					{
						"name" : "method",
						"type" : "ATTRIBUTE",
						"code" : "method=\"\"",
						"fullDescription" : "Specifies how to send form-data"
					},
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "Specifies the name for a form"
					},
					{
						"name" : "onreset",
						"type" : "ATTRIBUTE",
						"code" : "onreset=\"\"",
						"fullDescription" : "Script to be run when a form is reset"
					},
					{
						"name" : "onsubmit",
						"type" : "ATTRIBUTE",
						"code" : "onsubmit=\"\"",
						"fullDescription" : "Script to be run when a form is submitted"
					} ]
		},
		{
			"name" : "frame",
			"type" : "TAG",
			"code" : "<frame />",
			"fullDescription" : "Defines a window (a frame) in a frameset",
			"subTokenList" : [
					{
						"name" : "frameborder",
						"type" : "ATTRIBUTE",
						"code" : "frameborder=\"\"",
						"fullDescription" : "Specifies whether or not to display a border around a frame"
					},
					{
						"name" : "longdesc",
						"type" : "ATTRIBUTE",
						"code" : "longdesc=\"\"",
						"fullDescription" : "Specifies a page that contains a long description of the content of a frame"
					},
					{
						"name" : "marginheight",
						"type" : "ATTRIBUTE",
						"code" : "marginheight=\"\"",
						"fullDescription" : "Specifies the top and bottom margins of a frame"
					},
					{
						"name" : "marginwidth",
						"type" : "ATTRIBUTE",
						"code" : "marginwidth=\"\"",
						"fullDescription" : "Specifies the left and right margins of a frame"
					},
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "Specifies the name of a frame"
					},
					{
						"name" : "noresize",
						"type" : "ATTRIBUTE",
						"code" : "noresize=\"\"",
						"fullDescription" : "Specifies that a frame cannot be resized"
					},
					{
						"name" : "scrolling",
						"type" : "ATTRIBUTE",
						"code" : "scrolling=\"\"",
						"fullDescription" : "Specifies whether or not to display scrollbars in a frame"
					},
					{
						"name" : "src",
						"type" : "ATTRIBUTE",
						"code" : "src=\"\"",
						"fullDescription" : "Specifies the URL of the document to show in a frame"
					} ]
		},
		{
			"name" : "frameset",
			"type" : "TAG",
			"code" : "<frameset></frameset>",
			"fullDescription" : "Defines a set of frames",
			"subTokenList" : [
					{
						"name" : "cols",
						"type" : "ATTRIBUTE",
						"code" : "cols=\"\"",
						"fullDescription" : "Specifies the number and size of columns in a frameset"
					},
					{
						"name" : "rows",
						"type" : "ATTRIBUTE",
						"code" : "rows=\"\"",
						"fullDescription" : "Specifies the number and size of rows in a frameset"
					} ]
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
			"fullDescription" : "Defines information about the document",
			"subTokenList" : [ {
				"name" : "profile",
				"type" : "ATTRIBUTE",
				"code" : "profile=\"\"",
				"fullDescription" : "Specifies a URL to a document that contains a set of rules. The rules can be read by browsers to clearly understand the information in the <meta> tag's content attribute"
			} ]
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
			"fullDescription" : "Defines an HTML document",
			"subTokenList" : [ {
				"name" : "xmlns",
				"type" : "ATTRIBUTE",
				"code" : "xmlns=\"\"",
				"fullDescription" : "Specifies the namespace to use (only for XHTML documents!)"
			} ]
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
			"fullDescription" : "Defines an inline frame",
			"subTokenList" : [
					{
						"name" : "frameborder",
						"type" : "ATTRIBUTE",
						"code" : "frameborder=\"\"",
						"fullDescription" : "Specifies whether or not to display a border around an iframe"
					},
					{
						"name" : "longdesc",
						"type" : "ATTRIBUTE",
						"code" : "longdesc=\"\"",
						"fullDescription" : "Specifies a page that contains a long description of the content of a frame"
					},
					{
						"name" : "marginheight",
						"type" : "ATTRIBUTE",
						"code" : "marginheight=\"\"",
						"fullDescription" : "Specifies the top and bottom margins of a frame"
					},
					{
						"name" : "marginwidth",
						"type" : "ATTRIBUTE",
						"code" : "marginwidth=\"\"",
						"fullDescription" : "Specifies the left and right margins of a frame"
					},
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "Specifies the name of a frame"
					},
					{
						"name" : "width",
						"type" : "ATTRIBUTE",
						"code" : "width=\"\"",
						"fullDescription" : "Specifies the width of an iframe"
					},
					{
						"name" : "scrolling",
						"type" : "ATTRIBUTE",
						"code" : "scrolling=\"\"",
						"fullDescription" : "Specifies whether or not to display scrollbars in a frame"
					},
					{
						"name" : "src",
						"type" : "ATTRIBUTE",
						"code" : "src=\"\"",
						"fullDescription" : "Specifies the URL of the document to show in a frame"
					}, {
						"name" : "height",
						"type" : "ATTRIBUTE",
						"code" : "height=\"\"",
						"fullDescription" : "Specifies the height of an iframe"
					} ]
		},
		{
			"name" : "img",
			"type" : "TAG",
			"code" : "<img />",
			"fullDescription" : "Defines an image",
			"subTokenList" : [
					{
						"name" : "alt",
						"type" : "ATTRIBUTE",
						"code" : "alt=\"\"",
						"fullDescription" : "Specifies an alternate text for an image"
					},
					{
						"name" : "src",
						"type" : "ATTRIBUTE",
						"code" : "src=\"\"",
						"fullDescription" : "Specifies the URL of an image"
					},
					{
						"name" : "height",
						"type" : "ATTRIBUTE",
						"code" : "height=\"\"",
						"fullDescription" : "Specifies the height of an image"
					},
					{
						"name" : "ismap",
						"type" : "ATTRIBUTE",
						"code" : "ismap=\"\"",
						"fullDescription" : "Specifies an image as a server-side image-map. Rarely used. Look at usemap instead"
					},
					{
						"name" : "longdesc",
						"type" : "ATTRIBUTE",
						"code" : "longdesc=\"\"",
						"fullDescription" : "Specifies the URL to a document that contains a long description of an image"
					},
					{
						"name" : "usemap",
						"type" : "ATTRIBUTE",
						"code" : "usemap=\"\"",
						"fullDescription" : "Specifies an image as a client-side image-map"
					}, {
						"name" : "width",
						"type" : "ATTRIBUTE",
						"code" : "width=\"\"",
						"fullDescription" : "Specifies the width of an image"
					} ]
		},
		{
			"name" : "input",
			"type" : "TAG",
			"code" : "<input />",
			"fullDescription" : "Defines an input control",
			"subTokenList" : [
					{
						"name" : "accept",
						"type" : "ATTRIBUTE",
						"code" : "accept=\"\"",
						"fullDescription" : "Specifies the types of files that can be submitted through a file upload (only for type=\"file\")"
					},
					{
						"name" : "alt",
						"type" : "ATTRIBUTE",
						"code" : "alt=\"\"",
						"fullDescription" : "Specifies an alternate text for an image input (only for type=\"image\")"
					},
					{
						"name" : "checked",
						"type" : "ATTRIBUTE",
						"code" : "checked=\"\"",
						"fullDescription" : "Specifies that an input element should be preselected when the page loads (for type=\"checkbox\" or type=\"radio\")"
					},
					{
						"name" : "disabled",
						"type" : "ATTRIBUTE",
						"code" : "disabled=\"\"",
						"fullDescription" : "Specifies that an input element should be disabled when the page loads"
					},
					{
						"name" : "maxlength",
						"type" : "ATTRIBUTE",
						"code" : "maxlength=\"\"",
						"fullDescription" : "Specifies the maximum length (in characters) of an input field (for type=\"text\" or type=\"password\")"
					},
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "Specifies a name for an input element"
					},
					{
						"name" : "readonly",
						"type" : "ATTRIBUTE",
						"code" : "readonly=\"\"",
						"fullDescription" : "Specifies that an input field should be read-only (for type=\"text\" or type=\"password\")"
					},
					{
						"name" : "size",
						"type" : "ATTRIBUTE",
						"code" : "size=\"\"",
						"fullDescription" : "Specifies the width of an input field"
					},
					{
						"name" : "src",
						"type" : "ATTRIBUTE",
						"code" : "src=\"\"",
						"fullDescription" : "Specifies the URL to an image to display as a submit button"
					},
					{
						"name" : "type",
						"type" : "ATTRIBUTE",
						"code" : "type=\"\"",
						"fullDescription" : "Specifies the type of an input element"
					},
					{
						"name" : "value",
						"type" : "ATTRIBUTE",
						"code" : "value=\"\"",
						"fullDescription" : "Specifies the value of an input element"
					},
					{
						"name" : "onblur",
						"type" : "ATTRIBUTE",
						"code" : "onblur=\"\"",
						"fullDescription" : "Script to be run when an element loses focus"
					},
					{
						"name" : "onchange",
						"type" : "ATTRIBUTE",
						"code" : "onchange=\"\"",
						"fullDescription" : "Script to be run when an element change"
					},
					{
						"name" : "onfocus",
						"type" : "ATTRIBUTE",
						"code" : "onfocus=\"\"",
						"fullDescription" : "Script to be run when an element gets focus"
					},
					{
						"name" : "onselect",
						"type" : "ATTRIBUTE",
						"code" : "onselect=\"\"",
						"fullDescription" : "Script to be run when an element is selected"
					} ]
		},
		{
			"name" : "ins",
			"type" : "TAG",
			"code" : "<ins></ins>",
			"fullDescription" : "Defines inserted text",
			"subTokenList" : [
					{
						"name" : "cite",
						"type" : "ATTRIBUTE",
						"code" : "cite=\"\"",
						"fullDescription" : "Specifies a URL to a document which explains why the text was inserted/changed"
					},
					{
						"name" : "datetime",
						"type" : "ATTRIBUTE",
						"code" : "datetime=\"\"",
						"fullDescription" : "Specifies the date and time when the text was inserted/changed"
					} ]
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
			"fullDescription" : "Defines a label for an input element",
			"subTokenList" : [
					{
						"name" : "for",
						"type" : "ATTRIBUTE",
						"code" : "for=\"\"",
						"fullDescription" : "Specifies which form element a label is bound to"
					},
					{
						"name" : "onblur",
						"type" : "ATTRIBUTE",
						"code" : "onblur=\"\"",
						"fullDescription" : "Script to be run when an element loses focus"
					},
					{
						"name" : "onfocus",
						"type" : "ATTRIBUTE",
						"code" : "onfocus=\"\"",
						"fullDescription" : "Script to be run when an element gets focus"
					} ]
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
			"fullDescription" : "Defines the relationship between a document and an external resource",
			"subTokenList" : [
					{
						"name" : "charset",
						"type" : "ATTRIBUTE",
						"code" : "charset=\"\"",
						"fullDescription" : "Specifies the character encoding of the linked document"
					},
					{
						"name" : "href",
						"type" : "ATTRIBUTE",
						"code" : "href=\"\"",
						"fullDescription" : "Specifies the location of the linked document"
					},
					{
						"name" : "hreflang",
						"type" : "ATTRIBUTE",
						"code" : "hreflang=\"\"",
						"fullDescription" : "Specifies the language of the text in the linked document"
					},
					{
						"name" : "media",
						"type" : "ATTRIBUTE",
						"code" : "media=\"\"",
						"fullDescription" : "Specifies on what device the linked document will be displayed"
					},
					{
						"name" : "rel",
						"type" : "ATTRIBUTE",
						"code" : "rel=\"\"",
						"fullDescription" : "Specifies the relationship between the current document and the linked document"
					},
					{
						"name" : "rev",
						"type" : "ATTRIBUTE",
						"code" : "rev=\"\"",
						"fullDescription" : "Specifies the relationship between the linked document and the current document"
					},
					{
						"name" : "target",
						"type" : "ATTRIBUTE",
						"code" : "target=\"\"",
						"fullDescription" : "Specifies where the linked document is to be loaded"
					},
					{
						"name" : "type",
						"type" : "ATTRIBUTE",
						"code" : "type=\"\"",
						"fullDescription" : "Specifies the MIME type of the linked document"
					} ]
		},
		{
			"name" : "map",
			"type" : "TAG",
			"code" : "<map></map>",
			"fullDescription" : "Defines an image-map",
			"subTokenList" : [ {
				"name" : "name",
				"type" : "ATTRIBUTE",
				"code" : "name=\"\"",
				"fullDescription" : "Specifies the name for an image-map"
			} ]
		},
		{
			"name" : "meta",
			"type" : "TAG",
			"code" : "<meta />",
			"fullDescription" : "Defines metadata about an HTML document",
			"subTokenList" : [
					{
						"name" : "content",
						"type" : "ATTRIBUTE",
						"code" : "content=\"\"",
						"fullDescription" : "Specifies the content of the meta information"
					},
					{
						"name" : "http-equiv",
						"type" : "ATTRIBUTE",
						"code" : "http-equiv=\"\"",
						"fullDescription" : "Provides an HTTP header for the information in the content attribute"
					},
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "Provides a name for the information in the content attribute"
					},
					{
						"name" : "scheme",
						"type" : "ATTRIBUTE",
						"code" : "scheme=\"\"",
						"fullDescription" : "Specifies a scheme to be used to interpret the value of the content attribute"
					} ]
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
			"fullDescription" : "Defines an embedded object",
			"subTokenList" : [
					{
						"name" : "align",
						"type" : "ATTRIBUTE",
						"code" : "align=\"\"",
						"fullDescription" : "Defines the text alignment around the object"
					},
					{
						"name" : "archive",
						"type" : "ATTRIBUTE",
						"code" : "archive=\"\"",
						"fullDescription" : "A space separated list of URL's to archives. The archives contains resources relevant to the object"
					},
					{
						"name" : "border",
						"type" : "ATTRIBUTE",
						"code" : "border=\"\"",
						"fullDescription" : "Defines a border around the object"
					},
					{
						"name" : "classid",
						"type" : "ATTRIBUTE",
						"code" : "classid=\"\"",
						"fullDescription" : "Defines a class ID value as set in the Windows Registry or a URL"
					},
					{
						"name" : "codebase",
						"type" : "ATTRIBUTE",
						"code" : "codebase=\"\"",
						"fullDescription" : "Defines where to find the code for the object"
					},
					{
						"name" : "codetype",
						"type" : "ATTRIBUTE",
						"code" : "codetype=\"\"",
						"fullDescription" : "The internet media type of the code referred to by the classid attribute"
					},
					{
						"name" : "data",
						"type" : "ATTRIBUTE",
						"code" : "data=\"\"",
						"fullDescription" : "Defines a URL that refers to the object's data"
					},
					{
						"name" : "declare",
						"type" : "ATTRIBUTE",
						"code" : "declare=\"\"",
						"fullDescription" : "Defines that the object should only be declared, not created or instantiated until needed"
					},
					{
						"name" : "height",
						"type" : "ATTRIBUTE",
						"code" : "height=\"\"",
						"fullDescription" : "Defines the height of the object"
					},
					{
						"name" : "hspace",
						"type" : "ATTRIBUTE",
						"code" : "hspace=\"\"",
						"fullDescription" : "Defines the horizontal spacing around the object"
					},
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "Defines the name for an object (to use in scripts)"
					},
					{
						"name" : "standby",
						"type" : "ATTRIBUTE",
						"code" : "standby=\"\"",
						"fullDescription" : "Defines a text to display while the object is loading"
					},
					{
						"name" : "type",
						"type" : "ATTRIBUTE",
						"code" : "type=\"\"",
						"fullDescription" : "Defines the MIME type of data specified in the data attribute"
					},
					{
						"name" : "usemap",
						"type" : "ATTRIBUTE",
						"code" : "usemap=\"\"",
						"fullDescription" : "Specifies a URL of a client-side image map to be used with the object"
					},
					{
						"name" : "vspace",
						"type" : "ATTRIBUTE",
						"code" : "vspace=\"\"",
						"fullDescription" : "Defines the vertical spacing around the object"
					}, {
						"name" : "width",
						"type" : "ATTRIBUTE",
						"code" : "width=\"\"",
						"fullDescription" : "Defines the width of the object"
					} ]
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
			"fullDescription" : "Defines a group of related options in a select list",
			"subTokenList" : [
					{
						"name" : "label",
						"type" : "ATTRIBUTE",
						"code" : "label=\"\"",
						"fullDescription" : "Specifies a description for a group of options"
					},
					{
						"name" : "disabled",
						"type" : "ATTRIBUTE",
						"code" : "disabled=\"\"",
						"fullDescription" : "Specifies that an option group should be disabled"
					} ]
		},
		{
			"name" : "optin",
			"type" : "TAG",
			"code" : "<option></option>",
			"fullDescription" : "Defines an option in a select list",
			"subTokenList" : [
					{
						"name" : "disabled",
						"type" : "ATTRIBUTE",
						"code" : "disabled=\"\"",
						"fullDescription" : "Specifies that an option should be disabled"
					},
					{
						"name" : "label",
						"type" : "ATTRIBUTE",
						"code" : "label=\"\"",
						"fullDescription" : "Specifies a shorter label for an option"
					},
					{
						"name" : "selected",
						"type" : "ATTRIBUTE",
						"code" : "selected=\"\"",
						"fullDescription" : "Specifies that an option should be selected by default"
					},
					{
						"name" : "value",
						"type" : "ATTRIBUTE",
						"code" : "value=\"\"",
						"fullDescription" : "Specifies the value to be sent to a server when a form is submitted"
					} ]
		},
		{
			"name" : "p",
			"type" : "TAG",
			"code" : "<p></p>",
			"fullDescription" : "Defines a paragraph"
		},
		{
			"name" : "param",
			"type" : "TAG",
			"code" : "<param />",
			"fullDescription" : "Defines a parameter for an object",
			"subTokenList" : [
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "Defines the name for a parameter (to use in script)"
					},
					{
						"name" : "type",
						"type" : "ATTRIBUTE",
						"code" : "type=\"\"",
						"fullDescription" : "Specifies the MIME type for a parameter"
					},
					{
						"name" : "value",
						"type" : "ATTRIBUTE",
						"code" : "value=\"\"",
						"fullDescription" : "Specifies the value of a parameter"
					},
					{
						"name" : "valuetype",
						"type" : "ATTRIBUTE",
						"code" : "valuetype=\"\"",
						"fullDescription" : "Specifies the type of the value"
					},
					{
						"name" : "id",
						"type" : "ATTRIBUTE",
						"code" : "id=\"\"",
						"fullDescription" : "Specifies a unique id for an element"
					} ]

		},
		{
			"name" : "pre",
			"type" : "TAG",
			"code" : "<pre></pre>",
			"fullDescription" : "Defines preformatted text"
		},
		{
			"name" : "q",
			"type" : "TAG",
			"code" : "<q></q>",
			"fullDescription" : "Defines a short quotation",
			"subTokenList" : [ {
				"name" : "cite",
				"type" : "ATTRIBUTE",
				"code" : "cite=\"\"",
				"fullDescription" : "Specifies the source of a quotation"
			} ]
		},
		{
			"name" : "samp",
			"type" : "TAG",
			"code" : "<samp></samp>",
			"fullDescription" : "Defines sample computer code"
		},
		{
			"name" : "script",
			"type" : "TAG",
			"code" : "<script></script>",
			"fullDescription" : "Defines a client-side script",
			"subTokenList" : [
					{
						"name" : "type",
						"type" : "ATTRIBUTE",
						"code" : "type=\"\"",
						"fullDescription" : "Specifies the MIME type of a script"
					},
					{
						"name" : "charset",
						"type" : "ATTRIBUTE",
						"code" : "charset=\"\"",
						"fullDescription" : "Specifies the character encoding used in an external script file"
					},
					{
						"name" : "defer",
						"type" : "ATTRIBUTE",
						"code" : "defer=\"\"",
						"fullDescription" : "Specifies that the execution of a script should be deferred (delayed) until after the page has been loaded"
					},
					{
						"name" : "src",
						"type" : "ATTRIBUTE",
						"code" : "src=\"\"",
						"fullDescription" : "Specifies the URL of an external script file"
					},
					{
						"name" : "xml:space",
						"type" : "ATTRIBUTE",
						"code" : "xml:space=\"\"",
						"fullDescription" : "Specifies whether whitespace in code should be preserved"
					} ]
		},
		{
			"name" : "select",
			"type" : "TAG",
			"code" : "<select></select>",
			"fullDescription" : "Defines a select list (drop-down list)",
			"subTokenList" : [
					{
						"name" : "disabled",
						"type" : "ATTRIBUTE",
						"code" : "disabled=\"\"",
						"fullDescription" : "Specifies that a drop-down list should be disabled"
					},
					{
						"name" : "multiple",
						"type" : "ATTRIBUTE",
						"code" : "multiple=\"\"",
						"fullDescription" : "Specifies that multiple options can be selected"
					},
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "Specifies the name of a drop-down list"
					},
					{
						"name" : "size",
						"type" : "ATTRIBUTE",
						"code" : "size=\"\"",
						"fullDescription" : "Specifies the number of visible options in a drop-down list"
					},
					{
						"name" : "xml:space",
						"type" : "ATTRIBUTE",
						"code" : "xml:space=\"\"",
						"fullDescription" : "Specifies whether whitespace in code should be preserved"
					},
					{
						"name" : "onblur",
						"type" : "ATTRIBUTE",
						"code" : "onblur=\"\"",
						"fullDescription" : "Script to be run when an element loses focus"
					},
					{
						"name" : "onchange",
						"type" : "ATTRIBUTE",
						"code" : "onchange=\"\"",
						"fullDescription" : "Script to be run when an element change"
					},
					{
						"name" : "onfocus",
						"type" : "ATTRIBUTE",
						"code" : "onfocus=\"\"",
						"fullDescription" : "Script to be run when an element gets focus"
					} ]
		},
		{
			"name" : "small",
			"type" : "TAG",
			"code" : "<small></small>",
			"fullDescription" : "Defines small text"
		},
		{
			"name" : "span",
			"type" : "TAG",
			"code" : "<span></span>",
			"fullDescription" : "Defines a section in a document"
		},
		{
			"name" : "strong",
			"type" : "TAG",
			"code" : "<strong></strong>",
			"fullDescription" : "Defines strong text"
		},
		{
			"name" : "style",
			"type" : "TAG",
			"code" : "<style></style>",
			"fullDescription" : "Defines style information for a document",
			"subTokenList" : [
					{
						"name" : "type",
						"type" : "ATTRIBUTE",
						"code" : "type=\"\"",
						"fullDescription" : "Specifies the MIME type of the style sheet"
					},
					{
						"name" : "media",
						"type" : "ATTRIBUTE",
						"code" : "media=\"\"",
						"fullDescription" : "Specifies styles for different media types"
					} ]
		},
		{
			"name" : "sub",
			"type" : "TAG",
			"code" : "<sub></sub>",
			"fullDescription" : "Defines subscripted text"
		},
		{
			"name" : "sup",
			"type" : "TAG",
			"code" : "<sup></sup>",
			"fullDescription" : "Defines superscripted text"
		},
		{
			"name" : "table",
			"type" : "TAG",
			"code" : "<table></table>",
			"fullDescription" : "Defines a table",
			"subTokenList" : [
					{
						"name" : "border",
						"type" : "ATTRIBUTE",
						"code" : "border=\"\"",
						"fullDescription" : "Specifies the width of the borders around a table"
					},
					{
						"name" : "cellpadding",
						"type" : "ATTRIBUTE",
						"code" : "cellpadding=\"\"",
						"fullDescription" : "Specifies the space between the cell wall and the cell content"
					},
					{
						"name" : "cellspacing",
						"type" : "ATTRIBUTE",
						"code" : "cellspacing=\"\"",
						"fullDescription" : "Specifies the space between cells"
					},
					{
						"name" : "frame",
						"type" : "ATTRIBUTE",
						"code" : "frame=\"\"",
						"fullDescription" : "Specifies which parts of the outside borders that should be visible"
					},
					{
						"name" : "rules",
						"type" : "ATTRIBUTE",
						"code" : "rules=\"\"",
						"fullDescription" : "Specifies which parts of the inside borders that should be visible"
					},
					{
						"name" : "summary",
						"type" : "ATTRIBUTE",
						"code" : "summary=\"\"",
						"fullDescription" : "Specifies a summary of the content of a table"
					}, {
						"name" : "width",
						"type" : "ATTRIBUTE",
						"code" : "width=\"\"",
						"fullDescription" : "Specifies the width of a table"
					} ]
		},
		{
			"name" : "tbody",
			"type" : "TAG",
			"code" : "<tbody></tbody>",
			"fullDescription" : "Groups the body content in a table",
			"subTokenList" : [
					{
						"name" : "align",
						"type" : "ATTRIBUTE",
						"code" : "align=\"\"",
						"fullDescription" : "Aligns the content inside the tbody element"
					},
					{
						"name" : "char",
						"type" : "ATTRIBUTE",
						"code" : "char=\"\"",
						"fullDescription" : "Aligns the content inside the tbody element to a character"
					},
					{
						"name" : "charoff",
						"type" : "ATTRIBUTE",
						"code" : "charoff=\"\"",
						"fullDescription" : "Sets the number of characters the content inside the tbody element will be aligned from the character specified by the char attribute"
					},
					{
						"name" : "valign",
						"type" : "ATTRIBUTE",
						"code" : "valign=\"\"",
						"fullDescription" : "Vertical aligns the content inside the tbody element"
					} ]
		},
		{
			"name" : "td",
			"type" : "TAG",
			"code" : "<td></td>",
			"fullDescription" : "Defines a cell in a table",
			"subTokenList" : [
					{
						"name" : "abbr",
						"type" : "ATTRIBUTE",
						"code" : "abbr=\"\"",
						"fullDescription" : " 	Specifies an abbreviated version of the content in a cell"
					},
					{
						"name" : "align",
						"type" : "ATTRIBUTE",
						"code" : "align=\"\"",
						"fullDescription" : "Aligns the content in a cell"
					},
					{
						"name" : "axis",
						"type" : "ATTRIBUTE",
						"code" : "axis=\"\"",
						"fullDescription" : "Categorizes cells"
					},
					{
						"name" : "char",
						"type" : "ATTRIBUTE",
						"code" : "char=\"\"",
						"fullDescription" : "Aligns the content in a cell to a character"
					},
					{
						"name" : "charoff",
						"type" : "ATTRIBUTE",
						"code" : "charoff=\"\"",
						"fullDescription" : "Sets the number of characters the content will be aligned from the character specified by the char attribute"
					},
					{
						"name" : "colspan",
						"type" : "ATTRIBUTE",
						"code" : "colspan=\"\"",
						"fullDescription" : "Specifies the number of columns a cell should span"
					},
					{
						"name" : "headers",
						"type" : "ATTRIBUTE",
						"code" : "headers=\"\"",
						"fullDescription" : "Specifies the table headers related to a cell"
					},
					{
						"name" : "rowspan",
						"type" : "ATTRIBUTE",
						"code" : "rowspan=\"\"",
						"fullDescription" : "Sets the number of rows a cell should span"
					},
					{
						"name" : "scope",
						"type" : "ATTRIBUTE",
						"code" : "scope=\"\"",
						"fullDescription" : "Defines a way to associate header cells and data cells in a table"
					},
					{
						"name" : "valign",
						"type" : "ATTRIBUTE",
						"code" : "valign=\"\"",
						"fullDescription" : "Vertical aligns the content in a cell"
					} ]
		},
		{
			"name" : "textarea",
			"type" : "TAG",
			"code" : "<textarea></textarea>",
			"fullDescription" : "Defines a multi-line text input control",
			"subTokenList" : [
					{
						"name" : "cols",
						"type" : "ATTRIBUTE",
						"code" : "cols=\"\"",
						"fullDescription" : "Specifies the visible width of a text-area"
					},
					{
						"name" : "rows",
						"type" : "ATTRIBUTE",
						"code" : "rows=\"\"",
						"fullDescription" : "Specifies the visible number of rows in a text-area"
					},
					{
						"name" : "disabled",
						"type" : "ATTRIBUTE",
						"code" : "disabled=\"\"",
						"fullDescription" : "Specifies that a text-area should be disabled"
					},
					{
						"name" : "name",
						"type" : "ATTRIBUTE",
						"code" : "name=\"\"",
						"fullDescription" : "Specifies the name for a text-area"
					},
					{
						"name" : "readonly",
						"type" : "ATTRIBUTE",
						"code" : "readonly=\"\"",
						"fullDescription" : "Specifies that a text-area should be read-only"
					},
					{
						"name" : "onblur",
						"type" : "ATTRIBUTE",
						"code" : "onblur=\"\"",
						"fullDescription" : "Script to be run when an element loses focus"
					},
					{
						"name" : "onchange",
						"type" : "ATTRIBUTE",
						"code" : "onchange=\"\"",
						"fullDescription" : "Script to be run when an element change"
					},
					{
						"name" : "onfocus",
						"type" : "ATTRIBUTE",
						"code" : "onfocus=\"\"",
						"fullDescription" : "Script to be run when an element gets focus"
					},
					{
						"name" : "onselect",
						"type" : "ATTRIBUTE",
						"code" : "onselect=\"\"",
						"fullDescription" : "Script to be run when an element is selected"
					} ]
		},
		{
			"name" : "tfoot",
			"type" : "TAG",
			"code" : "<tfoot></tfoot>",
			"fullDescription" : "Groups the footer content in a table",
			"subTokenList" : [
					{
						"name" : "align",
						"type" : "ATTRIBUTE",
						"code" : "align=\"\"",
						"fullDescription" : "Aligns the content inside the tfoot element"
					},
					{
						"name" : "char",
						"type" : "ATTRIBUTE",
						"code" : "char=\"\"",
						"fullDescription" : "Aligns the content inside the tfoot element to a character"
					},
					{
						"name" : "charoff",
						"type" : "ATTRIBUTE",
						"code" : "charoff=\"\"",
						"fullDescription" : "Sets the number of characters the content inside the tfoot element will be aligned from the character specified by the char attribute"
					},
					{
						"name" : "valign",
						"type" : "ATTRIBUTE",
						"code" : "valign=\"\"",
						"fullDescription" : "Vertical aligns the content inside the tfoot element"
					} ]
		},
		{
			"name" : "th",
			"type" : "TAG",
			"code" : "<th></th>",
			"fullDescription" : "Defines a header cell in a table",
			"subTokenList" : [
					{
						"name" : "abbr",
						"type" : "ATTRIBUTE",
						"code" : "abbr=\"\"",
						"fullDescription" : "Specifies an abbreviated version of the content in a cell"
					},
					{
						"name" : "align",
						"type" : "ATTRIBUTE",
						"code" : "align=\"\"",
						"fullDescription" : "Aligns the content in a cell"
					},
					{
						"name" : "axis",
						"type" : "ATTRIBUTE",
						"code" : "axis=\"\"",
						"fullDescription" : "Categorizes cells"
					},
					{
						"name" : "char",
						"type" : "ATTRIBUTE",
						"code" : "char=\"\"",
						"fullDescription" : "Aligns the content in a cell to a character"
					},
					{
						"name" : "charoff",
						"type" : "ATTRIBUTE",
						"code" : "charoff=\"\"",
						"fullDescription" : "Sets the number of characters the content will be aligned from the character specified by the char attribute"
					},
					{
						"name" : "colspan",
						"type" : "ATTRIBUTE",
						"code" : "colspan=\"\"",
						"fullDescription" : "Sets the number of columns a cell should span"
					},
					{
						"name" : "rowspan",
						"type" : "ATTRIBUTE",
						"code" : "rowspan=\"\"",
						"fullDescription" : "Sets the number of rows a cell should span"
					},
					{
						"name" : "scope",
						"type" : "ATTRIBUTE",
						"code" : "scope=\"\"",
						"fullDescription" : "Defines a way to associate header cells and data cells in a table "
					},
					{
						"name" : "valign",
						"type" : "ATTRIBUTE",
						"code" : "valign=\"\"",
						"fullDescription" : "Vertical aligns the content in a cell"
					} ]
		},
		{
			"name" : "thead",
			"type" : "TAG",
			"code" : "<thead></thead>",
			"fullDescription" : "Groups the header content in a table",
			"subTokenList" : [
					{
						"name" : "align",
						"type" : "ATTRIBUTE",
						"code" : "align=\"\"",
						"fullDescription" : "Aligns the content inside the thead element"
					},
					{
						"name" : "char",
						"type" : "ATTRIBUTE",
						"code" : "char=\"\"",
						"fullDescription" : "Aligns the content inside the thead element to a character"
					},
					{
						"name" : "charoff",
						"type" : "ATTRIBUTE",
						"code" : "charoff=\"\"",
						"fullDescription" : "Sets the number of characters the content inside the thead element will be aligned from the character specified by the char attribute"
					},
					{
						"name" : "valign",
						"type" : "ATTRIBUTE",
						"code" : "valign=\"\"",
						"fullDescription" : "Vertical aligns the content inside the thead element"
					} ]
		},
		{
			"name" : "title",
			"type" : "TAG",
			"code" : "<title></title>",
			"fullDescription" : "Defines the title of a document"
		},
		{
			"name" : "tr",
			"type" : "TAG",
			"code" : "<tr></tr>",
			"fullDescription" : "Defines a row in a table",
			"subTokenList" : [
					{
						"name" : "align",
						"type" : "ATTRIBUTE",
						"code" : "align=\"\"",
						"fullDescription" : "Aligns the content in a table row"
					},
					{
						"name" : "char",
						"type" : "ATTRIBUTE",
						"code" : "char=\"\"",
						"fullDescription" : "Aligns the content in a table row to a character"
					},
					{
						"name" : "charoff",
						"type" : "ATTRIBUTE",
						"code" : "charoff=\"\"",
						"fullDescription" : "Sets the number of characters the content will be aligned from the character specified by the char attribute"
					},
					{
						"name" : "valign",
						"type" : "ATTRIBUTE",
						"code" : "valign=\"\"",
						"fullDescription" : "Vertical aligns the content in a table row"
					} ]
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
		} ];

var html_attributes = [ {
	"name" : "class",
	"type" : "ATTRIBUTE",
	"code" : "class=\"\"",
	"fullDescription" : "Specifies a classname for an element"
}, {
	"name" : "id",
	"type" : "ATTRIBUTE",
	"code" : "id=\"\"",
	"fullDescription" : "Specifies a unique id for an element"
}, {
	"name" : "style",
	"type" : "ATTRIBUTE",
	"code" : "style=\"\"",
	"fullDescription" : "Specifies an inline style for an element"
}, {
	"name" : "title",
	"type" : "ATTRIBUTE",
	"code" : "title=\"\"",
	"fullDescription" : "Specifies extra information about an element"
} ];

var html_baseEvents = [
		{
			"name" : "onkeydown",
			"type" : "ATTRIBUTE",
			"code" : "onkeydown=\"\"",
			"fullDescription" : "Script to be run when a key is pressed"
		},
		{
			"name" : "onkeypress",
			"type" : "ATTRIBUTE",
			"code" : "onkeypress=\"\"",
			"fullDescription" : "Script to be run when a key is pressed and released"
		},
		{
			"name" : "onkeyup",
			"type" : "ATTRIBUTE",
			"code" : "onkeyup=\"\"",
			"fullDescription" : "Script to be run when a key is released"
		},
		{
			"name" : "onclick",
			"type" : "ATTRIBUTE",
			"code" : "onclick=\"\"",
			"fullDescription" : "Script to be run on a mouse click"
		},
		{
			"name" : "ondblclick",
			"type" : "ATTRIBUTE",
			"code" : "ondblclick=\"\"",
			"fullDescription" : "Script to be run on a mouse double-click"
		},
		{
			"name" : "onmousedown",
			"type" : "ATTRIBUTE",
			"code" : "onmousedown=\"\"",
			"fullDescription" : "Script to be run when mouse button is pressed"
		},
		{
			"name" : "onmousemove",
			"type" : "ATTRIBUTE",
			"code" : "onmousemove=\"\"",
			"fullDescription" : "Script to be run when mouse pointer moves"
		},
		{
			"name" : "onmouseout",
			"type" : "ATTRIBUTE",
			"code" : "onmouseout=\"\"",
			"fullDescription" : "Script to be run when mouse pointer moves out of an element"
		},
		{
			"name" : "onmouseover",
			"type" : "ATTRIBUTE",
			"code" : "onmouseover=\"\"",
			"fullDescription" : "Script to be run when mouse pointer moves over an element"
		},
		{
			"name" : "onmouseup",
			"type" : "ATTRIBUTE",
			"code" : "onmouseup=\"\"",
			"fullDescription" : "Script to be run when mouse button is released"
		} ];
