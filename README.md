# -DEV 프로젝트 시작시 설정사항

## 설정이유
현재 프로젝트는 민감한 정보를 많이 포함하고 있어서 application.yml 파일을 Git에 푸시하지 않는 상태입니다. 그러나 현재 프로젝트는 Spring Security를 사용한 회원가입과 JavaMail을 의존성으로 추가한 상태입니다. 따라서 아래와 같은 설정이 필요합니다.

## 설정 내용

### Application.yml 설정
-먼저 개발 PC에 SMTP 활성화 해야 사용가능-

**ㅇ네이버 기준**

    mail:
        host: smtp.naver.com
        username: [네이버 아이디]
        password: [네이버 비밀번호]
        properties:
            mail:
                transport:
                    protocol: smtp
                smtp:
                    auth: true
                    starttls:
                        enable: true
                    debug: true
                    ssl:
                        trust: smtp.naver.com
                        enable: true
해당 코드를 yml에 추가

### DB에 회원 정보를 넣고 사용시


회원가입 기능 구현 시 passwordEncoder 통해서 암호화한 뒤에 DB에 저장하는 로직으로 구현해 INSERT INTO SQL을 사용해서 회원 정보를 추가하게 되면 로그인 시 오류 발생 다음과 같은 순으로 회원가입 진행 후 사용!
1. RecipeRadar/Service/impl/JoinEmailServiceImplV1 이동후  message.setFrom([보낼 이메일 주소입력]); //나중에 @Value로 수정!
2. "localhost:8080/api/join/mailConfirm?email=[회원가입시 작성될 이메일]"  post 형식으로 요청 -> Json 형식으로응답 
 ```json 
    { 
   "인증번호": "인증번호"
   }
 ```
3. "localhost:8080/api/singup?code=[전송된 인증번호]"   Body에 작성
```json
   {
      "username": "이름", 
      "nickName": "닉네임",
      "password": "비밀번호",
      "passwordRe": "비밀번호 재입력!@",
      "loginId": "사용할 아이디",
      "email": "회원가입할 이메일 주소"
   }
```
4. 회원가입 성공 후 response Header에 있는 Authorization = Bearer [acces_Token]값을 Requeset 요청 헤더에 담아 전송하면 됩니다.
