package com.didichuxing.datachannel.agentmanager.thirdpart.agent.collect.configuration.extension.impl;

import com.didichuxing.datachannel.agentmanager.common.bean.domain.host.HostDO;
import com.didichuxing.datachannel.agentmanager.common.exception.ServiceException;

/**
 * @author huqidong
 * @date 2020-09-21
 * 主机过滤规则表达式
 */
public class HostFilterRule {

    /**
     * @param host 主机对象
     * @param hostFilterRuleLogicJsonString 主机过滤规则逻辑 json 化字符串（类 BaseHostFilterRuleLogic 某具体实现类对应 json 化字符串形式）
     * @return 返回给定主机对象是否命中给定主机过滤规则逻辑。主机过滤规则逻辑（BaseHostFilterRuleLogic）被日志采集任务（LogCollectTaskPO）所持有
     *         true：表示持有该主机过滤规则逻辑的日志采集任务可被部署到给定主机对应的Agent上进行采集
     *         false：表示持有该主机过滤规则逻辑的日志采集任务不可被部署到给定主机对应的Agent上进行采集
     * @throws ServiceException 执行"返回给定主机对象是否命中给定主机过滤规则逻辑"过程中出现的异常
     */
//    public static boolean getResult(HostPO host, String hostFilterRuleLogicJsonString) throws ServiceException {
//
//        /*
//         * 根据主机过滤规则逻辑 json 化字符串反序列为对应 BaseHostFilterRuleLogic 实现类
//         * 注：之所以须反序列化两次，原因为：第一次反序列化仅可反序列化类型为 BaseHostFilterRuleLogic，通过其属性 hostFilterRuleType
//         *     进一步判断其实现类型，然后根据其实现类型进行二次反序列化
//         */
//
//        //第一次反序列化 BaseHostFilterRuleLogic
//        if(StringUtils.isBlank(hostFilterRuleLogicJsonString)) {
//            ServiceException ex = new ServiceException("参数 [hostFilterRuleLogicJsonString] 值为空", ResultType.ILLEGAL_PARAMS.getCode());
//            ex.addAttribute("hostFilterRuleLogicJsonString", hostFilterRuleLogicJsonString);
//            throw ex;
//        }
//        BaseHostFilterRuleLogic baseHostFilterRuleLogic = null;
//        try {
//            baseHostFilterRuleLogic = JSON.parseObject(hostFilterRuleLogicJsonString, BaseHostFilterRuleLogic.class);
//        } catch (Exception ex) {
//            ServiceException e = new ServiceException(String.format("参数 [hostFilterRuleLogicJsonString] 值格式错误导致反序列化为类型 BaseHostFilterRuleLogic 失败，其值必须为 BaseHostFilterRuleLogic 某子类对象的 JSON 序列化形式"), ex, ResultType.SYSTEM_INTERNAL_ERROR.getCode());
//            e.addAttribute("hostFilterRuleLogicJsonString", hostFilterRuleLogicJsonString);
//            e.addAttribute("host", host);
//            throw e;
//        }
//        if(null == baseHostFilterRuleLogic) {
//            ServiceException e = new ServiceException(String.format("参数 [hostFilterRuleLogicJsonString] 值格式错误导致其反序列化结果为 null，其值必须为 BaseHostFilterRuleLogic 某子类的 JSON 序列化形式"), ResultType.SYSTEM_INTERNAL_ERROR.getCode());
//            e.addAttribute("hostFilterRuleLogicJsonString", hostFilterRuleLogicJsonString);
//            e.addAttribute("host", host);
//            throw e;
//        }
//
//        //通过 BaseHostFilterRuleLogic 实际类型进行二次反序列化，并根据实际类型进行具体主机过滤规则逻辑执行流程
//        if(HostFilterRuleLogicEnum.ExpressionHostFilterRuleLogic.getCode() == baseHostFilterRuleLogic.getHostFilterRuleType()) {
//            ExpressionHostFilterRuleLogic expressionHostFilterRuleLogic = null;
//            try {
//                expressionHostFilterRuleLogic = JSON.parseObject(hostFilterRuleLogicJsonString, ExpressionHostFilterRuleLogic.class);
//            } catch (Exception ex) {
//                ServiceException e = new ServiceException(String.format("参数 [hostFilterRuleLogicJsonString] 值格式错误导致反序列化为类型 ExpressionHostFilterRuleLogic 失败，其值必须为 ExpressionHostFilterRuleLogic 类型对象的 JSON 序列化形式"), ex, ResultType.SYSTEM_INTERNAL_ERROR.getCode());
//                e.addAttribute("hostFilterRuleLogicJsonString", hostFilterRuleLogicJsonString);
//                e.addAttribute("host", host);
//                throw e;
//            }
//            String expression = expressionHostFilterRuleLogic.getExpression();
//            //TODO：根据 expression & host 得到执行结果
//
//        } else {
//            ServiceException ex = new ServiceException(String.format("参数 [hostFilterRuleLogicJsonString] 值非法，其反序列化得到的类型 BaseHostFilterRuleLogic 对象对应的属性 hostFilterRuleType 值 [%d] 非法，该属性值枚举范围见枚举类 HostFilterRuleLogicEnum", baseHostFilterRuleLogic.getHostFilterRuleType()), ResultType.SYSTEM_INTERNAL_ERROR.getCode());
//            ex.addAttribute("baseHostFilterRuleLogic", baseHostFilterRuleLogic);
//            ex.addAttribute("hostFilterRuleLogicJsonString", hostFilterRuleLogicJsonString);
//            ex.addAttribute("host", host);
//            throw ex;
//        }
//
//        return false;
//
//    }

    //TODO: used to test

    public static boolean getResult(HostDO host, String hostFilterRuleLogicJsonString) throws ServiceException {

        return true;

    }

}
