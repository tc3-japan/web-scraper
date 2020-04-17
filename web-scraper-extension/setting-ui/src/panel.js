import swal from 'sweetalert';
import Split from 'split.js';
import ace from 'ace-builds/src-noconflict/ace';
import 'ace-builds/src-noconflict/mode-json';
import 'bootstrap/dist/css/bootstrap.min.css';

import './panel.css';

let isSettingsPageOpen = false;
let baseApi = 'https://scraper-stub-api.herokuapp.com/scrapers';
const site = 'amazon';
const type = 'purchase_history';

function storageGet(key) {
  return new Promise((resolve, reject) => {
    chrome.storage.local.get([key], result => {
      // runtime.lastError will be defined during an API method callback if there was an error
      const error = chrome.runtime.lastError;
      if (error) {
        reject(error);
      } else {
        resolve(result[key]);
      }
    });
  });
}

function storageSet(key, value) {
  return new Promise((resolve, reject) => {
    chrome.storage.local.set({[key]: value}, () => {
      // runtime.lastError will be defined during an API method callback if there was an error
      const error = chrome.runtime.lastError;
      if (error) {
        reject(error);
      } else {
        resolve();
      }
    });
  });
}

// delete last '/' in api
function normalizeUrl(url) {
  if (url[url.length - 1] === '/') {
    return url.substring(0, url.length - 1);
  }
  return url;
}

async function getApi() {
  const api = await storageGet('api');
  if (api) {
    baseApi = api;
  }
  return baseApi;
}

// read base api from extention storage
async function getUrl() {

  try {
    const api = await getApi();
    return `${normalizeUrl(api)}/${site}/${type}`;
  } catch (error) {
    logError(error);
    return '';
  }
}

async function fetchRequest(request) {
  try {
    const response = await fetch(request);
    if (response.status === 200) {
      return response.text();
    }
    return Promise.reject(`JSON load failed with status code ${response.status}`);
  } catch (error) {
    return Promise.reject(`JSON load exception ${error}`);
  }
}

function padZero(num) {
    return num.toString().padStart(2, "0")
}

function getLocalDatetime() {
  const d = new Date();
  return d.getFullYear()
         + padZero(d.getMonth() + 1)
         + padZero(d.getDate())
         + padZero(d.getHours())
         + padZero(d.getMinutes())
         + padZero(d.getSeconds());
}

function logError(error) {
  const messageBoard = document.getElementById('message');
  const errElem = document.createElement('div');
  errElem.classList.add('error');
  errElem.textContent = getLocalDatetime() + " " + error;
  // new errors comes at top of message board
  messageBoard.insertAdjacentElement('afterbegin', errElem);
}

function logSucceeded(message, data) {
  const messageBoard = document.getElementById('message');

  if (data) {
    const dataElem = document.createElement('div');
    dataElem.classList.add('data');
    dataElem.textContent = getLocalDatetime() + " " + data;
    // new data comes at top of message board
    messageBoard.insertAdjacentElement('afterbegin', dataElem);
  }

  const messageElem = document.createElement('div');
  messageElem.classList.add('success');
  messageElem.textContent = message;
  // new message comes at top of message board
  messageBoard.insertAdjacentElement('afterbegin', messageElem);
}

function spinnerHandler(button) {
  button.disabled = true;
  const spinner = button.querySelector('.hidden-spinner');
  spinner.classList.toggle('hidden-spinner');

  return function stopSpinner() {
    spinner.classList.toggle('hidden-spinner');
    button.disabled = false;
  };
}

function addListeners(editor) {
  document.getElementById('load').addEventListener('click', async function() {
    // doesn't show message when editor is empty
    if (editor.getValue() !== '') {
      const result = await swal({
        title: 'Are you sure to load the JSON?',
        text: 'Unsaved changes will be lost.',
        buttons: ['Cancel', 'Confirm'],
      });
      if (!result) {
        return;
      }
    }

    const url = await getUrl();
    const request = new Request(url);
    const stop = spinnerHandler(this);
    try {
      const code = await fetchRequest(request);
      logSucceeded('JSON load succeeded');
      // 1 set cursor at end
      editor.setValue(code, 1);
    } catch (error) {
      logError(error);
    } finally {
      stop();
    }
  });

//  const head = new Headers({ 'Content-Type': 'application/json' });
//  const head = new Headers({ 'Content-Type': 'text/json' });

  document.getElementById('save').addEventListener('click', async function() {
    const result = await swal({
      title: 'Are you sure to save the JSON?',
      buttons: ['Cancel', 'Confirm'],
    });
    if (!result) {
      return;
    }
    
    const url = await getUrl();
    const request = new Request(url, {method: 'PUT', body: editor.getValue()});
    const stop = spinnerHandler(this);
    try {
      await fetchRequest(request);
      logSucceeded('JSON save succeeded');
    } catch (error) {
      logError(error);
    } finally {
      stop();
    }
  });

  document.getElementById('test').addEventListener('click', async function() {
    const result = await swal({
      title: 'Are you sure to test the JSON?',
      buttons: ['Cancel', 'Confirm'],
    });
    if (!result) {
      return;
    }
    
    const url = (await getUrl()) + '/test';
    const request = new Request(url, {method: 'POST', body: editor.getValue()});
    const stop = spinnerHandler(this);
    try {
      const response = await fetchRequest(request);
      try {
        // beautiful json
        logSucceeded('JSON test succeeded', JSON.stringify(JSON.parse(response), null, 2));
      } catch (_) {
        // if json was not valid just simple response
        logSucceeded('JSON test succeeded', response);
      }
    } catch (error) {
      logError(error);
    } finally {
      stop();
    }
  });

  document.getElementById('settings').addEventListener('click', toggleSettingsPage);

  document.getElementById('back').addEventListener('click', toggleSettingsPage);

  document.getElementById('settings-form').addEventListener('submit', async function(event) {
    event.preventDefault();
    const apiUrlBase = document.getElementById('api-url-base').value.trim();
    if (apiUrlBase) {
      // save api to storage extention
      await storageSet('api', apiUrlBase);
    }
    toggleSettingsPage();
  });
}

async function toggleSettingsPage() {
  isSettingsPageOpen = !isSettingsPageOpen;
  if (isSettingsPageOpen) {
    try {
      const api = await getApi();
      document.getElementById('api-url-base').value = api;
    } catch (error) {
      logError(error);
    }
  }
  document.getElementById('main-page').classList.toggle('hidden');
  document.getElementById('settings-page').classList.toggle('hidden');
}

async function main() {
  // create editor from <div id="editor" />
  const editor = ace.edit('editor');
  editor.session.setMode('ace/mode/json');

  Split(['#editor-wrapper', '#message'], {
    sizes: [75, 25],
    onDragEnd: () => editor.resize(),
  });

  addListeners(editor);
}

main();
