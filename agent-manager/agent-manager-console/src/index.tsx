import * as ReactDOM from 'react-dom';
import * as React from 'react';
import App from './app';

const renderApp = () => {
  ReactDOM.render(<App />, document.getElementById('fe-container'));
};

renderApp();
