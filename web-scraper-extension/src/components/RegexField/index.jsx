/* global window */

import React from 'react';
import PT from 'prop-types';
import { v4 as uuid } from 'uuid';
import { useGlobalState } from '@dr.pogodin/react-global-state';

import InputField from '../InputField';
import { sendMessageToPage } from '../../services/utils';

import './style.scss';

const INVALID_REGEX_CODE = 1;

function Tip({ i18n, tip, tipClassName }) {
  if (!tip) return null;

  let tipClass = 'tip';
  if (tipClassName) tipClass += ` ${tipClassName}`;

  if (tip === INVALID_REGEX_CODE) {
    return (
      <div className={tipClass}>
        <strong>
          {i18n('editor.invalidRegex')}
        </strong>
      </div>
    );
  }

  return (
    <div className={tipClass}>
      {
        tip.length ? (
          tip.map((item) => (
            <div
              className="item"
              key={item.uuid}
            >
              {`• ${item}`}
            </div>
          ))
        ) : (
          'No Match'
        )
      }
    </div>
  );
}

Tip.propTypes = {
  i18n: PT.func.isRequired,
  tip: PT.oneOfType([PT.arrayOf(PT.string), PT.number]),
  tipClassName: PT.string,
};

Tip.defaultProps = {
  tip: undefined,
  tipClassName: undefined,
};

export default function RegexField({
  attribute,
  disabled,
  inputClassName,
  onChange,
  regex,
  selector,
  tipClassName,
  title,
}) {
  const { current: heap } = React.useRef({});
  const [attrs, setAttrs] = React.useState();
  const [innerHtml, setInnerHtml] = React.useState();
  const [showTip, setShowTip] = React.useState(false);
  const [i18n] = useGlobalState('i18n');

  React.useEffect(() => {
    const onMessage = (message) => {
      switch (message.action) {
        case 'getAttributesResult': {
          if (message.opid === heap.opid) setAttrs(message.result);
          break;
        }
        case 'getInnerHtmlResult': {
          if (message.opid === heap.opid) setInnerHtml(message.result);
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

  const tip = React.useMemo(() => {
    let res;
    if (((attribute && attrs) || innerHtml) && showTip && regex) {
      res = [];

      let rx;
      try {
        rx = new RegExp(regex);
      } catch (error) {
        return INVALID_REGEX_CODE;
      }
      if (attribute) {
        for (let i = 0; i < attrs.length; ++i) {
          const keys = Object.keys(attrs[i]);
          for (let j = 0; j < keys.length; ++j) {
            const key = keys[j];
            if (attribute === key) {
              const value = attrs[i][key];
              const m = value && value.match(rx);
              if (m) {
                m.uuid = uuid();
                res.push(m[0]);
              }
              if (res.length === 3) { i = attrs.length; break; }
            }
          }
        }
      } else {
        for (let i = 0; i < innerHtml.length; ++i) {
          const m = (innerHtml[i] || '').match(rx);
          if (m) {
            m.uuid = uuid();
            res.push(m[0]);
          }
          if (res.length === 3) break;
        }
      }
    }
    return res;
  }, [attrs, innerHtml, showTip, attribute, regex]);

  return (
    <div className="RegexField">
      <InputField
        className={inputClassName}
        disabled={disabled}
        onBlur={() => setShowTip(false)}
        onChange={onChange}
        onFocus={() => {
          if (selector) {
            heap.opid = uuid();
            setShowTip(true);
            setAttrs(null);
            setInnerHtml(null);
            sendMessageToPage({
              action: attribute ? 'getAttributes' : 'getInnerHtml',
              selector,
              opid: heap.opid,
            });
          }
        }}
        title={title || i18n('editor.regex')}
        value={regex}
      />
      <Tip
        i18n={i18n}
        tip={tip}
        tipClassName={tipClassName}
      />
    </div>
  );
}

RegexField.propTypes = {
  attribute: PT.string,
  disabled: PT.bool,
  inputClassName: PT.string,
  onChange: PT.func.isRequired,
  regex: PT.string,
  selector: PT.string,
  tipClassName: PT.string,
  title: PT.string,
};

RegexField.defaultProps = {
  attribute: '',
  disabled: false,
  inputClassName: '',
  regex: '',
  selector: '',
  tipClassName: '',
  title: undefined,
};
