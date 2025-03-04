# 바로인턴 9기 온보딩 과제

### 스웨거 링크
- [링크](http://4.221.117.88:8080/swagger-ui/index.html)

### 구현 목록

- [x]  Junit를 이용한 테스트 코드 작성법 이해
- [x]  Spring Security를 이용한 Filter에 대한 이해
- [x]  JWT와 구체적인 알고리즘의 이해
- [x]  PR 날려보기
- [x]  리뷰 바탕으로 개선하기
- [x]  EC2에 배포해보기

**Spring Security 기본 이해**

- [x]  Filter란 무엇인가?(with Interceptor, AOP)
- [x]  Spring Security란?

**JWT 기본 이해**

- [x]  JWT란 무엇인가요?

**토큰 발행과 유효성 확인**

- [x]  Access / Refresh Token 발행과 검증에 관한 **테스트 시나리오** 작성하기

**유닛 테스트 작성**

- [x]  JUnit를 이용한 JWT Unit 테스트 코드 작성해보기

### 백엔드 배포하기

**테스트 완성**

- [x]  백엔드 유닛 테스트 완성하기

**로직 작성**

- [x]  백엔드 로직을 Spring Boot로
- [x]  회원가입 - /signup
    - [x]  Request Message

```json
{
  "username": "JIN HO",
  "password": "12341234",
  "nickname": "Mentos"
}
```

- [x]  Response Message

```json
{
  "username": "JIN HO",
  "nickname": "Mentos",
  "authorities": [
    {
      "authorityName": "ROLE_USER"
    }
  ]
}
```

- [x]  로그인 - /sign
    - [x]  Request Message

```json
{
  "username": "JIN HO",
  "password": "12341234"
}
```

- [x] Response Message

```json
{
  "token": "eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL"
}
  ```

**배포해보기**

- [x]  AWS EC2 에 배포하기

**API 접근과 검증**

- [x]  Swagger UI 로 접속 가능하게 하기

**AI-assisted programming**

- [x]  AI 에게 코드리뷰 받아보기

**Refactoring**

- [x]  피드백 받아서 코드 개선하기

**마무리**

- [x]  AWS EC2 재배포하기

**최종**

- [x]  과제 제출
