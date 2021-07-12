export interface MenuChild {
  name: string;
  nameEn: string;
  path: string;
  icon: string;
}
export interface Menu {
  name: string;
  nameEn: string;
  type: 'group',
  children: MenuChild[],
}

export const prefixCls = 'ecmc';
