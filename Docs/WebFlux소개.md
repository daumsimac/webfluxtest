# Webflux활용에 관하여

<br>
HTTP를 이용한 시스템 범위가 확대되고 동시 액세스가 늘면서 웹 애플리케이션의 컴퓨터 리소스 대기 시간이 많이 늘어났다. 이에 따라 비동기 처리를 이용한 웹 애플리케이션이 필요해졌다. 그 해결책으로 스프링 5와 스프링 부트 2는 리액티브 프로그래밍인 웹플럭스를 이용해 웹 프로그램을 작성한다. 이 프로그램은 동기 방식이 아니므로 블록되지 않고 실행된다. 즉, I/O 대기 같은 상태가 되지 않으므로 I/O가 발생하더라도 실행 중인 상태를 유지할 수 있다.
 
2018년에는 IoT나 API를 활용한 데이터 수집과 같이 기본보다 더 많은 데이터를 취급하는 경우가 많아졌다. 많은 데이터를 처리하기 위해 풍부한 리소스를 이용해 병렬 처리하려 했지만, 입출력 대기 등이 영향을 끼쳐 그다지 효과적으로 병행이나 병렬 처리를 할 수 없었다. 따라서 지금까지의 방식과는 다른 방식인 비동기 스트리밍 처리에 관심이 집중되었다.
 
웹플럭스를 이용해 웹 애플리케이션을 개발하려면 기존의 스프링 MVC 개발 방법에서 사용하는 어노테이션을 이용한 개발 방법과 자바 8 이상에서 적용되는 함수형 개발 방법 이 두가지 방법을 사용한다.


## 1. 어노테이션을 사용한 개발

스프링 MVC에서 사용하는 어노테이션으로 리액티브 프로그래밍하는 방법이 개발자의 학습 비용을 낮추는 바람직한 방법이었다. 따라서 웹플럭스는 어노테이션 프로그래밍 모델을 제공한다. 웹플럭스에서 어노테이션으로 라우팅(URL과 처리 매핑)과 파라미터 매핑을 구현한 다음, 리액터의 Mono나 Flux를 이용해서 리액티브 프로그램을 작성한다.

#### GET 요청에 문자열 반환하기
```java
@GetMapping("/annotation/sample1")
public Mono<String> sample1() {
        return Mono.just("Hello Annotation WebFlux World!");
}
```

@GetMapping 어노테이션은 스프링 MVC와 마찬가지로 대상 경로를 설정한다. 웹플럭스 고유의 작성 방법은 반환값을 Mono 클래스로 반환한다. 스프링에서는 리액티브 프로그래밍 라이브러리인 리액터에서 제공하는 Mono 클래스로 반환한다. 스프링에서는 리액티브 프로그래밍 라이브러리인 리액터에서 제공하는 Mono를 사용한다. 이 클래스는 제네릭(generic)의 대상인 클래스를 비동기 스트리밍으로 반환한다. 이 반환 횟수가 1회이면 Mono를 사용하고, 여러 번이면 Flux를 사용한다. Mono 클래스의 just 메서드는 인숫값이 확정된 타이밍에 Mono 인스턴스를 작성한다.
<br>
<br>
어노테이션을 이용한 작성 방법은 기존의 스프링 MVC 어노테이션을 사용하면서 Mono나 Flux 클래스를 이용해 비동기 스트리밍에 대응하는 기법이다.
<br>

## 2. 함수형 프로그래밍 개발
<br>
웹플럭스는 어노테이션으로 프로그램을 작성하는 것이 아니라 자바 8에 도입된 함수형 프로그래밍(functional programming) 모델을 이용한다. 이 방법을 사용하기 위해 라우터 함수(router function)를 묶은 스프링 빈과 웹 처리를 실시하는 핸들러 함수(handler function)을 작성한다.
<br>

#### 핸들러 함수
핸들러 함수는 웹처리를 실시하는 메서드다. 가인수로 ServerRequest를 갖고, 반환값은 Mono<ServerResponse> 또는 Flux<ServerResponse>로 구현한다. 아래 예시 메소드를 준비하여 메서드 참조로 라우터 함수에 등록한다.

```java
@Component
public class GreetingHandler {

    public Mono<ServerResponse> hello(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromObject("Hello, Spring!"));
    }
}
```

#### 라우터 함수
라우터 함수는 URL의 일부인 경로와 핸들러 함수를 연결한다. 이 함수는 RouterFunction<ServerResponse>를 반환한다. 스프링은 이 타입의 빈을 웹 애플리케이션의 경로 정보로 이용한다. @Bean을 이용한 메서드에서 경로 정보를 정의하고, 그 대상 클래스에 설정을 나타내는 @Configuration 어노테이션을 부여한다.
```java
@Configuration
public class GreetingRouter {

    @Bean
    public RouterFunction<ServerResponse> route(GreetingHandler greetingHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/hello").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), greetingHandler::hello);
    }
}
```

RouterFunction<ServerResponse>를 작성하기 위해, 라우터 함수의 route 메서드를 이용한다. route 메서드에서는 첫번째 인수에 RequestPredicates 인터페이스, 두번째 인수에 HandlerFunction 인터페이스(함수 인터페이스)를 취한다.
<br>

RequestPredicates의 GET 메서드를 이용해 RequestPredicates 구현 클래스의 인스턴스를 생성한다. 경로(/hello)에 GET 요청이 있고, accept 헤더가 TEXT/PLAIN인 액세스 조건을 나타낸다. 이 조건을 만족할때, greetingHandler 클래스의 hello() 메서드를 호출한다.

#### Flux
다음은 Flux의 예로, 0부터 24를 순차적으로 송신한다.
```java
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.IntStream;

@Component
public class FluxDemoHandler {

    public Mono<ServerResponse> fluxHandler(ServerRequest req) {
        
        Flux<Integer> stream = Flux.fromStream(IntStream.range(0,24).boxed());
        
        return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(stream, Integer.class);
    }
    
    public RouterFunction<ServerResponse> routerule() {
        return RouterFunctions.route(RequestPredicates.GET("/func/sample8"), this::fluxHandler);
    }
}
```

## 3. 웹플럭스를 사용한 웹 액세스
스프링 MVC에서는 RestTemplate을 사용하여 웹을 동기로 액세스한다. 그에 반해 웹플럭스에서는 비동기로 액세스하는 WebClient를 제공한다. 다음은 WebClient를 사용해 깃허브의 공개 API를 호출한다.

```java
public Mono<ServerResponse> handle1(ServerRequest req) {

        WebClient webClient = WebClient.create(GITHUB_BASE_URL);

        // 쿼리 스트링에 user가 포함되어 있는지 체크
        Optional<String> userName = req.queryParam("user");
        if (!userName.isPresent()) {
        return ServerResponse.ok().body(Mono.just("user is Required"), String.class);
        }

        // 비동기 호출(비블록)
        Mono<List<GithubRepository>> values = webClient.get()
        .uri("users/" + userName.get() + "/repos")
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<GithubRepository>>(){});

        return ServerResponse.ok().body(values, new ParameterizedTypeReference<List<GitHubRepository>>(){})
        }
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.core.ParameterizedTypeReference;
        import org.springframework.stereotype.Component;
        import org.springframework.web.reactive.function.client.WebClient;
        import org.springframework.web.reactive.function.server.*;
        import reactor.core.publisher.Mono;

        import java.util.List;
        import java.util.Optional;

@Component
public class ClientDemoHandler {

    private static final String GITHUB_BASE_URL = "https://api.github.com";

    public Mono<ServerResponse> handle1(ServerRequest req) {
        WebClient webClient = WebClient.create(GITHUB_BASE_URL);

        // 쿼리 스트링에 user가 포함되어 있는지 체크
        Optional<String> userName = req.queryParam("user");
        if (!userName.isPresent()) {
            return ServerResponse.ok().body(Mono.just("user is Required"), String.class);
        }

        // 비동기 호출(비블록)
        Mono<List<GithubRepository>> values = webClient.get()
                .uri("users/" + userName.get() + "/repos")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GithubRepository>>(){});

        return ServerResponse.ok().body(values, new ParameterizedTypeReference<List<GitHubRepository>>(){})
    }

    public RouterFunction<ServerResponse> routerule() {
        return RouterFunctions.route(RequestPredicates.GET("/func/sample9"), this::handle1);
    }
}
```
#### 전체 라우팅 설정

마지막으로 각 라우터 함수를 통합하는 빈을 클래스로 정의한다. RouterFunction<ServerResponse>를 반환하는 @Bean을 만들어 스프링에 함수형 웹플럭스 처리를 설정한다.
```java
@Bean
public RouterFunction<ServerResponse> route() {
    return this.demoGetHandler.routerule()
        .and(demoPostHandler.routerule)
        .and(fluxDemoHandler.routerule)
        .and(clientDemoHandler.routerule());
}
```

