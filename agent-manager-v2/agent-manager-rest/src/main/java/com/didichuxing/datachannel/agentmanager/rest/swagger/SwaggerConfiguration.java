package com.didichuxing.datachannel.agentmanager.rest.swagger;

import com.didichuxing.datachannel.agentmanager.common.util.EnvUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by limeng on 2020-04-16
 */
@Configuration
@EnableSwagger2
@Profile({"dev", "test"})
public class SwaggerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerConfiguration.class);private static List<String> ENVS = Lists.newArrayList();

    @Bean
    public Docket createRestApi() {
        String envStr = String.join(",", Lists.newArrayList(ENVS));
        if (!envStr.contains(EnvUtil.EnvType.TEST.getStr()) && !envStr.contains(EnvUtil.EnvType.DEV.getStr())) {
            return new Docket(DocumentationType.SWAGGER_2).enable(true);
        }

        LOGGER.info("swagger started||env={}", envStr);

        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder user = new ParameterBuilder();
        user.name("X-SSO-USER").description("操作人").modelRef(new ModelRef("string")).parameterType("header")
                .required(true).defaultValue("").build();

        ParameterBuilder appid = new ParameterBuilder();
        appid.name("X-agentmanager-APP-ID").description("APPID").modelRef(new ModelRef("string")).parameterType("header")
                .required(false).defaultValue("").build();

        pars.add(appid.build());
        pars.add(user.build());

        List<ResponseMessage> responseMessageList = new ArrayList<>();
        responseMessageList.add(new ResponseMessageBuilder().code(200).message("OK；服务正常返回或者异常都返回200，通过返回结构中的code来区分异常；code为0表示操作成功，其他为异常").build());
        responseMessageList.add(new ResponseMessageBuilder().code(404).message("资源找不到；请确认URL是否正确").build());

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.didichuxing.datachannel.agentmanager.rest.controller"))
                .paths(PathSelectors.any()).build().globalOperationParameters(pars)
                .globalResponseMessage(RequestMethod.GET, responseMessageList)
                .globalResponseMessage(RequestMethod.POST, responseMessageList)
                .globalResponseMessage(RequestMethod.PUT, responseMessageList)
                .globalResponseMessage(RequestMethod.DELETE, responseMessageList);
    }

    /**
     * 创建该Api的基本信息（这些基本信息会展现在文档页面中）
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("agentmanager接口文档").description(String.format("当前环境:%s", ENVS))
                .version("1.0_" + String.join(",", ENVS)).build();
    }

    public static void initEnv(String[] args) {
        if (args == null) {
            return;
        }

        try {
            for (String arg : args) {
                String[] argArr = arg.split("=");
                if (argArr[0].toLowerCase().contains("spring.profiles.active")) {
                    ENVS.add(argArr[1]);
                }
            }
            LOGGER.info("swagger||env={}", ENVS);
        } catch (Exception e) {
            LOGGER.warn("initEnv error||args={}", args, e);
        }
    }

}
