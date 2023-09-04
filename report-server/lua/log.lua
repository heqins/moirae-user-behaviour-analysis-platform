--引入json解析库
local cjson = require("cjson")

local ngx = require "ngx";
ngx.req.read_body()  -- 开启读取请求体模式
local data = ngx.req.get_body_data()  -- 获取内存中的请求体

--kafka库
local producer = require "resty.kafka.producer"
--kafka的链接地址
local broker_list = {
      { host = "192.168.71.16", port = 9092 }
}
--生产者
local pro = producer:new(broker_list,{ producer_type="async"})

--用户IP
local headers=ngx.req.get_headers()
local ip=headers["X-REAL-IP"] or headers["X_FORWARDED_FOR"] or ngx.var.remote_addr or "0.0.0.0"

--消息内容
local logjson = {}
logjson["uri"]=ngx.var.uri
logjson["ip"]=ip
logjson["productId"]="haha"
logjson["accesstime"]=os.date("%Y-%m-%d %H:%m:%S")
-- logjson["body"]= data

--发送消息
local offset, err = pro:send("test_redis_state", nil, cjson.encode(logjson))

if not ok then
	ngx.log(ngx.ERR, "kafka send err:", err)
	return
end