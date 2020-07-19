import React from 'react';
import PT from 'prop-types';
import './styles.scss';

/**
 * check box component
 */
export default function Checkbox({
  label,
  onChange,
  value,
}) {
  let res = (
    <div
      className="checkbox-container"
      onClick={() => onChange(!value)}
      onKeyPress={() => onChange(!value)}
      role="button"
      tabIndex={0}
    >
      {value && <div className="checked" />}
    </div>
  );
  if (label) {
    res = (
      <div className="checkbox-outer">
        {res}
        <div className="label">{label}</div>
      </div>
    );
  }
  return res;
}

Checkbox.propTypes = {
  label: PT.string,
  onChange: PT.func,
  value: PT.bool,
};

Checkbox.defaultProps = {
  label: '',
  onChange: () => {},
  value: false,
};
