# redis使用lua脚本
``解决问题：计算隧道内实时车流量、上行、下行、各地拍照车流量。为解决MQ并发消费情况下，页面可能出现上行+下行车流量不等于实时车流量情况，所以引入lua脚本解决所有车流量的计算原子性执行，前端或接口查询结果都是正确的。``

## 1.redis保证并发操作安全性
 ``Redis提供加锁、原子性操作确保并发操作安全性``
 - 加锁是在指令前后添加加锁/解锁操作，缺点是加锁操作多，会降低系统的并发访问性能，并且redis客户端要加锁时，需要用到分布式锁，但是分布式锁的实现复杂。
 - 原子操作是保持用户指令原子性执行，并且原子性操作执行时无需加锁，这样既能保证并发控制，也能减少对系统并发性能的影响。
## 2.redis提供的原子性方法
 - INCR、DECR、INCRBY、DECRBY、INCRBYFLOAT、HINCRBY、命令，对值进行增加、减少
 - 使用Lua脚本
## 4.Lua脚本简介
- 设计目的是为了通过灵活嵌入应用程序中从而为应用程序提供灵活的扩展和定制功能。
- Lua由标准C编写而成，几乎在所有操作系统和平台上都可以编译，运行。
- Lua体积小、启动速度快。
## 4.Redis使用Lua脚本好处
 - Lua脚本可以确保命令执行的原子性，脚本所有语句在Redis服务器端是串行执行的，因此脚本的执行不会被打断，可以实现类似事务的功能。
 - Java使用RedisTemplate或Jedis操作redis客户端，并发量较大情况下，会出现脏数据情况，并且降低网络开销，将多个请求通过脚本的形式一次发送到服务器，减少了网络的时延
 - 客户端发送的脚本可支持永久存在redis中，这样其他客户端可以复用这一脚本，而不需要使用代码完成相同的逻辑。



## 3.redis端使用Lua脚本
### 1.redis使用指令
```
    //普通set操作
    192.168.52.83:6379>set key value
    192.168.52.83:6379>get key
    //集合操作
    192.168.52.83:6379>lpush key value
    192.168.52.83:6379>lrange key startRow endRow
    //Map操作
    192.168.52.83:6379>hset key hashkey value
    192.168.52.83:6379>hget key hashkey
    //返回值
    192.168.52.83:6379>OK
```
### 2.使用lua脚本
- `Lua脚本通过各种语言的redis客户端都可以调用`
- `基本：eval命令`

```
   脚本语句：
   192.168.52.83:6379>eval script numkeys key [key...] arg [arg...]
   -script lua脚本内容.
   -numkeys 脚本中key的个数，用来区分key和value
   -key [key ...] 需要操作的键值，可以指定多个，在lua脚本中通过KEYS[1], KEYS[2]获取
   -arg [arg ...] 附加参数，在lua脚本中通过全局变量ARGV数组访问。例如：ARGV[1], ARGV[2]
   eval "redis.call('get/set/lpush/lrange/hset/hget等redis操作命令',KEYS[1](传入的key值),ARGV[1])" 1(key的个数) key(key的值) value(value)的值
   eval "redis.call('lpush',KEYS[1],ARGV[1]);redis.call('lpush',KEYS[1],ARGV[2]);return redis.call('lrange',KEYS[1],0,4);" 1 test:listTest 测试1 测试2
```
- `Lua脚本预加载`
```
   192.168.52.83:6379>script load "脚本语句"
   192.168.52.83:6379>返回具有唯一性的脚本的hash值
   192.168.52.83:6379>evalsha hash值 1 key value
```

### 3.RedisTemplate使用Lua脚本
```
    RedisScript 加载lua脚本类
    <T> RedisScript<T> of(String script){...}  //lua脚本作为字符串传入
    <T> RedisScript<T> of(String script, Class<T> resultType){...}  //设置返回值类型
    <T> RedisScript<T> of(Resource resource){...}  //使用Resource读取lua脚本，转成流。
    <T> RedisScript<T> of(Resource resource,Class<T> resultType){...}  //设置返回值类型
    RedisTemplate执行脚本方法：
    public <T> T execute(RedisScript<T> script, List<K> keys, Object... args) {
        return this.scriptExecutor.execute(script, keys, args);
    }
```

### 4.Jedis使用Lua脚本
```
    Object eval = jedisPool.getResource().eval("return redis.call('hset',KEYS[1],KEYS[2],ARGV[1])" 2 mapTest:map mapKey value1);
```


### 5.调试Lua脚本
 - ``使用Redis日志打印变量信息``

    修改redis.conf，开启redis日志：修改`logfile`属性，添加日志输出位置，并且修改`loglevel`,将属性修改为`debug`。
    
    `·注意：只有设置的错误等级大于等于redis实例日志等级才会被记录下来·`
```
redis.conf:
    logfile "/usr/local/redis/redis.log"
    loglevel debug
lua脚本:
    redis.log(loglevel, message)  
    redis.log(redis.LOG_DEBUG,"传入key"..key1)
        redis.LOG_DEBUG     -- 会打印生成大量信息，适用于开发/测试阶段
        redis.LOG_VERBOSE   -- 包含很多不太有用的信息，但是不像debug级别那么混乱
        redis.LOG_NOTICE    -- 适度冗长，适用于生产环境
        redis.LOG_WARNING   -- 仅记录非常重要、关键的警告消息
```
- `使用Redis.breakPoint()方法进行debug`

## Lua脚本语法
   





    
