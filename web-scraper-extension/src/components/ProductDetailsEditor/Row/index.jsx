/* global window */

import _ from 'lodash';
import CodeMirror from 'codemirror';
import 'codemirror/mode/javascript/javascript';
import React, { useEffect, useState } from 'react';
import ReactDropdown from 'react-dropdown';
import { SortableElement, SortableHandle } from 'react-sortable-hoc';
import { v4 as uuid } from 'uuid';
import { useGlobalState } from '@dr.pogodin/react-global-state';

import PT from 'prop-types';

import AttributeField from '../../AttributeField';
import Button from '../../Button';
import Checkbox from '../../Checkbox';
import { PRODUCT_DETAILS_DROPDOWN } from '../../../config/dropdown-list';
import IconButton, { TYPES as IB_TYPES } from '../../IconButton';
import InputField from '../../InputField';
import RegexField from '../../RegexField';
import TargetButton from '../../TargetButton';
import { sendMessageToPage } from '../../../services/utils';

import './style.scss';

const Handle = SortableHandle(() => (
  <div className="handle">⋮</div>
));

function Row({
  row,
  updateRow,
}) {
  const { current: heap } = React.useRef({});

  const [i18n] = useGlobalState('i18n');
  const [expanded, setExpanded] = useState(false);
  const [highlightOwner, setHighlightOwner] = useGlobalState('highlightOwner');
  const [scriptMode, setScriptMode] = useState(false);
  const [scriptExecResult, setScriptExecResult] = useState();

  useEffect(() => {
    const onMessage = (message) => {
      switch (message.action) {
        case 'click': {
          if (highlightOwner === row.uuid) {
            updateRow({ ...row, selector: message.optimalSelector });
            sendMessageToPage({ action: 'stopInspector' });
            setHighlightOwner(null);
          }
          break;
        }
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
  }, [highlightOwner, row, updateRow]);

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
        heap.scriptEditor.on('change', () => {
          const value = heap.scriptEditor.doc.getValue();
          updateRow({ ...row, script: value });
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
    <div className="ProductDetailsEditor_Row">
      <Handle />
      <IconButton
        className="removeRowButton"
        onClick={updateRow}
        type={IB_TYPES.MINUS}
      />
      <div className="mainContent">
        <div className="mainContentRow">
          <ReactDropdown
            onChange={(option) => {
              updateRow({ ...row, item: option.value });
            }}
            options={PRODUCT_DETAILS_DROPDOWN}
            placeholder=""
            value={row.item}
          />
          <div className="seq" />
          <InputField
            disabled={scriptMode}
            onChange={(value) => updateRow({ ...row, selector: value })}
            title={i18n('editor.selector')}
            value={row.selector}
          />
          <TargetButton
            disabled={scriptMode}
            selector={row.selector}
            uuid={row.uuid}
          />
          <div className="seq" />
          <IconButton
            onClick={() => setExpanded(!expanded)}
            title={i18n('editor.advanced')}
            type={expanded ? IB_TYPES.MINUS : IB_TYPES.PLUS}
          />
        </div>
        {
          expanded ? (
            <>
              <div className="mainContentRow">
                <Checkbox
                  label={i18n('editor.script')}
                  onChange={() => setScriptMode(!scriptMode)}
                  value={scriptMode}
                />
                <div className="seq" />
                <AttributeField
                  attribute={row.attribute}
                  disabled={scriptMode}
                  onChange={(attribute) => updateRow({ ...row, attribute })}
                  selector={row.selector}
                />
                <div className="seq" />
                <RegexField
                  attribute={row.attribute}
                  disabled={scriptMode}
                  onChange={(regex) => updateRow({ ...row, regex })}
                  regex={row.regex}
                  selector={row.selector}
                />
              </div>
              {
                scriptMode ? (
                  <>
                    <textarea
                      className="scriptEditor"
                      onChange={_.noop}
                      ref={scriptEditorHandler}
                      value={row.script}
                    />
                    <div className="mainContentRow">
                      <Button
                        onClick={() => {
                          let { script } = row;
                          script = script.replace(
                            /\{(orderIndex|productIndex)\}/g,
                            1,
                          );
                          heap.execScriptOpid = uuid();
                          setScriptExecResult(null);
                          sendMessageToPage({
                            action: 'execScript',
                            script,
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
                  </>
                ) : null
              }
            </>
          ) : null
        }
      </div>
    </div>
  );
}

Row.propTypes = {
  row: PT.shape({
    attribute: PT.string,
    item: PT.string,
    regex: PT.string,
    selector: PT.string,
    script: PT.string,
    uuid: PT.string,
  }).isRequired,
  updateRow: PT.func.isRequired,
};

export default SortableElement(Row);
