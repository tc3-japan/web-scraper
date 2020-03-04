/*******************************************************************************
 * This module handles most of the scraper business logic.
 */

/*******************************************************************************
 * Default settings and global variables.
 */

/* Default base API URL, which is used if scraper user has not set it before
 * via Settings panel. */
const DEFAULT_BASE_API_URL = 'https://scraper-stub-api.herokuapp.com/scrapers/';

/* In the current version the name of script to receive from Scraper API is
 * provided by this constant. */
const SCRIPT_NAME = 'amazon/purchase_history';

/* This variable is set to a promise once the module is loaded, and it is
 * resolve once the scraper initialization is completed. Thus, it acts as
 * an async barrier, which should be awaited by any other logic which can
 * be triggered by UI. */
let ready;

/* Current scraper state. */
let baseApiUrl;
let editor;
let settingsPanelOpen = false;

/* References to UI elements. */
let alertBox;
let alertBoxMessage;
let baseApiUrlInput;
let consoleContent;
let loadingConfirmationDialog;
let saveConfirmationDialog;
let testConfirmationDialog;
let settingsPanel;
let throbber;
let throbberCircles;

/*******************************************************************************
 * Auxiliary stuff.
 */

let throbberAnimationFrameId;
/**
 * Animates the throbber.
 */
async function animateThrobber() {
  await ready;
  const ANIMATION_SPEED = 0.006;
  const now = ANIMATION_SPEED * Date.now();
  const pos1 = 0.3 * Math.sin(now - Math.PI / 2);
  const pos2 = 0.3 * Math.sin(now);
  const pos3 = 0.3 * Math.sin(now + Math.PI / 2);
  throbberCircles[0].setAttribute('style', `top: ${pos1}em`);
  throbberCircles[1].setAttribute('style', `top: ${pos2}em`);
  throbberCircles[2].setAttribute('style', `top: ${pos3}em`);
  throbberAnimationFrameId = window.requestAnimationFrame(animateThrobber);
}

/**
 * Shows the throbber.
 */
function showThrobber() {
  throbberAnimationFrameId = window.requestAnimationFrame(animateThrobber);
  throbber.setAttribute('style', 'display: flex');
}

/**
 * Hides the throbber.
 */
function hideThrobber() {
  cancelAnimationFrame(throbberAnimationFrameId);
  throbber.setAttribute('style', 'display: none');
}

/**
 * Opens alert dialog.
 * @param {string} message Message to show in the alert box.
 */
function showAlert(message) {
  alertBoxMessage.innerHTML = message;
  alertBox.setAttribute('open', true);
}

/**
 * Closes alert dialog.
 */
function closeAlert() {
  alertBox.removeAttribute('open');
}

/*******************************************************************************
 * Main scraper logic.
 */

function addToConsole(content) {
  const node = document.createElement('div');
  node.innerHTML = content;
  node.classList.add('entry');
  consoleContent.appendChild(node);
}

/**
 * Gets the main API URL from the storage API.
 * @returns {Prmose<string>} Resolves to the value.
 */
async function getBaseApiUrlFromStorage() {
  return new Promise((done) => {
    chrome.storage.sync.get(['baseApiUrl'], res => done(res.baseApiUrl));
  });
}

/**
 * Saves specified value to the storage as the base API URL.
 * @param {String} value
 * @returns {Promise}
 */
async function saveBaseApiUrlToStorage(value) {
  return new Promise((done) => {
    chrome.storage.sync.set({ baseApiUrl: value }, done);
  });
}

/**
 * Closes the script loading confirmation dialog without any action.
 */
function closeScriptLoadingConfirmationDialog() {
  loadingConfirmationDialog.removeAttribute('open');
}

/**
 * Closes the script save confirmation dialog without any action.
 */
function closeSaveScriptConirmationDialog() {
  saveConfirmationDialog.removeAttribute('open');
}

/**
 * Closes the script save confirmation dialog without any action.
 */
function closeTestScriptConirmationDialog() {
  testConfirmationDialog.removeAttribute('open');
}

/**
 * Loads the script.
 */
async function loadScript() {
  closeScriptLoadingConfirmationDialog();
  showThrobber();
  let url = baseApiUrl;
  if (!url.endsWith('/')) url += '/';
  url += SCRIPT_NAME;
  
  try {
    const response = await fetch(url);
    const responseBody = await response.text();
    if (response.status >= 400) {
      throw Error(`Error ${response.status}: ${responseBody}`);
    }
    editor.doc.setValue(responseBody);
  } catch (error) {
    showAlert(error.message);
  }
  hideThrobber();
}

/**
 * Saves the script.
 */
async function saveScript() {
  closeSaveScriptConirmationDialog();
  showThrobber();
  let url = baseApiUrl;
  if (!url.endsWith('/')) url += '/';
  url += SCRIPT_NAME;
  try {
    const response = await fetch(url, {
      method: 'PUT',
      body: editor.getValue()
    });
    const responseBody = await response.text();
    if (response.status >= 400) {
      throw Error(`Error ${response.status}: ${responseBody}`);
    }
  } catch (error) {
    showAlert(error.message);
  }
  hideThrobber();
}

/**
 * Tests script.
 */
async function testScript() {
  closeTestScriptConirmationDialog();
  showThrobber();
  let url = baseApiUrl;
  if (!url.endsWith('/')) url += '/';
  url += SCRIPT_NAME;
  url += '/test';
  let responseBody;
  try {
    const response = await fetch(url, {
      method: 'POST',
      body: editor.getValue()
    });
    responseBody = await response.text();
    if (response.status >= 400) {
      throw Error(`Error ${response.status}: ${responseBody}`);
    }
  } catch (error) {
    showAlert(error.message);
  }
  addToConsole(responseBody);
  hideThrobber();
}

/**
 * Initiates loading of the script. If something is loaded into the editor,
 * it shows the confirmation dialog, otherwise just shortcuts to the loading.
 */
async function initScriptLoading() {
  await ready;
  if (editor.doc.getValue()) {
    loadingConfirmationDialog.setAttribute('open', true);
  } else {
    loadScript();
  }
}

/**
 * Initiates saving of the script.
 */
async function initScriptSaving() {
  await ready;
  saveConfirmationDialog.setAttribute('open', true);
}

/**
 * Initiates testing of the script.
 */
async function initScriptTesting() {
  await ready;
  testConfirmationDialog.setAttribute('open', true);
}

/*******************************************************************************
 * UI logic & Initialization.
 */

/**
 * Opens/closes Settings panel.
 */
async function toggleSettingsPanel() {
  await ready;
  settingsPanelOpen = !settingsPanelOpen;
  if (settingsPanelOpen) {
    baseApiUrlInput.value = baseApiUrl;
    settingsPanel.classList.add('open');
  }
  else settingsPanel.classList.remove('open');
}

/**
 * Initialization.
 */
ready = new Promise((done) => {
  window.addEventListener('load', async () => {
    alertBox = document.querySelector('#alert');
    alertBoxMessage = alertBox.querySelector('.message');
    alertBoxClose = alertBox.querySelector('button');
    baseApiUrlInput = document.querySelector('#base-api-url-input');
    consoleContent = document.querySelector('#console-content');
    const editorContainer = document.querySelector('div#editor');
    const loadButton = document.querySelector('button#load');
    const saveButton = document.querySelector('button#save');
    const testButton = document.querySelector('button#test');
    loadingConfirmationDialog = document.querySelector(
      '#loading-confirmation-dialog'
    );
    saveConfirmationDialog = document.querySelector(
      '#save-confirmation-dialog'
    );
    testConfirmationDialog = document.querySelector(
      '#test-confirmation-dialog'
    );
    const saveSettingsButton = document.querySelector('button#save-settings');
    const settingsButton = document.querySelector('button#settings');
    settingsPanel = document.querySelector('div.settings');
    throbber = document.querySelector('#throbber');
    throbberCircles = document.querySelectorAll('.throbber-circle');

    alertBoxClose.onclick = closeAlert;

    loadButton.onclick = initScriptLoading;
    saveButton.onclick = initScriptSaving;
    testButton.onclick = initScriptTesting;

    saveSettingsButton.onclick = () => {
      baseApiUrl = baseApiUrlInput.value;
      saveBaseApiUrlToStorage(baseApiUrl);
      toggleSettingsPanel();
    };

    editor = CodeMirror(editorContainer, {
      lineNumbers: true,
    });
    settingsButton.onclick = toggleSettingsPanel;

    loadingConfirmationDialog.querySelector('.cancel')
      .onclick = closeScriptLoadingConfirmationDialog;
    loadingConfirmationDialog.querySelector('.load')
      .onclick = loadScript;

    saveConfirmationDialog.querySelector('.cancel')
      .onclick = closeSaveScriptConirmationDialog;
    saveConfirmationDialog.querySelector('.save')
      .onclick = saveScript;

    testConfirmationDialog.querySelector('.cancel')
      .onclick = closeTestScriptConirmationDialog;
    testConfirmationDialog.querySelector('.test')
      .onclick = testScript;

    baseApiUrl = await getBaseApiUrlFromStorage() || DEFAULT_BASE_API_URL;
    baseApiUrlInput.addEventListener('keypress', (event) => {
      if (event.key === 'Enter') {
        saveSettingsButton.focus();
      }
    });

    done();
  });
});
