프로젝트 환경
---
* SpringBoot 2.3.7
* java 1.8
* MySQL

<br>

Job의 흐름
---
* 변경할 고객 정보 불러오기 (CustomerUpdate)
* 거래 정보 불러오기 (Transaction)
* 거래 정보 반영하기 (Transaction)
* 거래명세서 생성하기 (Statement)

<br>

배치 돌리기 전 사전 작업
---
Spring Batch 테이블들과 같은 데이터베이스에 테이블 생성

* src/main/resources 폴더 내 schema-mysql.sql 실행
* src/main/resources/data 폴더 내 account.sql, customer.sql, customer_account.sql 실행
