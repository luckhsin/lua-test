-- 简单的 table
local mytable = {}

mytable[1]= "Lua"
mytable["wow"] = "修改前"
redis.call('set','objTest',tostring(mytable))
