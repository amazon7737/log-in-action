package org.example.test5;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class AbstractTest {


    @Test
    public void CarSpeedUpAndDownTest(){
        Car[] carList = new Car[5];

        carList[0] = new Car("재규어");
        carList[1] = new Sedan("소나타" , 20);


        carList[1].setSpeedUp();
        carList[1].setSpeedUp();
        carList[1].setSpeedUp();
        log.info("sedan : {}", carList[1].getSpeed());


    }


}


/**
 * 그냥 자동차
 */
class Car {
    private String carname;

    protected int speed;

    public Car(){

    }

    public Car(String carname){
        this.carname = carname;
        this.speed = 100;
    }

    public String getName(){
        return this.carname;
    }

    public int getSpeed(){
        return this.speed;
    }

    public void setSpeedUp(){
        speed += 10;
    }

    public void setSpeedDown(){
        speed -= 10;
    }
}

/**
 * 세단 자동차
 *
 */
class Sedan extends Car{
    private String carname;

    public Sedan(){

    }

    public Sedan(String carname, int speed){
        this.speed = speed;
        this.carname = carname;
    }

    public String getName(){
        return this.carname;
    }

    public int getSpeed(){
        return this.speed;
    }

    public void setSpeedUp(){
        speed += 20;
    }

    public void setSpeedDown(){
        speed -= 20;
    }

}

/**
 * 트럭
 *
 */
class Truck extends Car{
    private String carname;

    public Truck(){
    }

    public Truck(String carname, int speed){
        this.carname = carname;
        this.speed = speed;
    }
    public String getName(){
        return this.carname;
    }

    public int getSpeed(){
        return this.speed;
    }

    public void getSpeedUp(){
        super.speed += 5;
    }

    public void getSpeedDown(){
        super.speed -= 5;
    }

}
