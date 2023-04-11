package com.teemo.shopping.order.controller;

import com.teemo.shopping.order.dto.OrderDTO;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.service.OrderService;
import com.teemo.shopping.account.service.AccountAuthenticationService;
import com.teemo.shopping.security.PermissionChecker;
import com.teemo.shopping.security.PermissionUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AccountAuthenticationService accountAuthenticationService;
    @Autowired
    private PermissionChecker permissionChecker;
    @Autowired
    private PermissionUtil permissionUtil;
    @GetMapping(path = "/{orderId}")
    public OrderDTO get(@PathVariable("orderId") Long orderId) throws Exception {
        if(!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        OrderDTO orderDTO = orderService.get(orderId);
        if(!permissionChecker.checkAdmin() && !permissionChecker.checkResourceOwner(orderDTO.getAccountId())) {
            throw new SecurityException("접근 권한 없음");
        }
        return orderDTO;
    }

    @PostMapping(path = "/")
    public Long add(@RequestBody
        int point, @RequestBody List<PaymentMethod> methods, @RequestBody
        Map<Long, Optional<Long>> gameCouponIdMap) throws Exception {

        if(!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        Long accountId = permissionUtil.getAuthenticatedAccountId().get();

        return orderService.createOrder(accountId, point, methods, gameCouponIdMap);
    }

}
