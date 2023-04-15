package com.teemo.shopping.account;

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
    private PermissionUtil permissionUtil;
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

    @PostMapping(path = "/follow/{gameId}")
    public String followGame(@PathVariable Long gameId) {
        if (!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        Long accountId = permissionUtil.getAuthenticatedAccountId().get();
        accountService.followGame(accountId, gameId);
        return "success";
    }

    @DeleteMapping(path = "/unfollow/{gameId}")
    public String unfollowGame(@PathVariable Long gameId) {
        if (!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        Long accountId = permissionUtil.getAuthenticatedAccountId().get();
        accountService.unfollowGame(accountId, gameId);
        return "success";
    }

    @PostMapping(path = "/coupons/{couponId}")
    public String addCoupon(@PathVariable Long couponId) {
        if (!permissionChecker.checkAdmin()) {
            throw new SecurityException("접근 권한 없음");
        }
        Long accountId = permissionUtil.getAuthenticatedAccountId().get();
        accountService.addCoupon(accountId, couponId, 1);
        return "success";
    }

    @GetMapping(path = "/own/games/")
    public List<GameDTO> ownGameList() {
        if (!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        return accountService.getOwnGames(permissionUtil.getAuthenticatedAccountId().get());
    }

    @GetMapping(path = "/follow/games/")
    public List<GameDTO> followGameList() {
        if (!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        return accountService.getFollowGames(permissionUtil.getAuthenticatedAccountId().get());
    }

    @GetMapping(path = "/reviews/")
    public List<ReviewDTO> reviewList() {
        if (!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        return accountService.getReviews(permissionUtil.getAuthenticatedAccountId().get());
    }

    @PostMapping(path = "/{gamesId}/reviews")
    public Long addReview(@PathVariable Long gameId, @RequestBody ReviewDTO reviewDTO)
        throws Exception {
        if (!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        Long accountId = permissionUtil.getAuthenticatedAccountId().get();
        return reviewService.add(
            ReviewDTO.builder().accountId(accountId).gameId(gameId).content(reviewDTO.getContent())
                .rating(reviewDTO.getRating()).build());
    }
    @PostMapping(path = "/orders")
    public OrderAddResponse add(@RequestHeader("user-agent") String userAgent,
        @RequestBody OrderAddRequest orderAddRequest) throws Exception {

        if (!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }

        //improve: useragent 에 따른 redirect 변화
        Long accountId = permissionUtil.getAuthenticatedAccountId().get();
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
    @GetMapping(path = "/{orderId}")
    public OrderDTO get(@PathVariable("orderId") Long orderId) throws Exception {
        if (!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        OrderDTO orderDTO = orderService.get(orderId);
        if (!permissionChecker.checkAdmin() && !permissionChecker.checkResourceOwner(
            orderDTO.getAccountId())) {
            throw new SecurityException("접근 권한 없음");
        }
        return orderDTO;
    }

    @GetMapping("/orders/")
    public List<OrderDTO> getOrders() {
        if (!permissionChecker.checkAuthenticated()) {
            throw new SecurityException("접근 권한 없음");
        }
        Long accountId = permissionUtil.getAuthenticatedAccountId().get();
        return orderService.getOrdersByAccount(accountId);
    }
}
