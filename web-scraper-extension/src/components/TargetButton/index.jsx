/**
 * This is an auxiliary wrapper around <Button>, which implements buttons for
 * highlighting current selector in the page, and for triggering the update of
 * that selector based on user clicks in the page. While the code written
 * earlier for this purpose relies on <Button> directly, <TargetButton> allows
 * to simplify the new code. The refactoring of the older code to use
 * <TargetButton> as well is no in a scope, as of now.
 */

import React from 'react';
import PT from 'prop-types';
import { useGlobalState } from '@dr.pogodin/react-global-state';

import Button from '../Button';
import { sendMessageToPage } from '../../services/utils';

export default function TargetButton({
  disabled,
  selector,
  uuid,
}) {
  const [highlightOwner, setHighlightOwner] = useGlobalState('highlightOwner');
  return (
    <Button
      disabled={disabled}
      type="selector"
      highlight={highlightOwner}
      onClick={() => {
        if (highlightOwner === uuid) {
          sendMessageToPage({ action: 'stopInspector' });
          setHighlightOwner(null);
        } else {
          sendMessageToPage({
            action: 'startInspector',
            selector,
            path: uuid,
          });
          setHighlightOwner(uuid);
        }
      }}
      path={uuid}
    />
  );
}

TargetButton.propTypes = {
  disabled: PT.bool,
  selector: PT.string.isRequired,
  uuid: PT.string.isRequired,
};

TargetButton.defaultProps = {
  disabled: false,
};
