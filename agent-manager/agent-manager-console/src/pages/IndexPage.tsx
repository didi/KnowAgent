import React, { useEffect } from 'react';
import { Redirect } from 'react-router-dom';

export const IndexPage = () => {
  return <Redirect to={'/version/operation'} />;
};

export default IndexPage;
