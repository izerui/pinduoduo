package com.example.pingduoduo;

import com.example.pingduoduo.selenium.CustomerInfoEmulator;
import org.junit.jupiter.api.Test;

public class PingduoduoApplicationTests {

    @Test
    public void manualExport() {
        new CustomerInfoEmulator().simulation();
    }

}
