package com.didichuxing.datachannel.agentmanager.persistence.mysql;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 *
 * @author d06679
 */
@Configuration
@MapperScan(value = "com.didichuxing.datachannel.agentmanager.persistence.mysql",
        sqlSessionFactoryRef = "agentSqlSessionFactory")
public class DruidDbConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource dataSource() {
        return new DruidDataSource();
    }

    /**
     * 配置SqlSessionFactory.
     *
     * @param dataSource dataSource
     * @return SqlSessionFactory
     * @throws Exception Exception
     */
    @Bean("agentSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mybatis/*.xml"));
        return bean.getObject(); // 设置mybatis的xml所在位置
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager txManager = new DataSourceTransactionManager();
        txManager.setDataSource(dataSource);
        return txManager;
    }

}
