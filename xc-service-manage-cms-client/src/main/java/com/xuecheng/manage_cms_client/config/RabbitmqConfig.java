package com.xuecheng.manage_cms_client.config;

import com.xuecheng.framework.exception.ExceptionCast;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
    //队列名称
    public static final String QUEUE_CMS_POSTPAGE = "queue_cms_postpage";
    //交换机名称
    public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";
    //队列名称
    @Value("${xuecheng.mq.queue}")
    public String queue_cms_postpage_name;
    //routingKEY   即站点id
    @Value("${xuecheng.mq.routingKey}")
    public String routingKey;


    //交换机使用
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EXCHANGE_TOPICS_INFORM(){
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

    //声明队列
    @Bean(QUEUE_CMS_POSTPAGE)
    public Queue QUEUE_CMS_POSTPAGE(){
        return new Queue(queue_cms_postpage_name);
    }

    //绑定到交换机
    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS(@Qualifier(QUEUE_CMS_POSTPAGE)Queue queue,
                                            @Qualifier(EX_ROUTING_CMS_POSTPAGE)Exchange exchange){
         return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
    }

}
