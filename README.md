# 탈래말래


<div align="center">
<p align="center">
    <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="logo" width="250" height="250"/>
</p>
<h4 align="center">지하철 실시간 탑승 알리미</h4>
<p align="center">
    눈앞에서 놓치는 지하철, 아쉽지 않으셨나요?
    <br/>
    “내 위치에서 지하철을 타기까지 얼마나 걸리는지 미리 알면 좋을 텐데….”
    <br/>
    <br/>
    그래서 등장한 지하철 실시간 탑승 알리미, 탈래말래!
    <br/>
    “지금부터 20초간 뛰어간다면, 오이도행 지하철에 탑승하실 수 있습니다.”
    <br/>
    탈래말래는 지하철역에 들어가기 전에 이러한 푸시 알림을 전송하여
    <br/>
    더 나은 이동 경험을 제공합니다.
</p>
</div>


## 프로젝트 소개
대도시에는 지하철을 이용하는 수많은 사람이 있습니다. 하지만 지하철 도착 정보를 얻기 위해서는 지도 앱을 직접 켜고 복잡한 메뉴를 통해 여러 화면을 열어야 하는 번거로움이 있습니다. '탈래말래'는 이 문제를 해결하기 위해 사용자의 GPS를 기반으로 현 위치에서 탑승 가능한 지하철 도착 정보를 모바일 푸시 형식으로 제공하는 앱입니다.

## 기술 스택
| Frontend | Backend | Tools |
| :--------: | :--------: | :--------: |
| <img src="https://img.shields.io/badge/Android Studio-3DDC84?style=flat&logo=Android Studio&logoColor=white"/> <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white"/> <img src="https://img.shields.io/badge/GCP-4285F4?style=flat&logo=Google Cloud&logoColor=white"/> <img src="https://img.shields.io/badge/Naver Maps-03C75A?style=flat&logo=Naver&logoColor=white"/> <img src="https://img.shields.io/badge/SQLite-003B57?style=flat&logo=sqlite&logoColor=white"/> | <img src="https://img.shields.io/badge/IntelliJ-000000?style=flat&logo=intellijidea&logoColor=white"/> <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=Spring Boot&logoColor=white"/> <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=Java&logoColor=white"/> <img src="https://img.shields.io/badge/AWS-232F3E?style=flat&logo=Amazon AWS&logoColor=white"/> <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white"/> <img src="https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white"/> | <img src="https://img.shields.io/badge/Git-F05032?style=flat&logo=Git&logoColor=white"/> <img src="https://img.shields.io/badge/GitLab-FC6D26?style=flat&logo=gitlab&logoColor=white"/> <img src="https://img.shields.io/badge/Notion-000000?style=flat&logo=Notion&logoColor=white"/> <img src="https://img.shields.io/badge/Discord-5865F2?style=flat&logo=Discord&logoColor=white"/> <img src="https://img.shields.io/badge/Figma-F24E1E?style=flat&logo=figma&logoColor=white"/> |

## 주요 기능
| 기능                          | 설명                                                         |
| ----------------------------- | ------------------------------------------------------------ |
| 실시간 도착정보 조회          | 서울 열린데이터광장 Open API를 활용하여 사용자가 특정 지하철역의 특정 출구를 기준으로 3m 이내에 위치할 때, 해당 역의 실시간 열차 도착정보를 제공합니다. |
| 승차 알림                     | 사용자가 특정 지하철역의 특정 출구를 기준으로 3m 이내에 위치할 때, 기기에 등록된 사용자의 이동속도와 현재 위치로부터 해당 지하철역의 탑승구까지의 거리를 이용해 사용자가 탑승구까지 이동하는 데 걸리는 시간을 예측하여 이를 푸시 알림으로 전송합니다. <br/> ex) 지금부터 n초간 달리면 OO방면 열차를 탑승할 수 있습니다. |
| 역사 혼잡도 조회              | 사용자가 특정 지하철역의 특정 출구를 기준으로 3m 이내에 위치할 때, 해당 역의 해당 시간대 이용자 수를 다년간의 데이터를 기반으로 예측하여 혼잡도를 계산해 알림으로 전송하거나 화면상에서 제공합니다. |
| 역 북마크                     | 사용자가 특정 역을 자주 가는 역으로 설정하면 사용자의 위치가 그 역의 주변이 아니어도 해당 역의 정보를 알려줍니다. |
| 역 검색                       | 사용자가 원하는 역을 직접 검색하면 해당 역의 정보를 제공하며, 최근에 검색한 역을 자동으로 저장합니다.     |
| 회원가입 / 로그인             | 사용자의 프로필 정보를 토대로 알림을 생성하기 위해, 구글 소셜 로그인을 연동하여 앱 내 사용자를 식별합니다. |

## 시연 영상
[https://youtu.be/bMhVxTmJhqk](https://youtu.be/bMhVxTmJhqk)

## 결과물 상세 이미지

|실시간 도착정보|검색|북마크|승차 알림|
|:---:|:---:|:---:|:---:|
|<img src = "https://github.com/RideOrNot/RideOrNot_Android/assets/68229317/49d5d35c-9c32-4fb7-b2c3-e07d615475af" width = 120vw height = 250vh>|<img src = "https://github.com/RideOrNot/RideOrNot_Android/assets/68229317/d685ad63-5d31-435a-b1eb-c978ea108ff1" width = 120vw height = 250vh>|<img src = "https://github.com/RideOrNot/RideOrNot_Android/assets/68229317/c632697d-8d04-4e2e-93c2-fcdd717b2693" width = 120vw height = 250vh>|<img src = "https://github.com/RideOrNot/RideOrNot_Android/assets/68229317/34d12ae0-70ec-471d-af85-3f9c766b6d73" width = 120vw height = 250vh>|
