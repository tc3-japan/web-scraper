Scraper Configuration UI - Chrome Extension Development
===

### Prerequisites

 1. Node 12.x.x or Node 13.x.x
 2. npm 6.x.x or more

### Installation (Local Deployment)

 * Run: `npm install`
 * Run: `npm run build`
 * Open [chrome://extensions](chrome://extensions)
 * Enable 'Developer Mode' checkbox
 * Click 'Load unpacked extensions...'
 * Select the `build` folder from source-code folder

### Packaging

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

### Uninstall

 * Open [chrome://extensions](chrome://extensions)
 * Enable 'Developer Mode' checkbox
 * Click `Remove` from scrapper extention box

### Config

#### i18next

- open *src/config/config.js*, update `I18_CONFIG.lng` value to use other lang,  you also can follow other json file in *src/locales*,  then update ``I18_CONFIG.resources` to add new lang

#### Dropdown values

- all drowndown values are locate in src/config/config.js, you can update these if needed



### Firefox support

- all native api are used `chrome||browser` to compatibility for firefox, so it can run firefox in theory (didn't test).

### Log panel

- click log button, the tool will show a overlay log panel, this panel only show log, and will not receive any events,
- click log button again, the log panel will close

### Validation

open amazone history page, launch the devtools, you should see a new tab called 'Scrapper Helper', click it the tab switch this tool, current json file from heroku is write by hand, so there are some different with this tool, please clear it before test

- select site and type, then click load to load json
  - click current url to set current page url
  - **Purchase Order**.UrlSelecor is a root path, so it only need click 1 time when selector
  - click any selector button under **Purchase Order**, it will need click choose two elements, then it will fill parent(if not exist), and fill it owner self path, then click selector any, you will see the highlight elements
  - when Root path is checked, you will only need choose one element
- when you completed edit json, click save button to save json, open log panel to check the json content
- You also can click test button to test, open log panel to check the returned content
- click setting button, you can update the API base url 