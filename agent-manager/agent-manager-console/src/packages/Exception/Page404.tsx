import React from 'react';
import { Button } from 'antd';
import { RouteComponentProps } from 'react-router-dom';


const Page404: React.FC<RouteComponentProps> = (props) => {
  const { history } = props;
  const prefixCls = 'ecmc-exception';
  return (
    <div className={prefixCls}>
      <div className={`${prefixCls}-main`}>
        <div className={`${prefixCls}-title`}>404</div>
        <div className={`${prefixCls}-content mb10`}>抱歉，你访问的页面不存在</div>
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

export default Page404;
