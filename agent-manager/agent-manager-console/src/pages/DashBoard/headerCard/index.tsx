import React, { useState, useEffect } from 'react';
import { cardList } from './config';
import { Container, IconFont, Tooltip } from '@didi/dcloud-design';
import classNames from 'classnames';

interface IProps {
  dashBoardData: Record<string, any>;
}

const headerClass = 'dashboardHeader';

const HeaderCard = (props: IProps): JSX.Element => {
  const { dashBoardData } = props;
  const [dataSource, setDataSource] = useState<Record<string, any>>({});

  useEffect(() => {
    setDataSource(dashBoardData);
  }, [dashBoardData]);

  return (
    <div className={headerClass}>
      <h3 className={`${headerClass}-title`}>采集任务</h3>
      <Container gutter={24} fluid={224} className={`${headerClass}-wrap`}>
        {cardList.map((item, index) => (
          <div className={`${headerClass}-item`} key={index}>
            <div className={`${headerClass}-item-icon`}>
              <IconFont type={item.icon} className={`${headerClass}-item-icon-item`} />
            </div>
            <div className={`${headerClass}-item-content`}>
              <div className={`${headerClass}-item-content-title`}>{item.title}</div>
              <div className={`${headerClass}-item-content-context`}>
                {item.type === 'formatSize' ? (
                  <>
                    <span className={`${headerClass}-item-content-context-num`}>
                      {dataSource[item.api] && item.format ? parseFloat(item.format(dataSource[item.api], 2)) : dataSource[item.api]}
                    </span>
                    <span className={`${headerClass}-item-content-context-span`}>{item.format(dataSource[item.api])?.match(/[A-Za-z]*$/)}</span>
                  </>
                ) : (
                  <>
                    <span className={`${headerClass}-item-content-context-num`}>
                      {dataSource[item.api] && item.format ? item.format(dataSource[item.api]) : dataSource[item.api]}
                    </span>
                    <span className={`${headerClass}-item-content-context-span`}>{item.unit ? item.unit : '个'}</span>
                  </>
                )}

              </div>
              {item.text ? (
                <div className={`${headerClass}-item-content-text`}>
                  <span className={`${headerClass}-item-content-text-label`}><span className='scale-font'>{item.text}：</span></span>
                  <span className={`${headerClass}-item-content-text-span`}>
                    {item.type === 'formatSize' ? (
                      <Tooltip
                        title={
                          ((dataSource[item.textApi] && item.format ? parseFloat(item.format(dataSource[item.textApi], 2)) : dataSource[item.textApi]) ||
                            0) + (item.format(dataSource[item.api])?.match(/[A-Za-z]*$/))
                        }
                      >
                        {dataSource[item.textApi] && item.format ? parseFloat(item.format(dataSource[item.textApi], 2)) : dataSource[item.textApi]}
                        <span>{item.format(dataSource[item.api])?.match(/[A-Za-z]*$/)}</span>
                      </Tooltip>
                    ) : (
                      <Tooltip
                        title={
                          ((dataSource[item.textApi] && item.format ? item.format(dataSource[item.textApi]) : dataSource[item.textApi]) ||
                            0) + (item.unit ? item.unit : '个')
                        }
                      >
                        {dataSource[item.textApi] && item.format ? item.format(dataSource[item.textApi]) : dataSource[item.textApi]}
                        <span>{item.unit ? item.unit : '个'}</span>
                      </Tooltip>
                    )}

                  </span>
                </div>
              ) : null}
            </div>
          </div>
        ))}
      </Container>
    </div>
  );
};

export default HeaderCard;
