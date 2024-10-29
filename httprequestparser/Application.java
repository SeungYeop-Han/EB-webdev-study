import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Application {
    
    public static void main(String[] args) {
        
        String httpRequestMsg = receive();
        if (httpRequestMsg == null) {
            System.err.println("요청 메시지를 수신하지 못했습니다.");
            System.exit(0);
        }

        HttpRequest httpRequest = new HttpRequest(httpRequestMsg);
        httpRequest.saveAllMultiparts("./out");
    }

    /**
     * HTTP 요청 메시지 수신(블록 됨)
     * @return 수신한 메시지
     */
    public static String receive() {

        String httpRequestMessage = null;
        String targetFilename = "./in/dummyrequest.txt";

        try (FileReader reader = new FileReader(targetFilename)) {

            char[] buffer = new char[8 * 1024];
            StringBuffer sb = new StringBuffer();
            
            int sizeRead;
            while ((sizeRead = reader.read(buffer)) > 0) {
                sb.append(buffer, 0, sizeRead);
            }

            httpRequestMessage = sb.toString();
            
        } catch (FileNotFoundException e) {
            System.err.println("파일 " + targetFilename + "을 찾을 수 없습니다.");
        } catch (IOException e) {
            System.err.println("파일 " + targetFilename + "을 읽는 도중에 문제가 발생했습니다.");
        }

        return httpRequestMessage;
    }
}
