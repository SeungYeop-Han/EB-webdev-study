package httprequestparser;

import static httprequestparser.HttpSeparator.CRLF;
import static httprequestparser.HttpSeparator.EQUALS;
import static httprequestparser.HttpSeparator.SEMICOLON;
import static httprequestparser.HttpSeparator.BOUNDARY_BEGIN;
import static httprequestparser.HttpSeparator.BOUNDARY_END;
import static httprequestparser.HttpSeparator.COLON;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpHeaders;

public class Multipart {

    List<Part> parts = new ArrayList<>();

    public Multipart(String body, String charset, String boundary) {

        String[] rawParts = body.split(BOUNDARY_BEGIN + boundary);
        for (String rawPart : rawParts) {
            
            if (rawPart.equals("")) {
                continue;   // 첫 토큰은 빈 문자열임
            } else if (rawPart.startsWith(BOUNDARY_END)) {
                continue;   // 구분자 뒤에 -- 가 붙으면 끝을 나타냄
            } else {
                parts.add(new Part(rawPart));
            }
        }
    }

    public void saveAll(String path) {
        for (Part part : parts) {
            part.tofile(path);
        }
    }

    private class Part {

        private String rawPart;

        private HashMap<String, String> headers = new HashMap<>();
        private String contentDispositionName;
        private String contentDispositionFilename;
        private String content;

        Part(String rawPart) {

            this.rawPart = rawPart.trim();
            parse();
        }

        void parse() {

            int idxHeaderEnd = rawPart.indexOf(CRLF+CRLF);
            int idxBodyBegin = idxHeaderEnd + 2;

            String[] headerLines = rawPart
                    .substring(0, idxHeaderEnd)
                    .split(CRLF);

            for (String header : headerLines) {
                int iColon = header.indexOf(COLON);
                String k = header.substring(0, iColon);
                String v = header
                        .substring(iColon + 1)
                        .trim();    // OWS 제거
                this.headers.put(k, v);
            }

            // multipart/form-data 본문에서의 Content-Disposition 일반 헤더의 경우 다음의 값을 가질 수 있음
            //  1. 미디어 타입
            //      - 이 값은 항상 먼저 명시되며, 별도의 이름이 붙지 않는다.
            //      - 이 값은 언제나 "form-data" 이다.
            //  2. name
            //      - 이 하위 파트가 참조하는 폼의 HTML 필드에서 사용한 그 이름
            //  3. filename
            //      - 전송된 해당 파일의 원래 이름
            String[] contentDispositionValues = this.headers.get(HttpHeaders.CONTENT_DISPOSITION).split(SEMICOLON);

            final String NAME = "name";
            final String FILENAME = "filename";

            for (String headerValues : contentDispositionValues) {

                headerValues = headerValues.trim();

                var split = headerValues.split(EQUALS);
                String name = split[0];

                if (name.equals(NAME)) {
                    contentDispositionName = split[1];
                } else if (name.equals(FILENAME)) {
                    contentDispositionFilename = split[1];
                }
            }

            content = rawPart.substring(idxBodyBegin).trim();
        }

        public void tofile(String path) {

            if (!path.endsWith(File.separator)) {
                path += File.separator;
            }

            try (FileWriter writer = 
                    new FileWriter(path + contentDispositionFilename)) {

                writer.write(content);
            } catch (IOException e) {
                System.err.println("파일 [" + path + contentDispositionFilename + "] 쓰기 실패");
                System.err.println(e.getMessage());
            }
        }
    }
}
