package com.istef.OutdoorActivity2024.service.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.istef.OutdoorActivity2024.OutdoorActivityApp;
import com.istef.OutdoorActivity2024.exceptios.OutputException;
import com.istef.OutdoorActivity2024.model.io.StoreKind;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


/**
 * PersistenceService implementation through local file storage
 * and JSON serialization/deserialization
 *
 * @see PersistenceService
 */
public class FilePersistService implements PersistenceService {

    private static PersistenceService _instance;

    private final static String FILE_EXT = ".json";
    private final ObjectMapper objectMapper;

    private Map<String, Object> store;

    private FilePersistService() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static PersistenceService getInstance() {
        if (_instance == null) _instance = new FilePersistService();
        return _instance;
    }

    @Override
    @Nullable
    public <T> T getData(String storeId, Class<T> type, StoreKind storeKind) {
        T outputData = null;

        if (store != null && store.get(storeId) != null) {
            @SuppressWarnings("unchecked")
            T data = (T) store.get(storeId);
            return data;
        }

        File jsonFile = fileForStoreId(storeId, storeKind);
        if (!jsonFile.exists()) return null;

        try (BufferedReader br = new BufferedReader(new FileReader(jsonFile))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            if (sb.length() == 0) return null;

            if (store == null) store = new HashMap<>();

            outputData = objectMapper.readValue(sb.toString(), type);
            store.put(storeId, outputData);

        } catch (IOException | ClassCastException e) {
            System.err.println(e.getMessage());
        }
        return outputData;
    }

    @Override
    public void saveData(Object newData, StoreKind storeKind) throws OutputException {

        if (store == null) store = new HashMap<>();

        String storeId = newData.getClass().getSimpleName();
        store.put(storeId, newData);

        File jsonFile = fileForStoreId(storeId, storeKind);


        try {
            if (!jsonFile.getParentFile().exists() && !jsonFile.getParentFile().mkdirs())
                throw new IOException("Failed to create folder " + jsonFile.getParentFile());

            if (!jsonFile.exists() && !jsonFile.createNewFile())
                throw new IOException("Failed to create file " + jsonFile.getName());
            objectMapper.writeValue(jsonFile, newData);
        } catch (IOException e) {
            throw new OutputException(e.getMessage());
        }
        System.out.println("Changed data saved: " + storeId);
    }

    /**
     * @param storeId use camelCase values, preferably - the simple class name of the data type, for example:
     *                <blockquote><pre>OutputData class.getSimpleName()</pre></blockquote>
     * @return the json file that will store the serialized data.
     */
    private File fileForStoreId(String storeId, StoreKind storeKind) {
        String fileName = PersistenceService.camelToSnake(storeId);
        return Path.of(OutdoorActivityApp.APP_DIR_PATH,
                storeKind.name().toLowerCase(),
                fileName + FILE_EXT).toFile();
    }


}
