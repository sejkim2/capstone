![컴공2분반_8조_포스터_AI를 활용한 CCTV 기반 매장 고객관리 플랫폼_다보아](https://github.com/user-attachments/assets/a6530e1f-946b-47cb-a793-021cd4417e75)

## Makefile 사용법
1. make : 빌드
2. make up : 재빌드
3. make down : 실행 중인 컨테이너 모두 종료
4. make clean : 실행 중인 컨테이너 모두 종료 및 이미지 파일 삭제

## Docker 사용법
Docker는 경량화된 가상 머신이라고 보면 됩니다. 빌드 하면 도커 이미지(버전 등이 기록됨)를 만들고 그 이미지로 컨테이너 (실행하려는 서비스 하나)를 실행시킵니다.
make 하면 빌드 후 컨테이너는 계속 실행되고 있고 make clean 시 컨테이너가 종료됩니다.

1. docker ps -a : 실행 중인 컨테이너 확인 (컨테이너 ID랑 정상 실행 중인지 등 확인 가능)
2. docker logs [container ID] : 실행 중인 컨테이너 로그 확인 ([] 부분은 실제 컨테이너 ID 넣어야 함)
