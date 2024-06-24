import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;


public class ThingTest
{
    @Test
    public void testWriteFileException() {
        Thing thing = new Thing() {
            @Override
            protected Writer createWriter() {
                return new Writer() {
                    @Override
                    public int write(String f) throws IOException {
                        throw new IOException();
                    }
                };
            }
        };
        assertFalse(thing.writeFile("file"));
    }


}
