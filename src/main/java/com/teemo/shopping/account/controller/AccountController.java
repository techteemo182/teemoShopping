package com.teemo.shopping.account.controller;

import com.teemo.shopping.account.dto.request.ReviewAddRequest;
import com.teemo.shopping.account.service.AccountService;
import com.teemo.shopping.game.dto.GameDTO;
import com.teemo.shopping.order.dto.OrderDTO;
import com.teemo.shopping.order.dto.payment.KakaopayPaymentDTO;
import com.teemo.shopping.order.dto.payment.PaymentDTO;
import com.teemo.shopping.account.dto.request.OrderAddRequest;
import com.teemo.shopping.account.dto.response.OrderAddResponse;
import com.teemo.shopping.order.enums.PaymentMethod;
import com.teemo.shopping.order.enums.PaymentStatus;
import com.teemo.shopping.order.service.KakaopayPaymentService;
import com.teemo.shopping.order.service.OrderService;
import com.teemo.shopping.review.dto.ReviewDTO;
import com.teemo.shopping.review.service.ReviewService;
import com.teemo.shopping.security.PermissionChecker;
import com.teemo.shopping.security.PermissionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private PermissionChecker permissionChecker;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private KakaopayPaymentService kakaopayPaymentService;
    @Operation(operationId = "게임 팔로우", summary = "게임 팔로우", tags = {"계정"})
    @PostMapping(path = "/follow/games/{gameId}")
    public String followGame(@PathVariable Long gameId) {
        Long accountId = permissionChecker.getAccountIdElseThrow();
        accountService.followGame(accountId, gameId);
        return "success";
    }

    @Operation(operationId = "게임 언팔로우", summary = "게임 언팔로우", tags = {"계정"})
    @DeleteMapping(path = "/unfollow/games/{gameId}")
    public String unfollowGame(@PathVariable Long gameId) {
        Long accountId = permissionChecker.getAccountIdElseThrow();
        accountService.unfollowGame(accountId, gameId);
        return "success";
    }
    @Operation(operationId = "계정에 쿠폰 추가", summary = "계정에 쿠폰 추가", tags = {"계정"})
    @PostMapping(path = "/coupons/{couponId}")
    public String addCoupon(@PathVariable Long couponId) {
        Long accountId = permissionChecker.getAccountIdElseThrow();
        accountService.addCoupon(accountId, couponId, 1);
        return "success";
    }

    @Operation(operationId = "게정이 소유한 게임 리스트", summary = "게정이 소유한 게임 리스트", tags = {"계정"})
    @GetMapping(path = "/own/games/")
    public List<GameDTO> ownGameList() {
        Long accountId = permissionChecker.getAccountIdElseThrow();
        return accountService.getOwnGames(accountId);
    }

    @Operation(operationId = "계정의 게임 팔로우 리스트", summary = "계정의 게임 팔로우 리스트", tags = {"계정"})
    @GetMapping(path = "/follow/games/")
    public List<GameDTO> followGameList() {
        Long accountId = permissionChecker.getAccountIdElseThrow();
        return accountService.getFollowGames(accountId);
    }

    @Operation(operationId = "계정의 리뷰 리스트", summary = "계정의 리뷰 리스트", tags = {"계정"})
    @GetMapping(path = "/reviews/")
    public List<ReviewDTO> reviewList() {
        Long accountId = permissionChecker.getAccountIdElseThrow();
        return accountService.reviewList(accountId);
    }

    @Operation(operationId = "계정에 리뷰 추가", summary = "계정에 리뷰 추가", tags = {"계정"})
    @PostMapping(path = "/games/{gameId}/reviews")
    public Long addReview(@PathVariable("gameId") Long gameId, @RequestBody ReviewAddRequest reviewAddRequest)
        throws Exception {
        Long accountId = permissionChecker.getAccountIdElseThrow();
        return reviewService.add(
            ReviewDTO.builder().accountId(accountId).gameId(gameId).content(reviewAddRequest.getContent())
                .rating(reviewAddRequest.getRating()).build());
    }
    @Operation(operationId = "계정에 주문 추가", summary = "계정에 주문 추가", tags = {"계정"})
    @PostMapping(path = "/orders")
    public OrderAddResponse add(@RequestHeader("user-agent") String userAgent,
        @RequestBody OrderAddRequest orderAddRequest) throws Exception {
        //improve: useragent 에 따른 redirect 변화
        Long accountId = permissionChecker.getAccountIdElseThrow();
        Long orderId = orderService.addOrder(accountId, orderAddRequest.getPoint(),
            orderAddRequest.getMethods(), orderAddRequest.getGameIds(),
            orderAddRequest.getGameCouponIdMap(), orderAddRequest.getRedirect());
        List<PaymentDTO> payments = orderService.getPayments(orderId);
        var responseBuilder = OrderAddResponse.builder();
        for (var payment : payments) {
            if (payment.getMethod().equals(PaymentMethod.KAKAOPAY)) {
                KakaopayPaymentDTO kakaopayPaymentDTO = kakaopayPaymentService.get(
                    payment.getId());
                responseBuilder.redirect(kakaopayPaymentDTO.getNextRedirectPcUrl());
            }
        }
        List<Long> paymentIds = payments.stream().map((payment) -> payment.getId()).toList();
        List<Long> pendingPaymentIds = payments.stream().filter(payment -> payment.getStatus().equals(
            PaymentStatus.PENDING)).map((payment) -> payment.getId()).toList();

        responseBuilder.orderId(orderId);
        responseBuilder.paymentIds(paymentIds);
        responseBuilder.pendingPaymentIds(pendingPaymentIds);
        return responseBuilder.build();
    }
    @Operation(operationId = "계정의 주문 조회", summary = "계정의 주문 조회", tags = {"계정"})
    @GetMapping(path = "/orders/{orderId}")
    public OrderDTO get(@PathVariable("orderId") Long orderId) throws Exception {
        permissionChecker.checkAuthenticatedAndThrow();
        OrderDTO orderDTO = orderService.get(orderId);
        if (!permissionChecker.checkAdmin() && !permissionChecker.checkResourceOwner(
            orderDTO.getAccountId())) {
            throw new SecurityException("접근 권한 없음");
        }
        return orderDTO;
    }

    @Operation(operationId = "계정의 주문 리스트", summary = "계정의 주문 리스트", tags = {"계정"})
    @GetMapping("/orders/")
    public List<OrderDTO> getOrderList() {
        Long accountId = permissionChecker.getAccountIdElseThrow();
        return accountService.orderList(accountId);
    }
}