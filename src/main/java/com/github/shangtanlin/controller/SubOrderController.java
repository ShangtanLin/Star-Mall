package com.github.shangtanlin.controller;

import com.github.shangtanlin.model.vo.order.SubOrderVO;


import com.github.shangtanlin.result.Result;
import com.github.shangtanlin.service.SubOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.github.shangtanlin.result.PageResult;
import java.io.IOException;


@RestController
@RequestMapping("/api/order_sub")
public class SubOrderController {

    @Autowired
    private SubOrderService subOrderService;


    /**
     * 查询订单列表(ES索引实现)
     * @param subOrderId
     * @param status
     * @param spuName
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    public Result<?> getSubOrderList
    (@RequestParam(value = "subOrderId",required = false) Long subOrderId,
     @RequestParam(value = "status",required = false) Integer status,
     @RequestParam(value = "spuName",required = false) String spuName,
     @RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
     @RequestParam(value = "PageSize",defaultValue = "12") Integer pageSize) throws IOException {
        PageResult<SubOrderVO> subOrderVOPageResult = subOrderService
                .getSubOrderList(
                        subOrderId,status,spuName,
                        pageNo,pageSize);
        return Result.ok(subOrderVOPageResult);
    }


    /**
     * 确认收货
     * @param subOrderId
     */
    @PostMapping("/confirm/{subOrderId}")
    public Result<?> confirmReceipt(@PathVariable("subOrderId") Long subOrderId) {
        // 1. 调用 Service 执行确认收货逻辑
        // 内部包含：校验订单所属用户、修改数据库状态、同步更新 ES
        subOrderService.confirmReceive(subOrderId);

        return Result.ok();
    }


    /**
     * 删除订单 (逻辑删除)
     * @param subOrderId 子订单ID
     */
    @DeleteMapping("/delete/{subOrderId}")
    public Result<?> deleteOrder(@PathVariable("subOrderId") Long subOrderId) {
        // 1. 调用 Service 执行删除逻辑
        // 内部包含：权限校验、MySQL 状态更新、ES 索引更新
        subOrderService.deleteSubOrder(subOrderId);

        return Result.ok();
    }




}
