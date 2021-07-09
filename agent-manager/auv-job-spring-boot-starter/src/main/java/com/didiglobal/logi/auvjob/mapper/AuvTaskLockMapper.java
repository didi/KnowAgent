package com.didiglobal.logi.auvjob.mapper;

import com.didiglobal.logi.auvjob.common.bean.AuvTaskLock;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 任务锁 Mapper 接口.
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
public interface AuvTaskLockMapper {

  @Insert("INSERT INTO auv_task_lock(task_code, worker_code, expire_time, create_time, update_time)"
          + " VALUES(#{taskCode}, #{workerCode}, #{expireTime}, #{createTime}, #{updateTime})")
  int insert(AuvTaskLock auvTaskLock);

  @Update("update auv_task_lock set expire_time=#{expireTime} where id=#{id}")
  int update(@Param("id") Long id, @Param("expireTime") Long expireTime);

  @Select("select id, task_code, worker_code, expire_time, create_time, update_time "
          + "from auv_task_lock")
  List<AuvTaskLock> selectAll();

  @Select("select id, task_code, worker_code, expire_time, create_time, update_time "
          + "from auv_task_lock where task_code=#{taskCode}")
  List<AuvTaskLock> selectByTaskCode(@Param("taskCode") String taskCode);

  @Select("select id, task_code, worker_code, expire_time, create_time, update_time "
          + "from auv_task_lock where worker_code=#{workerCode}")
  List<AuvTaskLock> selectByWorkerCode(@Param("workerCode") String workerCode);

  @Select("select id, task_code, worker_code, expire_time, create_time, update_time from "
          + "auv_task_lock where task_code=#{taskCode} and worker_code=#{workerCode}")
  List<AuvTaskLock> selectByTaskCodeAndWorkerCode(@Param("taskCode") String taskCode,
                                                  @Param("workerCode") String workerCode);

  @Delete("delete from auv_task_lock where id=#{id}")
  int deleteById(@Param("id") Long id);

  @Delete("<script>"
          + "  delete from auv_task_lock where id in "
          + "  <foreach collection='ids' open='(' item='id' separator=',' close=')'> "
          + "    #{id}"
          + "  </foreach> "
          + "</script>")
  int deleteByIds(@Param("ids") List<Long> ids);

}
