package changs.android.widget.datepick;

import org.junit.Test;

import java.util.Calendar;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {

        final Calendar instance1 = Calendar.getInstance();
        final Calendar instance2 = Calendar.getInstance();

        instance1.set(Calendar.MILLISECOND,0);
        instance2.set(Calendar.MILLISECOND,0);
        instance1.set(2004, 1, 1, 0, 0, 0);
        instance2.set(2004, 0, 1, 0, 0, 0);
        final int i = instance1.compareTo(instance2);

        System.out.println("i = " + i);

    }
}