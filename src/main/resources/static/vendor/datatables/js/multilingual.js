/*-------------------------------------- TRANSLATE EN to HI CODE ---------------------------------- */


function googleTranslateElementInit() {
    new google.translate.TranslateElement(
        {
        	defaultLanguage:'en',
            pageLanguage: 'en',
            includedLanguages: 'en,hi,ta,bn',
            layout: google.translate.TranslateElement.InlineLayout.SIMPLE,
            autoDisplay: false,
            multiLanguagePage:true
        },
        'google_translate_element'
    );
}