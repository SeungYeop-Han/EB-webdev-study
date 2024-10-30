import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

public class Application {

    private static final String APP_ROOT = 
            System.getProperty("user.dir");

    private static final String DUMMY_FILE_PATH
            = APP_ROOT + File.separator + "in" + File.separator + "dummyrequest.txt";

    private static final String UPLOAD_PATH
            = APP_ROOT + File.separator + "out";
    
    public static void main(String[] args) {
        
        String httpRequestMsg = receive();
        if (httpRequestMsg == null) {
            System.err.println("요청 메시지를 수신하지 못했습니다.");
            System.exit(0);
        }

        HttpRequest httpRequest = new HttpRequest(httpRequestMsg);
        httpRequest.saveAllMultiparts(UPLOAD_PATH);
    }

    /**
     * HTTP 요청 메시지 수신(블록 됨)
     * @return 수신한 메시지
     */
    public static String receive() {

        String httpRequestMessage = null;

        try (FileReader reader = new FileReader(DUMMY_FILE_PATH)) {

            char[] buffer = new char[8 * 1024];
            StringBuffer sb = new StringBuffer();
            
            int sizeRead;
            while ((sizeRead = reader.read(buffer)) > 0) {
                sb.append(buffer, 0, sizeRead);
            }

            httpRequestMessage = sb.toString();
            
        } catch (FileNotFoundException e) {
            System.err.println("파일 " + DUMMY_FILE_PATH + "을 찾을 수 없습니다.");
        } catch (IOException e) {
            System.err.println("파일 " + DUMMY_FILE_PATH + "을 읽는 도중에 문제가 발생했습니다.");
        }

        return httpRequestMessage;
    }
}
