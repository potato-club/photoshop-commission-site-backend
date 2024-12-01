# 📷 포토샵 커미션 사이트(backend)

## 프로젝트 설명
- 포토샵 커미션을 의뢰하거나 수주할 수 있는 다목적 커뮤니티 사이트
    - 의뢰 전, 의뢰 중, 의뢰 완료 게시판을 나눠 의뢰 내역들을 쉽게 볼 수 있음.
    - 의뢰자들은 신청한 아티스트의 정보를 확인하고 의뢰를 맡길 수 있음.
    - 의뢰가 끝난 후 리뷰 작성을 통해 해당 아티스트에게 평점 및 후기를 남길 수 있음.
      
- 프로젝트 기간 :  2022년 7월 25일 ~ 2022년 12월 21일

## 기술 스택
- **Java 11**
- **Spring**
    - **Framework**
    - **Spring Boot**
- **AWS EC2 (Ubuntu + Nginx)**
- **AWS RDS** (MariaDB)
- **AWS S3** - 파일 저장용 스토리지
- **Redis**
- **Spring Data JPA + QueryDsl**
- **Swagger** (Springfox)

## 주요 기능 설명
- 메인 페이지
    - 의뢰 전, 의뢰 중, 의뢰 완료 미리 보기가 게시됨.
    - 의뢰를 하거나 수주하기 위해선 로그인 필요.
    
    ![메인 페이지](https://github.com/user-attachments/assets/97a77a6f-a9b5-427a-af1e-670f7c0e95e7)
    
- 커미션 의뢰하기
    - User 권한인 사용자는 의뢰 글을 작성할 수 있음.
    - 작성자는 의뢰가 수주 되기 전까지 사진을 비공개 처리할 수 있음.

![글 쓰기2](https://github.com/user-attachments/assets/f00c2146-84fe-458a-bed2-2f7e28e36fe0)

- 커미션 수주 및 의뢰 진행
    - 의뢰자는 신청한 아티스트들 중에서 한 명을 선택할 수 있고 선택하면 사진이 공개됨.
    - 아티스트는 공개된 사진을 다운로드할 수 있고, 작업 후 결과물을 등록할 수 있음.
    - 만약, 결과물이 마음에 들지 않는다면 작업 후 재 업로드를 할 수 있음.
    
    ![게시글 커미션](https://github.com/user-attachments/assets/d909a748-4e14-4850-889b-384092de3877)

    ![게시글 커미션 완료](https://github.com/user-attachments/assets/af3e420d-28ad-4de4-93cf-a560b6e34c58)

    
- 커미션 완료 및 후기 작성
    - 의뢰자가 커미션이 마음에 든다면 마이페이지에서 후기 작성 후 의뢰를 종료함.
    - 해당 의뢰에 대한 아티스트의 평점과 후기가 등록됨.
    
    ![게시글 커미션 등록 완료](https://github.com/user-attachments/assets/1379e41e-ba8f-42c5-ae83-6ed749512dc6)

<hr/>

## 기타 참고 사항

### 백엔드 인력 충원으로 인한 Repository 이동

- 기존 Repository : https://github.com/cheomuk/SessionCookieLogin.git


### 브랜치 설명

- 메인 브랜치 : main

- 개인 테스트 브랜치 : develop_first, develop_second


### 프론트/백엔드 도메인 주소 (서비스 종료)

- 프론트 : https://easyphoto.site

- 백엔드 : https://photoshopcommission.shop


### 프론트 깃 주소

- https://github.com/potato-club/photoshop-commission-site
