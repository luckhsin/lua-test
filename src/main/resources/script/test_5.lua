local key = KEYS[1]
local mapJson = cjson.decode(ARGV[1])
local keys = mapJson.keys
local values = mapJson.values
for i,j in pairs(keys) do
    local mapkey = tostring(j);
    local mapValue = tostring(values[i])
    redis.log(redis.LOG_DEBUG,mapkey..'-----'..mapValue)
    redis.call('hset',key,mapkey,mapValue)
end
return mapJson
