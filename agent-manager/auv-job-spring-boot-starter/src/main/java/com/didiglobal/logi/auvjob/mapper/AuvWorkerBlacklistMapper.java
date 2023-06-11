package com.didiglobal.logi.auvjob.mapper;

import com.didiglobal.logi.auvjob.common.bean.AuvWorkerBlacklist;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * worker黑名单列表信息 Mapper 接口.
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
public interface AuvWorkerBlacklistMapper {

  @Insert("INSERT INTO auv_worker_blacklist(worker_code) VALUES(#{workerCode})")
  int insert(AuvWorkerBlacklist auvWorkerBlacklist);

  @Delete("delete from auv_worker_blacklist where worker_code=#{workerCode}")
  int deleteByWorkerCode(@Param("workerCode") String workerCode);

  @Select("select id, worker_code from auv_worker_blacklist")
  List<AuvWorkerBlacklist> selectAll();

}
