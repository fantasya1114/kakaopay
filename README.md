# KAKAOPAY 사전과제

**개발환경**

- Backend
  - Openjdk-14.0.1
  - Spring Boot 2.3.1
  - Maven
  - MariaDB
  - MyBatis
  - log4j


## 1. 문제 해결 전략

### 1. 1. 사전 조건 정의

```
1. 대화방에는 최소 2명 이상의 인원이 존재하며 뿌릴 대상 수는 항상 최대 인원보다 적은수로 요청된다.
2. 사용자의 잔액은 무제한으로 간주하여 잔액 체크는 하지 않는다.
3. 뿌리기 건마다 3자리 문자열의 token을 생성하는데 timestamp 기준 hash 처리했을 때 서로 중복되지 않는다고 가정한다. (다만 3자리는 중복되지 않기에는 부족해보임)
4. 동일한 뿌리기에서 한번만 받을 수 있다.
5. 뿌린 당사자는 받을 수 없다.
6. 같은 방에 위치한 인원들만 받을 수 있다.
7. 뿌린건은 10분만 유효하다.
8. 10분 내, 모두 받아지지 않아 남은 금액에 대해서는 별도의 배치나 프로그램이 처리한다고 가정한다. (이번 과제 범위에서는 미작성)
9. 현재 뿌리기 현황은 뿌린사람만 확인 가능하다.
10. 뿌리기 현황 확인은 7일동안만 가능하다.
```
### 1. 2. 데이터 설계
```
1. 사전 조건에 따른 최소한의 테이블만 작성되도록 분석한다.
2. 뿌리기 이벤트 저장을 위한 EventMaster 테이블을 생성한다.
3. 뿌리기 요청이 오면 뿌린 사람이 지정한 수만큼 금액을 나누어 저장하는 EventDetail 테이블을 생성한다.
4. 방에 있는 사람 및 뿌린 사람 구분을 위하여 방와 사용자를 맵핑할 RoomUser 테이블을 생성한다.
5. 사용자와 방을 관리하는 테이블은 별도로 존재한다고 가정한다.
```
### 1. 3. 테스트 데이터 생성
```
1. 개별 테이블 단위로 단위 테스트 수행이 가능한 테스트 데이터를 생성한다.
2. RoomUser 테이블에는 뿌린지 10분이 지난방 / 모든 뿌리기를 받은 방 / 그 외 단위테스트를 위한 방을 생성한다.
3. 각각의 방에는 뿌리기를 수행한 OwnerUser와 받기를 시도를 TestUser 유저와 이미 받은 WinnerUser 데이터를 생성한다.
4. EventMaster 테이블에는 뿌리기가 이미 진행된 방의 데이터를 생성한다.
5. 10분이 지난 뿌리기 / 모든 뿌리기를 받은 건의 데이터를 생성하며 생성자는 각 방의 OwnerUser로 한다.
6. 10분이 지난 뿌리기를 만들기 위해서 해당 뿌리기의 StartTime을 과거 시간으로 조정한다.
7. 모든 뿌리기를 받은 건의 데이터를 만들기 위해서 StartTime을 29991231로 설정한다.
8. EventDetail 테이블에는 등록된 뿌리기별로 받아갈 수 있는 Reward 데이터를 생성한다.
9. 모든 뿌리기를 받은 건의 데이터는 모든 받기 건이 소진된 것으로 생성한다.
```


## 2. 개발

### 2. 1. API 정의

```
1. 뿌리기 / 받기 / 조회 API를 위한 Controller를 생성한다.
2. 모든 API의 요청/리턴은 json 형태를 사용한다.
3. API별 Method는 뿌리기 - POST, 받기 - PUT, 조회 - GET 으로 정의한다.
```

### 2. 2. 개발 정책

```
1. 모든 요청은 Controller - Service - Mapper (Repository) 객체를 통하여 DB로 쿼리하는 구조를 사용한다.
2. 요청 파라미터에 대한 기본적인 Validation (존재여부, 정합성)은 Controller 에서 처리한다.
3. 비즈니스 요구사항에 따른 Validation 체크는 Service 에서 처리한다.
4. DataBase 접근은 Mapper 객체를 통해서만 수행한다.
5. 쿼리는 사용하기 익숙하고 쿼리 확인이 수월한 MyBatis를 통하여 작성한다.
```

### 2. 3. 패키지 구조

```
1. Layer별 패키지를 생성한다. (controller, service, repo, model)
2. Service의 경우, 임시 과제로 별도 interface 는 정의하지 않는다.
3. test 파일은 프로젝트 생성 후, 자동 생성되는 test 위치에 모두 작성한다.
```
### 2. 4. 개발 전략

```
1. 요구 사항을 분석하여 각각의 API Spec 을 정의한다.
2. Controller - Service - Mapper 파일별로 필요한 메서드를 정의한다.
3. 요구 사항과 맵핑시켜 각 메서드별 테스트 케이스를 정의한다.
4. 서비스 로직을 구현하고, 하나의 요구 사항이 구현될때마다 테스트 케이스를 작성하여 검사한다.
```


## 3. API 정보

### 3. 1. 기본정보

| 구분    | 내용             | 비고                             |
| :------ | :--------------- | :------------------------------- |
| code    | 응답코드         | 200, 400, 500 등                |
| message | 메시지           | 성공 : 성공, 실패 : 실패 사유    |

### 3. 2. API LIST

#### 3.2.1. 뿌리기

> URL : /event
>
> Method : POST
>

#### - Request

#### Header 

| 구분  | Type   | 필수여부 |
| :---- | :----- | :------- |
| X-USER-ID | String | Y        |
| X-ROOM-ID | String | Y        |

#### Parameter 

| 구분  | Type   | 필수여부 |
| :---- | :----- | :------- |
| amount | Number | Y        |
| winner | Number | Y        |

#### - Response

#### Parameter 

| 구분  | Type   | 필수여부 |
| :---- | :----- | :------- |
| token | String | Y        |


#### 3.2.2. 받기

> URL : /event
>
> Method : PUT
>

#### - Request

#### Header 

| 구분  | Type   | 필수여부 |
| :---- | :----- | :------- |
| X-USER-ID | String | Y        |
| X-ROOM-ID | String | Y        |

#### Parameter 

| 구분  | Type    | 필수여부               |
| :--- | :------ | :--------------------- |
| token | String | Y       |

#### - Response

#### Parameter 

| 구분  | Type   | 필수여부 |
| :---- | :----- | :------- |
| reward | Number | Y        |


#### 3.2.3. 조회

> URL : /event
>
> Method : GET

#### - Request

#### Header 

| 구분  | Type   | 필수여부 |
| :---- | :----- | :------- |
| X-USER-ID | String | Y        |
| X-ROOM-ID | String | Y        |

#### QueryString 

| 구분  | Type    | 필수여부               |
| :--- | :------ | :--------------------- |
| token | String | Y       |

#### - Response

#### Parameter 

| 구분  | Type   | 필수여부 |
| :---- | :----- | :------- |
| startTime | String | Y        |
| amount | Number | Y        |
| remain | Number | Y        |
| winners | List | Y        |
| ㄴreward | Number | Y        |
| ㄴuserId | String | Y        |
