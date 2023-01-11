----redis.log(redis.LOG_DEBUG,carType)
----redis.log(redis.LOG_DEBUG,type(carType))
------redis.log(redis.LOG_DEBUG,redis.call('hget',key,'warningType'+carType))
------redis.log(redis.LOG_DEBUG,type(redis.call('hget',key,'warningType'+carType)))
local key = KEYS[1]
local mapJson = cjson.decode(ARGV[1])
redis.log(redis.LOG_DEBUG,ARGV[1])
--车辆进还是出
local passVoucher = tonumber(mapJson.passVoucher)
--上行还是下行
local vehicles = tostring(mapJson.vehicles)
--失效时间
local expireTime = mapJson.expireTime

local carStream = redis.call('hget',key,'carStream')
local downVehicles = redis.call('hget',key,'downVehicles')
local upVehicles = redis.call('hget',key,'upVehicles')
if carStream ==  false then carStream = 0 end
if downVehicles == false then downVehicles = 0 end
if upVehicles == false then upVehicles = 0 end


if(passVoucher == 1) then
    carStream = carStream + 1
    if vehicles == "DOWN" then downVehicles = downVehicles + 1 end
    if vehicles == "UP" then upVehicles = upVehicles + 1 end
else if(passVoucher == 2) then
    if tonumber(carStream) > 0 then carStream = carStream - 1 end
    if tonumber(downVehicles) > 0 and vehicles == "DOWN" then downVehicles = downVehicles - 1 end
    if tonumber(upVehicles) > 0 and vehicles == "UP" then upVehicles = upVehicles - 1 end
end
end
redis.call('hset',key,'carStream',carStream)
redis.call('hset',key,'downVehicles',downVehicles)
redis.call('hset',key,'upVehicles',upVehicles)
if expireTime ~= false then  redis.call('expire',key,expireTime) end



