import * as React from 'react';
import { Breadcrumb, Popconfirm, Dropdown, Tooltip, Descriptions, PageHeader, Spin, Tag } from '@didi/dcloud-design';
import { withRouter, Link } from 'react-router-dom';
import { IBaseInfo } from '../interface/common';
import { cutString } from './../lib/utils';

const { Item } = Descriptions;

export interface IBtn {
  clickFunc?: any;
  isRouterNav?: boolean;
  label: string | JSX.Element;
  className?: string;
  needConfirm?: boolean;
  aHref?: string;
  confirmText?: string;
  noRefresh?: boolean;
  loading?: boolean;
  invisible?: boolean; // 不可见
  needTooltip?: boolean; // 是否需要提示框
  tooltipText?: string; // 提示框文本
  state?: any;
}

interface IBreadProps {
  btns: IBtn[];
}

export const CustomBreadcrumb = withRouter<any, any>((props: IBreadProps) => {
  const { btns } = props;
  return (
    <>
      <Breadcrumb className="bread-crumb">
        {btns.map((v, index) => (
          <Breadcrumb.Item key={index}>{v.aHref ? <Link to={v.aHref}>{v.label}</Link> : v.label}</Breadcrumb.Item>
        ))}
      </Breadcrumb>
    </>
  );
});

interface ICommonHeadProps {
  heads: any;
}

export const CommonHead = withRouter<any, any>((props: ICommonHeadProps) => {
  const { heads } = props;
  return (
    <>
      <div className="lh-24">
        <h2>{heads.value}</h2>
        <p>{heads.label}</p>
      </div>
    </>
  );
});

interface IMoreBtnsProps {
  btns: IBtn[];
  data: object;
}

export const MoreBtns = (props: IMoreBtnsProps) => {
  const { btns, data } = props;
  const btnsMenu = (
    <ul className="dropdown-menu">
      {btns.map((v, index) => {
        if (v.invisible) return null;

        if (v.isRouterNav) {
          return (
            <li key={index} className="epri-theme">
              {v.label}
            </li>
          );
        }

        if (v.needTooltip) {
          return (
            <Tooltip placement="topLeft" title={v.tooltipText ? v.tooltipText : '更多功能请关注商业版'}>
              <span key={index}>
                <a style={{ color: '#00000040' }}>{v.label}</a>
              </span>
            </Tooltip>
          );
        }

        if (v.clickFunc) {
          return (
            <li key={index} onClick={() => v.clickFunc(data)} className="epri-theme">
              <a>{v.label}</a>
            </li>
          );
        }
        return (
          <li key={index} className="epri-theme">
            <a>{v.label}</a>
          </li>
        );
      })}
    </ul>
  );
  return (
    <Dropdown key="2" overlay={btnsMenu} trigger={['click', 'hover']} placement="bottomLeft">
      <span className="epri-theme ml-10">
        <a>更多</a>
      </span>
    </Dropdown>
  );
};

export const renderOperationBtns = (buttons: IBtn[], record: any) => {
  const btns = buttons?.filter((ele) => !ele?.invisible);
  const freeBtns: IBtn[] = btns.length <= 4 ? btns : Array.from(btns).splice(0, 3);
  const leftBtns = Array.from(btns).splice(3);
  return (
    <>
      <span className="table-operation">
        {freeBtns.map((item, index) => {
          if (item.invisible) return null;

          if (item.isRouterNav) {
            return <span key={index}>{item.label}</span>;
          }

          if (item.needTooltip) {
            return (
              <Tooltip placement="topLeft" title={item.tooltipText ? item.tooltipText : '更多功能请关注商业版'}>
                <span key={index}>
                  <a style={{ color: '#00000040' }}>{item.label}</a>
                </span>
              </Tooltip>
            );
          }

          if (item.needConfirm) {
            return (
              <Popconfirm
                key={index}
                title={`确认${item.confirmText}?`}
                onConfirm={() => item.clickFunc(record)}
                okText={item.confirmText}
                cancelText="取消"
              >
                <a type="javascript;">{item.label}</a>
              </Popconfirm>
            );
          }

          if (item.clickFunc) {
            return (
              <a type="javascript;" key={index} onClick={() => item.clickFunc(record)}>
                {item.label}
              </a>
            );
          }

          if (item.aHref) {
            return <NavRouterLink key={item.aHref} element={item.label} href={item.aHref} state={item.state} />;
          }

          return (
            <span key={index}>
              <a>{item.label}</a>
            </span>
          );
        })}
        {btns.length > 4 ? <MoreBtns btns={leftBtns} data={record} /> : null}
      </span>
    </>
  );
};

interface INavRouterLinkProps {
  element: JSX.Element | string;
  href: string;
  needToolTip?: boolean;
  state?: any;
  key?: any;
  textLength?: number;
}

export const NavRouterLink = withRouter<any, any>((props: INavRouterLinkProps) => {
  return (
    <Link
      to={{
        pathname: props.href,
        state: props.state,
      }}
    >
      {props.needToolTip ? (
        <Tooltip key={props.key} placement="bottomLeft" title={props.element}>
          {typeof props.element === 'string'
            ? props?.element?.length > 26
              ? props?.element.substring(0, 26) + '...'
              : props?.element
            : props.element}
        </Tooltip>
      ) : (
        props.element
      )}
    </Link>
  );
});

interface IDescriptionsProps {
  title: string;
  loading?: boolean;
  subTitle?: any;
  column: number;
  baseInfo: IBaseInfo[];
  baseData: any;
  haveClass?: boolean;
}

export const DescriptionsItems = withRouter<any, any>((props: IDescriptionsProps) => {
  const { loading, title, subTitle, column, baseInfo, baseData, haveClass } = props;
  const base = baseInfo?.filter((ele) => !ele?.invisible);
  return (
    <div className={haveClass === undefined ? 'detail-header' : ''}>
      <Spin spinning={loading === undefined ? false : loading}>
        <PageHeader ghost={false} title={renderTooltip(title, 25)} subTitle={subTitle}>
          <Descriptions size="middle" column={column}>
            {base.map((item: IBaseInfo, index: number) => {
              const value = baseData?.[item.key];
              return (
                <Item key={'info' + index} label={item.label}>
                  {item.render ? (
                    item.render(value)
                  ) : (
                    <Tooltip title={value} placement="bottomLeft">
                      {value?.length > 16 ? value.substring(0, 16) + '...' : value}
                    </Tooltip>
                  )}
                </Item>
              );
            })}
          </Descriptions>
        </PageHeader>
      </Spin>
    </div>
  );
});

export const renderTooltip = (text: string, num?: number) => {
  const figure = num ? num : 16;
  return (
    <>
      {text ? (
        <Tooltip title={text} placement="bottomLeft">
          {text?.length > figure ? text?.substring(0, figure) + '...' : text}
        </Tooltip>
      ) : (
        <Tag />
      )}
    </>
  );
};

export const TextRouterLink = withRouter<any, any>((props: INavRouterLinkProps) => {
  return (
    <Link
      to={{
        pathname: props.href,
        state: props.state,
      }}
      style={{ color: 'rgba(0,0,0,0.65)' }}
    >
      {props.needToolTip ? (
        <Tooltip key={props.key} placement="bottomLeft" title={props.element}>
          {typeof props.element === 'string' ? cutString(props.element, props.textLength || 15) : props.element}
        </Tooltip>
      ) : (
        props.element
      )}
    </Link>
  );
});
