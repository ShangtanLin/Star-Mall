-- KEYS[1]: 优惠券库存 Key (coupon:stock:templateId)
-- KEYS[2]: 已领券用户集合 Key (coupon:users:templateId)
-- ARGV[1]: userId
-- ARGV[2]: 扣减数量 (通常为 1)

-- 1. 校验库存 Key 是否存在 (防止未预热直接抢购导致数据错误)
if redis.call('EXISTS', KEYS[1]) == 0 then
    return -1 -- 错误：活动未初始化
end

-- 2. 判断用户是否已领过 (幂等性校验)
if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then
    return 2 -- 错误：重复领取
end

-- 3. 获取并校验库存
local stock = tonumber(redis.call('GET', KEYS[1]))
local num = tonumber(ARGV[2])
if stock < num then
    return 1 -- 错误：库存不足
end

-- 4. 扣减库存并记录用户 (原子操作)
redis.call('DECRBY', KEYS[1], num)
redis.call('SADD', KEYS[2], ARGV[1])

return 0 -- 成功