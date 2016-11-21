package org.allesoft.messenger;

import org.allesoft.messenger.swingui.SwingUI;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.core.matcher.FrameMatcher;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.launcher.ApplicationLauncher;
import org.testng.annotations.Test;

/**
 * Created by kabramovich on 21.11.2016.
 */
public class UITest {

    @Test
    public void testUI() throws Exception {
        ApplicationLauncher.application(SwingUI.class)
                .withArgs("app-test-1").start();
        Robot robot = BasicRobot.robotWithCurrentAwtHierarchy();
        FrameMatcher matcher = FrameMatcher.withName("mainWin");
        FrameMatcher addContactMatcher = FrameMatcher.withName("addWin");
        FrameFixture fixture = WindowFinder.findFrame(matcher).using(robot);
        fixture.button("connectButton").click();
        Thread.sleep(1000l);
        fixture.button("addContactButton").click();
        Thread.sleep(1000l);
        fixture = WindowFinder.findFrame(addContactMatcher).using(robot);
        fixture.textBox("userIdField").enterText("new user");
        Thread.sleep(1000l);
        fixture.button("addContactDoneButton").click();

        for (;;);
    }
}
