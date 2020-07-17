import _ from 'lodash';
import React from 'react';
import ReactDropdown from 'react-dropdown';
import PT from 'prop-types';

import Button from '../Button';
import './style.scss';

import Checkbox from '../Checkbox';

import { JSON_DROPDOWN } from '../../config/dropdown-list';
import getI18T from '../../i18nSetup';

const iconP = require('../../assets/icon_+.png');
const iconM = require('../../assets/icon_-.png');

/**
 * the editor expandable row
 */
export default function ExpandRow({
  indent,
  advancedExpanded,
  highlight,
  toggleSelectorBtn,
  rows,
  onUpdate,
  path,
  row,
}) {
  const expanded = advancedExpanded ? _.get(advancedExpanded, path) : false;

  const options = _.filter(
    JSON_DROPDOWN,
    (op) => _.findIndex(rows, (r) => r.type === op.value) < 0,
  );
  const option = _.find(JSON_DROPDOWN, (op) => op.value === row.type);

  const t = getI18T();

  const renderInput = (key, title) => (
    <div className="input-container">
      <span>{title}</span>
      <input value={row[key] || ''} onChange={(e) => onUpdate(`${path}.${key}`, e.target.value)} />
    </div>
  );
  return (
    <div className={`expand-row indent${indent}`}>
      <div className="editor-row">

        <div
          className="icon-btn"
          onClick={() => onUpdate(path, null)}
          onKeyPress={() => onUpdate(path, null)}
          role="button"
          tabIndex={0}
        >
          <img alt="btn" className="btn-icon" src={iconM} />
        </div>
        <ReactDropdown
          options={options}
          onChange={(op) => onUpdate(`${path}.type`, op.value)}
          value={option}
          placeholder=""
        />
        <div className="seq" />
        {renderInput('element', t('editor.selector'))}
        <Button
          type="selector"
          path={`${path}.element`}
          highlight={highlight}
          onClick={toggleSelectorBtn}
        />
        <div className="seq" />
        <div
          className="icon-btn"
          onClick={() => onUpdate(`meta.advancedExpanded.${path}`, !expanded)}
          onKeyPress={
            () => onUpdate(`meta.advancedExpanded.${path}`, !expanded)
          }
          role="button"
          tabIndex={0}
        >
          <img
            alt="btn"
            className="btn-icon"
            src={expanded ? iconM : iconP}
          />
          <span>{t('editor.advanced')}</span>
        </div>
      </div>

      {expanded && (
      <div className="editor-row">
        <div className="row">
          <Checkbox value={row.full_path} onChange={(v) => onUpdate(`${path}.full_path`, v)} />
          <span>{t('editor.rootPath')}</span>
        </div>
        <div className="seq big" />
        {renderInput('attribute', t('editor.attribute'))}
        <div className="seq big" />
        {renderInput('regex', t('editor.regex'))}
      </div>
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
    full_path: PT.string,
    type: PT.string,
  }),
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
};
