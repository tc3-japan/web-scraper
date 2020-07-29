/* global window */

import React, { useEffect, useState } from 'react';
import ReactDropdown from 'react-dropdown';
import { SortableElement, SortableHandle } from 'react-sortable-hoc';
import { useGlobalState } from '@dr.pogodin/react-global-state';

import PT from 'prop-types';

import AttributeField from '../../AttributeField';
import Checkbox from '../../Checkbox';
import { PRODUCT_DETAILS_DROPDOWN } from '../../../config/dropdown-list';
import IconButton, { TYPES as IB_TYPES } from '../../IconButton';
import InputField from '../../InputField';
import JsEditor from '../../JsEditor';
import RegexField from '../../RegexField';
import TargetButton from '../../TargetButton';
import { sendMessageToPage } from '../../../services/utils';

import './style.scss';

const Handle = SortableHandle(() => (
  <div className="handle">⋮</div>
));

/**
 * That's the row view for any type beside `Model No (Label)`, which is
 * a special case, defined below with some extra input fields.
 */
function RegularRow({
  expanded,
  i18n,
  row,
  scriptMode,
  setExpanded,
  setScriptMode,
  updateRow,
}) {
  return (
    <div className="mainContent">
      <div className="mainContentRow">
        <ReactDropdown
          onChange={(option) => {
            const newRow = { ...row, item: option.value };

            // `model_no_label` row has extra attributes, which other items
            // don't have. Thus here we add/remove these attibutes based on
            // the user choice of row type.
            if (newRow.item === 'model_no_label') {
              newRow.labelAttribute = '';
              newRow.labelRegex = '';
              newRow.labelSelector = '';
              newRow.labelValue = '';
            } else {
              delete newRow.labelAttribute;
              delete newRow.labelRegex;
              delete newRow.labelSelector;
              delete newRow.labelValue;
            }

            updateRow(newRow);
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
                <JsEditor
                  onChange={(script) => updateRow({ ...row, script })}
                  script={row.script}
                />
              ) : null
            }
          </>
        ) : null
      }
    </div>
  );
}

RegularRow.propTypes = {
  expanded: PT.bool.isRequired,
  i18n: PT.func.isRequired,
  row: PT.shape({
    attribute: PT.string,
    item: PT.string,
    regex: PT.string,
    selector: PT.string,
    script: PT.string,
    uuid: PT.string,
  }).isRequired,
  scriptMode: PT.bool.isRequired,
  setExpanded: PT.func.isRequired,
  setScriptMode: PT.func.isRequired,
  updateRow: PT.func.isRequired,
};

/**
 * That's the row view for any type beside `Model No (Label)`, which is
 * a special case, defined below with some extra input fields.
 */
function ModelNoLabelRow({
  expanded,
  i18n,
  row,
  scriptMode,
  setExpanded,
  setScriptMode,
  updateRow,
}) {
  return (
    <div className="mainContent">
      <div className="mainContentRow">
        <ReactDropdown
          onChange={(option) => {
            const newRow = { ...row, item: option.value };

            // `model_no_label` row has extra attributes, which other items
            // don't have. Thus here we add/remove these attibutes based on
            // the user choice of row type.
            if (newRow.item === 'model_no_label') {
              newRow.labelAttribute = '';
              newRow.labelRegex = '';
              newRow.labelSelector = '';
              newRow.labelValue = '';
            } else {
              delete newRow.labelAttribute;
              delete newRow.labelRegex;
              delete newRow.labelSelector;
              delete newRow.labelValue;
            }

            updateRow(newRow);
          }}
          options={PRODUCT_DETAILS_DROPDOWN}
          placeholder=""
          value={row.item}
        />
        <div className="seq" />
        <InputField
          disabled={scriptMode}
          onChange={(labelSelector) => updateRow({ ...row, labelSelector })}
          title={i18n('editor.labelSelector')}
          value={row.labelSelector}
        />
        <TargetButton
          disabled={scriptMode}
          selector={row.labelSelector}
          uuid={`${row.uuid}-label`}
        />
        <div className="seq" />
        <InputField
          disabled={scriptMode}
          onChange={(labelValue) => updateRow({ ...row, labelValue })}
          title={i18n('editor.label')}
          value={row.labelValue}
        />
      </div>
      <div className="mainContentRow">
        <div className="seq modelNoLabelPreSelectorFiller" />
        <InputField
          disabled={scriptMode}
          onChange={(selector) => updateRow({ ...row, selector })}
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
              <div className="seq modelNoLabelPreSelectorFiller" />
              <AttributeField
                attribute={row.labelAttribute}
                disabled={scriptMode}
                onChange={
                  (labelAttribute) => updateRow({ ...row, labelAttribute })
                }
                selector={row.labelSelector}
                tipClassName="labelAttributeTip"
                title={i18n('editor.labelAttribute')}
              />
              <div className="seq" />
              <RegexField
                attribute={row.labelAttribute}
                disabled={scriptMode}
                onChange={(labelRegex) => updateRow({ ...row, labelRegex })}
                regex={row.labelRegex}
                selector={row.labelSelector}
                title={i18n('editor.labelRegex')}
                tipClassName="labelRegexTip"
              />
            </div>
            <div className="mainContentRow">
              <Checkbox
                label={i18n('editor.script')}
                onChange={() => setScriptMode(!scriptMode)}
                value={scriptMode}
              />
              <div className="seq attributeFiller" />
              <AttributeField
                attribute={row.attribute}
                disabled={scriptMode}
                onChange={(attribute) => updateRow({ ...row, attribute })}
                selector={row.selector}
              />
              <div className="seq regexFiller" />
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
                <JsEditor
                  onChange={(script) => updateRow({ ...row, script })}
                  script={row.script}
                />
              ) : null
            }
          </>
        ) : null
      }
    </div>
  );
}

ModelNoLabelRow.propTypes = {
  expanded: PT.bool.isRequired,
  i18n: PT.func.isRequired,
  row: PT.shape({
    attribute: PT.string,
    item: PT.string,
    labelAttribute: PT.string,
    labelRegex: PT.string,
    labelSelector: PT.string,
    labelValue: PT.string,
    regex: PT.string,
    selector: PT.string,
    script: PT.string,
    uuid: PT.string,
  }).isRequired,
  scriptMode: PT.bool.isRequired,
  setExpanded: PT.func.isRequired,
  setScriptMode: PT.func.isRequired,
  updateRow: PT.func.isRequired,
};

function Row({
  row,
  updateRow,
}) {
  const [i18n] = useGlobalState('i18n');
  const [expanded, setExpanded] = useState(false);
  const [highlightOwner, setHighlightOwner] = useGlobalState('highlightOwner');
  const [scriptMode, setScriptMode] = useState(false);

  useEffect(() => {
    const onMessage = (message) => {
      switch (message.action) {
        case 'click': {
          if (highlightOwner === row.uuid) {
            updateRow({ ...row, selector: message.optimalSelector });
            sendMessageToPage({ action: 'stopInspector' });
            setHighlightOwner(null);
          } else if (highlightOwner === `${row.uuid}-label`) {
            updateRow({ ...row, labelSelector: message.optimalSelector });
            sendMessageToPage({ action: 'stopInspector' });
            setHighlightOwner(null);
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

  return (
    <div className="ProductDetailsEditor_Row">
      <Handle />
      <IconButton
        className="removeRowButton"
        onClick={updateRow}
        type={IB_TYPES.MINUS}
      />
      {
        row.item === 'model_no_label' ? (
          <ModelNoLabelRow
            expanded={expanded}
            i18n={i18n}
            row={row}
            scriptMode={scriptMode}
            setExpanded={setExpanded}
            setScriptMode={setScriptMode}
            updateRow={updateRow}
          />
        ) : (
          <RegularRow
            expanded={expanded}
            i18n={i18n}
            row={row}
            scriptMode={scriptMode}
            setExpanded={setExpanded}
            setScriptMode={setScriptMode}
            updateRow={updateRow}
          />
        )
      }
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
