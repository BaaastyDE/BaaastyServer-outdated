package de.baaasty.baaastyserver.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class FileRepository {
    private final Logger logger = LoggerFactory.getLogger(FileRepository.class.getName());
    private final String name;
    private final File folder;
    private File file;
    private Yaml yaml;
    private LinkedHashMap<String, Object> data;

    public FileRepository(String name, File folder) {
        this.name = name;
        this.folder = folder;
    }

    public void initFile() {
        try {
            if (!folder.exists())
                folder.mkdir();

            this.file = new File(folder.getPath(), name + ".yml");

            if (!file.exists())
                if (file.createNewFile())
                    logger.info("The '" + name + ".yml' was created successfully");

            DumperOptions options = new DumperOptions();
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            yaml = new Yaml(options);

            load();
        } catch (IOException exception) {
            logger.warn("Failed to create the '" + name + ".yml'");
        }
    }

    public LinkedHashMap<String, Object> data() {
        return data;
    }

    public void data(LinkedHashMap<String, Object> data) {
        this.data = data;
    }

    public void dump() {
        try {
            yaml.dump(data, new FileWriter(file, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            logger.warn("Failed to update the '" + name + ".yml'");
        }
    }

    public void load() {
        try {
            LinkedHashMap<String, Object> loadedData = yaml.load(new FileReader(file, StandardCharsets.UTF_8));

            data = (loadedData == null ? new LinkedHashMap<>() : loadedData);
        } catch (IOException exception) {
            logger.warn("Failed to load the '" + name + ".yml'");
        }
    }

    public String name() {
        return name;
    }
}