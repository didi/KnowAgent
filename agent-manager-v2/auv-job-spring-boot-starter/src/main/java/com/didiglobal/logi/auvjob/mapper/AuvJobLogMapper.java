package com.didiglobal.logi.auvjob.mapper;

import com.didiglobal.logi.auvjob.common.bean.AuvJobLog;
import java.sql.Timestamp;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * job执行历史日志 Mapper 接口.
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
public interface AuvJobLogMapper {

  @Insert("INSERT INTO auv_job_log(job_code, task_code, class_name, try_times, worker_code, "
          + "start_time, end_time, status, error, result, create_time, update_time) "
          + "VALUES(#{jobCode}, #{taskCode}, #{className}, #{tryTimes}, #{workerCode}, #{startTime}"
          + ", #{endTime}, #{status}, #{error}, #{result}, #{createTime}, #{updateTime})")
  int insert(AuvJobLog auvJobLog);

  @Select("select id, job_code, task_code, class_name, try_times, worker_code, start_time, "
          + "end_time, status, error, result, create_time, update_time from auv_job_log where "
          + "task_code=#{taskCode} limit #{limit}")
  List<AuvJobLog> selectByTaskCode(@Param("taskCode") String taskCode,
                                   @Param("limit") Integer limit);

  @Delete("delete from auv_job_log where create_time<=#{createTime}")
  int deleteByCreateTime(@Param("createTime") Timestamp createTime);

}
