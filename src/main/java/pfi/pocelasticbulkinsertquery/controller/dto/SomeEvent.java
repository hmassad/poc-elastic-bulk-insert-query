package pfi.pocelasticbulkinsertquery.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SomeEvent {
    private Instant timestamp;
    private String localidad;
}
