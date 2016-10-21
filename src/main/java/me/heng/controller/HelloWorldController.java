package me.heng.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import me.heng.service.FooService;
import me.heng.service.HelloWorldService;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by bojack on 16/7/12.
 */
@RestController
public class HelloWorldController {


    @Resource
    HelloWorldService helloWorldService;

    @Resource
    FooService fooService;

    @RequestMapping("/hello")
    public String hello() {
        return helloWorldService.getString();
    }

    @RequestMapping("/foo")
    public String foo() {

        return fooService.getFoo("http://www.google.com");

    }

    @RequestMapping(value = "/ok", method = RequestMethod.GET)
    @HystrixCommand(fallbackMethod = "okFallback",
            threadPoolProperties = {
            @HystrixProperty(name = "coreSize", value = "30"),
            @HystrixProperty(name = "maxQueueSize", value = "100"),
            @HystrixProperty(name = "queueSizeRejectionThreshold", value = "20") }, commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "100"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "90"),
    })
    public String ok() throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://www.google.com");
        CloseableHttpResponse response = null ;
        try {
            response = httpclient.execute(httpGet);

            HttpEntity entity = (HttpEntity) response.getEntity();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
            String total = "";
            String line = bufferedReader.readLine();
            while (line != null){
                total += line;
                line = bufferedReader.readLine();
            }
            return total;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ok";
    }



    public String okFallback( ) {
        return "fallback";
    }



}
