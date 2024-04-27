# MOONGTEL-dog-hotel-reservation-v2.0

기존 프로젝트 리팩토링 및 기능 추가

- ERD 개편
  - 중복되는 컬럼 값 제거
  - 잘못 설계된 연관관계 재설정
- 불필요한 양방향 연관관계 제거
- QueryDSL을 활용한 검색기능 구현
- Controller에 위치한 Response Entity -> DTO로 대체하여 순환참조 이슈 해결
- @Setter 삭제로 객체 불변성 보장
- test 추가

