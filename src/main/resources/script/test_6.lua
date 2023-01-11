local key = KEYS[1]
local value = ARGV[1]
redis.call('set',key,value);
local val = cjson.decode(value);
local expireTime = val[2].expireTime
redis.log(redis.LOG_DEBUG,expireTime[2])
redis.log(redis.LOG_DEBUG,expireTime[1])