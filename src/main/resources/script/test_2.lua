local arg = ARGV[1]
redis.log(redis.LOG_DEBUG,arg)
local mapjson = cjson.decode(arg)
redis.log(redis.LOG_DEBUG,mapjson)
local value1 = mapjson.key1
redis.log(redis.LOG_DEBUG,value1)
local value2 = mapjson.key2
redis.log(redis.LOG_DEBUG,value2)

