package com.luaTest.listener;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.luaTest.domain.PassCarDo;
import com.luaTest.utils.RedisUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static com.luaTest.constants.Constants.*;

/**
 * @Description TODO
 * @Author dinghaodong
 * @Date 2022/11/24 13:22
 **/
@Component
@Slf4j
public class ThunnelListener {

    @Autowired
    private RedisUtil redisUtil;

    //执行lua脚本的工具类
    static RedisScript script;
    static {
        String path = "script/redis_car.lua";
        ClassPathResource resource = new ClassPathResource(path);
        script = RedisScript.of(resource, Map.class);
    }


    @RabbitListener(queues = TUNNEL_PASS_CAR_QUEUE_NAME)
    public void excute(Message message, Channel channel) {
        MessageProperties properties = message.getMessageProperties();
        long tag = properties.getDeliveryTag();
        try {
            PassCarDo passCarDo = JSON.parseObject(message.getBody(),PassCarDo.class);
            System.out.println("接收到对象"+passCarDo);
            String sbxh = passCarDo.getSbxh();
            //判断车辆上行还是下行
            String vehicles = judgeVehicles(sbxh);
            //判断车辆进入或者驶出  并计算车辆速度
            String passVoucher = judegPassVoucher(vehicles,passCarDo);
            if(passVoucher == null) return;
            //调用lua脚本计算实时车流量、上下行车流量
            Map<String,Object> argv = new HashMap<String,Object>();
            argv.put("passVoucher",passVoucher);
            argv.put("vehicles",vehicles);
            argv.put("expireTime",EXPIRETIME);
            argv.put("hphm", StringUtils.substring(passCarDo.getHphm(),0,1));
            redisUtil.getRedisTemplate().execute(script, Collections.singletonList(TUNNEL_CAR_STREAM + DateUtil.format(new Date(),"yyyy-MM-dd")), argv);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                channel.basicAck(tag, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String judgeVehicles(String sbxh){
        String vehicles = "";
        if(UPVEHICLES.containsValue(sbxh)){
            vehicles = UP;
        }else if(DOWNVEHICLES.containsValue(sbxh)){
            vehicles = DOWN;
        }
        return vehicles;
    }
    private String judegPassVoucher(String vehicles,PassCarDo passCarDo){
        String sbxh = passCarDo.getSbxh();
        String hphm = passCarDo.getHphm();
        String passVoucher = null;
        String key = THUNNEL_PASS_CAR +vehicles+"_"+ hphm;
        List<String> in = Arrays.asList("512000000000021946","512000000000021945");
        List<String> out = Arrays.asList("512000000000070095","512000000000070096");
        if(in.contains(sbxh)){
            redisUtil.set(key, passCarDo.getJgsj());
            passVoucher= "1";
            log.info("车辆"+hphm+"进入隧道");
        }else if(out.contains(sbxh)){
            String sjq = (String)redisUtil.get(key);
            if(sjq != null && passCarDo.getJgsj() != null){
                passVoucher = "2";
                log.info("车辆"+hphm+"驶出隧道");
                redisUtil.del(key);
            }
        }
        return passVoucher;
    }
}
