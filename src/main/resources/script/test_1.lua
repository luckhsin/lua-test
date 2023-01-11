local key = KEYS
redis.log(redis.LOG_DEBUG,key)
--输出key
local key1 = KEYS[1]
redis.log(redis.LOG_DEBUG,"传入key"..key1)
local key2 = KEYS[2]
redis.log(redis.LOG_DEBUG,"传入key"..key2)

redis.call("set",key1,"测试数据")
redis.call("set",key2,"测试数据2")


