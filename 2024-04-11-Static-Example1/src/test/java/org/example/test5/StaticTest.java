package org.example.test5;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StaticTest {

    @Test
    void plus(){

        int number1 = 1;
        int number2 = 2;

        int result = Calculator.plus(1,2);

        Assertions.assertEquals(result, number1 + number2);

    }


}

class Calculator{
    public static int plus(int num1, int num2){
        return num1 + num2;
    }
}
