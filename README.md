
teemoShopping
===
설명 
--
게임 구매 사이트를 위한 Rest API 제공 Backend

기간
--
2023.03.28 ~

기능 
--
- 로그인/ 회원가입
- JWTtoken 기반 Stateless 인증 구현
- RestAPI 를 통한 API 구현
- SpringSecurity의 Filter를 통해 보안 및 Jwt필터 구현
- WebClient을 이용한 카카오페이 API 연동
- 게임 팔로우 구현 
- 쿠폰 구현
- 단건 결제 및 묶음 결제 구현
- 일부 환불 및 전체 환불 구현
- 게임 리뷰 구현
- 쿠폰 발급 구현

기술 스택
--
1. Spring
  - SpringBoot
  - SpringSecurity
  - SpringValidation
  - SpringTest
2. H2
3. MYSQL
4. Util
  - Lombok
  - Jwt
  - Gson

ERD: https://www.erdcloud.com/d/dg3RDn7rvmosF5Txz


API: https://teemoshopping.techteemo.store/swagger-ui/index.html

보완점 / 추가할 점
--
- MySQL 연결 
- CI/CD 추가

대표 로직
--

KakaoPayment 결제 로직
![image](https://user-images.githubusercontent.com/59180460/234909500-f8cab0ba-ac15-41cc-a763-d806c7172652.png)


Payment 상태 로직
![image](https://user-images.githubusercontent.com/59180460/234911007-f493f3db-5730-41aa-836e-c40d31729f0f.png)







API Example
====
회원가입 
---
```
요청 
POST https://teemoshopping.techteemo.store/accounts/register
body
{
  "username": "cdjsak232",
  "password": "asd12k2"
}
응답 
200 success
```

로그인
---
```
요청 
POST https://teemoshopping.techteemo.store/accounts/register
body
{
  "username": "cdjsak232",
  "password": "asd12k2"
}
응답 
200 eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJ0ZWVtb19zaG9wcGluZyIsImV4cCI6MTY4MjEzMjM5NSwicm9sZSI6IiIsImFjY291bnRfaWQiOjN9.toeBFhQTdJL2vRxDhb1M7lsJvrlhINOhnAvZg9DIJlwcRsow1anKPdx5IgfC5v45XzXJKo8Q4-tKy72Jm59h5bnOshBiz9y5pM00Mry0IOi9Lq1MhMclnrLX73H1KuIkqacoXuJ8asmS75FxdhC6uCDLT6J71eb57tpIHlzpO6bc6JDMTorAq52J-3iCp-YoZgkKzuzMDa1FoBTSACve_Dc7Marq_xyWc4ef43-IiOc3BBWTePDyrpoi1Ac3Kh_kdOMKoiFH5gMkKBSPZhkfzTOp4c6npGjeu0dAaGHCNY3Kt5NJbOFealZ3tIkQxC3LKmZrpAlAtlYO8Sk0M_BQsA
```

게임 조회
---
```
요청 
GET https://teemoshopping.techteemo.store/games/
응답 
200 
[
  {
  "id": 1,
  "name": "티모어드벤쳐",
  "description": "티모와 게임을",
  "ratingAvg": 0.0,
  "ratingCount": 0,
  "price": 10300,
  "discountPercent": 0.0
  },
  {
  "id": 2,
  "name": "리그오브치킨",
  "description": "최고의 치킨이 되자",
  "ratingAvg": 0.0,
  "ratingCount": 0,
  "price": 20000,
  "discountPercent": 0.0
  }
]
```
주문 생성
===
```
요청
{
  "point": 0,
  "payment_method": "KAKAOPAY",
  "redirect": "http://naver.com",
  "game_infos": [
    {
      "game_id": 1
    }
  ]
}
응답 
200
{
    "order_id": 1,
    "redirects": [
        "https://online-pay.kakao.com/mockup/v1/83301da3113c0603e53e06280e086b57094b23e4d9701cb8a876a3e1b9cef34d/info"
    ],
    "payment_ids": [
        1
    ],
    "pending_payment_ids": [
        1
    ]
}

```
