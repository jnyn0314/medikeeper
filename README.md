# 💊 Medikeeper

위치 기반 약국 검색과 복약 알림 기능을 제공하는 Android 앱입니다.  
사용자는 복용 중인 알약 정보를 앱에 저장하고,  
복용 시간에 맞춰 알림을 받을 수 있으며,  
근처 약국 위치를 지도에서 쉽게 확인할 수 있습니다.

---

## 📅 프로젝트 기간

**2024.09 ~ 2024.12.24**

---

## 🧠 프로젝트 개요

> **코틀린을 이용한 위치 기반 약국 알림 앱**

- 알약 정보 저장 및 복약 시간 설정
- 복약 알람 Notification 전송
- 공공데이터 기반 약국 정보 제공
- 지도 기반 약국 위치 확인 (SKT TMap API 활용)

---

## ⚙️ 개발 환경

| 항목         | 내용                                |
|--------------|-------------------------------------|
| IDE          | Android Studio Giraffe (2022.3.1)   |
| 언어         | Kotlin                              |
| Target SDK   | Android 14                          |
| Compile SDK  | Android 15                          |
| Min SDK      | Android 7.0                         |
| DB           | Room (로컬 알약 데이터 저장)        |
| 기타 사용 기술 | LiveData, ViewModel, Coroutine 등   |

---

## 📌 주요 기능

- **📍 약국 위치 찾기**  
  - TMap OpenAPI 기반, 사용자 위치 기준 주변 약국 시각화

- **⏰ 복약 알람 서비스**  
  - 알약 복용 시간 등록 → Notification 알림 전송

- **💊 알약 정보 저장**  
  - 사용자 알약 이름, 복용 시간 정보 저장 (Room DB)

- **📋 약국 정보 조회**  
  - 공공데이터 포털 API 연동으로 실시간 약국 리스트 출력

- **📱 리스트 UI 구성**  
  - RecyclerView + ListAdapter로 알약/약국 목록 표시

- **🔄 비동기 처리**  
  - Coroutine 및 ViewModel을 통한 비동기 로직 구성

---

## 🌐 사용한 외부 API

### ▫ 공공데이터 포털 API
- **API명**: 서울특별시 성북구 약국 현황  
- **제공처**: 공공데이터 포털 (ODcloud)  
- **목적**: 약국명, 주소 등 기본 정보 조회  
- **URL**: https://api.odcloud.kr

### ▫ SKT TMap OpenAPI
- **API명**: Raster Map API  
- **제공처**: SKT  
- **목적**: 지도 기반 약국 위치 시각화  
- **URL**: https://apis.openapi.sk.com/tmap/maps

---

## 📂 프로젝트 구조

![image](https://github.com/user-attachments/assets/3150ffdc-77d3-4d64-8d9b-97cc85156e31)

---

## ✍️ 기여자

- **정여진 (20220803)**

---

## ⚠️ 참고 사항

- Android 12(API 31) 이상에서는 알림 권한 허용이 필요합니다.
- TMap API 사용을 위해 **API Key 등록이 필수**입니다.
