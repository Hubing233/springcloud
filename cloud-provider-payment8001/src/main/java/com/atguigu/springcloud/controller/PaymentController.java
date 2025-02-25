package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import com.atguigu.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class PaymentController {
    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping(value = "/payment/create")
    public CommonResult create(@RequestBody Payment payment){
    //@RequestBody  前端传给后端的json数据，请求体中的数据 要不然无法接收到。
          int result = paymentService.create(payment);
            log.info("*****插入结果："+result);
            if(result>0){
                return new CommonResult(200,"插入数据库成功,serverport:"+serverPort,result);
            }else {
                return new CommonResult(444,"插入数据库错误",null);
            }
    }
    @GetMapping("/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id){
        //@PathVariable（"id"）  可以在get后面附带参数
        Payment payment = paymentService.getPaymentById(id);
        log.info("*****插入结果："+payment);
        if(payment!=null){
            return new CommonResult(200,"查询成功,serverport:"+serverPort,payment);
        }else {
            return new CommonResult(444,"没有对应记录,查询id:"+id,null);
        }
    }
    @GetMapping(value = "/payment/discovery")
    public Object discovery(){
        List<String> service = discoveryClient.getServices();
        for(String element : service){
            log.info("******element"+element);
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for(ServiceInstance instance : instances){
            log.info(instance.getServiceId()+"\t"+instance.getHost()+"\t"+instance.getPort()+"\t"+instance.getUri());
        }
        return this.discoveryClient;
    }
        @GetMapping(value = "/payment/lb")
        public String getPayment(){

        return serverPort;
        }
        @GetMapping(value = "/payment/feign/timeout")
        public String paymentFeignTimeout(){
        try {
            TimeUnit.SECONDS.sleep(3);
        }catch (InterruptedException e){
            e.printStackTrace();

        }
            return serverPort;
        }
  }
