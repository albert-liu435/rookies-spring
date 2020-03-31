package com.rookie.bigdata.aware;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.support.StandardServletEnvironment;

/**
 * @author rookie
 * @version 1.0
 * @date 2020/4/1 7:30
 * EnvironmentCapable的含义是让其继承类具有Environment的能力，也就是说可以提供Environment
 */
public class SpringEnvironment implements EnvironmentAware, EnvironmentCapable {

    public SpringEnvironment(){

    }


    @Nullable
    private ConfigurableEnvironment environment;

    @Override
    public void setEnvironment(Environment environment) {
        Assert.isInstanceOf(ConfigurableEnvironment.class, environment, "ConfigurableEnvironment required");
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = createEnvironment();
        }
        return this.environment;
    }

    protected ConfigurableEnvironment createEnvironment() {
        return new StandardServletEnvironment();
    }
}
