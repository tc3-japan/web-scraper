Scrapper DevTools Extension
===

DevTools Chrome Extension with simple UI to help debug web scraper backend.

Prerequisites
===

 1. Node 12.x.x or Node 13.x.x
 2. npm 6.x.x or more

Installation (Local Deployment)
===

 * Run: `npm install`
 * Run: `npm run build`
 * Open [chrome://extensions](chrome://extensions)
 * Enable 'Developer Mode' checkbox
 * Click 'Load unpacked extensions...'
 * Select the `build` folder from source-code folder

Packaging
===
 there are three options:
  * run: `npm run package` (zip file there is in artifacts folder)
  * zip `build` folder manaually
  * or in [chrome://extensions](chrome://extensions) select Pack extention

give zip file to anyone and do as:
 * extract the zip file
 * Open [chrome://extensions](chrome://extensions)
 * Enable 'Developer Mode' checkbox
 * Click 'Load unpacked extensions...'
 * Select the `extracted` folder

Uninstall
===
 * Open [chrome://extensions](chrome://extensions)
 * Enable 'Developer Mode' checkbox
 * Click `Remove` from scrapper extention box

Usage
===

While on any page, launch the devtools, you should see a new tab called 'Scrapper Helper'.
* `Copy Css Selector` right click anywhere and select Copy Css Selector from context menu
* `Load button` load script and show script
* `Save button` save script
* `Test button` run the script on server
* `Settings button` toggle the settings page
* in `Settings page` we can set the base url e.g.https://scraper-stub-api.herokuapp.com/scrapers
* in `Settings page` Update button save api in storage
* in `Settings page` we can back to main page by click Back button or Settings button
* all api calls errors and responses will log in the right side message board
* on loading, saving and testing theres a spinner in Load, Save, Test buttons