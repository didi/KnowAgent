package com.didiglobal.logi.auvjob.mapper;

import com.didiglobal.logi.auvjob.common.bean.AuvTask;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 任务信息 Mapper 接口.
 * </p>
 *
 * @author dengshan
 * @since 2020-11-10
 */
public interface AuvTaskMapper {

  @Insert("INSERT INTO auv_task(code, name, description, cron, class_name, params, retry_times,"
          + " last_fire_time, timeout, status, sub_task_codes, consensual, task_worker_str) "
          + "VALUES(#{code}, #{name}, #{description}, #{cron}, #{className}, #{params}, "
          + "#{retryTimes}, #{lastFireTime}, #{timeout}, #{status}, #{subTaskCodes}, "
          + "#{consensual}, #{taskWorkerStr})")
  int insert(AuvTask auvTask);

  @Delete("delete from auv_task where code=#{code}")
  int deleteByCode(@Param("code") String code);

  @Update("update auv_task set name=#{name}, description=#{description}, cron=#{cron}, class_name="
          + "#{className}, params=#{params}, retry_times=#{retryTimes}, last_fire_time="
          + "#{lastFireTime}, timeout=#{timeout}, status=#{status}, sub_task_codes=#{subTaskCodes},"
          + " consensual=#{consensual}, task_worker_str=#{taskWorkerStr} where code=#{code}")
  int updateByCode(AuvTask auvTask);

  @Select("select id, code, name, description, cron, class_name, params, retry_times, "
          + "last_fire_time, timeout, status, sub_task_codes, consensual, create_time, update_time "
          + ", task_worker_str, create_time, update_time from auv_task where code=#{code}")
  AuvTask selectByCode(@Param("code") String code);

  @Select("<script>"
          + "select id, code, name, description, cron, class_name, params, retry_times, "
          + "last_fire_time, timeout, status, sub_task_codes, consensual, task_worker_str, "
          + "create_time, update_time from auv_task where codes in "
          + "<foreach collection='codes' item='code' index='index' open='(' close=')' "
          + "separator=','>"
          + "  #{code} "
          + "</foreach> "
          + "</script>")
  List<AuvTask> selectByCodes(@Param("codes") List<String> codes);

  @Select("select id, code, name, description, cron, class_name, params, retry_times, "
          + "last_fire_time, timeout, status, sub_task_codes, consensual, task_worker_str,"
          + " create_time, update_time from auv_task")
  List<AuvTask> selectAll();

}
