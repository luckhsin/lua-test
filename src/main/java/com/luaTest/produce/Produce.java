package com.luaTest.produce;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.luaTest.domain.PassCarDo;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.luaTest.constants.Constants.*;

/**
 * @Description:
 * @ClassName: produce
 * @Author: dhd
 * @Date: 2021/12/27 9:42
 */
@Slf4j
public class Produce implements Runnable {
    private Channel channel;
    public Produce(Channel channel){
        this.channel = channel;
    }

    public static void main(String[] args) {
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost("192.168.52.83");
        cf.setPort(5672);
        cf.setUsername("admin");
        cf.setPassword("123456");
        try {
            Connection conn = null;
            conn = cf.newConnection();
            Channel channel = conn.createChannel();
            channel.queueDeclare(TUNNEL_PASS_CAR_QUEUE_NAME, true, false, false,  null);
            channel.exchangeDeclare(TUNNEL_PASS_CAR_EXCHANGE_NAME, BuiltinExchangeType.DIRECT,true);
            channel.queueBind(TUNNEL_PASS_CAR_QUEUE_NAME, TUNNEL_PASS_CAR_EXCHANGE_NAME, TUNNEL_PASS_CAR_ROUTING_KEY);
            ExecutorService executorService = Executors.newCachedThreadPool();
            Produce produce = new Produce(channel);
            for(int i=0;i<200;i++){
                executorService.execute(produce);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }



    @SneakyThrows
    @Override
    public void run() {
        PassCarDo passCarDo = randomPassCar();
        Random random = new Random();
        boolean res = random.nextBoolean();
        //??????????????????????????????   ??????????????????
        if (res) {
            passCarDo.setSbxh("512000000000021946");
        } else {
            passCarDo.setSbxh("512000000000021945");
        }
        //????????????1~10S???????????????
        Thread.currentThread().sleep(new Double((Math.random() * (10) + 1) * 1000).longValue());
        passCarDo.setJgsj(DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        log.info("{}???{}??????????????????",passCarDo.getHphm(),passCarDo.getJgsj());
        channel.basicPublish(TUNNEL_PASS_CAR_EXCHANGE_NAME, TUNNEL_PASS_CAR_ROUTING_KEY , null, JSON.toJSONBytes(passCarDo));
        //??????????????????????????????
        Thread.currentThread().sleep(new Double((Math.random() * (100) + 10) * 1000).longValue());
        //???????????????????????????
        if (res) {
            passCarDo.setSbxh("512000000000070096");
        } else {
            passCarDo.setSbxh("512000000000070095");
        }
        passCarDo.setJgsj(DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        log.info("{}???{}??????????????????",passCarDo.getHphm(),passCarDo.getJgsj());
        channel.basicPublish(TUNNEL_PASS_CAR_EXCHANGE_NAME, TUNNEL_PASS_CAR_ROUTING_KEY , null, JSON.toJSONBytes(passCarDo));
        }


        private PassCarDo randomPassCar(){
            PassCarDo passCarDo  = new PassCarDo();
            String[] citys = {"???", "???", "???","???","???","???","???","???"};
            String city = citys[(new Random().nextInt(8))];//????????????????????????
            StringBuffer plateNumStr = new StringBuffer(city);//????????????????????????????????????????????????0???9???6????????????
            plateNumStr.append((char)(Math.random()*26+'A'))
                    .append(new Random().nextInt(10))
                    .append((char)(Math.random()*26+'A'))
                    .append(new Random().nextInt(10))
                    .append((char)(Math.random()*26+'A'))
                    .append(new Random().nextInt(10));
            passCarDo = new PassCarDo();
            passCarDo.setLsh(String.valueOf(Math.random()*1000 + 1));
            passCarDo.setClpp("");
            passCarDo.setCsz("0");
            passCarDo.setTpurl1("");
            passCarDo.setTpurl2("");
            passCarDo.setHpzl("02");
            String cllx = "";
            int i = (int) (Math.random() * 5 + 1);
            if(i==1){
                cllx = "A"+i;
            }else if(i ==2 ){
                cllx = "B"+i;
            }else if(i == 3){
                cllx = "H"+i;
            }else if(i == 4){
                cllx = "E"+i;
            }else if(i == 5){
                cllx = "G"+i;
            }
            passCarDo.setCllx(cllx);
            passCarDo.setTid("");
            passCarDo.setSjly("");
            passCarDo.setSd("0");
            passCarDo.setTpurl("https://www.baidu.com");
            passCarDo.setHphm(plateNumStr.toString());
            return passCarDo;
        }

}
