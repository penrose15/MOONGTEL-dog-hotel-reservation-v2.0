# MOONGTEL-dog-hotel-reservation-v2.0

기존 프로젝트 리팩토링 및 기능 추가

- ERD 개편
  - 중복되는 컬럼 값 제거
  - 잘못 설계된 연관관계 재설정
- 불필요한 양방향 연관관계 제거
- Controller에 위치한 Entity -> DTO로 대체
- MapStruct -> DTO 내부 Entity로 변환하는 로직 추가
- @Setter 삭제
- test 추가
- spring batch 로 특정 시간에 별점 평균 계산

