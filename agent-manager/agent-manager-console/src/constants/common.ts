import CustomSelect from '../components/CustomSelect';

export const defaultPagination = {
  current: 1,
  pageSize: 10,
  position: 'bottomRight',
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
  showTotal: (total: number) => `共 ${total} 条目`,
  locale: {
    items_per_page: '条',
  },
  selectComponentClass: CustomSelect,
};

export const defaultPaginationConfig = {
  showQuickJumper: true,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50', '100', '200', '500'],
  showTotal: (total: number) => `共 ${total} 条`,
};

export const cellStyle = {
  overflow: 'hidden',
  whiteSpace: 'nowrap' as any,
  textOverflow: 'ellipsis',
  cursor: 'pointer',
  maxWidth: 150,
};

export const oneDayMillims = 24 * 60 * 60 * 1000;

export const classNamePrefix = 'bdp';

export const timeFormat = 'YYYY-MM-DD HH:mm:ss';

export const dateFormat = 'YYYY-MM-DD';

export const SMALL_DRAWER_WIDTH = 480;
export const MIDDLE_DRAWER_WIDTH = 728;
export const LARGE_DRAWER_WIDTH = 1080;
