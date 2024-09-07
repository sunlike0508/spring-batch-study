# 스프링 배치

## 배치란?

사전적 의미로 *일정 시간 동안 대량의 데이터를 한 번에 처리하는 방식*

프레임워크를 사용하는 이유는 아주 많은 데이터를 처리하는 중간에 프로그램이 멈출 수 있는 상황을 대비해 안전 장치를 마련하기 위해서이다.

중간에 멈추면 작업지점을 기록하여 재시작이 가능하고, 했던 처리를 다시하는 중복처리 불상사를 막을 수 있다.

* 중요사항

배치는 데이터를 효율적으로 빠르게 처리하는 것도 중요하지만, 더 중요하게 생각하는 부분은 아래와 같다.

1. 내가 하고 있던 작업을 어디까지 했는지 계속해서 파악
2. 이미 했던 일을 중복해서 하지 않게 파악(배치는 보통 주기적으로 스케쥴러에 잡혀 실행되기 때문에)

즉, 중복이나 놓치는 부분을 파악하기 위해 **기록** 하는 부분이 아주 중요.

읽기 -> 처리 -> 쓰기 라는 단계를 거치는데

이때 읽기 부분에서 한 번에 다 읽지 않는다.

이유는 한 번에 많은 양을 읽어서 처리할 경우 여러 문제가 생기는데

1. 읽은 데이터를 메모리에 올릴 수 없을 수 있다.
2. 실패했을 경우 위험성이 크고, 속도적인 문제가 발생한다.

따라서 데이터를 끊어서 읽고 처리하는 방식으로 진행한다.

따라서 내가 하던 작업을 기록해야 하는 이유이다.

**배치에서는 내가 했던 작업, 했던 작업을 기록하는 테이블을 메타 데이터라고 부른다.**

### 배치 활용 상황 : 메타 데이터 존재 이유

* 은행 이자 시스템 : 매일 자정 전일 데이터를 기반으로 이자를 계산하여 이자 지급을 수행
    * 오늘 자정 기준에 데이터만 처리해야하고 처리 했던 계좌를 또 처리하면 안된다.
    * 10분안에 빠르게 처리해야 영업에 차질이 없도록 만들어야 한다.

<img width="726" alt="Screenshot 2024-09-03 at 23 37 52" src="https://github.com/user-attachments/assets/0d39688f-3cf5-4af0-821e-593f6e475075">

* JobLauncher : 하나의 배치 작업을 실행시키는 시작점
* Job : 읽기 -> 처리 -> 쓰기 과정을 정의한 배치 작업
* Step : 실제 하나의 읽기 -> 처리 -> 쓰기 작업을 정의한 부분으로, 1개의 Job에서 여러 과정을 진행할 수 있기 때문에 1:N의 구조를 가진다.
* ItemReader : 읽어오는 부분
* ItemProcessor : 처리하는 부분
* ItemWriter : 쓰는 부분
* JobRepository : 얼만큼 했는지, 특정일자 배치를 이미 했는지 "메타 데이터"에 기록하는 부분

### 스프링 설정

스프링 부트에서 2개 이상의 DB를 연결하려면 Config 클래스를 필수로 작성해야한다.

충돌 방지를 위해 @Primary Config를 설정해야 한다.

기본적으로 메타데이터는 @Primary로 잡혀 있는 DB 소스에 초기화되게 된다.

## 메타데이터 테이블

### 메타데이터 테이블이란?

배치에서 중요한 작업에 대한 트래킹을 수행하는 테이블로, 스프링 배치에서도 메타데이터를 관리해야 합니다.

## 구현

* JobRepository : 배치의 작업 기록 tracking

### Step

* chunk : 데이터 읽는 단위
* reader : 읽는 메소드 처리
* processor : 처리 메소드
* writer : 쓰기 메소드 처리

```java
// pseudocode
class JobLauncherClass {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    JobParameters jobParameters = new JobParametersBuilder().addString("date", value).toJobParameters();

    jobLauncher.run(jobRegistry.getJob("firstJob"),jobParameters);
}
```

* JobLauncher : Job 실행 시점
* JobRegistry : 특정 배치(JOB)를 가져오는 registry
* JobParameter : 실행할 job에게 특정날짜(보통 그렇다) or 특정번호 같은것을 가지고 실행할 수 있게 하는 것. 작업 통제

## 영속성별 구현

### JDBC

https://github.com/spring-projects/spring-batch/tree/main/spring-batch-samples/src/main/java/org/springframework/batch/samples/jdbc

### MongoDB

https://github.com/spring-projects/spring-batch/tree/main/spring-batch-samples/src/main/java/org/springframework/batch/samples/mongodb

## ItemStreamReader

배체에서 데이터를 읽는 Reader

스프링 배치에서 가장 중요한 부분은 Reader 부분이다.

현재까지 실행한 부분을 메타데이터에 저장해야하고 처리한 부분은 스킵해야 되기 때문이다.

그래서 스프링에서 다양한 Reader 구현제를 제공하는데 가끔 없다.

없는 경우 커스텀 reader를 만들어야 한다.

ItemStreamReader = ItemStream + ItemReader

### ItemStream

```java
public interface ItemStream {

    default void open(ExecutionContext executionContext) throws ItemStreamException {
    }

    default void update(ExecutionContext executionContext) throws ItemStreamException {
    }

    default void close() throws ItemStreamException {
    }

}
```

* open : 배치가 시작되었을때, step에서 처음 reader를 부르면 시작되며, 초기화나 이미 했던 작업의 경우 중단점까지 건너 뛰도록 설계하는 부분

* update : 반복 실행 되는 만큼 실행. reader의 read()에서 실행한 단위를 기록하는 용도.

* close : 배치가 끝나고 파일을 저장하거나 필드 변수 초기화등. 단 한번만 실행.

### ItemReader

데이터 읽기를 위한 read() 메소드

```java

@FunctionalInterface
public interface ItemReader<T> {

    @Nullable
    T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException;

}
```

### ExecutionContext

ItemStream의 Open(), update()에 매개변수로 주입되어 있는 객체로 배치 작업 처리시 기준점을 잡을 변수를 계속하여 트래킹하기 위한 저장소로 사용된다.

해당 클래스에서 put으로 값을 넣고 get으로 넣은 값을 가져온다.

ExecutionContext 데이터는 JdbcExecutionContextDao에 의해 메타데이터 테이블에 저장되며 범위에 따라 아래와 같이 나뉩니다.

* BATCH_JOB_Execution_Context

* BATCH_STEP_Execution_Context