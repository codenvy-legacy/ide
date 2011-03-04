// see http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.config.html for details

CKEDITOR.config.toolbar_IDEall = [
    ["Upload","Source", "-", 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Find','Replace','SelectAll','RemoveFormat'],
    ['Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton', 'HiddenField'],
    '/',    // it is needed to normal work of "Collapse Toolbar" button
    ['Bold','Italic','Underline','Strike','-','Subscript','Superscript','-','NumberedList','BulletedList','-','Outdent','Indent','Blockquote','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
    ['Templates', '-', 'Link','Unlink','Anchor','-','Image','Flash','Table','HorizontalRule','Smiley','SpecialChar'],
];

CKEDITOR.config.width = "100%";
//CKEDITOR.config.height = "100%";     // does not supported still in CKEditor 3.0
CKEDITOR.config.minWidth = "100px";
CKEDITOR.config.minHeight = "100px";
CKEDITOR.dialog.minHeight = 50;
CKEDITOR.dialog.minWidth = 50;
/*
CKEDITOR.config.toolbar = "IDEall";
CKEDITOR.config.theme = "default";
CKEDITOR.config.skin = "ideall";
CKEDITOR.config.language = "en";
CKEDITOR.config.fullPage = true;   // A full page includes the <html>, <head> and <body> tags. The final output will include the <body> contents.
*/
CKEDITOR.config.resize_enabled = false;
CKEDITOR.config.baseFloatZIndex = 220000;    // to display ckeditor dialogs under the smartGWT tabSet which has ZIndex = 200000 

CKEDITOR.config.blockedKeystrokes[CKEDITOR.config.blockedKeystrokes.length] = CKEDITOR.CTRL + 115;   // this disables "Save As" browser dialog after clicking on "Ctrl+s"
CKEDITOR.config.blockedKeystrokes[CKEDITOR.config.blockedKeystrokes.length] = CKEDITOR.CTRL + 83;   // this disables "Save As" browser dialog after clicking on "Ctrl+S"

CKEDITOR.config.enterMode = CKEDITOR.ENTER_BR;  // Sets the behavior for the ENTER key. It also dictates other behaviour rules in the editor, like whether the <br> element is to be used as a paragraph separator when indenting text. The allowed values are the following constants, and their relative behavior: (CKEDITOR.ENTER_P (default), CKEDITOR.ENTER_BR, CKEDITOR.ENTER_DIV)

CKEDITOR.config.removePlugins = 'elementspath,';  // To remove plugin which displays bottom element navigating line  
CKEDITOR.config.removePlugins += 'colorbutton,colordialog,font,format,maximize,newpage,pagebreak,preview,print,save,stylescombo,about,showblocks';

// protect CDATA tags
CKEDITOR.config.protectedSource.push( /<\s*!\s*\[\s*CDATA\s*\[/img );
CKEDITOR.config.protectedSource.push( /\]\s*\]\s*>/img );

// protect <% %> tags of groovy templates
CKEDITOR.config.protectedSource.push( /<%/img );
CKEDITOR.config.protectedSource.push( /%>/img );