/* eslint-disable react/prop-types */
import React from 'react';
import { propsType as IcontainerProps } from 'knowdesign/es/extend/container';
import { SortableContainer, SortableContainerProps } from 'react-sortable-hoc';
import { Container } from 'knowdesign';

interface propsType extends React.HTMLAttributes<HTMLDivElement> {
  children: React.ReactNode;
  dragContainerProps?: SortableContainerProps;
  containerProps?: IcontainerProps;
}

const SortableCon = SortableContainer(({ children, containerProps }) => <Container {...containerProps}> {children} </Container>) as any;

const DragableContainer: React.FC<propsType> = ({ children, dragContainerProps, containerProps }) => {
  return (
    <SortableCon {...dragContainerProps} containerProps={containerProps}>
      {children}
    </SortableCon>
  );
};

export default DragableContainer;
