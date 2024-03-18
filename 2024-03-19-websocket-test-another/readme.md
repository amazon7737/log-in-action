### WebSocket을 사용하는 이유
실시간 채팅 앱과 같은 요구사항을 반영하기 위해 클라이언트에서 매번 HTTP 프로토콜을 통해 서버에 요청하는 것은 비효율적이다.

클라이언트에서 Ajax 통신 등으로 일부 보완할 수 있지만, Ajax도 결국 HTTP를 사용하기 때문에 여전히 문제가 되며, 웹 소켓을 통해 이를 해결할 수 있다.

### WebSocket의 특징
WebSOcket은 HTTP를 통해 최초 연결되며, 이후 일정 시간이 지나면 HTTP 연결은 자동으로 끊어지고 webSOcket connection은 유지된다.

HTTP와 달리 WebSOcket은 stateful 프로토콜이다.

HTTP는 stateless하기 때문에 서버에 변경사항이 생겨도 클라이언트에서 요청을 하지 않으면 변경사항이 적용되지 않지만, WebSocket은 지속적으로 connection을 유지하기 때문에 실시간으로 변경사항이 적용된다.

WebSocket은 HTTP와 동일한 port(80)를 사용한다.

### WebSocket 사용 시 주의점
WebSocket은 stateful 프로토콜로써 connection을 항상 유지해야하기 때문에 트래픽이 많은 경우 서버에 부담이 될 수 있다.
연결이 계속 유지되어야 하기 때문에, 연결이 끊어졌을 때 적절히 대응할 수 있어야 한다.



