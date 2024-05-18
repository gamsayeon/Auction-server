# Auction-server
옥션 중고 경매 시스템과 같은 경매 서비스 백엔드 서버

- 참고 : https://corners.auction.co.kr/corner/UsedMarket.aspx

- 경매 판매 방식
    - 경매 시작 금액을 1,000원 부터 원하시는 대로 설정할 수 있으며, 즉시구매 가격도 필요한 경우 등록 가능
    - 경매로 등록한 물품은 입찰 후 내용 수정이 불가하며, 물품 상세 설명 추가 및 등록
- 프로토타입 : https://ovenapp.io/view/sb7Z9CpgM2xzq7HRLyYAyNr6enzrarx3/
---
# 목적
- 객체 지향적 코드 작성 및 개발 생산성을 위한 JPA 연동
- 고가용성 애플리케이션 처리를 위한 MQ 연동
- 조회 성능을 높이기 위한 Elasticsearch 연동
- 대용량 트래픽을 고려한 어플리케이션(초당 500tps 이상의 상품 검색API)
- 객체지향적으로 접근하여 유지보수할때 클린 코드구현을 통해 가독성을 높이는 목적
- 단위테스트를 통한 검증가능한 코드

---
# 사용기술
- JAVA11, Spring Boot, JPA, Redis, MySQL, Rabbit-MQ, Elasticsearch, Docker, Locust, junit5
---
# 프로그램 주요 기능
<details><summary>Auctino-server 주요 기능</summary>
- 회원관리
    - 회원가입 기능, 회원 수정, 회원탈퇴
    - 아이디 중복 체크
    - 비밀번호 암호화
    - 로그인, 로그아웃
    - 회원 수정
    - 유저 상태(판매자, 구매자, 관리자, 인증대기)
    - 본인확인 기능(이메일을 통한 인증)
- 상품 관리 및 경매 상태 관리
    - 상품 등록, 조회, 삭제 기능
    - 상품 문의 기능(댓글)
    - 입찰 종료 상품은 자신만 조회
    - 상품 상태관리
        - 상품 등록
        - 경매 시작
        - 경매 종료
        - 배송 중
        - 배송 완료
        - 경매 일시 정지(관리자에 의한)
        - 물품 삭제(관리자에 의한)
- 관리자 기능
    - 이상 유저 제재
    - 카테고리 관리
        - 입찰 단위 설정
    - 공지글 추가
    - 상품 관리
- 상품 검색
    - 상품이름, 판매자, 카테고리로 검색
    - 입찰자가 많은 순으로 정렬
    - 최저, 최고 즉시 구매가 순
    - 최저, 최고 입찰가 순
- 입찰 기능
    - 즉시 구매
        - 즉시 구매가로 입찰시 Toss Payments 결제 요청 API, 승인 API 연동
    - 입찰
        - 최저 입찰 단가
        - 최고 입찰 단가
        - 입찰 단위
        - 입찰 종료시 최고가 Toss Payments 결제 요청 API, 승인 API 연동
        - 유효성 체크
            - 물품id, 입찰자id, 가격 3가지로 이전 요청이 있는지 확인
    - 히스토리 데이터 추가(입찰시간, 입찰자, 입찰가, 판매자, 즉시구매가)
- 알림
    - 시작 및 종료 알림
    - 입찰시 알림
    - 경매 결과 알림
    - 경매 기록(ID, 입찰일자, 입찰가격)
- 경매 이력 조회
    - 자신의 경매 기록 확인
    - 상품의 경매 기록 확인
 - 결제관리
    - 취소 API(Toss Payments 결제 취소 API 연동)
    - 환불 API(Toss Payments 결제 환불 API 연동)
    - 결제 이력 정보 조회 API
    - 유효성 체크
        - 가격, 물품 상태 확인
    
</details>

---
# ERD(Entity Relationship Diagram)
![image](https://github.com/gamsayeon/Auction-Server/assets/75170367/863b5bdf-4e4b-41ef-85d9-4ed6c20e07e1)

---
# 시퀀스
<details><summary>Sequence Diagram</summary>
    
## 입찰 API 시퀀스
![입찰 API 시퀀스](https://github.com/gamsayeon/Auction-Server/assets/75170367/1a019623-1c7d-4125-8343-150457ed317a)

### 구현 설명
- Rabbit-MQ가 비동기적으로 동작하기 때문에 입찰이 언제 성공할지 예측하기 어렵습니다. 따라서, 입찰이 성공한 후에 이를 확인하기 위해 AWS SES를 통해 이메일을 회신하는 것을 선택했습니다. 
이렇게 함으로써, 성공적인 입찰이 이루어진 사실을 사용자에게 자연스럽게 알릴 수 있도록 구성하였습니다.

## 결제 API 시퀀스
![결제API 시퀀스](https://github.com/gamsayeon/Auction-Server/assets/75170367/cf699e07-2bb3-4309-8014-3cf574a0f3b9)

## 검색 API 시퀀스
![검색 API 시퀀스](https://github.com/gamsayeon/Auction-Server/assets/75170367/a7a244e8-f00b-4f47-a924-30c43c9973fe)

### 구현 설명
- Elasitcsearch을 적용하기 전 성능테스트를 통해 현재 성능을 확인 후 Elasticsearch을 도입하였습니다.
- [경매 서버 성능 최적화: 경매 서버의 Elasticsearch 도입](https://gamsayeon.tistory.com/56)

</details>

---

# [성능테스트 계획서](https://lean-pen-0eb.notion.site/b25e5f901d9f4bacae7fb39c1a48fea5?pvs=4)
- 경매 서버의 성능 테스트 계획서를 작성하여 성능 테스트를 진행했습니다. 이번 성능 테스트의 대상은 검색 API, 입찰 API, 경매 조회 API입니다.
- 경매 프로젝트의 주요 API의 성능 테스트 목표는 상품 검색API는 분당 약 300회, 동시사용자는 100명으로 예상하였습니다. 이를 고려하여 해당 API의 목표 TPS는 500TPS로 설정하였습니다. 입찰 조회API는 분당 약 100회, 동시사용자는 100명으로 예상하였으며, 목표 TPS는 160TPS로 설정하였습니다. 입찰은 분당 약 30회의 요청, 동시 사용자는 30명으로 예상하였으며, 목표 TPS는 15TPS을 목표로 설정하였습니다.
- [Auction-Server 성능 테스트: 응답 시간 최적화 방법](https://gamsayeon.tistory.com/65)

---
