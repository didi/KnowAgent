/* eslint-disable react/prop-types */
import React from 'react';
import { Collapse } from '@didi/dcloud-design';
const { Panel } = Collapse;
import { CaretRightOutlined } from '@ant-design/icons';
import { DragGroup } from '@didi/dcloud-design';

interface propsType extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
  dragEnd: (params) => void;
  groupId: string | number;
  index: number;
}

const GroupItem: React.FC<propsType> = ({ children, dragEnd, groupId, index }) => {
  const sortEnd = ({ oldIndex, newIndex, collection }) => {
    dragEnd({ oldIndex, newIndex, collection });
  };
  return (
    <>
      <Collapse defaultActiveKey={['1']} expandIcon={({ isActive }) => <CaretRightOutlined rotate={isActive ? 90 : 0} />} ghost>
        <Panel header="This is panel header 1" key="1">
          <DragGroup
            dragContainerProps={{
              onSortEnd: sortEnd,
              axis: 'xy',
            }}
            dragItemProps={{
              collection: groupId,
            }}
          >
            {children}
          </DragGroup>
        </Panel>
      </Collapse>
    </>
  );
};

export default GroupItem;
