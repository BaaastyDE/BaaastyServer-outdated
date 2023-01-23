package de.baaasty.baaastyserver.file.type;

import de.baaasty.baaastyserver.file.FileRepository;

import java.io.File;
import java.util.LinkedHashMap;

public class ConfigFile extends FileRepository {
    public ConfigFile(File folder) {
        super("Config", folder);
    }

    public void setupDefault() {
        initFile();

        LinkedHashMap<String, Object> configData = getData();

        configData.putIfAbsent("algorithmSecret", "S3CR3T");
        configData.putIfAbsent("adminToken", "T0K3N");

        setData(configData);
        dump();
    }

    public String algorithmSecret() {
        return (String) getData().get("algorithmSecret");
    }

    public String adminToken() {
        return (String) getData().get("adminToken");
    }
}
