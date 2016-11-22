package org.allesoft.messenger;

import org.allesoft.messenger.jclient.RosterItem;
import org.allesoft.messenger.swingui.SwingUI;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.core.matcher.FrameMatcher;
import org.fest.swing.finder.WindowFinder;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTableFixture;
import org.fest.swing.launcher.ApplicationLauncher;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by kabramovich on 21.11.2016.
 */
public class UITest {

    private Robot robot;
    private FrameMatcher matcher;
    private FrameMatcher addContactMatcher;

    @BeforeClass
    public void prepare() throws Exception {
        System.out.println(new File(".").getAbsolutePath());
        new File("./app-test-1/roster").delete();
        ApplicationLauncher.application(SwingUI.class)
                .withArgs("app-test-1").start();
        robot = BasicRobot.robotWithCurrentAwtHierarchy();
        matcher = FrameMatcher.withName("mainWin");
        addContactMatcher = FrameMatcher.withName("addWin").andShowing();
    }

    @Test
    public void checkNewUser() throws Exception {
        String newUserName = "new user";

        FrameFixture fixture = WindowFinder.findFrame(matcher).using(robot);
        createUser(newUserName, fixture, true, null);
        createUser(newUserName, fixture, false, "Duplicated user name");

        //assertEquals(fixture.table().cell("new user").toString(), "new user");
        JTableFixture tableFixture = fixture.table();
        tableFixture.requireCellValue(tableFixture.cell(newUserName), newUserName);
        Thread.sleep(5000l);
        assertEquals(
                ((RosterItem) (
                        tableFixture
                                .component()
                                .getModel()
                                .getValueAt(0, 1)))
                        .getValue(),
                newUserName);
    }

    private void createUser(String newUserName, FrameFixture fixture, boolean expectSuccess,
                            String expectedErrorMessage) {
        fixture.button("connectButton").click();
        fixture.button("addContactButton").click();

        FrameFixture addFixture = WindowFinder.findFrame(addContactMatcher).using(robot);
        addFixture.textBox("userIdField").setText("");
        addFixture.textBox("userIdField").enterText(newUserName);
        assertTrue(addFixture.component().isVisible());
        addFixture.button("addContactDoneButton").click();
        //addFixture.requireNotVisible();
        if (expectSuccess) {
            assertTrue(!addFixture.component().isVisible());
        } else {
            addFixture.label().foreground().requireEqualTo(Color.RED);
            addFixture.label().requireText(expectedErrorMessage);
            addFixture.close();
        }
    }

    @Test
    public void checkUsernameAllowedSymbols() throws Exception {
        String newUserName = "";

        FrameFixture fixture = WindowFinder.findFrame(matcher).using(robot);
        createUser(newUserName, fixture, false, "Empty user name");
        Thread.sleep(5000l);
    }
}
