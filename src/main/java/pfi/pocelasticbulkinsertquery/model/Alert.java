package pfi.pocelasticbulkinsertquery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    private String localidad;
    private int casosLocalidad;
    private String provincia;
    private int casosProvincia;
    private String pais;
    private int casosPais;
    private boolean threatThresholdReached;
}
