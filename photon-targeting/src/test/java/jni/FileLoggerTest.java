/*
 * MIT License
 *
 * Copyright (c) PhotonVision
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package wpiutil_extras;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import edu.wpi.first.hal.HAL;
import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.photonvision.common.hardware.Platform;
import org.photonvision.jni.PhotonTargetingJniLoader;
import org.photonvision.jni.QueuedFileLogger;
import org.photonvision.jni.WpilibLoader;

public class FileLoggerTest {
    @BeforeAll
    public static void load_wpilib() throws UnsatisfiedLinkError, IOException {
        if (!WpilibLoader.loadLibraries()) {
            fail();
        }
        if (!PhotonTargetingJniLoader.load()) {
            fail();
        }

        HAL.initialize(1000, 0);
    }

    @AfterAll
    public static void teardown() {
        HAL.shutdown();
    }

    @Test
    public void smoketest() throws InterruptedException {
        assumeTrue(Platform.isLinux());

        var logger = new QueuedFileLogger("/var/log/kern.log");
        for (int i = 0; i < 1; i++) {
            for (var line : logger.getNewlines()) {
                System.out.println(" ->:" + line);
            }
        }

        logger.stop();
    }
}
