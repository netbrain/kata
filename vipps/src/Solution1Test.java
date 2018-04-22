import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class Solution1Test {
    @Test
    public void TestSolution() {
        String[] ss = new String[]{
                "00-44  48 5555 8361",
                "0 - 22 1985--324",
                "555372654",
                "55",
                "555",
                "5555",
                "55-----4-----55",
        };

        String[] expected = new String[]{
                "004-448-555-583-61",
                "022-198-53-24",
                "555-372-654",
                "55",
                "555",
                "55-55",
                "554-55"

        };

        for (int i = 0; i < ss.length; i++) {
            String result = new Solution1().solution(ss[i]);
            Assertions.assertEquals(expected[i], result, "I = " + i);
        }

    }

}