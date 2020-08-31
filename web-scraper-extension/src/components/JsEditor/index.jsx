/**
 * JS editor for advanced row.
 */
/* global window */

import _ from 'lodash';
import CodeMirror from 'codemirror';
import cmResize from 'cm-resize';
import PT from 'prop-types';
import React from 'react';
import { v4 as uuid } from 'uuid';
import { useGlobalState } from '@dr.pogodin/react-global-state';

import 'codemirror/mode/javascript/javascript';

import Button from '../Button';
import { sendMessageToPage } from '../../services/utils';

import './style.scss';

export default function JsEditor({
  onChange,
  script,
}) {
  const { current: heap } = React.useRef({});

  const [scriptExecResult, setScriptExecResult] = React.useState();
  const [i18n] = useGlobalState('i18n');

  React.useEffect(() => {
    const onMessage = (message) => {
      switch (message.action) {
        case 'execScriptResult': {
          if (message.opid === heap.execScriptOpid) {
            setScriptExecResult(message.result.value);
          }
          break;
        }
        default:
      }
    };
    window.messageListeners.push(onMessage);
    return () => {
      window.messageListeners = window.messageListeners.filter(
        (item) => item !== onMessage,
      );
    };
  }, []);

  /**
   * Manages initialisation / teardown of the script editor. To work properly,
   * it has to be a stand-alone function which is persistent across component
   * re-renders by React.
   */
  const scriptEditorHandler = React.useCallback(
    (node) => {
      if (node && !heap.scriptEditor) {
        heap.scriptEditor = CodeMirror.fromTextArea(node, {
          lineNumbers: true,
          mode: 'javascript',
        });
        heap.scriptEditor.setSize(null,100);
        heap.scriptEditor.on('change', () => {
          const value = heap.scriptEditor.doc.getValue();
          onChange(value);
        });
      } else if (!node && heap.scriptEditor) {
        heap.scriptEditor.toTextArea();
        setScriptExecResult(null);
        delete heap.scriptEditor;
      }
    },
    [],
  );

  return (
    <div className="JsEditor">
      <textarea
        className="scriptEditor"
        onChange={_.noop}
        ref={scriptEditorHandler}
        value={script}
      />
      <div
        className="resizeHandle"
        ref={(node) => {
          if (node && heap.scriptEditor) {
            cmResize(heap.scriptEditor, {
              resizableWidth: false,
              resizableHeight: true,
              handle: node,
            });
          }
        }}
      />
      <div className="mainContentRow">
        <Button
          onClick={() => {
            heap.execScriptOpid = uuid();
            setScriptExecResult(null);
            sendMessageToPage({
              action: 'execScript',
              script: script.replace(
                /\{(orderIndex|productIndex)\}/g,
                1,
              ),
              opid: heap.execScriptOpid,
            });
          }}
          title={i18n('editor.check')}
        />
        {
          scriptExecResult ? (
            <div className="scriptExecResult">
              <span className="label">
                Script Execution Result:
              </span>
              <br />
              {scriptExecResult}
            </div>
          ) : null
        }
      </div>
    </div>
  );
}

JsEditor.propTypes = {
  onChange: PT.func.isRequired,
  script: PT.string.isRequired,
};
