package com.teemo.shopping.order.controller;

import com.teemo.shopping.order.dto.OrderDTO;
import com.teemo.shopping.order.service.OrderRefundService;
import com.teemo.shopping.order.service.OrderService;
import com.teemo.shopping.security.PermissionChecker;
import io.swagger.v3.oas.annotations.Operation;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRefundService orderRefundService;

    @Autowired
    private PermissionChecker permissionChecker;
    @Operation(operationId = "주문 조회", summary = "주문 조회", tags = {"주문"})
    @GetMapping(path = "/{orderId}")
    public ResponseEntity<OrderDTO> get(@PathVariable("orderId") Long orderId) throws Exception {
        permissionChecker.checkAdminAndThrow();
        return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
            .body(orderService.get(orderId));
    }
    @Operation(operationId = "주문 환불", summary = "주문 환불", tags = {"주문"})
    @PostMapping("/{orderId}/refund")
    public void refundOrder(@PathVariable Long orderId) throws Exception{
        permissionChecker.checkAdminAndThrow();
        orderRefundService.refundOrder(orderId);
    }
    @Operation(operationId = "주문 에서 게임 환불", summary = "주문 에서 게임 환불", tags = {"주문"})
    @GetMapping("/{orderId}/games/{gameId}/refund")
    public void refundGame(@PathVariable Long orderId, @PathVariable Long gameId) {
        permissionChecker.checkAdminAndThrow();
        orderRefundService.refundGame(orderId, gameId);
    }
}

