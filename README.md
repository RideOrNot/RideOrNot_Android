# 탈래말래🚇: 지하철 실시간 탑승 알리미

<img src="https://github.com/RideOrNot/RideOrNot_Android/assets/68229317/169a9d0e-e3bc-49c5-81d5-7aa891ff1e1b" width="800">

###

> 눈앞에서 놓치는 지하철, 아쉽지 않으셨나요?
>
> “내 위치에서 지하철을 타기까지 얼마나 걸리는지 미리 알면 좋을 텐데….”
> 
> 그래서 등장한 **지하철 실시간 탑승 알리미, 탈래말래**!
>
>  “지금부터 20초간 뛰어간다면, 오이도행 지하철에 탑승하실 수 있습니다.”
> 
>  탈래말래는 지하철역에 들어가기 전에 푸시 알림을 전송하여 더 나은 이동 경험을 제공합니다.

<br>

## 📜 프로젝트 소개
바쁜 출근길, 혹은 등굣길에, 혹은 약속에 늦었을 때, 다음 열차를 탈 수 있을지, 타기 위해 뛰어야 할지 말아야 할지 고민되신 적 없으신가요? 이용자님의 소중한 시간을 헛되이 하지 않기 위해, '탈래말래' 는 지하철역의 승강장과 이용자님 현위치 사이의 거리를 측정하여 해당 역사가 지금 얼마나 혼잡한지, 이용자님의 걸음 속도가 얼마나 빠른지 등의 정보를 토대로 다음 열차를 타기까지 시간이 얼마나 남았는지, 그리고 얼마나 빠른 속도로 걷거나 뛰어야 다음 열차를 탈 수 있는지 푸시 메시지로 알려드립니다.

또한 '탈래말래' 는 실시간 위치 정보와 푸시 메시지를 활용하여 이용자님이 성공적으로 열차에 탑승하였는지 체크하고, 이 정보를 기반으로 점점 더 정확도를 향상시켜 나갑니다. 따라서 '탈래말래' 앱을 이용할 때마다 더욱 정확해지는, 이용자님께 맞춤화된 도착 안내 서비스를 제공 받아보실 수 있습니다.


## 🛠 기술 스택
| Frontend | Backend | Tools |
| :--------: | :--------: | :--------: |
| <img src="https://img.shields.io/badge/Android Studio-3DDC84?style=flat&logo=Android Studio&logoColor=white"/> <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white"/> <img src="https://img.shields.io/badge/GCP-4285F4?style=flat&logo=Google Cloud&logoColor=white"/> <img src="https://img.shields.io/badge/Naver Maps-03C75A?style=flat&logo=Naver&logoColor=white"/> <img src="https://img.shields.io/badge/SQLite-003B57?style=flat&logo=sqlite&logoColor=white"/> | <img src="https://img.shields.io/badge/IntelliJ-000000?style=flat&logo=intellijidea&logoColor=white"/> <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=Spring Boot&logoColor=white"/> <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=Java&logoColor=white"/> <img src="https://img.shields.io/badge/AWS-232F3E?style=flat&logo=Amazon AWS&logoColor=white"/> <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white"/> <img src="https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white"/> | <img src="https://img.shields.io/badge/Git-F05032?style=flat&logo=Git&logoColor=white"/> <img src="https://img.shields.io/badge/GitLab-FC6D26?style=flat&logo=gitlab&logoColor=white"/> <img src="https://img.shields.io/badge/Notion-000000?style=flat&logo=Notion&logoColor=white"/> <img src="https://img.shields.io/badge/Discord-5865F2?style=flat&logo=Discord&logoColor=white"/> <img src="https://img.shields.io/badge/Figma-F24E1E?style=flat&logo=figma&logoColor=white"/> |


## 🌟 주요 기능
| 기능                          | 설명                                                         |
| ----------------------------- | ------------------------------------------------------------ |
| 실시간 도착정보 조회          | 서울 열린데이터광장 Open API를 활용하여 사용자가 특정 지하철역의 특정 출구를 기준으로 3m 이내에 위치할 때, 해당 역의 실시간 열차 도착정보를 제공합니다. |
| 승차 알림                     | 사용자가 특정 지하철역의 특정 출구를 기준으로 3m 이내에 위치할 때, 기기에 등록된 사용자의 이동속도와 현재 위치로부터 해당 지하철역의 탑승구까지의 거리를 이용해 사용자가 탑승구까지 이동하는 데 걸리는 시간을 예측하여 이를 푸시 알림으로 전송합니다. <br/> ex) 지금부터 n초간 달리면 OO방면 열차를 탑승할 수 있습니다. |
| 역사 혼잡도 조회              | 사용자가 특정 지하철역의 특정 출구를 기준으로 3m 이내에 위치할 때, 해당 역의 해당 시간대 이용자 수를 다년간의 데이터를 기반으로 예측하여 혼잡도를 계산해 알림으로 전송하거나 화면상에서 제공합니다. |
| 역 북마크                     | 사용자가 특정 역을 자주 가는 역으로 설정하면 사용자의 위치가 그 역의 주변이 아니어도 해당 역의 정보를 알려줍니다. |
| 역 검색                       | 사용자가 원하는 역을 직접 검색하면 해당 역의 정보를 제공하며, 최근에 검색한 역을 자동으로 저장합니다.     |
| 회원가입 / 로그인             | 사용자의 프로필 정보를 토대로 알림을 생성하기 위해, 구글 소셜 로그인을 연동하여 앱 내 사용자를 식별합니다. |


## 🎬 시연 영상
[https://youtu.be/bMhVxTmJhqk](https://youtu.be/bMhVxTmJhqk)


## 📱 결과물 상세 이미지

|실시간 도착정보|검색|
|:---:|:---:|
|<img src = "https://github.com/RideOrNot/RideOrNot_Android/assets/68229317/49d5d35c-9c32-4fb7-b2c3-e07d615475af" width = 240vw height = 500vh>|<img src = "https://github.com/RideOrNot/RideOrNot_Android/assets/68229317/d685ad63-5d31-435a-b1eb-c978ea108ff1" width = 240vw height = 500vh>|

|북마크|승차 알림|
|:---:|:---:|
|<img src = "https://github.com/RideOrNot/RideOrNot_Android/assets/68229317/c632697d-8d04-4e2e-93c2-fcdd717b2693" width = 240vw height = 500vh>|<img src = "https://github.com/RideOrNot/RideOrNot_Android/assets/68229317/34d12ae0-70ec-471d-af85-3f9c766b6d73" width = 240vw height = 500vh>|
