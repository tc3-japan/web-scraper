import PT from 'prop-types';
import React from 'react';
import { SortableContainer } from 'react-sortable-hoc';
import { v4 as uuid } from 'uuid';
import { useGlobalState } from '@dr.pogodin/react-global-state';

import IconButton, { TYPES as IB_TYPES } from '../../IconButton';
import Row from '../Row';

import './style.scss';

const Rows = SortableContainer(({
  group,
  updateGroup,
}) => (
  <div>
    {
      group.map((item, index) => (
        <Row
          row={item}
          index={index}
          key={item.uuid}
          updateRow={(updatedRow) => {
            let newRows = group.slice(0, index);
            if (updatedRow) newRows.push(updatedRow);
            newRows = newRows.concat(group.slice(index + 1, group.length));
            newRows.uuid = group.uuid;
            updateGroup(newRows);
          }}
        />
      ))
    }
  </div>
));

export default function RowGroup({
  updateGroup,
  group,
}) {
  const [i18n] = useGlobalState('i18n');
  return (
    <div className="ProductDetailsEditor_RowGroup">
      <IconButton
        onClick={updateGroup}
        topMargin
        type={IB_TYPES.MINUS}
      />
      <div className="frame">
        <Rows
          group={group}
          onSortEnd={({ newIndex, oldIndex }) => {
            const newGroup = [...group];
            const [item] = newGroup.splice(oldIndex, 1);
            newGroup.splice(newIndex, 0, item);
            updateGroup(newGroup);
          }}
          updateGroup={updateGroup}
          useDragHandle
        />
        <IconButton
          className="addItem"
          onClick={() => {
            const newItem = {
              attribute: '',
              item: '',
              regex: '',
              script: '',
              selector: '',
              uuid: uuid(),
            };
            const newGroup = [...group, newItem];
            newGroup.uuid = group.uuid;
            updateGroup(newGroup);
          }}
          topMargin
          title={i18n('editor.addItem')}
          type={IB_TYPES.PLUS}
        />
      </div>
    </div>
  );
}

RowGroup.propTypes = {
  updateGroup: PT.func.isRequired,
  group: PT.arrayOf(PT.shape()).isRequired,
};
