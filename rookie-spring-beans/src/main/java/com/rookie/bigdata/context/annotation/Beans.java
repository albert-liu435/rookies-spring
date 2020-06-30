package com.rookie.bigdata.context.annotation;

import com.rookie.bigdata.beanfactory.domain.Car;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName Beans
 * @Description Beans
 * @Author
 * @Date 2020/6/30 17:24
 * @Version 1.0
 */
@Configuration
public class Beans {

    @Bean(name = "car")
    public Car buildCar() {
        Car car = new Car();
        car.setBrand("红旗CA72");
        car.setMaxSpeed(200);
        return car;
    }
}
