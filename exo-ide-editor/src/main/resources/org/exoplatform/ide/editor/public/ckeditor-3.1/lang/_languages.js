/*
Copyright (c) 2003-2010, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

var CKEDITOR_LANGS = (function()
{
    var langs =
    {
        en        : 'English',
        fr        : 'French',
        ru        : 'Russian',
        uk        : 'Ukrainian',
        vi        : 'Vietnamese'
    };

    var langsArray = [];

    for ( var code in langs )
    {
        langsArray.push( { code : code, name : langs[ code ] } );
    }

    langsArray.sort( function( a, b )
        {
            return ( a.name < b.name ) ? -1 : 1;
        });

    return langsArray;
})();
