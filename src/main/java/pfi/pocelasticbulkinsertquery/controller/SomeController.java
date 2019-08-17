package pfi.pocelasticbulkinsertquery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pfi.pocelasticbulkinsertquery.controller.dto.SomeAlert;
import pfi.pocelasticbulkinsertquery.controller.dto.SomeEvent;
import pfi.pocelasticbulkinsertquery.model.Alert;
import pfi.pocelasticbulkinsertquery.model.Event;
import pfi.pocelasticbulkinsertquery.model.Localidad;
import pfi.pocelasticbulkinsertquery.service.ElasticSearchService;

import java.io.IOException;
import java.time.Instant;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class SomeController {

    private final ElasticSearchService elasticSearchService;

    @Autowired
    public SomeController(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @RequestMapping(method = GET, path = "/status")
    public boolean getStatus() {
        return true;
    }

    @RequestMapping(method = POST, path = "/events")
    public SomeAlert processSomeEvent(SomeEvent someEvent) throws IOException, InterruptedException {
        Localidad localidad = Localidad.findByName(someEvent.getLocalidad());
        Alert alert = elasticSearchService.processEvent(new Event(
                someEvent.getTimestamp().toEpochMilli(),
                localidad.getId(),
                localidad.getName(),
                localidad.getProvincia().getId(),
                localidad.getProvincia().getName(),
                localidad.getProvincia().getPais().getId(),
                localidad.getProvincia().getPais().getName()));
        return new SomeAlert(
                alert.getLocalidad(),
                alert.getCasosLocalidad(),
                alert.getProvincia(),
                alert.getCasosProvincia(),
                alert.getPais(),
                alert.getCasosPais(),
                alert.isThreatThresholdReached());
    }

    @RequestMapping(method = POST, path = "/bulk")
    public void generateBulk() throws IOException, InterruptedException {
        for (Localidad localidad : Localidad.values()) {
            for (int i = 0; i < 1; i++) {
                elasticSearchService.processEvent(new Event(
                        Instant.now().toEpochMilli(),
                        localidad.getId(),
                        localidad.getName(),
                        localidad.getProvincia().getId(),
                        localidad.getProvincia().getName(),
                        localidad.getProvincia().getPais().getId(),
                        localidad.getProvincia().getPais().getName()));
            }
        }
    }
}
