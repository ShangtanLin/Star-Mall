package com.github.shangtanlin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.shangtanlin.model.entity.order.SubOrder;
import com.github.shangtanlin.model.vo.order.SubOrderVO;
import com.github.shangtanlin.result.PageResult;

import java.io.IOException;


public interface SubOrderService  extends IService<SubOrder> {

    /**
     * 查询订单列表
     * @param orderSubId
     * @param status
     * @param spuName
     * @param pageNo
     * @param pageSize
     * @return
     */
    PageResult<SubOrderVO>
    getSubOrderList(Long orderSubId,
                    Integer status,
                    String spuName,
                    Integer pageNo,
                    Integer pageSize) throws IOException;

    /**
     * 确认收货
     * @param subOrderId
     */
    void confirmReceive(Long subOrderId);

    /**
     * 删除订单 (逻辑删除)
     * @param subOrderId 子订单ID
     */
    void deleteSubOrder(Long subOrderId);


    /**
     * 从数据库中查出子订单表数据并封装成SubOrderVO
     * @param subOrderId
     * @return
     */
    SubOrderVO getDetailForEs(Long subOrderId);
}
