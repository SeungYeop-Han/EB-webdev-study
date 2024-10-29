import java.util.HashMap;

public class HttpRequest {

    private static final String CRLF = "\r\n";
    
    private String httpRequestMessage;
    
    // 요청줄
    private String method;
    private String url;
    private String version;

    // 헤더
    private HashMap<String, String> headers = new HashMap<>();

    // 본문
    private String body;
    private Multipart multipart;    // optional

    // 생성자
    public HttpRequest(String httpRequestMessage) {

        this.httpRequestMessage = httpRequestMessage;
        parse();
    }

    /**
     * HTTP 요청 메시지를 파싱합니다.
     * H
     * HTTP 구문 요소는 내부적으로 저장되며, getter 메서드로 가져올 수 있습니다.
     */
    public void parse() {

        // 주요 분리 지점 인덱스
        int idxRequestLineBegin = 0;
        int idxRequestLineEnd = httpRequestMessage.indexOf(CRLF);
        int idxHeaderBegin = idxRequestLineEnd + CRLF.length();
        int idxHeaderEnd = httpRequestMessage.indexOf(CRLF+CRLF);
        int idxBodyBegin = idxHeaderEnd + (CRLF.length() * 2);
        int idxBodyEnd = httpRequestMessage.length();

        // 요청줄-헤더-본문 분리
        String requestLine = httpRequestMessage.substring(idxRequestLineBegin, idxRequestLineEnd);
        String headerLines = httpRequestMessage.substring(idxHeaderBegin, idxHeaderEnd);
        String body = httpRequestMessage.substring(idxBodyBegin, idxBodyEnd);

        parseRequestLine(requestLine);
        parseHeader(headerLines);
        parseBody(body);
    }

    private void parseRequestLine(String requestLine) {

        // [HTTP Method](sp)[URL](sp)[HTTP Version]CRLF
        // * sp: 공백
        String[] requestLineTokens = requestLine.split(" ");
        this.method = requestLineTokens[0];
        this.url = requestLineTokens[1];
        this.version = requestLineTokens[2];
    }

    private void parseHeader(String headerLines) {

        // [Name]:(OWS)[Value](OWS)CRLF ... 
        // * OWS: Optional White Space
        String[] headers = headerLines.split(CRLF);
        for (String header : headers) {
            int idxColon = header.indexOf(':');
            String k = header.substring(0, idxColon);
            String v = header
                    .substring(idxColon + 1)
                    .trim();
            this.headers.put(k, v);
        }
    }

    private void parseBody(String body) {

        String contentType = this.headers.get("Content-Type");

        // Content-type 헤더와 본문은 항상 함께 존재함
        if (contentType == null) {
            body = null;
            return;
        }

        // Content-type 헤더는 다음의 값을 가질 수 있음
        //  1. 미디어 타입
        //      - 이 값은 항상 가장 먼저 명시되며, 별도의 이름이 붙지 않는다.
        //  2. charset
        //      - 문자 인코딩 표준
        //  3. boundary
        //      - 멀티파트 개체의 각 부분을 구분하기 위해 필수적으로 명시되어야 함.
        String[] contentTypeHeaderValues = contentType.split(";");
        String mediaType = contentTypeHeaderValues[0].trim();
        String charset = null;      // optional
        String boundary = null;     // optional, 멀티파트인 경우에만 필수
        for (String headerValue : contentTypeHeaderValues) {
            headerValue = headerValue.trim();

            var split = headerValue.split("=");
            String name = split[0];

            if (name.equals("charset")) {
                charset = split[1];
            } else if (name.equals("boundary")) {
                boundary = split[1];
            }
        }

        if (mediaType.startsWith("multipart")) {
            multipart = new Multipart(body, charset, boundary);
        }
    }

    /**
     * 요청이 멀티파트인 경우, 각 부분을 별도의 파일로 저장합니다. 
     */
    public void saveAllMultiparts(String path) {
        if (multipart != null) {
            multipart.saveAll(path);
        }
    }


    // Getters
    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }
}
