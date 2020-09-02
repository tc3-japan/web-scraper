import _ from 'lodash';
import React from 'react';
import ReactDropdown from 'react-dropdown';
import PT from 'prop-types';

import AttributeField from '../AttributeField';
import Button from '../Button';
import IconButton, { TYPES as IB_TYPES } from '../IconButton';
import JsEditor from '../JsEditor';
import RegexField from '../RegexField';

import './style.scss';

import Checkbox from '../Checkbox';

import { JSON_DROPDOWN } from '../../config/dropdown-list';
import getI18T from '../../i18nSetup';

/**
 * the editor expandable row
 */
export default function ExpandRow({
  indent,
  advancedExpanded,
  highlight,
  selectorPrefix,
  toggleSelectorBtn,
  rows,
  onUpdate,
  path,
  row,
}) {
  const expanded = advancedExpanded ? _.get(advancedExpanded, path) : false;
  const scriptMode = row.is_script;

  const options = _.filter(
    JSON_DROPDOWN,
    (op) => _.findIndex(rows, (r) => r.type === op.value) < 0,
  );
  const option = _.find(JSON_DROPDOWN, (op) => op.value === row.type);

  const t = getI18T();

  const renderInput = (key, title, disabled) => (
    <div className="input-container">
      <span>{title}</span>
      <input
        disabled={disabled}
        value={row[key] || ''}
        onChange={(e) => onUpdate(`${path}.${key}`, e.target.value)}
      />
    </div>
  );
  return (
    <div className={`expand-row indent${indent}`}>
      <div className="editor-row">
        <IconButton
          onClick={() => onUpdate(path, null)}
          type={IB_TYPES.MINUS}
        />
        <ReactDropdown
          options={options}
          onChange={(op) => onUpdate(`${path}.type`, op.value)}
          value={option}
          placeholder=""
        />
        <div className="seq" />
        {renderInput('element', t('editor.selector'), scriptMode)}
        <Button
          disabled={scriptMode}
          type="selector"
          path={`${path}.element`}
          highlight={highlight}
          onClick={toggleSelectorBtn}
        />
        <div className="seq" />
        <IconButton
          onClick={() => onUpdate(`meta.advancedExpanded.${path}`, !expanded)}
          title={t('editor.advanced')}
          type={expanded ? IB_TYPES.MINUS : IB_TYPES.PLUS}
        />
      </div>

      {expanded && (
        <>
          <div className="editor-row">
            <div className="row">
              <Checkbox
                disabled={scriptMode}
                value={row.full_path}
                onChange={(v) => onUpdate(`${path}.full_path`, v)}
              />
              <span>{t('editor.rootPath')}</span>
            </div>
            <div className="seq big" />
            <AttributeField
              attribute={row.attribute}
              disabled={scriptMode}
              onChange={(value) => onUpdate(`${path}.attribute`, value)}
              selector={
                selectorPrefix ? `${selectorPrefix} > ${row.element}`
                  : row.element
              }
            />
            <div className="seq big" />
            <RegexField
              attribute={row.attribute}
              disabled={scriptMode}
              onChange={(value) => onUpdate(`${path}.regex`, value)}
              regex={row.regex}
              selector={
                selectorPrefix ? `${selectorPrefix} > ${row.element}`
                  : row.element
              }
            />
          </div>
          <div className="editor-row">
            <div className="row">
              <Checkbox
                label={t('editor.script')}
                onChange={() => onUpdate(`${path}.is_script`, !scriptMode)}
                value={scriptMode}
              />
            </div>
            {
              scriptMode ? (
                <JsEditor
                  onChange={(script) => onUpdate(`${path}.script`, script)}
                  script={row.script}
                  description={t('editor.jsDescription')}
                />
              ) : null
            }
          </div>
        </>
      )}
    </div>
  );
}

ExpandRow.propTypes = {
  indent: PT.string,
  path: PT.string,
  rows: PT.arrayOf(PT.shape({})),
  onUpdate: PT.func,
  toggleSelectorBtn: PT.func,
  highlight: PT.string,
  advancedExpanded: PT.shape({}),
  row: PT.shape({
    attribute: PT.string,
    element: PT.string,
    full_path: PT.bool,
    is_script: PT.bool,
    regex: PT.string,
    script: PT.string,
    type: PT.string,
  }),
  selectorPrefix: PT.string,
};

ExpandRow.defaultProps = {
  advancedExpanded: undefined,
  indent: undefined,
  path: undefined,
  rows: undefined,
  onUpdate: undefined,
  toggleSelectorBtn: undefined,
  highlight: undefined,
  row: undefined,
  selectorPrefix: '',
};
