package de.tobias.mcutils.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.lang.module.ModuleDescriptor;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class BukkitAutoUpdater {

    public BukkitLogger logger;
    public Plugin plugin;
    public String FILE_URL;
    public String YML_URL;
    public File updaterDir;

    public BukkitAutoUpdater(BukkitLogger baseLogger, Plugin pl, String jarFileURL, String pluginYMLUrl) {
        logger = new BukkitLogger("§7[§aUpdater§7] ", baseLogger);
        plugin = pl;
        FILE_URL = jarFileURL;
        YML_URL = pluginYMLUrl;
        updaterDir = new File(pl.getDataFolder(), "updater");
    }

    public String getYMLString() throws Exception {
        URL url = new URL(YML_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine + System.getProperty("line.separator"));
        }
        in.close();
        con.disconnect();
        return content.toString();
    }

    public String getCurrentVersion() {
        try {
            String ymlContent = getYMLString();
            int versionStart = ymlContent.indexOf("version");
            String versionLine = ymlContent.substring(versionStart);
            String version = versionLine.replace("version: ", "").split("\n")[0];
            return version;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "0.0";
        }
    }

    public Boolean downloadNewJAR() {
        try {
            URL website = new URL(FILE_URL);
            logger.info("§xDownloading file from '§6" + website.toString() + "§x'...");
            File newFile = new File(updaterDir, "update.jar");
            Files.copy(website.openStream(), Paths.get(newFile.toURI()), StandardCopyOption.REPLACE_EXISTING);
            logger.info("§xDownload complete!");
            return true;
        } catch (Exception ex) {
            logger.error("§xError while downloading update file :");
            ex.printStackTrace();
            return false;
        }
    }

    public Boolean overwriteOldJAR() {
        try {
            logger.warn("§xOverwriting old file (DO NOT ABORT!)...");
            File newJAR = new File(updaterDir, "update.jar");
            String filePath = plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File oldJAR = new File(filePath);

            InputStream in = new BufferedInputStream(new FileInputStream(newJAR));
            writeBytesFromInputStreamIntoFile(in, oldJAR);

            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                logger.warn("§xUpdate installed! Please restart!!!");
            }, 20L, 20L*60);
            return true;
        } catch (Exception ex) {
            logger.error("§xError while applying update file :");
            ex.printStackTrace();
            return false;
        }
    }

    public Boolean checkForUpdateAndUpdate() {
        if(!updaterDir.exists()) updaterDir.mkdirs();

        logger.info("§xSearching updates...");
        ModuleDescriptor.Version latest = ModuleDescriptor.Version.parse(getCurrentVersion().trim());
        ModuleDescriptor.Version installed = ModuleDescriptor.Version.parse(plugin.getDescription().getVersion());
        logger.info("§xLatest version is §b" + latest.toString() + " §x(current: §6" + installed.toString() + "§x)");

        if(latest.compareTo(installed) == 1) {
            logger.info("§xPerforming upgrade §x(§c" + installed + " §x--> §a" + latest.toString() + "§x)...");
            if(downloadNewJAR()) {
                if(overwriteOldJAR()) {
                    logger.info("§aPlugin has been updated!");
                    return true;
                } else {
                    logger.error("§xFailed to update Plugin!");
                }
            } else {
                logger.error("§xFailed to update Plugin!");
            }
        }
        return false;
    }

    public static boolean writeBytesFromInputStreamIntoFile(InputStream in, File f) {
        try {
            OutputStream outStream = new FileOutputStream(f);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            in.close();
            outStream.close();
            return true;
        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

}
