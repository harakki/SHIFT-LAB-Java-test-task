package dev.harakki.shiftlab;

import org.springframework.boot.SpringApplication;

public class TestShiftLabApplication {

    public static void main(String[] args) {
        SpringApplication.from(ShiftLabApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
