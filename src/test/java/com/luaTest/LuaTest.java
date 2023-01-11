package com.luaTest;

import com.luaTest.domain.Student;
import com.luaTest.utils.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.JedisPool;

import java.util.*;

import static com.luaTest.constants.Constants.EXPIRETIME;

/**
 * @Description TODO
 * @Author dinghaodong
 * @Date 2022/11/24 13:20
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class LuaTest {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private JedisPool jedisPool;

    @Test
    public void test1(){
        String path = "script/test_1.lua";
        ClassPathResource resource = new ClassPathResource(path);
        RedisScript<List> script = RedisScript.of(resource, List.class);
        List<String> keys = new ArrayList<>(Arrays.asList("key1","key2"));
        System.out.println(redisTemplate.execute(script,keys));
    }

    @Test
    public void test2(){
        String path = "script/test_2.lua";
        ClassPathResource resource = new ClassPathResource(path);
        RedisScript<Map> script = RedisScript.of(resource, Map.class);
        String key1 = "测试key1";
        String key2 = "测试key2";
        List<String> keys = new ArrayList<>(Arrays.asList(key1,key2));
        Map<String,Object> arg = new HashMap<>();
        arg.put(key1,"测试数据");
        arg.put(key2,keys);
        System.out.println(redisTemplate.execute(script,keys,arg));
    }

    @Test
    public void jedisPoolTest(){
        Object eval = jedisPool.getResource().eval("return redis.call('hset',KEYS[1],KEYS[2],ARGV[1]) 2 mapTest:map mapKey value1");
        System.out.println(eval);
    }


    @Test
    public void test3(){
        String path = "script/test_3.lua";
        ClassPathResource resource = new ClassPathResource(path);
        RedisScript<Map> script = RedisScript.of(resource, Map.class);
        String key1 = "测试student";
        String key2 = "测试key2";
        List<String> keys = new ArrayList<>(Arrays.asList(key1,key2));
        Map<String,Object> arg = new HashMap<>();
        Student build = new Student().builder().gender("男").name("丁浩东").password("1234567").score(65).phone("17805591018").build();
        arg.put("value1",build);
        System.out.println(redisTemplate.execute(script,keys,arg));
    }



    //打印日志
    @Test
    public void test4(){
        String path = "script/test_5.lua";
        ClassPathResource resource = new ClassPathResource(path);
        RedisScript<Map> script = RedisScript.of(resource, Map.class);
        String key1 = "student";
        String key2 = "password";
        List<String> keys = new ArrayList<>(Arrays.asList(key1,key2));
        Map<String,Object> arg = new HashMap<>();
        System.out.println(redisTemplate.execute(script,keys,arg));
    }
    @Test
    public void test5(){
        String path = "script/test_5.lua";
        ClassPathResource resource = new ClassPathResource(path);
        RedisScript<Object> script = RedisScript.of(resource, Object.class);
        String key1 = "test:studentHash";
        Map<String,Object> arg = new HashMap<>();
        arg.put("keys",new ArrayList<>(Arrays.asList("name","age","phone")));
        arg.put("values",new ArrayList<>(Arrays.asList("丁浩东","18","17805591108")));
        System.out.println(redisTemplate.execute(script,Collections.singletonList(key1),arg));
    }

    @Test
    public void test6(){
        String path = "script/test_6.lua";
        ClassPathResource resource = new ClassPathResource(path);
        RedisScript<Object> script = RedisScript.of(resource, Object.class);
        Map<String,Object> map = new HashMap<>();
        map.put("passVoucher","1");
        map.put("vehicles","DOWN");
        map.put("expireTime",EXPIRETIME);
        redisTemplate.execute(script,Collections.singletonList("test:xuliehua"),map);
    }


}
