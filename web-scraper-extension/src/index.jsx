/* global document */

import React from 'react';
import ReactDOM from 'react-dom';
import { GlobalStateProvider } from '@dr.pogodin/react-global-state';

import App from './App';
import './i18nSetup';

ReactDOM.render(
  (
    <GlobalStateProvider>
      <App />
    </GlobalStateProvider>
  ),
  document.getElementById('root'),
);
