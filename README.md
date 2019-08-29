# GetHabit  
부산대학교 컴퓨터응용설계및실험 004분반 5팀 - 습관만들기 안드로이드 어플리케이션  
주요 기능들은 아래와 같습니다.  
- 계정관리 (로그인, 로그아웃, 회원가입, 회원탈퇴, 이메일인증) 
- CURD (습관 형성을 위한 습관 체크하기, 습관 일기 쓰기, 다른 사람들과 함께 습관만들기)
- OpenCV를 활용한 얼굴인식 및 이미지 마스크 씌우기, 동영상 녹화와 저장
- 동영상 업로드 및 재생
- 사진파일을 모아 타임라인 동영상 생성

## 1. 사용된 자원
- OpenCV v4.1
- JCodec v0.1.9
- PostgreSQL
- Java Spring v2.1.4
- AWS S3
- RestFul API

## 2. 데모영상
> 이미지 용량이 크므로 로딩이 오래 걸릴 수 있습니다 (약 13MB)
  
![Demo](./misc/gethabit_demo.gif)

Backend 서버 코드는 [여기](https://github.com/pnu-004-team5/CreatingHabits)에 있습니다.

## 3. 구성도
![Diagram](./misc/gethabit_diagram.png)

## 4. 설정
  
#### 4.1 상수 설정
> app/java/[package_name]/interface/AppConstants.java 
```java
String API_URL = "http://[[백엔드 서버 주소]]:8080"; //ex) http://10.0.2.2:8080; AVD로 실행하고 백엔드서버를 로컬로 실행할때
String S3_URL = "[아마존 S3 주소]"; //ex) https://s3.amazonaws.com/creatinghabits-userfiles-mobilehub-665767729/public
```





