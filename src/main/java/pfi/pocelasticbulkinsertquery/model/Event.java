package pfi.pocelasticbulkinsertquery.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Event {
    private long timestamp;
    private int localidadId;
    private String localidad;
    private int provinciaId;
    private String provincia;
    private int paisId;
    private String pais;
}
