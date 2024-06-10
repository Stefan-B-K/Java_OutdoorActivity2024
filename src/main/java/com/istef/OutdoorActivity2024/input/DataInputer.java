package com.istef.OutdoorActivity2024.input;

import com.istef.OutdoorActivity2024.exceptios.InputException;
import com.istef.OutdoorActivity2024.model.io.InputData;

/**
 * Definition of a type for data input,
 * compatible for use in the OutdoorActivity2024 project
 * @see InputData
 */
public interface DataInputer {
   InputData getInputs() throws InputException;
}
