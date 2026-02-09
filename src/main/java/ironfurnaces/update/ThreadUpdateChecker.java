package ironfurnaces.update;

import ironfurnaces.loaders.forge.ForgeEntrypoint;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

/**
 * Credits: Ellpeck, creator of the Actually Additions update checker, which I (pizzaatime) modified.
 * Link to the Actually Additions repo: https://github.com/Ellpeck/ActuallyAdditions/
 * Link to the Actually Additions curse page: https://minecraft.curseforge.com/projects/actually-additions
 */
public class ThreadUpdateChecker extends Thread {

    public ThreadUpdateChecker() {
        this.setName("Iron Furnaces Update Checker");
        this.setDaemon(true);
        this.start();
    }

    @Override
    public void run() {

        ForgeEntrypoint.LOGGER.info("Starting Update Check...");
        try {
            URL newestURL = new URL("https://raw.githubusercontent.com/Qelifern/IronFurnaces/" + ForgeEntrypoint.GITHUB_BRANCH + "/update/updateVersions.properties");
            Properties updateProperties = new Properties();
            updateProperties.load(new InputStreamReader(newestURL.openStream()));

            String currentMcVersion = ForgeEntrypoint.MC_VERSION;
            String newestVersionProp = updateProperties.getProperty(currentMcVersion);

            UpdateChecker.updateVersionInt = Integer.parseInt(newestVersionProp);
            UpdateChecker.updateVersionString = currentMcVersion + "-release" + newestVersionProp;

            int clientVersion = Integer.parseInt(ForgeEntrypoint.VERSION);
            if (UpdateChecker.updateVersionInt > clientVersion) {
                UpdateChecker.needsUpdateNotify = true;
            }

            ForgeEntrypoint.LOGGER.info("Update Check done!");
        } catch (Exception e) {
            ForgeEntrypoint.LOGGER.error("Update Check failed!", e);
            UpdateChecker.checkFailed = true;
        }

        if (!UpdateChecker.checkFailed) {
            if (UpdateChecker.needsUpdateNotify) {
                ForgeEntrypoint.LOGGER.info("There is an Update for Iron Furnaces available!");
                ForgeEntrypoint.LOGGER.info("Current Version: " + ForgeEntrypoint.MC_VERSION + "-" + ForgeEntrypoint.VERSION + ", newest Version: " + UpdateChecker.updateVersionString + "!");
                ForgeEntrypoint.LOGGER.info("View the Changelog at " + UpdateChecker.CHANGELOG_LINK);
                ForgeEntrypoint.LOGGER.info("Download at " + UpdateChecker.DOWNLOAD_LINK);
            } else {
                ForgeEntrypoint.LOGGER.info("Iron Furnaces is up to date!");
            }
        }

        UpdateChecker.threadFinished = true;
    }

}
