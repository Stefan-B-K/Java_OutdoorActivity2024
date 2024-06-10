package com.istef.OutdoorActivity2024.service.persistence;

import com.istef.OutdoorActivity2024.exceptios.OutputException;
import com.istef.OutdoorActivity2024.model.io.StoreKind;

import javax.annotation.Nullable;


/**
 * Definition of a service providing persistence
 * for use in the OutdoorActivity2024 project
 */
public interface PersistenceService {

    /**
     * @param storeId   the String identifier of the data type
     * @param type      the type of the data object
     * @param storeKind the StoreKind for the data
     * @return the previously persisted data
     */
    @Nullable
    <T> T getData(String storeId, Class<T> type, StoreKind storeKind);

    /**
     * @param newData   the latest (updated) data
     * @param storeKind the StoreKind for the data
     */
    void saveData(Object newData, StoreKind storeKind) throws OutputException;


    static String camelToSnake(String camelCase) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        return camelCase
                .replaceAll(regex, replacement)
                .toLowerCase();
    }

}
