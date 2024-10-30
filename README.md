# 0주차(~24.11.01)

### 프로젝트1: HTTP 요청 메시지 파싱
- [x] v1: 텍스트 파일에 저장되어있는 더미 요청을 읽어들어 파싱 후, 개별 서브파트를 파일로 저장
- [x] v1.1: minor 리팩토링
  - [x] 매직 리터럴을 상수로 분리
    - [x] 경로 관련 매직 리터럴
    - [x] HTTP 메시지 구분자 매직 리터럴을 별도 클래스로 분리
    - [x] HTTP 헤더 이름 상수 클래스 활용?
  - [x] 문자열 일치 여부 시, NullPointerException 예방을 위해, 참조 위치 치환  
    - (ex. StrObj.equals("abcd") → "abcd".equals(StrObj))
  - [x] 가능한 한 순수 함수로 변경
    - [x] HttpRequest::parse
    - [x] Multipart.Part::parse
- [ ] + 주석 보강
  - [ ] public 메서드에 javadoc 명시
  - [ ] 코드 블록에 요약 주석 달기
- [ ] v1.2: 입출력 방식 변경
  - [ ] 문자 스트림 → 바이트 스트림 (이미지 파일 등을 위해)
  - [ ] 다 읽고 나서 결과를 전달하던 방식에서 → 읽으면서 파싱할 수 있도록 스트림을 전달하는 방식으로 변경

### 프로젝트2: Javascript 빙고 게임 만들기
- [ ] 시간이 많이 않으므로 최소한의 요구사항만 지키자
  - [ ] 양방향 옵저버 패턴?

### 매일 자바 학습
- [x] 10.29 화: Java InetAddress, Socket & ServerSocket
- [x] 10.30 수: Java I/O API: File & Path
- [ ] 10.31 목: Java FileInputStream, FileOutStream
- [ ] 11.01 금: 
- [ ] 11.02 토: 

### 매일 코테 연습
- [x] 10.29 화: 알고리즘 성능 평가(시간복잡도) 및 디버깅 스킬 복습
- [x] 10.30 수: 배열 3문제
    | 문제 분류 | 문제 | 문제 제목 |
    | --- | --- | --- |
    | 연습 문제 | 10808 | [알파벳 개수](https://www.acmicpc.net/problem/10808) |
    | 기본 문제✔ | 2577 | [숫자의 개수](https://www.acmicpc.net/problem/2577) |
    | 기본 문제✔ | 1475 | [방 번호](https://www.acmicpc.net/problem/1475) |
- [ ] 10.31 목: 
- [ ] 11.01 금: 
- [ ] 11.02 토: 

### 기타 학습 목표
- [ ] HTTP CSS JS 복습
- [x] HTTP 복습
- [ ] 디자인 패턴
  - [ ] 옵저버 패턴
  - [ ] 커맨드 패턴
  - [ ] 템플릿 메서드 패턴
  - [ ] 전략 패턴
- [ ] REST API 설계 가이드