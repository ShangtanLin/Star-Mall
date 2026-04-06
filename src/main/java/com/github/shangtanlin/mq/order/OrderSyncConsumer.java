package com.github.shangtanlin.mq.order;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.github.shangtanlin.config.mq.SubOrderMQConfig;
import com.github.shangtanlin.mapper.ParentOrderMapper;
import com.github.shangtanlin.mapper.SubOrderMapper;
import com.github.shangtanlin.model.dto.es.OrderSyncMessage;
import com.github.shangtanlin.model.dto.es.SubOrderIndexDoc;
import com.github.shangtanlin.model.entity.order.ParentOrder;
import com.github.shangtanlin.model.entity.order.SubOrder;
import com.github.shangtanlin.model.vo.order.SubOrderVO;
import com.github.shangtanlin.service.SubOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderSyncConsumer {

    @Resource
    private ElasticsearchClient elasticsearchClient;

    @Resource
    private SubOrderService subOrderService; // 或者你的 Feign 客户端
    @Autowired
    private ParentOrderMapper parentOrderMapper;
    @Autowired
    private SubOrderMapper subOrderMapper;

    /**
     * 监听子订单同步队列
     * concurrency = "3-5" 表示最少开启 3 个线程，最多 5 个线程并发处理，提升同步效率
     */
    @RabbitListener(
            queues = SubOrderMQConfig.SUB_ORDER_ES_QUEUE,
            concurrency = "3-5"
    )
    public void onOrderSyncMessage(OrderSyncMessage message) {
        // 1. 基础校验
        if (message == null || message.getSubOrderId() == null) {
            log.warn("收到无效的订单同步消息，直接丢弃");
            return;
        }

        Long subOrderId = message.getSubOrderId();
        Integer type = message.getType();

        log.info("开始处理 ES 同步消息 - 子订单ID: {}, 操作类型: {}", subOrderId, type);

        try {
            // 2. 根据类型分流处理
            if (type == 1 || type == 2) {
                // 新增或更新：逻辑统一为“反查+全量覆盖”
                handleSaveOrUpdate(subOrderId);
            } else if (type == 3) {
                // 删除逻辑
                handleDelete(subOrderId);
            }
        } catch (Exception e) {
            log.error("同步 ES 索引失败，子订单ID: {}", subOrderId, e);
            // 抛出异常，触发 MQ 的重试机制
            throw new RuntimeException("ES同步失败，等待重试", e);
        }
    }

    private void handleSaveOrUpdate(Long subOrderId) throws IOException {
        // 待实现：反查数据库并写入 ES 的逻辑
        // 1. 反查数据库：获取最全的订单数据（包含 items 列表）
        // 注意：这个方法内部应该关联查询 order_item 表，确保 items 不为空
        SubOrderVO fullOrder = subOrderService.getDetailForEs(subOrderId);

        if (fullOrder == null) {
            log.warn("同步失败：数据库中未找到子订单ID {}，可能已被物理删除或数据库延迟", subOrderId);
            return;
        }

        // 2. 将数据库对象转换为 ES 文档对象 (SubOrderIndexDoc)
        SubOrderIndexDoc doc = convertToEsDoc(fullOrder);
        //封装其他字段
        doc.setSubOrderId(subOrderId);
        SubOrder subOrder = subOrderMapper.selectById(subOrderId);
        ParentOrder parentOrder = parentOrderMapper.selectById(subOrder.getParentOrderId());
        doc.setParentOrderId(parentOrder.getId());
        doc.setParentOrderSn(parentOrder.getOrderSn());
        doc.setUserId(parentOrder.getUserId());




        // 3. 调用 ElasticsearchClient 执行写入 (Index 操作天然支持新增和全量覆盖)
        IndexResponse response = elasticsearchClient.index(i -> i
                .index("sub_order_index") // 指定索引名
                .id(subOrderId.toString()) // 必须使用数据库 ID 作为文档 ID
                .document(doc)             // 传入转换后的对象
        );

        log.info("ES同步成功 - 子订单ID: {}, 响应状态: {}", subOrderId, response.result());
    }

    private void handleDelete(Long subOrderId) throws IOException {



        // 待实现：调用 ES Client 删除文档
    }



    private SubOrderIndexDoc convertToEsDoc(SubOrderVO vo) {
        SubOrderIndexDoc doc = new SubOrderIndexDoc();

        // 2. 手动处理金额字段 (BigDecimal -> Double)
        // 假设你的字段名如下，请根据实际情况调整
        if (vo.getGoodsAmount() != null) {
            doc.setGoodsAmount(vo.getGoodsAmount().doubleValue());
        }
        if (vo.getPayAmount() != null) {
            doc.setPayAmount(vo.getPayAmount().doubleValue());
        }
        if (vo.getCouponAmount() != null) {
            doc.setCouponAmount(vo.getCouponAmount().doubleValue());
        }
        if (vo.getFreightAmount() != null) {
            doc.setFreightAmount(vo.getFreightAmount().doubleValue());
        }

        // 使用 Spring 的 BeanUtils 拷贝基础字段 (id, sn, status, address等)
        BeanUtils.copyProperties(vo, doc);


        // 3. 处理嵌套的 items 列表中的金额
        if (vo.getItems() != null) {
            List<SubOrderIndexDoc.ItemInnerDTO> esItems = vo.getItems().stream().map(item -> {
                SubOrderIndexDoc.ItemInnerDTO esItem = new SubOrderIndexDoc.ItemInnerDTO();
                BeanUtils.copyProperties(item, esItem);

                // 手动处理 item 中的金额
                if (item.getPrice() != null) {
                    esItem.setPrice(item.getPrice().doubleValue());
                }
                return esItem;
            }).collect(Collectors.toList());
            doc.setItems(esItems);
        }

        return doc;
    }
}
