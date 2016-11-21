package org.allesoft.messenger;

import org.allesoft.messenger.jclient.RosterItem;
import org.allesoft.messenger.swingui.SwingUI;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.core.matcher.FrameMatcher;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.launcher.ApplicationLauncher;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
        fixture.button("addContactButton").click();

        FrameFixture addFixture = WindowFinder.findFrame(addContactMatcher).using(robot);
        addFixture.textBox("userIdField").setText("");
        addFixture.textBox("userIdField").enterText("new user");
        assertTrue(addFixture.component().isVisible());
        addFixture.button("addContactDoneButton").click();
        //addFixture.requireNotVisible();
        assertTrue(!addFixture.component().isVisible());

        //assertEquals(fixture.table().cell("new user").toString(), "new user");
        fixture.table().requireCellValue(fixture.table().cell("new user"), "new user");
        assertEquals(
                ((RosterItem)(
                        fixture
                                .table()
                                .component()
                                .getModel()
                                .getValueAt(7, 1)))
                        .getValue(),
                "new user");
    }
}
