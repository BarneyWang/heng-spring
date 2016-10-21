package me.heng.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by bojack on 16/8/23.
 */
@Component
public class FooService {

    @HystrixCommand(fallbackMethod = "failover",
            groupKey = "sarrs.usercenter.http",
            commandKey = "sarrs.usercenter.http",
            commandProperties = {   @HystrixProperty(name="fallback.enabled",value="true"),
                          @HystrixProperty(name="circuitBreaker.requestVolumeThreshold",value="50"),
                    @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds",value="60000"),
                    @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value="50"),
                    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value="1000"),
                    @HystrixProperty(name="execution.isolation.thread.interruptOnTimeout",value="true")
            },
            threadPoolProperties = {
                    @HystrixProperty(name="coreSize",value="40")
            }
    )
    public static String getFoo(String url) throws RuntimeException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
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

        return null;
    }

    public static String failover() {
        System.out.println("defaultfoo");
        return "defaultFoo";
    }
}
