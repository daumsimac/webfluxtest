# WebFlux+R2DBC 와 WebMVC+JPA 성능 대결

WebFlux + Spring Data R2DBC(이하 R2DBC)와 WebMVC + Spring Data JPA(이하 JPA) 의 GET API 성능을 비교하였습니다. 관계형 데이터베이스를 사용한 WebFlux 환경에서 기존의 JPA 로 구성한 시스템과 얼마나 성능 개선이 있을까를 알고 싶어서 시작한 작업입니다.
부하발생, 서비스를 모두 개인 랩탑에서 진행하여 운영상의 성능을 검증하기는 힘들지만, 두 구현의 성능 비교는 가능합니다.

성능테스트 결과에서도 볼 수 있듯이 WebFlux+R2DBC로 구현한 시스템이 초당응답수(RPS) 기준으로 WebMVC+JPA 대비 480% 향상되었음을 확인하였습니다.

이제 WebFlux를 상용화에 적용할 시기가 다가오고 있습니다.

## 성능테스트

#### 성능테스트 시나리오
* 가상 사용자 1000 명을 투입하였습니다.
* 가상 사용자는 /v1/students 를 1밀리세컨드만 Think Time 으로 가지고 100 번을 반복 호출합니다.
* Database 에 3Rows 가 등록되어 있습니다.
* 총 100K 번의 호출이 발생합니다.

#### 성능테스트 결과(Global Information)
* WebFlux+R2DBC
  * 100,000회의 호출이 모두 0.8초 이하로 응답하고 있습니다.
    ![Perf01](https://user-images.githubusercontent.com/2074496/186589796-6f4f3c62-929a-4ca2-854c-b8ed1ad00dd9.PNG)

* WebMVC+JPA
  * 100,000회의 호출 중 5만번 이상이 1.2초를 초과한 응답시간을 가지고 있습니다.
    ![Perf02](https://user-images.githubusercontent.com/2074496/186590079-4ca099cb-3c85-473b-b794-52977e36b156.PNG)

#### 응답시간분포
응답시간 분포는 큰 차이를 보입니다. WebFlux 와 R2DBC의 조합은 성능의 이점을 크게 보입니다.

* WebFlux+R2DBC
  ![Perf03](https://user-images.githubusercontent.com/2074496/186590417-1f293dda-2aaf-4e11-99fc-e3c8c21150b7.PNG)

* WebMVC+JPA
  ![Perf04](https://user-images.githubusercontent.com/2074496/186590631-2f03b500-e818-4ddc-b1db-cb1877fe3cf4.PNG)


#### 초당 응답수(RPS)
* WebFlux+R2DBC
  * 초당 평균 3700 번의 응답을 하였습니다.
    ![Perf05](https://user-images.githubusercontent.com/2074496/186591096-4e857130-dddf-4ae1-a2c9-deff9ee58a73.PNG)

* WebMVC+JPA
  * 초당 평균 770 번의 응답을 하였습니다.
    ![Perf06](https://user-images.githubusercontent.com/2074496/186591177-95342902-94b3-4f2b-92f7-10ca78aaff81.PNG)

> 소스코드링크
* https://github.com/zbum/non-r2dbc-demo.git
* https://github.com/zbum/r2dbc-demo.git