var css_tokens = [
		{
			"name" : "!important",
			"type" : "PROPERTY"
		},
		{
			"name" : "@charset",
			"type" : "PROPERTY"
		},
		{
			"name" : "@import",
			"type" : "PROPERTY"
		},
		{
			"name" : "@font-face",
			"type" : "PROPERTY"
		},
		{
			"name" : "@media",
			"type" : "PROPERTY"
		},
		{
			"name" : "@page",
			"type" : "PROPERTY"
		},
		{
			"name" : "background",
			"type" : "PROPERTY",
			"code" : "background:",
			"fullDescription" : "Sets all the background properties in one declaration"
		},
		{
			"name" : "background-attachment",
			"type" : "PROPERTY",
			"code" : "background-attachment:",
			"fullDescription" : "Sets whether a background image is fixed or scrolls with the rest of the page"
		},
		{
			"name" : "background-color",
			"type" : "PROPERTY",
			"code" : "background-color:",
			"fullDescription" : "Sets the background color of an element"
		},
		{
			"name" : "background-image",
			"type" : "PROPERTY",
			"code" : "background-image:",
			"fullDescription" : "Sets the background image for an element"
		},
		{
			"name" : "background-position",
			"type" : "PROPERTY",
			"code" : "background-position:",
			"fullDescription" : "Sets the starting position of a background image"
		},
		{
			"name" : "background-repeat",
			"type" : "PROPERTY",
			"code" : "background-repeat:",
			"fullDescription" : "Sets how a background image will be repeated"
		},
		{
			"name" : "border",
			"type" : "PROPERTY",
			"code" : "border:",
			"fullDescription" : "Sets all the border properties in one declaration"
		},
		{
			"name" : "border-bottom",
			"type" : "PROPERTY",
			"code" : "border-bottom:",
			"fullDescription" : "Sets all the bottom border properties in one declaration"
		},
		{
			"name" : "border-bottom-color",
			"type" : "PROPERTY",
			"code" : "border-bottom-color:",
			"fullDescription" : "Sets the color of the bottom border"
		},
		{
			"name" : "border-bottom-style",
			"type" : "PROPERTY",
			"code" : "border-bottom-style:",
			"fullDescription" : "Sets the style of the bottom border"
		},
		{
			"name" : "border-bottom-width",
			"type" : "PROPERTY",
			"code" : "border-bottom-width:",
			"fullDescription" : "Sets the width of the bottom border"
		},
		{
			"name" : "border-color",
			"type" : "PROPERTY",
			"code" : "border-color",
			"fullDescription" : "Sets the color of the four borders"
		},
		{
			"name" : "border-collapse",
			"type" : "PROPERTY",
			"code" : "border-collapse:",
			"fullDescription" : "Specifies whether or not table borders should be collapsed"
		},
		{
			"name" : "border-left",
			"type" : "PROPERTY",
			"code" : "border-left:",
			"fullDescription" : "Sets all the left border properties in one declaration"
		},
		{
			"name" : "border-left-color",
			"type" : "PROPERTY",
			"code" : "border-left-color:",
			"fullDescription" : "Sets the color of the left border"
		},
		{
			"name" : "border-left-style",
			"type" : "PROPERTY",
			"code" : "border-left-style:",
			"fullDescription" : "Sets the style of the left border"
		},
		{
			"name" : "border-left-width",
			"type" : "PROPERTY",
			"code" : "border-left-width:",
			"fullDescription" : "Sets the width of the left border"
		},
		{
			"name" : "border-right",
			"type" : "PROPERTY",
			"code" : "border-right:",
			"fullDescription" : "Sets all the right border properties in one declaration"
		},
		{
			"name" : "border-right-color",
			"type" : "PROPERTY",
			"code" : "border-right-color:",
			"fullDescription" : "Sets the color of the right border"
		},
		{
			"name" : "border-right-style",
			"type" : "PROPERTY",
			"code" : "border-right-style:",
			"fullDescription" : "Sets the style of the right border"
		},
		{
			"name" : "border-right-width",
			"type" : "PROPERTY",
			"code" : "border-right-width:",
			"fullDescription" : "Sets the width of the right border"
		},
		{
			"name" : "border-spacing",
			"type" : "PROPERTY",
			"code" : "border-spacing:",
			"fullDescription" : "Specifies the distance between the borders of adjacent cells"
		},
		{
			"name" : "border-style",
			"type" : "PROPERTY",
			"code" : "border-style:",
			"fullDescription" : "Sets the style of the four borders"
		},
		{
			"name" : "border-top",
			"type" : "PROPERTY",
			"code" : "border-top:",
			"fullDescription" : "Sets all the top border properties in one declaration"
		},
		{
			"name" : "border-top-color",
			"type" : "PROPERTY",
			"code" : "border-top-color:",
			"fullDescription" : "Sets the color of the top border"
		},
		{
			"name" : "border-top-style",
			"type" : "PROPERTY",
			"code" : "border-top-style:",
			"fullDescription" : "Sets the style of the top border"
		},
		{
			"name" : "border-top-width",
			"type" : "PROPERTY",
			"code" : "border-top-width:",
			"fullDescription" : "Sets the width of the top border"
		},
		{
			"name" : "border-width",
			"type" : "PROPERTY",
			"code" : "border-width:",
			"fullDescription" : "Sets the width of the four borders"
		},
		{
			"name" : "bottom",
			"type" : "PROPERTY",
			"code" : "bottom:",
			"fullDescription" : "Sets the bottom margin edge for a positioned box"
		},
		{
			"name" : "caption-side",
			"type" : "PROPERTY",
			"code" : "caption-side:",
			"fullDescription" : "Specifies the placement of a table caption"
		},
		{
			"name" : "clear",
			"type" : "PROPERTY",
			"code" : "clear:",
			"fullDescription" : "Specifies which sides of an element where other floating elements are not allowed"
		},
		{
			"name" : "clip",
			"type" : "PROPERTY",
			"code" : "clip:",
			"fullDescription" : "Clips an absolutely positioned element"
		},
		{
			"name" : "color",
			"type" : "PROPERTY",
			"code" : "color:",
			"fullDescription" : "Sets the color of text"
		},
		{
			"name" : "content",
			"type" : "PROPERTY",
			"code" : "content:",
			"fullDescription" : "Used with the :before and :after pseudo-elements, to insert generated content"
		},
		{
			"name" : "counter-increment",
			"type" : "PROPERTY",
			"code" : "counter-increment:",
			"fullDescription" : "Increments one or more counters"
		},
		{
			"name" : "counter-reset",
			"type" : "PROPERTY",
			"code" : "counter-reset:",
			"fullDescription" : "Creates or resets one or more counters"
		},
		{
			"name" : "cursor",
			"type" : "PROPERTY",
			"code" : "cursor:",
			"fullDescription" : "Specifies the type of cursor to be displayed"
		},
		{
			"name" : "direction",
			"type" : "PROPERTY",
			"code" : "direction:",
			"fullDescription" : "Specifies the text direction/writing direction"
		},
		{
			"name" : "display",
			"type" : "PROPERTY",
			"code" : "display:",
			"fullDescription" : "Specifies the type of box an element should generate"
		},
		{
			"name" : "empty-cells",
			"type" : "PROPERTY",
			"code" : "empty-cells:",
			"fullDescription" : "Specifies whether or not to display borders and background on empty cells in a table"
		},
		{
			"name" : "float",
			"type" : "PROPERTY",
			"code" : "float:",
			"fullDescription" : "Specifies whether or not a box should float"
		},
		{
			"name" : "font",
			"type" : "PROPERTY",
			"code" : "font:",
			"fullDescription" : "Sets all the font properties in one declaration"
		},
		{
			"name" : "font-family",
			"type" : "PROPERTY",
			"code" : "font-family:",
			"fullDescription" : "Specifies the font family for text"
		},
		{
			"name" : "font-size",
			"type" : "PROPERTY",
			"code" : "font-size:",
			"fullDescription" : "Specifies the font size of text"
		},
		{
			"name" : "font-style",
			"type" : "PROPERTY",
			"code" : "font-style:",
			"fullDescription" : "Specifies the font style for text"
		},
		{
			"name" : "font-variant",
			"type" : "PROPERTY",
			"code" : "font-variant:",
			"fullDescription" : "Specifies whether or not a text should be displayed in a small-caps font"
		},
		{
			"name" : "font-weight",
			"type" : "PROPERTY",
			"code" : "font-weight:",
			"fullDescription" : "Specifies the weight of a font"
		},
		{
			"name" : "height",
			"type" : "PROPERTY",
			"code" : "height:",
			"fullDescription" : "Sets the height of an element"
		},
		{
			"name" : "left",
			"type" : "PROPERTY",
			"code" : "left:",
			"fullDescription" : "Sets the left margin edge for a positioned box"
		},
		{
			"name" : "letter-spacing",
			"type" : "PROPERTY",
			"code" : "letter-spacing:",
			"fullDescription" : "Increase or decrease the space between characters in a text"
		},
		{
			"name" : "line-height",
			"type" : "PROPERTY",
			"code" : "line-height:",
			"fullDescription" : "Sets the line height"
		},
		{
			"name" : "list-style",
			"type" : "PROPERTY",
			"code" : "list-style:",
			"fullDescription" : "Sets all the properties for a list in one declaration"
		},
		{
			"name" : "list-style-image",
			"type" : "PROPERTY",
			"code" : "list-style-image:",
			"fullDescription" : "Specifies an image as the list-item marker"
		},
		{
			"name" : "list-style-position",
			"type" : "PROPERTY",
			"code" : "list-style-position:",
			"fullDescription" : "Specifies if the list-item markers should appear inside or outside the content flow"
		},
		{
			"name" : "list-style-type",
			"type" : "PROPERTY",
			"code" : "list-style-type:",
			"fullDescription" : "Specifies the type of list-item marker"
		},
		{
			"name" : "margin",
			"type" : "PROPERTY",
			"code" : "margin:",
			"fullDescription" : "Sets all the margin properties in one declaration"
		},
		{
			"name" : "margin-bottom",
			"type" : "PROPERTY",
			"code" : "margin-bottom:",
			"fullDescription" : "Sets the bottom margin of an element"
		},
		{
			"name" : "margin-left",
			"type" : "PROPERTY",
			"code" : "margin-left:",
			"fullDescription" : "Sets the left margin of an element"
		},
		{
			"name" : "margin-right",
			"type" : "PROPERTY",
			"code" : "margin-right:",
			"fullDescription" : "Sets the right margin of an element"
		},
		{
			"name" : "margin-top",
			"type" : "PROPERTY",
			"code" : "margin-top:",
			"fullDescription" : "Sets the top margin of an element"
		},
		{
			"name" : "max-height",
			"type" : "PROPERTY",
			"code" : "max-height:",
			"fullDescription" : "Sets the maximum height of an element"
		},
		{
			"name" : "max-width",
			"type" : "PROPERTY",
			"code" : "max-width:",
			"fullDescription" : "Sets the maximum width of an element"
		},
		{
			"name" : "min-height",
			"type" : "PROPERTY",
			"code" : "min-height:",
			"fullDescription" : "Sets the minimum height of an element"
		},
		{
			"name" : "min-width",
			"type" : "PROPERTY",
			"code" : "min-width:",
			"fullDescription" : "Sets the minimum width of an element"
		},
		{
			"name" : "orphans",
			"type" : "PROPERTY",
			"code" : "orphans:",
			"fullDescription" : "Sets the minimum number of lines that must be left at the bottom of a page when a page break occurs inside an element"
		},
		{
			"name" : "outline",
			"type" : "PROPERTY",
			"code" : "outline:",
			"fullDescription" : "Sets all the outline properties in one declaration"
		},
		{
			"name" : "outline-color",
			"type" : "PROPERTY",
			"code" : "outline-color:",
			"fullDescription" : "Sets the color of an outline"
		},
		{
			"name" : "outline-style",
			"type" : "PROPERTY",
			"code" : "outline-style:",
			"fullDescription" : "Sets the style of an outline"
		},
		{
			"name" : "outline-width",
			"type" : "PROPERTY",
			"code" : "outline-width:",
			"fullDescription" : "Sets the width of an outline"
		},
		{
			"name" : "overflow",
			"type" : "PROPERTY",
			"code" : "overflow:",
			"fullDescription" : "Specifies what happens if content overflows an element's box"
		},
		{
			"name" : "padding",
			"type" : "PROPERTY",
			"code" : "padding:",
			"fullDescription" : "Sets all the padding properties in one declaration"
		},
		{
			"name" : "padding-bottom",
			"type" : "PROPERTY",
			"code" : "padding-bottom:",
			"fullDescription" : "Sets the bottom padding of an element"
		},
		{
			"name" : "padding-left",
			"type" : "PROPERTY",
			"code" : "padding-left:",
			"fullDescription" : "Sets the left padding of an element"
		},
		{
			"name" : "padding-right",
			"type" : "PROPERTY",
			"code" : "padding-right:",
			"fullDescription" : "Sets the right padding of an element"
		},
		{
			"name" : "padding-top",
			"type" : "PROPERTY",
			"code" : "padding-top:",
			"fullDescription" : "Sets the top padding of an element"
		},
		{
			"name" : "page-break-after",
			"type" : "PROPERTY",
			"code" : "page-break-after:",
			"fullDescription" : "Sets the page-breaking behavior after an element"
		},
		{
			"name" : "page-break-before",
			"type" : "PROPERTY",
			"code" : "page-break-before:",
			"fullDescription" : "Sets the page-breaking behavior before an element"
		},
		{
			"name" : "page-break-inside",
			"type" : "PROPERTY",
			"code" : "page-break-inside:",
			"fullDescription" : "Sets the page-breaking behavior inside an element"
		},
		{
			"name" : "position",
			"type" : "PROPERTY",
			"code" : "position:",
			"fullDescription" : "the type of positioning for an element"
		},
		{
			"name" : "quotes",
			"type" : "PROPERTY",
			"code" : "quotes:",
			"fullDescription" : "Sets the type of quotation marks for embedded quotations"
		},
		{
			"name" : "right",
			"type" : "PROPERTY",
			"code" : "right:",
			"fullDescription" : "Sets the right margin edge for a positioned box"
		},
		{
			"name" : "table-layout",
			"type" : "PROPERTY",
			"code" : "table-layout:",
			"fullDescription" : "Sets the layout algorithm to be used for a table"
		},
		{
			"name" : "text-align",
			"type" : "PROPERTY",
			"code" : "text-align:",
			"fullDescription" : "Specifies the horizontal alignment of text"
		},
		{
			"name" : "text-decoration",
			"type" : "PROPERTY",
			"code" : "text-decoration:",
			"fullDescription" : "Specifies the decoration added to text"
		},
		{
			"name" : "text-indent",
			"type" : "PROPERTY",
			"code" : "text-indent:",
			"fullDescription" : "Specifies the indentation of the first line in a text-block"
		},
		{
			"name" : "text-shadow",
			"type" : "PROPERTY",
			"code" : "text-shadow:",
			"fullDescription" : "Specifies the shadow effect added to text"
		},
		{
			"name" : "text-transform",
			"type" : "PROPERTY",
			"code" : "text-transform:",
			"fullDescription" : "Controls the capitalization of text"
		},
		{
			"name" : "top",
			"type" : "PROPERTY",
			"code" : "top:",
			"fullDescription" : "Sets the top margin edge for a positioned box"
		},
		{
			"name" : "unicode-bidi",
			"type" : "PROPERTY",
			"code" : "unicode-bidi"
		},
		{
			"name" : "vertical-align",
			"type" : "PROPERTY",
			"code" : "vertical-align:",
			"fullDescription" : "Sets the vertical alignment of an element"
		},
		{
			"name" : "visibility",
			"type" : "PROPERTY",
			"code" : "visibility:",
			"fullDescription" : "Specifies whether or not an element is visible"
		},
		{
			"name" : "white-space",
			"type" : "PROPERTY",
			"code" : "white-space:",
			"fullDescription" : "Specifies how white-space inside an element is handled"
		},
		{
			"name" : "widows",
			"type" : "PROPERTY",
			"code" : "widows:",
			"fullDescription" : "Sets the minimum number of lines that must be left at the top of a page when a page break occurs inside an element"
		},
		{
			"name" : "width",
			"type" : "PROPERTY",
			"code" : "width:",
			"fullDescription" : "Sets the width of an element"
		},
		{
			"name" : "word-spacing",
			"type" : "PROPERTY",
			"code" : "word-spacing:",
			"fullDescription" : "Increases or decreases the space between words in a text"
		}, {
			"name" : "z-index",
			"type" : "PROPERTY",
			"code" : "z-index:",
			"fullDescription" : "Sets the stack order of an element"
		}
]
