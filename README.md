# 🍀 요리 공유소
<img width="1875" alt="스크린샷 2024-07-18 오후 3 03 12" src="https://github.com/user-attachments/assets/e9f510e5-db02-4858-9742-de17f3209728">
<br/>

## 📝 배포 주소
> 레시피 공유소: [레시피 공유소 바로가기](https://www.recipe.n-e.kr/)<br>
> API 명세서 : [Swagger 바로가기](https://www.recipe.n-e.kr:8443/swagger-ui/index.html)<br>
> 레시피 공유소 노션 : [노션 바로가기](https://indecisive-vertebra-5c0.notion.site/RecipeRadar-a4e3107b5a334bebad464e62b9096af0?pvs=4)<br>
> ERD : [ERD 바로가기](https://www.erdcloud.com/d/GFP2dNgLyDHczbpxx)<br>
>
## 🚩 프로젝트 간략소개

### 1️⃣ 냉장고 속 재료로 만드는 요리 레시피 공유 서비스

- 요리 공유소는 집 냉장고에 있는 재료를 활용해 만들 수 있는 요리 레시피를 찾아 요리하고, 그 경험을 다른 사용자들과 공유할 수 있는 플랫폼입니다. 사용자는 자신의 냉장고에 있는 재료들을 입력하면, 해당 재료로 만들 수 있는 다양한 요리 레시피를 검색할 수 있습니다. 이를 통해 새로운 요리에 도전하고, 요리의 재미를 느낄 수 있습니다.


### 2️⃣ 주요기능

- **재료 기반 레시피 검색:** 사용자가 입력한 재료를 바탕으로 적합한 요리 레시피를 제안합니다.
- **레시피 선택 및 요리:** 사용자는 제안된 레시피 중 하나를 선택하여 요리를 만들 수 있습니다.
- **후기 작성 및 공유:** 요리를 완료한 후, 사용자는 요리 후기를 작성하여 다른 사용자들과 경험을 공유할 수 있습니다.
- **다양한 요리 팁 제공:** 각 레시피에는 요리를 더 맛있게 만들기 위한 팁과 노하우가 포함되어 있어, 요리 초보자도 쉽게 따라할 수 있습니다.


### 3️⃣ 기대효과

- 이 서비스는 단순한 레시피 제공을 넘어, 사용자 간의 소통과 공유를 통해 요리에 대한 흥미를 지속적으로 유발시키는 것을 목표로 합니다.
- 다양한 재료와 요리법을 통해 사용자들이 새로운 맛을 발견하고, 요리의 즐거움을 느낄 수 있도록 도와줍니다.

### 4️⃣ 프로젝트 기간

> 2024.03.15 ~ 2024.07.17

## 👥 팀원 소개
<div align="center">

| **김민우** | **박상호** | **양승헌** |
|:---------:|:---------:|:---------:|
| [<img src="https://github.com/user-attachments/assets/5d57c81c-6daf-43c8-bb84-7c611ba5d207" height=150 width=150> <br/> @CHISANW](https://github.com/CHISANW) | [<img src="https://avatars.githubusercontent.com/u/97100045?v=4" height=150 width=150> <br/> @hopak-e](https://github.com/hopak-e) | [<img src="https://avatars.githubusercontent.com/u/113596008?v=4" height=150 width=150> <br/> @roel127](https://github.com/roel127) |

</div>
<br>

## 🖥 구현 이미지
[//]: # (레시피 검색)
<details>
  <summary>재료와 카테고리를 통해 레시피 찾기</summary>
  <ul>
    <h3> ※ 카테고리 검색</h3>
    <img alt="카테고리" src="https://github.com/user-attachments/assets/3194de1e-1c50-4834-8157-425b550f6859" width="500" height="500"/>
    <p> - 카테고리를 통해 레시피를 검색할 수 있습니다.</p>
    <hr>
    <h3> ※ 재료 검색</h3>
    <img alt="재료검색" src="https://github.com/user-attachments/assets/ba1ee9a7-3314-4ef5-9f40-aa71b02f18a1" width="500" height="500"/>
    <p> - 재료를 통해 레시피를 검색할 수 있습니다.</p><hr>
  </ul>
</details>

[//]: # (회원가입)
<details>
  <summary>회원 가입</summary>
    <ul>
        <img alt="회원가입" height="500" src="https://github.com/user-attachments/assets/59678de3-0460-4a56-b9ac-9455b9434044" width="800"/>
        <p> - 회원가입 시 이메일 인증을 통해 회원가입이 가능하며, 중복된 아이디, 이메일, 닉네임이므로는 가입이 불가능합니다. </p><hr>
    </ul>
</details>

[//]: # (로그인 및 로그아웃)
<details>
  <summary>로그인 및 로그아웃</summary>
    <div> ○ 로그인시 AccessToken은 로컬 스토리지를 통해서 저장되며 AccessToken은 쿠키의 저장됩니다.</div>
  <ul>
    <h3> ※ 일반 로그인</h3>
    <img alt="일반 로그인" height="500" src="https://github.com/user-attachments/assets/82e31485-e8a3-42d3-bc13-3da1b0c5e445" width="800"/>
    <p> - 사이트에 회원가입한 아이디를 통해 로그인 가능합니다.</p>
    <hr>
    <h3> ※ 소셜 로그인(구글, 카카오, 네이버)</h3>
    ※ 카카오 로그인 기준 ※<br>
    <img alt="소셜카카오 로그인" height="500" src="https://github.com/user-attachments/assets/33199657-d6e7-4cf2-b8cb-6cd762fbcb0c" width="800"/>
    <p> - 소셜 로그인을 통해 로그인 시 최초 1회 자동으로 회원가입 후 로그인이 됩니다.</p><hr>
    <h3> ※ 로그아웃</h3>
    <img alt="로그아웃" height="500" src="https://github.com/user-attachments/assets/9b7314f4-3f27-4b08-88bb-c10519464cfd" width="800"/>
    <p> - 로그아웃시 JWT 토큰과 쿠키를 만료</p>
  </ul>
</details>

[//]: # (아이디/비밀번호 찾기)
<details>
  <summary>아이디/ 비밀번호 찾기</summary>
    <ul>
        <h3> ※ 아이디 찾기</h3>
        <img alt="아이디 찾기" height="500" src="https://github.com/user-attachments/assets/fd9c926c-a457-452d-a8c3-1efff3eecd8c" width="800"/>
        <p> - 회원가입 시 입력한 실명, 이메일을 통해 인증 후 아이디를 반환합니다</p><hr>
        <h3> ※ 비밀번호 찾기</h3>
        <img alt="비밀번호 찾기" height="500" src="https://github.com/user-attachments/assets/df4acf5b-1412-4c39-a70d-56f5358037e0" width="800"/>
        <p> - 회원가입 시 입력한 실명, 로그인 아이디, 이메일을 통해 인증 후 해당 회원이 존재 시 일정 시간 쿠키 발급 후 비밀번호 변경(변경 시 쿠키 삭제)</p><hr>
    </ul>
</details>

[//]: # (실시간 알림)
<details>
  <summary>실시간 알림</summary>
  <ul>
    <h3> ※ 사용자 알림</h3>
    <img alt="사용자알림" height="500" src="https://github.com/user-attachments/assets/e09aa577-7aca-4a3c-a580-956a8434392a" width="800"/>
    <p> - 게시글 댓글, 좋아요 사용 시 작성자에게 알림이 전송, 댓글, 좋아요 해제 시 알림 내역 삭제</p>
    <h3> ※ 관리자 알림</h3>
    <img alt="관리자알림" height="500" src="https://github.com/user-attachments/assets/1dac9933-655d-4e9a-b137-894fde26246d" width="800"/>
    <p> - 관리자는 사용자가 문의사항 등록 시 관리자에게 문의사항 등록 알림 발송됩니다.</p>
    <h3> ※ 알림 내역</h3>
    <img alt="알림" height="500" src="https://github.com/user-attachments/assets/540097ba-c8a4-44fe-b840-9f3c11032eaf" width="800"/>
    <p> - 메인 페이지에서는 7개의 알림을 표시합니다.</p>
    <p> - 전체 보기 클릭 시 모든 알림 조회</p>
    <p> - 삭제를 통해 원하는 알림 내역을 삭제 가능합니다.</p>
    <hr>
  </ul>
</details>

[//]: # (관리자 페이지)
<details>
  <summary>관리자 페이지</summary>
    <H3>  모든 어드민 기능에서 페이지네이션 방식은 무한 스크롤 방식을 사용</H3>
  <ul>
    <h3> ※ 대시 보드</h3>
    <img alt="통계페이지" height="500" src="https://github.com/user-attachments/assets/2697f2dc-b11f-4fc2-bad1-515ee4d2b440" width="800"/>
    <p> - 현 사이트에 작성된 레시피 수, 게시글 수, 유저 수, 방문자 수, 통계표를 볼 수 있는 페이지입니다.</p>
    <hr>
    <h3> ※ 사용자 관리</h3>
    <img alt="사용자 추방_블랙리스트" height="500" src="https://github.com/user-attachments/assets/b41ea817-fb89-4167-9fc2-1b0c5824f177" width="800"/>
    <p> - 관리자는 사용자를 강제로 추방할 수 있습니다. 추방 시 가입한 이메일로 추방 안내 메일이 발송되게 되며, 추후 해당 이메일은 블랙리스트 등록되며 관리자가 해제하기 전까지 추방된 메일로는 회원가입이 불가합니다.</p>
    <hr>
    <h3> ※ 문의사항 등록 알림</h3>
    <img alt="관리자알림" height="500" src="https://github.com/user-attachments/assets/1dac9933-655d-4e9a-b137-894fde26246d" width="800"/>
    <p> - 문의사항 등록 시 관리자에게 문의사항 알림이 전송됩니다.</p>
    <hr>
    <h3> ※ 게시글 관리</h3>
    <img alt="게시글관리" height="500" src="https://github.com/user-attachments/assets/1707b55e-1cab-4ef3-905f-b1db5b1ea2fd" width="800"/>
    <p> - 게시글을 모두 조회할 수 있으며, 해당 게시글의 작성된 댓글을 모두 볼 수 있으며 댓글과 게시글에 대해 삭제 가능합니다.</p>
    <hr>
    <h3> ※ 레시피 관리</h3>
    <h4>레시피 등록</h4>
    <img alt="레시피등록" height="500" src="https://github.com/user-attachments/assets/98e85801-e348-4d65-9314-cdeea8b7be4c" width="800"/>
    <p> - 관리자는 레시피의 대해서 새롭게 등록이 가능합니다.</p>
    <br>
    <h4> ※ 레시피 수정</h4>
    <img alt="레시피수정" height="500" src="https://github.com/user-attachments/assets/f9fa4ebe-71c5-47f6-aa4f-2a9d132d2109" width="800"/>
    <p> - 관리자는 레시피에 대해서 수정이 가능하며, 조리 순서, 재료 등을 추가 가능합니다.</p>
    <hr>
    <h3> ※ 공지사항 관리</h3>
    <p> - 공지사항에 대해서 수정, 삭제, 등록이 가능합니다(이전과 동작 방식은 동일하여 참고 영상 생략)</p>
    <hr>
    <h3> ※ 문의사항 답변 </h3>
    <img alt="문의 사항답변" height="500" src="https://github.com/user-attachments/assets/1ea84162-3875-42d4-a0f5-7157a1ba6a0c" width="800"/>
    <p> - 관리자는 문의사항에 대해서 답변이 가능합니다. 문의사항 답변 등록 시 사용자에게 알림이 가며, 이메일 받기 체크 시 이메일로도 답변 작성 알림이 전송됩니다.</p>
    <hr>
    <h3> ※ 블랙리스트 관리 </h3>
    <p> - 관리자가 추방 시 블랙리스트의 추방한 이메일에 대해서 등록이 되며, 해당 이메일에 대해 차단해 제 여부를 선택합니다.(해제 기능은 삭제와 동일하여 사진 생략)</p>
  </ul>
</details>

[//]: # (문의사항 페이지)
<details>
  <summary>문의 하기</summary>
    <li>로그인한 사용자 문의와, 비로그인한 사용자 문의가 존재(로그인 유뮤 차이)</li>
    <h3> ※ 문의 사항 등록하기</h3>
        <img alt="문의 사항 작성" height="500" src="https://github.com/user-attachments/assets/2e210639-6a1e-4cee-9416-26201cdf0a2b" width="800"/>
    <p> - 문의사항에 대해서 질문 등록이 가능합니다. 이메일 알림 체크 시 해당 이메일로 답변 등록 시 알림 전송</p>
    <br>
    <h3> ※ 문의 사항 답변 등록시(이메일 알림 받기 선택시) </h3>
    <img alt="문의 답변 등록시" height="500" src="https://github.com/user-attachments/assets/c8a7d18b-2418-4b13-89d2-98e0ff3ee124" width="800"/>
    <p> - 관리자가 답변에 대해 등록이 되었을 때 답변 작성 알림이 전송되며, 이메일 알림 체크 시 이메일로 답변 알림 전송됩니다.</p>
    <hr>
</details>

[//]: # (사용자 페이지)
<details>
  <summary> 마이페이지 </summary>
    <h3> ※ 마이 페이지 최초 접근시</h3>
    <div> ■ 일반 로그인 사용자</div>
    <img alt="마이페이지 최초 접근 쿠키발급" height="500" src="https://github.com/user-attachments/assets/8a2ee338-caf8-485f-9875-fe343ca385c0" width="800"/>
    <div><br></div>
    <div> ■ 소셜로그인사용자</div>
    <img alt="소셜로그인 사용자 접근" height="500" src="https://github.com/user-attachments/assets/214b7d57-ad05-492a-a080-a79ea33b107a" width="800"/>
    <p> - 마이페이지 최초 접근 시 비밀번호 검증을 통해 마이페이지 접근 가능.</p>
    <p> - 비밀번호 검증 성공 시 20분간 채 접근이 가능한 쿠키 발급(소셜 로그인 사용자는 비밀번호 검증을 하지 않습니다.)</p>
    <hr>
    <h3> ※ 닉네임 변경</h3>
    <img alt="닉네임변경" height="500" src="https://github.com/user-attachments/assets/47f38d1a-cd21-4997-8bf9-ecf31ca8a9f2" width="800"/>
    <p> - 일반 로그인, 소셜 로그인 사용자는 닉네임 변경이 가능합니다.</p>
    <h3> ※ 비밀번호 변경</h3>
    <img alt="비밀번호 변경" height="500" src="https://github.com/user-attachments/assets/5abfb4b5-7d36-462c-af64-aefca44c6fd2" width="800"/>
    <p> - 일반 로그인 사용자만 변경이 가능 합니다.</p>
    <hr>
    <h3> ※ 이메일 변경</h3>
    <img alt="이메일변경" height="500" src="https://github.com/user-attachments/assets/cca7b0e0-f99a-4ab7-b8c4-7a775448798f" width="800"/>
    <p> - 이메일 변경은 일반 로그인 사용자만 가능하며 소셜 로그인 사용자는 불가합니다.</p>
    <hr>
    <h3> ※ 활동 내역 </h3>
    <h4> ■ 작성한 글 </h4>
    <img alt="작성게시글" height="500" src="https://github.com/user-attachments/assets/93e50f0b-b2b8-4655-bd27-66a1b286eb29" width="800"/>
    <p> - 작성한 게시글을 볼 수 있으며 수정 삭제 가능합니다.</p>
    <h4> ■ 좋아요/즐겨찾기 내역</h4>
    <p> - 게시글, 레시피의 좋아요 즐겨찾기 내역을 수정, 삭제, 조회 가능합니다.</p>
    <div> ■ 게시글 좋아요</div>
    <img alt="게시글 좋아요" height="500" src="https://github.com/user-attachments/assets/4c34a02b-2802-4202-a704-350d12153ab0" width="800"/>
    <div> ■ 레시피 좋아요</div>
    <img alt="레시피 좋아요" height="500" src="https://github.com/user-attachments/assets/7fa172af-3cb0-4b5a-910e-3a127f6d650c" width="800"/>
    <div> ■ 레시피 즐겨찾기</div>
    <img alt="즐겨찾기" height="500" src="https://github.com/user-attachments/assets/77c0f163-56f4-491c-ad45-f911f5909edc" width="800"/>
    <div> ■ 문의사항 내역</div>
    <img alt="문의사항" height="500" src="https://github.com/user-attachments/assets/16f83910-f374-4e7a-bc8a-41986d8d5342" width="800"/>
    <hr>
    <h3>회원 탈퇴</h3>
    <h4> ※ 사용자 탈퇴(소셜 로그인)</h4>
    <img alt="소셜로그인탈퇴" height="500" src="https://github.com/user-attachments/assets/780558cb-016d-4f3b-b8bb-383c937d4355" width="800"/>
    <p> - 일반 로그인 사용자는 소셜 로그인가 동일 동작, 회원 탈퇴 시 사용자와 관련된 데이터 모두 삭제됩니다.</p>
    <hr>
</details>

[//]: # (요리 게시글)
<details>
  <summary>요리 게시글</summary>
    <h3> ※ 게시글 등록</h3>
    <h4> ■ 레시피에서 게시글 등록</h4>
    <img alt="레시피에서 게시글 바로등록" height="500" src="https://github.com/user-attachments/assets/a236c0af-d020-4829-9314-7e354643d8c5" width="800"/>
    <p> - 레시피 상세 페이지에서 해당 레시피의 게시글을 바로 작성가능합니다.</p>
    <h4> ■ 레시피 검색후 게시글 등록</h4>
    <img alt="게시글따로 등록" height="500" src="https://github.com/user-attachments/assets/157a84dd-e44a-45af-a58e-a544dd40dc71" width="800"/>
    <p> - 게시글 등록시 게시글명을 통해 검색후 선택한 레시피의 대해서 게시글이 등록가능합니다.</p>
    <h3> ※ 게시글 수정,삭제</h3>
    <p> ■ 비작성자 삭제시</p>
    <img alt="게시글 삭제시 작성자가 아닐시" height="500" src="https://github.com/user-attachments/assets/0294f313-bc33-409e-9627-12e1e968c217" width="800"/>
    <p> - 작성자만 게시글 수정, 삭제 가능하며 게시글 등록시 등록했던 비밀번호를 통해서 사용자 검증</p>
    <h3> ※ 게시글 좋아요</h3>
    <img alt="게시글 좋아요" height="500" src="https://github.com/user-attachments/assets/8d53e277-a9f9-4e55-9f24-a902799d09bd" width="800"/>
    <p> - 사용자는 게시글에 대해서 좋아요 기능을 사용 가능합니다.</p>
    <h3> ※ 게시글 댓글</h3>
    <img alt="댓글" height="500" src="https://github.com/user-attachments/assets/62d5c360-2243-446d-842a-89781d6afcef" width="800"/>
    <p> - 게시글의 댓글을 작성 가능합니다.</p>
    <hr>
</details>

## 🔧 기술 스택
<h3 align="center"> 개발 / 테스트 </h3>

<p align="center">
<img src="https://img.shields.io/badge/Java 17-008FC7?style=flat-square&logo=Java&logoColor=white"/></img>
<img src="https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=Gradle&logoColor=white"/></img>
<img src="https://img.shields.io/badge/Spring-58CC02?style=flat-square&logo=Spring&logoColor=white"/></img>
<img src="https://img.shields.io/badge/Spring Boot 2.7.18-6DB33F?style=flat-square&logo=Spring Boot&logoColor=white"/></img>
<img src="https://img.shields.io/badge/Spring Data JPA-ECD53F?style=flat-square&logo=JPA&logoColor=white"/></img>
<img src="https://img.shields.io/badge/Query_DSL-0769AD?style=flat-square&logo=querydsl&logoColor=white"/></img>
<img src="https://img.shields.io/badge/JUnit5-25A162?style=flat-square&logo=JUnit5&logoColor=white"/></img>
</p>

<p align="center">

</p>

<h3 align="center"> 보안 / 모니터링 </h3>
<p align="center">
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat-square&logo=Spring Security&logoColor=white"/></img
<img src="https://img.shields.io/badge/OAuth2.0-EB5424?style=flat-square&logo=querydsl&logoColor=white"/></img>
<img src="https://img.shields.io/badge/JWT-6DB33F?style=flat-square&logo=JsonWebTokens&logoColor=white"/></img
<img src="https://img.shields.io/badge/Prometheus-E6522C?style=flat-square&logo=prometheus&logoColor=white"/></img>
<img src="https://img.shields.io/badge/Grafana-F46800?style=flat-square&logo=grafana&logoColor=white"/></img>

</p>

<h3 align="center"> DB </h3>

<p align="center">
<img src="https://img.shields.io/badge/MySQL 8.0-4479A1?style=flat-square&logo=MySQL&logoColor=white"/></img>
<img src="https://img.shields.io/badge/H2-008FC7?style=flat-square&logo=Java&logoColor=white"/></img>
</p>

<h3 align="center"> Infra </h3>

<p align="center">

<img src="https://img.shields.io/badge/jenkins-D24939?style=flat-square&logo=jenkins&logoColor=white"/>
<img src="https://img.shields.io/badge/Nginx-009639?style=flat-square&logo=nginx&logoColor=white"/>
<img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white"/>
<img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=flat-square&logo=Amazon EC2&logoColor=white"/>
<img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=flat-square&logo=Amazon RDS&logoColor=white"/>
<img src="https://img.shields.io/badge/Amazon S3-569A31?style=flat-square&logo=Amazon S3&logoColor=white"/>
<img src="https://img.shields.io/badge/Amazon Route 53-00A98F?style=flat-square&logo=amazonroute53&logoColor=white"/>
<img src="https://img.shields.io/badge/Amazon Cloud Front-00A98F?style=flat-square&logo=amazoncloudwatch&logoColor=white"/>

</p>

<h3 align="center"> 문서 / 협업 / 툴</h3>

<p align="center">
<img src="https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=Swagger&logoColor=white"/>
<img src="https://img.shields.io/badge/Notion-000000?style=flat-square&logo=Notion&logoColor=white"/>

<img src="https://img.shields.io/badge/Git-F05032.svg?style=flat-square&logo=Git&logoColor=white"/>
<img src="https://img.shields.io/badge/GitHub-181717.svg?style=flat-square&logo=GitHub&logoColor=white"/>
<img src="https://img.shields.io/badge/Postman-FF6C37.svg?style=flat-square&logo=Postman&logoColor=white"/>
<img src="https://img.shields.io/badge/Gather-0043CE.svg?style=flat-square&logo=Gather&logoColor=white"/>

</p>

<br/><br/>
## 📟 AWS 아키텍쳐

<img alt="aws 서버 구성도.PNG" src="https://d2d8tf2n841pdt.cloudfront.net/RecipeReadme/aws+%EC%84%9C%EB%B2%84+%EA%B5%AC%EC%84%B1%EB%8F%84.PNG" width="700"/>

## 🌞 CI/CD
<img alt="aws 서버 구성도.PNG" src="https://d2d8tf2n841pdt.cloudfront.net/RecipeReadme/%EC%BA%A1%EC%B2%98.PNG" width="1000" height=""/>

## 💾 ERD
<img alt="RecipeReader.png" src="https://d2d8tf2n841pdt.cloudfront.net/RecipeReadme/RecipeReader.png" width="700"/>


## 🎯 트러블 슈팅
> - [소셜로그인 문제](https://indecisive-vertebra-5c0.notion.site/73de8568a7ba4512a1e824b1dcdd0322?pvs=4)<br>
> - [쿠키 SameSite 문제](https://indecisive-vertebra-5c0.notion.site/SameSite-d0788f80e3e943af84d012aa867b8366?pvs=4)<br>
> - [Lombok사용지 @Qualifier문제](https://indecisive-vertebra-5c0.notion.site/Lombok-Qualifier-088c249870214b1b9d27c92150b3347f?pvs=4)<br>
> - [Maria-DB FullText With N-Gram 문제](https://indecisive-vertebra-5c0.notion.site/Maria-DB-FullText-With-N-Gram-bd969f40af5e4a9cb60a50191f83f18d?pvs=4)<br>
> - [QueryDSL - match().against() 사용자 정의 함수 적용시 테스트 실행시 실패 문제](https://indecisive-vertebra-5c0.notion.site/QueryDSL-match-against-6f1c47efcbe54f3382a55a8029676f0b?pvs=4)<br>
> - [디렉터리 구조의 문제](https://indecisive-vertebra-5c0.notion.site/1a0367a85a7943f1a368f7b8b6b3d1e1?pvs=4)<br>
> - [데이터 RDS로 이관시 문제](https://indecisive-vertebra-5c0.notion.site/RDS-8e433f97a13743b3a398ec3b8a03eebc?pvs=4)<br>
> - [이미지 로드 속도 문제](https://indecisive-vertebra-5c0.notion.site/ff6345b13c624507adfe225d94a5202f?pvs=4)<br>
> - [SpringSecurity 예외 처리 문제](https://indecisive-vertebra-5c0.notion.site/SpringSecurity-aabd3ec2d7d643798687cc5b5f51c7b7?pvs=4)<br>
> - [AOP 적용후 테스트 문제](https://indecisive-vertebra-5c0.notion.site/AOP-fe43174010494fc89db820aedcc73e1a?pvs=4)<br>
> - [Jenkins 공간 부족 문제](https://indecisive-vertebra-5c0.notion.site/Jenkins-8b8662b275264037a46aa5c3b1e97ce3?pvs=4)<br>

## 📈 성능 개선
> - [메인 페이지 좋아요순 8개 조회(60% 성능향상)](https://indecisive-vertebra-5c0.notion.site/bdb2c3fa4f1c4430a8b80e981449aaa9?pvs=4)
> - [로그인 (57.01% 성능향상)](https://indecisive-vertebra-5c0.notion.site/bdb2c3fa4f1c4430a8b80e981449aaa9?pvs=4)
> - [게시글 검색 (92.4% 성능향상)](https://indecisive-vertebra-5c0.notion.site/bdb2c3fa4f1c4430a8b80e981449aaa9?pvs=4)
> - [사용자 검색 (93.62% 성능향상)](https://indecisive-vertebra-5c0.notion.site/bdb2c3fa4f1c4430a8b80e981449aaa9?pvs=4)


## 📦 디렉토리 구조
<details>
<summary>디렉토리 구조</summary>
<div markdown="1"></div>
<pre>
com
└─ team
   └─ RecipeRadar
      ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain">domain</a>   // 도메인 관련 디렉터리
      │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/account">account</a>  // 계정 찾기
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/account/api">api</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/account/application">application</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/account/dao">dao</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/account/domain">domain</a>
      │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/account/dto/request">dto</a>
      │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/blackList"> blackList</a>  // 블랙리스트
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/blackList/api">api</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/blackList/application">application</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/blackList/dao">dao</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/blackList/domain">domain</a>
      │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/blackList/dto">dto</a>
      │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/bookmark">bookmark</a> // 레시피 즐겨 찾기
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/bookmark/api">api</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/bookmark/application">application</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/bookmark/dao">dao</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/bookmark/domain">domain</a>
      │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/bookmark/dto">dto</a>
      │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/comment">comment</a>      //댓글 관련
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/comment/api">api</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/comment/application">application</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/comment/dao">dao</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/comment/domain">domain</a>
      │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/comment/dto">dto</a>
      │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/email">email</a>        // 이메일 관련
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/email/api">api</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/email/application">application</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/email/dao">dao</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/email/domain">domain</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/email/dto/reqeust">dto</a>
      │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/email/event">event</a>      // 이메일 이벤트
      │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/Image">Image</a>       //이미지 관련
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/Image/application">application</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/Image/dao">dao</a>
      │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/Image/domain">domain</a>
      │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/like">like</a>     // 좋아요 관련
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/like/api">api</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/like/application">application</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/like/dao/like">dao</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/like/domain">domain</a>
      │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/like/dto">dto</a>
      │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/member">member</a>     // 사용자 관련
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/member/api">api</a>
      │  │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/member/api/admin">admin</a>
      │  │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/member/api/user">user</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/member/application">application</a>
      │  │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/member/application/admin">admin</a>
      │  │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/member/application/user">user</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/member/application/dao">dao</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/member/application/domain">domain</a>
      │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notice">notice</a>       // 공지사항 관련
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notice/api">api</a>
      │  │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notice/admin">admin</a>
      │  │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notice/user">user</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notice/application">application</a>
      │  │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notice/admin">admin</a>
      │  │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notice/user">user</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notice/dao">dao</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notice/domain">domain</a>
      │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notice/dto">dto</a>
      │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notification">notification</a>     // 알림 관련
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notification/api">api</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notification/application">application</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notification/dao">dao</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notification/domain">domain</a>
      │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/notification/dto">dto</a>
      │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/post">post</a>     // 게시글 관련
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/post/api">api</a>
      │  │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/post/admin">admin</a>
      │  │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/post/user">user</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/post/application">application</a>
      │  │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/post/admin">admin</a>
      │  │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/post/user">user</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/post/dao">dao</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/post/domain">domain</a>
      │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/post/dto">dto</a>
      │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/qna">qna</a>      // 문의사항 관련
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/qna/api">api</a>
      │  │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/qna/admin">admin</a>
      │  │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/qna/user">user</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/qna/application">application</a>
      │  │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/qna/admin">admin</a>
      │  │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/qna/user">user</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/qna/dao">dao</a>
      │  │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/qna/answer">answer</a>   // 답변
      │  │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/qna/question">question</a>   // 문의사항
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/qna/domain">domain</a>
      │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/qna/dto">dto</a>
      │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/recipe">recipe</a>       // 레시피 관련
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/recipe/api">api</a>
      │  │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/recipe/admin">admin</a>
      │  │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/recipe/user">user</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/recipe/application">application</a>
      │  │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/recipe/admin">admin</a>
      │  │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/recipe/user">user</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/recipe/dao">dao</a>
      │  │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/recipe/ingredient">ingredient</a>        // 재료
      │  │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/recipe/recipe">recipe</a>
      │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/recipe/domain">domain</a>
      │  │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/recipe/type">type</a>
      │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/recipe/dto">dto</a>
      └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/visit">visit</a>       // 방문자 집계
         ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/visit/api">api</a>
         ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/visit/application">application</a>
         ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/visit/dao">dao</a>
         ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/visit/domain">domain</a>
         └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/domain/visit/dto">dto</a>
      └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global">global</a>      // 전역 관련
         ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/annotations">annotations</a>       // 커스텀 에노테이션
         ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/aop">aop</a>       // AOP 설정
         ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/auth">auth</a>     // 보안 관련
         │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/auth/api">api</a>
         │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/auth/application">application</a>
         │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/auth/dao">dao</a>
         │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/auth/domain">domain</a>
         │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/auth/dto">dto</a>
         ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/config">config</a>     // 설정 관련
         ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/exception">exception</a>       // 예외 관련
         ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/payload">payload</a>       // 응답 값 형식
         ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/security">security</a>     // 스프링 시큐리티 관련
         │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/security/basic">basic</a>       // 인증 사용자 정보
         │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/security/exception">exception</a>       // 시큐리티 예외
         │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/security/jwt">jwt</a>       // JWT 관련
         │  │  ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/security/jwt/filter">filter</a>      // 시큐리티 필터
         │  │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/security/jwt/provider">provider</a>      // JWT 프로바이져
         │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/security/oauth2">oauth2</a>     // OAuth 2 관련
         │      ├─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/security/oauth2/application">application</a>
         │      │  └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/security/oauth2/application/impl">impl</a>
         │      └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/security/oauth2/provider">provider</a>      // 소셜로그인 프로바이저
         └─ <a href="https://github.com/Teeeeeeeam/Recipe-BE/tree/main/src/main/java/com/team/RecipeRadar/global/utils">utils</a>       // 유틸
</pre>
</details>


## 🤙 Convention
- 커밋 메시지
    1. 적절한 커밋 접두사 작성
    2. 커밋 메시지 내용 작성

| Tag Name | Description                                                    |
|----------|----------------------------------------------------------------|
| feat     | 새로운 기능                                                     |
| refactor | 프로덕션 코드 리팩토링                                          |
| docs     | 문서 수정                                                       |
| test     | 테스트 코드, 리펙토링 테스트 코드 추가, Production Code(실제로 사용하는 코드) 변경 없음     |
| chore    | 빌드 업무 수정, 패키지 매니저 수정, 패키지 관리자 구성 등 업데이트, Production Code 변경 없음 |
| remove   | 프로덕션 코드 삭제 및 디렉터리 삭제                            |
| rename   | 디렉터리 이름변경 및 폴더 이동                                 |
| fix      | 버그수정                                                        |

