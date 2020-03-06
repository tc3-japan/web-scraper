function contextMenuCopyCssClickHandler(info, tab) {
  // we use this message in content.js to notify clicked context menu
  chrome.tabs.sendMessage(tab.id, {target: 'copy'});
}

chrome.contextMenus.create({
  id: 'copy',
  title: 'Copy CSS Selector',
  contexts: ['all'],
  onclick: contextMenuCopyCssClickHandler,
});


// rerun content_scripts on updates and new install
chrome.runtime.onInstalled.addListener(details => {
  if (['install', 'update'].some(reason => details.reason === reason)) {
    setTimeout(injectScriptsInAllTabs, 5000);
  }
});

function injectScriptsInAllTabs() {
  // reinject content scripts into all tabs
  const manifest = chrome.runtime.getManifest();
  const scripts = manifest.content_scripts.reduce((script, cur) => script.concat(cur.js || []), []);
  chrome.tabs.query({url:"*://*/*"}, tabs => {
    const filtered = tabs.filter(tab => tab.url.indexOf("https://chrome.google.com/webstore/detail") !== 0);
    filtered.forEach(tab => {
      scripts.map(script => chrome.tabs.executeScript(tab.id, {file: script, allFrames: true}));
    });
  });
}