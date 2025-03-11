import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NginxDataReaderTest {

    
    @Test
    public void testRegex() {
        
        String line = "2607:f298:2:120::d6b:5f75 - - [02/Aug/2011:08:05:00 -0700] \"OPTIONS * HTTP/1.0\" 200 136 \"-\" \"Apache (internal dummy connection)\"";
        
        assertTrue(NginxDataReader.LOG_PATTERN.matcher(line).matches());
        
        assertTrue(NginxDataReader.parseLine(line).isPresent());
    }


}
