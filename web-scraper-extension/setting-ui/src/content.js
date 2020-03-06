// Content scripts are files that run in the context of web pages
// they are able to read details of the web pages
// and pass information to their parent extension.
// Content scripts declared in manifest.json
import getCssSelector from 'css-selector-generator';
import copy from 'copy-to-clipboard';

// right clicked element (opend context menu on)
let selectedElement;

document.addEventListener('contextmenu', event => {
  selectedElement = event.target;
});

// notified when "Copy Css Selector" in context menu clicked
chrome.runtime.onMessage.addListener(request => {
  if (request && request.target === 'copy') {
    const cssSelector = getCssSelector(selectedElement, {
      includeTag: true,
    });
    copy(cssSelector);
  }
});