import React, { useEffect, useState } from 'react';
import './style/index.less';
import _ from 'lodash';
import { IconFont } from '@didi/dcloud-design';

interface IProps {
  visibleHeight?: number;
}

const BackToTop = (props: IProps): JSX.Element => {
  const { visibleHeight = 500 } = props;
  const [visibleBackTopBtn, setVisibleBackTopBtn] = useState(false);

  useEffect(() => {
    document.addEventListener('scroll', handleScroll, true);
    return () => document.removeEventListener('scroll', handleScroll);
  }, [visibleBackTopBtn]);

  const handleScroll = _.throttle((e) => {
    const scrollTop = document.getElementsByClassName('main-content')[0].scrollTop;
    if (scrollTop > visibleHeight) {
      setVisibleBackTopBtn(true);
    } else {
      setVisibleBackTopBtn(false);
    }
  }, 1000);

  const backToTopHandle = () => {
    document.getElementsByClassName('main-content')[0].scrollTo({
      left: 0,
      top: 0,
      behavior: 'smooth',
    });
  };
  return (
    <>
      {visibleBackTopBtn && (
        <div id="backToTop" onClick={backToTopHandle}>
          <IconFont type="icon-huidaodingbumoren" className="backToTop-icon"></IconFont>
        </div>
      )}
    </>
  );
};

export default BackToTop;
