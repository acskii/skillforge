package databases;

import models.Model;
import models.User;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Andrew :)

/*
    Parent abstract class for all databases
*/

public abstract class Database<T extends Model> {
    protected String logName;
    protected final String filename;
    protected final Class<T> classType;
    protected final ObjectMapper mapper = new ObjectMapper();
    protected final List<T> records = new ArrayList<>();
    protected int index = 1;

    public Database(String filename, Class<T> type) {
        this.filename = filename;
        this.classType = type;

        readFile();
        this.index = this.records.size();
    }

    public List<T> getRecords() {
        return this.records;
    }

    protected void insertRecord(T record) {
        if (record != null) {
            if (getRecordById(record.getId()) == null) {
                this.records.add(record);
            }
        }
    }

    public T getRecordById(int id) {
        for (T record : this.records) {
            if (record.getId() == id) {
                return record;
            }
        }

        return null;
    }

    public void deleteRecord(int id) {
        this.records.removeIf((record) -> record.getId() == id);
    }

    public void readFile() {
        /* Read records from JSON file */
        File file = new File(this.filename);

        if (!file.exists() || !file.getName().endsWith(".json")) {
            System.out.printf("[%s]: Unable to read file!\n", logName);
            return;
        }

        if (file.length() == 0) {
            mapper.writeValue(file, Collections.emptyList());
            return;
        }

        try {
            this.records.clear();
            this.records.addAll(this.mapper.readValue(file,
                    this.mapper.getTypeFactory().constructCollectionType(List.class, this.classType)
            ));
            System.out.printf("[%s]: Successfully read all records!\n", logName);
        } catch (Exception e) {
            System.out.printf("[%s]: Error occurred, check trace: %s\n", logName, e.getMessage());
        }
    }

    public void saveToFile() {
        /* Overwrite JSON file to store records */
        File file = new File(this.filename);
        this.mapper.writeValue(file, records);
    }


}
