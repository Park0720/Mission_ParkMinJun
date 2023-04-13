# Title: [2Week] 박민준

---
## 미션 요구사항 분석 & 체크리스트

---
### 필수미션 - 호감표시 시 예외처리 케이스 3가지 처리

---
### 목표

---
- [x] 케이스 4 : 한명의 인스타회원이 다른 인스타회원에게 중복으로 호감표시를 할 수 없습니다.
  1. 예를 들어 본인의 인스타ID가 aaaa, 상대의 인스타ID가 bbbb 라고 하자.
  1. aaaa 는 bbbb 에게 호감을 표시한다.(사유 : 외모)
  1. 잠시 후 aaaa 는 bbbb 에게 다시 호감을 표시한다.(사유 : 외모)
  1. 이 경우에는 처리되면 안된다.(rq.historyBack)

- [x] 케이스 5 : 한명의 인스타회원이 11명 이상의 호감상대를 등록 할 수 없습니다.
  1. 예를 들어 본인의 인스타ID가 aaaa 라고 하자.
  1. aaaa 는 bbbb, cccc, dddd, eeee, ffff, gggg, hhhh, iiii, jjjj, kkkk 에 대해서 호감표시를 했다.(사유는 상관없음, aaaa는 현재까지 10명에게 호감표시를 했음)
  1. 잠시 후 aaaa 는 llll 에게 호감표시를 한다.(사유는 상관없음)
  1. 이 경우에는 처리되면 안된다.(rq.historyBack)

- [x] 케이스 6 : 케이스 4 가 발생했을 때 기존의 사유와 다른 사유로 호감을 표시하는 경우에는 성공으로 처리한다.
  1. 예를 들어 본인의 인스타ID가 aaaa, 상대의 인스타ID가 bbbb 라고 하자.
  1. aaaa 는 bbbb 에게 호감을 표시한다.(사유 : 외모)
  1. 잠시 후 aaaa 는 bbbb 에게 다시 호감을 표시한다.(사유 : 성격)
  1. 이 경우에는 새 호감상대로 등록되지 않고, 기존 호감표시에서 사유만 수정된다.
  - [x] 외모 => 성격
  - [x] resultCode=S-2
  - [x] msg=bbbb 에 대한 호감사유를 외모에서 성격으로 변경합니다.

### 선택미션 - 네이버 로그인

---
### 목표

---
- [x] 네이버 로그인 창 구현
- [x] 카카오 로그인이 가능한것 처럼, 네이버 로그인으로도 가입 및 로그인 처리가 되도록 해주세요.
  - [x] 스프링 OAuth2 클라이언트로 구현해주세요.
- [x] 네이버 로그인으로 가입한 회원의 providerTypeCode : NAVER


## 2주차 미션 요약

---

### 접근 방법

---
**호감표시 시 예외처리 케이스 3가지 처리**

---
- **케이스 5 : 한명의 인스타회원이 11명 이상의 호감상대를 등록 할 수 없습니다.**
  - 호감등록한 사람이 10명인 상태로 진행하려고 ```NotProd```에 insta_user3이 호감표시한 사람 10명으로 증가 시킴
  - ```member.getInstaMember().getFromLikeablePeople().size()==10```일 경우 F-3 실패코드 반환
- **케이스 4 : 한명의 인스타회원이 다른 인스타회원에게 중복으로 호감표시를 할 수 없습니다.**
  - ```LikeablePersonService```의 ```like```에서 중복체크 하려고 시도
  - ```InstaMember fromInstaMember = member.getInstaMember();```
  , ```InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();``` 이 두 데이터로 기존에 가지고 있는 호감표시 체크 하려고 했으나
  프로그램 실행 자체가 안됨
  - 아마 ```NotProd``` 생성 시에 오류가 발생해서 안되는 것 같음
  - 강사님 테스트 코드 보며 기존에 있는 호감표시를 어떻게 가져올 지 생각함
  - ```LikeablePersonRepository```에서 ```findByFromInstaMemberIdAndToInstaMemberId```로 ```LikeablePerson``` 가져온 뒤 ```add```시도 할 때 체크해서 같은 값이 있으면 F-4 실패코드 반환
  - 처음 생각했던 방법이랑 구현한 방식이 비슷한데 ```if(checkLikeablePerson != null)```이 부분에서 성공 실패 여부가 갈린 거 같음
    - 처음 하려고 했던 코드에서 저 부분이 없었을 때 프로그램 구동 자체가 안됨
    - 아마 저장하기 전에 찾아서 쓰려고 해서 오류가 난 것 같음
- **케이스 6 : 케이스 4 가 발생했을 때 기존의 사유와 다른 사유로 호감을 표시하는 경우에는 성공으로 처리한다.**
  - 기존 호감사유와 새로 등록하려는 호감사유를 체크해서 다르면 케이스 6으로 빠지도록 처리
  - ```LikeablePerson```에서 ```@Setter```추가 후 ```modifyDate``` 수정 및 ```attractiveTypeCode``` 수정
  - 메세지 출력과정에서 기존 호감사유를 저장해놓으려고 ```checkLikeablePersonAttractiveTypeCodeName``` 생성 후 저장
  - ```fromInstaMemberId```와 ```toInstaMemberId```가 기존에 있던 ```LikeablePerson``` 데이터와 일치하는 항목이 있고, 호감사유가 기존과 다르면 S-2 성공코드 반환
---
**네이버 로그인**

---
- 기존에 네이버 로그인 기능 구현 완료, 아이디 노출 형식만 처리
  - ```NAVER__{id: ~~~, name: ~~~, email: ~~~}``` 으로 뜨던 걸 ```oauth2.yml``` 파일의 ```scope```에서 유저네임과 이메일을 받아오지 않는 걸로 변경
    - 지금 당장 그램그램 서비스에서는 회원정보가 딱히 필요 없어 보여서 개인정보를 아무것도 받지 않는 걸로 처리
- 변경 후 ```NAVER__{id: ~~~}``` 으로 뜨는 건 ```CustomOAuth2UserService.java```에서 처리
- ```NAVER__~~~``` 으로 뜨게 처리 완료
---
### 특이사항

---
**네이버 로그인**

---
- ```oauth2.yml``` 파일에 네이버 로그인 구현 시```client-authentication-method: POST```로 두면 로그인 에러 발생
- ```client-authentication-method: POST``` 해당 부분을 지워서 로그인 성공하긴 했는데 이유는 모르겠음
- 후에 네이버 로그인 시 개인정보가 필요하다면 받아오는 걸로 변경 후 ID 포맷 변경 필요해보임
---
**호감표시 시 예외처리 케이스 3가지 처리**

---
- ```LikeablePersonRepository```에서 값을 가져올 때 내가 구현한 방법으로 처리해도 되는 지는 모르겠음
- 일단 ```LikeablePerson```에서 ```fromInstaMemberUsername```, ```toInstaMemberUsername```이 없어도 프로그램 정상 구동
---
### Refactoring

---
- [x] ```LikeablePersonService```에서 ```RsData<LikeablePerson>``` -> ```RsData```로 변경하기
- [x] ```LikeablePersonService```의 ```deleteLikeablePerson```에서 삭제할 유저 ```likeablePerson.getToInstaMemberUsername()``` -> ```likeablePerson.getToInstaMember().getUsername()``` 로 변경하기
---
**코드 리뷰 후**
- [x] ```like``` 메서드에서 기능을 다 구현하지 않고 기능 분산시키기
