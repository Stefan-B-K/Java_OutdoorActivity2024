package com.istef.OutdoorActivity2024.output;


import com.istef.OutdoorActivity2024.exceptios.OutputException;
import com.istef.OutdoorActivity2024.model.io.OutputData;
import org.jetbrains.annotations.Nullable;

/**
 * Definition of a type for data output,
 * compatible for use in the OutdoorActivity2024 project
 * @see OutputData
 */
public interface DataOutputer {
    void output(@Nullable final OutputData outputData) throws OutputException;
}
