import React from 'react';
import { Button } from 'antd';
import { RouteComponentProps } from 'react-router-dom';


const Page403: React.FC<RouteComponentProps> = (props) => {
  const { history } = props;
  const prefixCls = 'ecmc-exception';
  return (
    <div className={prefixCls}>
      <div className={`${prefixCls}-main`}>
        <div className={`${prefixCls}-title`}>403</div>
        <div className={`${prefixCls}-content mb10`}>抱歉，你无权访问该页面</div>
        <Button
          icon="arrow-left"
          type="primary"
          onClick={() => {
            history.push({
              pathname: '/',
            });
          }}
        >
          返回首页
        </Button>
      </div>
    </div>
  );
};

export default Page403;
