package com.luaTest.constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description TODO
 * @Author dinghaodong
 * @Date 2022/11/24 11:56
 **/
public class Constants {
    public static final String TUNNEL_PASS_CAR_QUEUE_NAME = "tunnel_pass_car_queue";
    public static final String TUNNEL_PASS_CAR_EXCHANGE_NAME = "tunnel_pass_car_exchange_name";
    public static final String TUNNEL_PASS_CAR_ROUTING_KEY = "tunnel_pass_car_routing_key";

    public static Map<String,String> UPVEHICLES = new HashMap();
    public static Map<String,String> DOWNVEHICLES = new HashMap();
    static {
        UPVEHICLES.put("in","512000000000021945");
        UPVEHICLES.put("out","512000000000070095");
        DOWNVEHICLES.put("in","512000000000021946");
        DOWNVEHICLES.put("out","512000000000070096");
    }


    //隧道内车辆过车时间存储KEY
    public static final String THUNNEL_PASS_CAR = "thunnel_pass_car:";
    public static final String TUNNEL_CAR_STREAM = "tunnel_car_stream_";

    //预警车辆流量类型存储KEY
    public static String TUNNEL_WARNING_TYPE = "tunnel_warning_type:";



    //当前时间至一天结束秒数
    public static long EXPIRETIME =  (LocalDateTime.of(LocalDate.now(), LocalTime.MAX).toEpochSecond(ZoneOffset.of("+8"))-System.currentTimeMillis() / 1000 );

    //上行流量
    public static String UP ="UP";
    //下行流量
    public static String DOWN ="DOWN";
}
