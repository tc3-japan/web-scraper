/**
 * Implements attribute field.
 */
/* global window */

import _ from 'lodash';
import React from 'react';
import PT from 'prop-types';
import { v4 as uuid } from 'uuid';
import { useGlobalState } from '@dr.pogodin/react-global-state';

import InputField from '../InputField';
import { sendMessageToPage } from '../../services/utils';

import './style.scss';

export default function AttributeField({
  attribute,
  disabled,
  inputClassName,
  onChange,
  selector,
  tipClassName,
}) {
  const { current: heap } = React.useRef({});
  const [attrs, setAttrs] = React.useState();
  const [showTip, setShowTip] = React.useState(false);
  const [i18n] = useGlobalState('i18n');

  React.useEffect(() => {
    const onMessage = (message) => {
      switch (message.action) {
        case 'getAttributesResult': {
          if (message.opid === heap.opid) setAttrs(message.result);
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
    if (attrs && showTip) {
      res = [];
      if (attrs[0]) {
        _.forOwn(attrs[0], (value, key) => {
          res.push(`${key}="${value}"`);
        });
      }
      /*
      attrs.forEach((item) => {
        _.forOwn(item, (value, key) => {
          res.push(`${key}="${value}"`);
        });
      });
      */
    }
    return res;
  }, [attrs, showTip]);

  let tipClass = 'tip';
  if (tipClassName) tipClass += ` ${tipClassName}`;

  return (
    <div className="AttributeField">
      <InputField
        className={inputClassName}
        disabled={disabled}
        onChange={onChange}
        onBlur={(e) => {
          // This hides the attributes tooltip, but only if the focus
          // was not lost to one of the tooltip elements. In the later
          // case removing it would interfere with handling the click
          // there, thus we leave it up to that handler to close the
          // tip.
          const { tipNode } = heap;
          if (!tipNode || !tipNode.contains(e.relatedTarget)) {
            setShowTip(false);
          }
        }}
        onFocus={() => {
          heap.opid = uuid();
          setShowTip(true);
          sendMessageToPage({
            action: 'getAttributes',
            selector,
            opid: heap.opid,
          });
        }}
        title={i18n('editor.attribute')}
        value={attribute}
      />
      {
        tip ? (
          <div
            className={tipClass}
            ref={(node) => { heap.tipNode = node; }}
          >
            {
              tip.length ? (
                tip.map((item) => {
                  const fire = () => {
                    onChange(item.match(/[^=]*/)[0]);
                    setShowTip(false);
                  };
                  return (
                    <div
                      className="item"
                      onClick={fire}
                      onKeyPress={fire}
                      key={item}
                      role="button"
                      tabIndex={0}
                    >
                      {`• ${item}`}
                    </div>
                  );
                })
              ) : (
                'No attributes'
              )
            }
          </div>
        ) : null
      }
    </div>
  );
}

AttributeField.propTypes = {
  attribute: PT.string,
  disabled: PT.bool,
  inputClassName: PT.string,
  onChange: PT.func.isRequired,
  selector: PT.string,
  tipClassName: PT.string,
};

AttributeField.defaultProps = {
  attribute: '',
  disabled: false,
  inputClassName: '',
  selector: '',
  tipClassName: '',
};
