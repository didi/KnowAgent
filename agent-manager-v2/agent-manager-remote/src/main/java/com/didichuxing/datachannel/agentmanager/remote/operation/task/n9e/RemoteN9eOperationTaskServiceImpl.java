package com.didichuxing.datachannel.agentmanager.remote.operation.task.n9e;

import com.didichuxing.datachannel.agentmanager.common.bean.common.Result;
import com.didichuxing.datachannel.agentmanager.common.util.ConvertUtil;
import com.didichuxing.datachannel.agentmanager.common.util.HttpUtils;
import com.didichuxing.datachannel.agentmanager.common.util.ValidateUtils;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.RemoteOperationTaskService;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.*;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskActionEnum;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskStateEnum;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskSubStateEnum;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.common.enumeration.AgentOperationTaskTypeEnum;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.n9e.entry.*;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.n9e.entry.enumeration.N9eTaskActionEnum;
import com.didichuxing.datachannel.agentmanager.remote.operation.task.n9e.entry.enumeration.N9eTaskStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RemoteN9eOperationTaskServiceImpl implements RemoteOperationTaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteN9eOperationTaskServiceImpl.class);

    @Value("${lam.n9e-job.base-url}")
    private String      n9eJobBaseUrl;

    @Value("${lam.n9e-job.account}")
    private String      n9eJobAccount;

    @Value("${lam.n9e-job.timeout}")
    private Integer     n9eJobTimeout;

    @Value("${lam.n9e-job.script-file}")
    private String      n9eJobScriptFile;

    @Value("${lam.n9e-job.user-token}")
    private String      n9eUserToken;

    private String      jobScript;

    /**
     * 并发度，顺序执行
     */
    private static final Integer BATCH = 1;

    /**
     * 失败的容忍度为0
     */
    private static final Integer TOLERANCE = 0;

    private static final String CREATE_TASK_URI = "/api/job-ce/tasks";

    private static final String ACTION_TASK_URI = "/api/job-ce/task/{taskId}/action";

    private static final String ACTION_HOST_TASK_URI = "/api/job-ce/task/{taskId}/host";

    private static final String TASK_STATE_URI = "/api/job-ce/task/{taskId}/state";

    private static final String TASK_SUB_STATE_URI = "/api/job-ce/task/{taskId}/result";

    private static final String TASK_STD_LOG_URI = "/api/job-ce/task/{taskId}/stdout.json";

    @PostConstruct
    public void init() {
        this.jobScript = readScriptInJarFile(n9eJobScriptFile);
    }

    @Override
    public Result<Long> createTask(AgentOperationTaskCreation agentOperationTaskCreation) {
        Result<N9eCreationTask> n9eCreationTaskResult = checkAndConvert2N9eCreationTask(agentOperationTaskCreation);
        if (n9eCreationTaskResult.failed()) {
            return Result.buildFail(n9eCreationTaskResult.getMessage());
        }

        String requestBody = ConvertUtil.obj2String(n9eCreationTaskResult.getData());

        String response = null;
        try {
            response = HttpUtils.postForString(
                    n9eJobBaseUrl + CREATE_TASK_URI,
                    requestBody,
                    buildHeader()
            );
            N9eResult n9eResult = ConvertUtil.string2Obj(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.warn("class=RemoteN9eOperationTaskServiceImpl||method=createTask||request={}||response={}||msg=create task failed",
                        requestBody, response);
                return Result.buildFail(n9eResult.getErr());
            }
            return Result.buildSucc(Long.valueOf(n9eResult.getDat().toString()));
        } catch (Exception e) {
            LOGGER.warn("class=RemoteN9eOperationTaskServiceImpl||method=createTask||request={}||response={}||errMsg={}||msg=create task failed",
                    requestBody, response, e.getMessage());
        }
        return Result.buildFail("create task failed");
    }

    @Override
    public Boolean actionTask(Long taskId, AgentOperationTaskActionEnum actionEnum) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("action", N9eTaskActionEnum.getByAgentOperationTaskActionEnum(actionEnum).getAction());

        String response = null;
        try {
            response = HttpUtils.putForString(
                    n9eJobBaseUrl + ACTION_TASK_URI.replace("{taskId}", taskId.toString()),
                    ConvertUtil.obj2String(param),
                    buildHeader()
            );
            N9eResult n9eResult = ConvertUtil.string2Obj(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.error("class=RemoteN9eOperationTaskServiceImpl||method=actionTask||taskId={}||action={}||response={}||msg=action task failed",
                        taskId, actionEnum.getAction(), response);
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.warn("class=RemoteN9eOperationTaskServiceImpl||method=actionTask||taskId={}||action={}||response={}||errMsg={}||msg=action task failed",
                    taskId, actionEnum.getAction(), response, e.getMessage());
        }
        return false;
    }

    @Override
    public Boolean actionHostTask(Long taskId, AgentOperationTaskActionEnum actionEnum, String hostname) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("action", N9eTaskActionEnum.getByAgentOperationTaskActionEnum(actionEnum).getAction());
        param.put("hostname", hostname);

        String response = null;
        try {
            response = HttpUtils.putForString(
                    n9eJobBaseUrl + ACTION_HOST_TASK_URI.replace("{taskId}", taskId.toString()),
                    ConvertUtil.obj2String(param),
                    buildHeader()
            );
            N9eResult n9eResult = ConvertUtil.string2Obj(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.error("class=RemoteN9eOperationTaskServiceImpl||method=actionHostTask||taskId={}||action={}||hostname={}||response={}||msg=action failed",
                        taskId, actionEnum.getAction(), hostname, response);
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("class=RemoteN9eOperationTaskServiceImpl||method=actionHostTask||taskId={}||action={}||hostname={}||response={}||errMsg={}||msg=action failed",
                    taskId, actionEnum.getAction(), hostname, response, e.getMessage());
        }
        return false;
    }

    @Override
    public Result<AgentOperationTaskStateEnum> getTaskExecuteState(Long taskId) {
        String response = null;
        try {
            // 获取任务的state
            response = HttpUtils.get(
                    n9eJobBaseUrl + TASK_STATE_URI.replace("{taskId}", taskId.toString()),
                    null,
                    buildHeader()
            );
            N9eResult n9eResult = ConvertUtil.string2Obj(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.error("class=RemoteN9eOperationTaskServiceImpl||method=getTaskExecuteState||taskId={}||response={}||msg=get task state failed",
                        taskId, response);
                return Result.buildFail(n9eResult.getErr());
            }

            String state = ConvertUtil.string2Obj(ConvertUtil.obj2String(n9eResult.getDat()), String.class);
            N9eTaskStateEnum n9eTaskStatusEnum = N9eTaskStateEnum.getByMessage(state);
            if (ValidateUtils.isNull(n9eTaskStatusEnum)) {
                LOGGER.error("class=RemoteN9eOperationTaskServiceImpl||method=getTaskExecuteState||taskId={}||response={}||msg=get task state failed",
                        taskId, response);
                return null;
            }
            return Result.buildSucc(AgentOperationTaskStateEnum.getByTaskStatusEnum(n9eTaskStatusEnum.getStatus()));
        } catch (Exception e) {
            LOGGER.error("class=RemoteN9eOperationTaskServiceImpl||method=getTaskExecuteState||taskId={}||response={}||errMsg={}||msg=get task state failed",
                    taskId, response, e.getMessage());
        }
        return Result.buildFail("get task execute state failed");
    }

    @Override
    public Result<Map<String, AgentOperationTaskSubStateEnum>> getTaskResult(Long taskId) {
        String response = null;
        try {
            // 获取子任务的state
            response = HttpUtils.get(
                    n9eJobBaseUrl + TASK_SUB_STATE_URI.replace("{taskId}", taskId.toString()),
                    null,
                    buildHeader()
            );
            N9eResult n9eResult = ConvertUtil.string2Obj(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.error("class=RemoteN9eOperationTaskServiceImpl||method=getTaskResult||taskId={}||response={}||msg=get task result failed", taskId, response);
                return Result.buildFail(n9eResult.getErr());
            }

            N9eTaskResult n9eTaskResult = ConvertUtil.string2Obj(ConvertUtil.obj2String(n9eResult.getDat()), N9eTaskResult.class);
            return Result.buildSucc(n9eTaskResult.convert2HostnameStatusMap());
        } catch (Exception e) {
            LOGGER.error("class=RemoteN9eOperationTaskServiceImpl||method=getTaskResult||taskId={}||response={}||errMsg={}||msg=get task result failed",
                    taskId, response, e.getMessage());
        }
        return Result.buildFail("get task result failed");
    }

    @Override
    public Result<AgentOperationTaskLog> getTaskLog(Long taskId, String hostname) {
        String response = null;
        try {
            Map<String, String> params = new HashMap<>(1);
            params.put("hostname", hostname);

            response = HttpUtils.get(
                    n9eJobBaseUrl + TASK_STD_LOG_URI.replace("{taskId}", taskId.toString()),
                    params,
                    buildHeader()
            );
            N9eResult n9eResult = ConvertUtil.string2Obj(response, N9eResult.class);
            if (!ValidateUtils.isBlank(n9eResult.getErr())) {
                LOGGER.error("class=RemoteN9eOperationTaskServiceImpl||method=getTaskLog||taskId={}||hostname={}||response={}||msg=get task log failed",
                        taskId, hostname, response);
                return Result.buildFail(n9eResult.getErr());
            }

            List<N9eTaskStdout> dtoList =
                    ConvertUtil.string2ArrObj(ConvertUtil.obj2String(n9eResult.getDat()), N9eTaskStdout.class);
            if (ValidateUtils.isEmptyList(dtoList)) {
                return Result.buildSucc();
            }
            AgentOperationTaskLog agentOperationTaskLog = new AgentOperationTaskLog();
            agentOperationTaskLog.setStdout(dtoList.get(0).getStdout());
            return Result.buildSucc(agentOperationTaskLog);
        } catch (Exception e) {
            LOGGER.error("class=RemoteN9eOperationTaskServiceImpl||method=getTaskLog||taskId={}||hostname={}||response={}||errMsg={}||msg=get task log failed",
                    taskId, hostname, response, e.getMessage());
        }
        return Result.buildFail("get task log failed");
    }

    private Map<String, String> buildHeader() {
        Map<String,String> headers = new HashMap<>(2);
        headers.put("Content-Type", "application/json;charset=UTF-8");
        headers.put("X-User-Token", n9eUserToken);
        return headers;
    }

    private static String readScriptInJarFile(String fileName) {
        InputStream inputStream = RemoteN9eOperationTaskServiceImpl.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            LOGGER.error("class=RemoteN9eOperationTaskServiceImpl||method=readScriptInJarFile||fileName={}||msg=read script failed", fileName);
            return "";
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder("");

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            LOGGER.error("class=RemoteN9eOperationTaskServiceImpl||method=readScriptInJarFile||fileName={}||errMsg={}||msg=read script failed",
                    fileName, e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error("class=RemoteN9eOperationTaskServiceImpl||method=readScriptInJarFile||fileName={}||errMsg={}||msg=close reading script failed",
                        fileName, e.getMessage());
            }
        }
        return "";
    }

    private Result<N9eCreationTask> checkAndConvert2N9eCreationTask(AgentOperationTaskCreation agentOperationTaskCreation) {
        if (ValidateUtils.isNull(agentOperationTaskCreation)) {
            return Result.buildFail("param is null");
        }

        N9eCreationTask n9eCreationTask = new N9eCreationTask();
        n9eCreationTask.setTitle(agentOperationTaskCreation.getTaskName());
        n9eCreationTask.setBatch(RemoteN9eOperationTaskServiceImpl.BATCH);
        n9eCreationTask.setTimeout(this.n9eJobTimeout);

        if (ValidateUtils.isEmptyList(agentOperationTaskCreation.getHostList())) {
            return Result.buildFail("hostList is empty");
        }
        n9eCreationTask.setTolerance(agentOperationTaskCreation.getHostList().size());
        n9eCreationTask.setPause("");
        n9eCreationTask.setScript(this.jobScript);

        StringBuilder args = new StringBuilder();
        if (!ValidateUtils.isNull(agentOperationTaskCreation.getTaskType())) {
            args.append(agentOperationTaskCreation.getTaskType()).append(",,");
        } else {
            return Result.buildFail("taskType is null");
        }
        if(!agentOperationTaskCreation.getTaskType().equals(AgentOperationTaskTypeEnum.UNINSTALL.getCode())) {
            if (!ValidateUtils.isBlank(agentOperationTaskCreation.getAgentPackageName())) {
                args.append(agentOperationTaskCreation.getAgentPackageName()).append(",,");
            } else {
                return Result.buildFail("agent package name is blank");
            }
            if (!ValidateUtils.isBlank(agentOperationTaskCreation.getAgentPackageMd5())) {
                args.append(agentOperationTaskCreation.getAgentPackageMd5()).append(",,");
            } else {
                return Result.buildFail("agent package md5 is blank");
            }
            if (!ValidateUtils.isBlank(agentOperationTaskCreation.getAgentPackageDownloadUrl())) {
                args.append(agentOperationTaskCreation.getAgentPackageDownloadUrl());
            } else {
                return Result.buildFail("agent package download url is blank");
            }
        }

        n9eCreationTask.setArgs(args.toString());

        n9eCreationTask.setAccount(this.n9eJobAccount);
        n9eCreationTask.setAction(N9eTaskActionEnum.PAUSE.getAction());
        n9eCreationTask.setHosts(agentOperationTaskCreation.getHostList());
        return Result.buildSucc(n9eCreationTask);
    }
}
