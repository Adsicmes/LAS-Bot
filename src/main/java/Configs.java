import org.dtools.ini.BasicIniFile;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniSection;

import java.io.File;
import java.io.IOException;

public class Configs {
    public static class env{
        public static class superuser{
            public static final int qq;
            static {
                IniFile iniFile = new BasicIniFile();
                File file = new File("src/main/config/env.ini");
                IniFileReader red = new IniFileReader(iniFile, file);
                try {
                    red.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                IniSection iniSection = iniFile.getSection("superuser");
                qq = Integer.parseInt(iniSection.getItem("qq").getValue());
            }
        }
        public static class MySql{
            public static final String driver;
            public static final String jdbc;
            public static final String user;
            public static final String passwd;
            static {
                IniFile iniFile = new BasicIniFile();
                File file = new File("src/main/config/env.ini");
                IniFileReader red = new IniFileReader(iniFile, file);
                try {
                    red.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                IniSection iniSection = iniFile.getSection("MySql");
                driver = iniSection.getItem("driver").getValue();
                jdbc = iniSection.getItem("jdbc").getValue();
                user = iniSection.getItem("user").getValue();
                passwd = iniSection.getItem("passwd").getValue();
            }
        }
    }
}
