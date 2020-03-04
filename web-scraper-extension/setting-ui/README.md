# Web Scraper DevTools Extension for Chrome

### Build & Local Testing

0.  Install [Node](https://nodejs.org/en/) if you don't have it onboard.

1.  Execute `npm install` in the root folder of extension codebase to pull
    required dependencies from NPM.

2.  In Chrome open `chrome://extensions` page; activate _Developer mode_ switch
    in the top right corner; press _Load unpacked_ button in the panel that
    appears on top; select the root folder of the extension codebase and click
    open. The extension card will appear in the extensions tab, ensure it is
    activated (the switch in the bottom-right corner of the card). Left to this
    switch is the extension reload button.

3.  In a new browser tab open DevTools (_Ctrl + Shift + I_), and select
    _Scraper_ tab in the DevTools panel. Load some page into the browser,
    say _https://topcoder.com_.

4.  By default, the base URL of scraper API is set equal to
    https://scraper-stub-api.herokuapp.com/scrapers/ (the default can be
    changed via `DEFAULT_BASE_API_URL` constant in the `/js/devtools-panel.js`
    module of the extension). You can verify and/or change the base URL in
    the Settings panel (button in the top right corner of the extension tab
    in DevTools). To apply and save the new URL be sure to press _Save Settings_
    button. The new value will be stored inside Chrome extensions storage API,
    and will be loaded as the new default when extension is restarted.

    The name of scraper script to load is hardcoded as `SCRIPT_NAME` constant
    inside `/js/devtools-panel.js`, as per challenge specs (to load a fixed
    script at this point).

5.  Interact with _Load_, _Save_, _Test_ buttons, and the script editor to
    verify their functionality fully matches challenge specifications. To see
    error messages update `SCRIPT_NAME` to a wrong value and reload extension,
    then try the functionality again (alternatively, update the base URL
    to a wrong one).

6.  To test _Copy CSS Selector_ functionality, do right-button mouse click on
    different elements of the loaded page, and then paste clipboard content
    into the script editor. Extension relies on a 3-rd party library,
    [optimal-select](https://www.npmjs.com/package/optimal-select)
    to construct unique extensions of the pointed elements, which is
    a reasonably good choice, according to
    https://github.com/fczbkk/css-selector-generator-benchmark.

7.  To uninstall extension just press _Remove_ button in the extension
    card in the `chrome://extensions` page.

### Publishing to Chrome Web Store

1.  Follow offical instructions to zip and publish the extension to
    Chrome Web Store: https://github.com/fczbkk/css-selector-generator-benchmark

2.  To install a published extension find it in the Chrome Web Store,
    and install pressing _Add to Chrome_.
