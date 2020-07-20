import React from 'react';
import PT from 'prop-types';

export default function InputField({
  className,
  disabled,
  onChange,
  onBlur,
  onFocus,
  title,
  value,
}) {
  let containerClass = 'input-container';
  if (className) containerClass += ` ${className}`;
  return (
    <div className={containerClass}>
      <span>{title}</span>
      <input
        disabled={disabled}
        onBlur={onBlur}
        onChange={onChange && ((e) => onChange(e.target.value))}
        onFocus={onFocus}
        value={value}
      />
    </div>
  );
}

InputField.propTypes = {
  className: PT.string,
  disabled: PT.bool,
  onBlur: PT.func,
  onChange: PT.func,
  onFocus: PT.func,
  title: PT.string,
  value: PT.string,
};

InputField.defaultProps = {
  className: undefined,
  disabled: false,
  onBlur: undefined,
  onChange: undefined,
  onFocus: undefined,
  title: '',
  value: undefined,
};
