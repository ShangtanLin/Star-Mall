package com.github.shangtanlin.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.shangtanlin.mapper.OrderItemMapper;
import com.github.shangtanlin.model.entity.order.OrderItem;
import com.github.shangtanlin.service.OrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl
        extends ServiceImpl<OrderItemMapper, OrderItem>
        implements OrderItemService {
}
