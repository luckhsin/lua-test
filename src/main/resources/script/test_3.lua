local key = KEYS[1]
local value = ARGV[1]
redis.log(redis.LOG_DEBUG,value)
redis.call("set",key,value)
