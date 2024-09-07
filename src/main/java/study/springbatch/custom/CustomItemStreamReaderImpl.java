package study.springbatch.custom;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.web.client.RestTemplate;

/**
 * 실제 작동 안함 그냥 이렇게 굴러간다 하는 슈도코드
 */
public class CustomItemStreamReaderImpl implements ItemStreamReader<String> {

    private final RestTemplate restTemplate;
    private final String CURRENT_ID_KEY = "current.call.id";
    private final String API_URL = "https://www.devyummi.com/page?id=";
    private int currentId;


    public CustomItemStreamReaderImpl(RestTemplate restTemplate) {

        this.currentId = 0;
        this.restTemplate = restTemplate;
    }


    /**
     * 최초 실행
     */
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        // 내가 마지막에 실행되었던 부분 찾는 부분.
        if(executionContext.containsKey(CURRENT_ID_KEY)) {
            currentId = executionContext.getInt(CURRENT_ID_KEY);
        }
    }


    // 읽기 수행
    @Override
    public String read() throws Exception {

        currentId++;

        String url = API_URL + currentId;
        String response = restTemplate.getForObject(url, String.class);

        return response;
    }


    // 성공 부분 기록
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt(CURRENT_ID_KEY, currentId);
    }


    @Override
    public void close() throws ItemStreamException {
        // 파일 읽기 수행이었다면 여기서 초기화
    }
}
