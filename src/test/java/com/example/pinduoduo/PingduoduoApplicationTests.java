package com.example.pinduoduo;

import com.example.pinduoduo.selenium.CustomerInfoEmulator;
import org.junit.jupiter.api.Test;

public class PingduoduoApplicationTests {

    @Test
    public void manualExport() {
        new CustomerInfoEmulator().simulation();
    }

}
