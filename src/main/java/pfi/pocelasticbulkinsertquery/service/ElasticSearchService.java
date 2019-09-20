package pfi.pocelasticbulkinsertquery.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pfi.pocelasticbulkinsertquery.model.Alert;
import pfi.pocelasticbulkinsertquery.model.Event;
import pfi.pocelasticbulkinsertquery.model.Localidad;
import pfi.pocelasticbulkinsertquery.model.Provincia;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class ElasticSearchService {

    private final ObjectMapper objectMapper;
    @Value("${elasticsearch-host}")
    private String elasticsearchHost;
    private RestHighLevelClient client;

    @Autowired
    public ElasticSearchService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws IOException {
        log.warn("elasticsearch-host: " + elasticsearchHost);

        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(elasticsearchHost, 9200, "http")));

        // verificar que existan los índices, y crear los que no estén
        for (Provincia provincia : Provincia.values()) {
            String index = "provincia-" + provincia.getId();
            if (!client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT)) {
                // crear índice
                client.indices().create(new CreateIndexRequest(index), RequestOptions.DEFAULT);

                // configurar field mapping https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-put-mapping.html
                PutMappingRequest putMappingRequest = new PutMappingRequest(index);
//                putMappingRequest.type("_doc");
                XContentBuilder builder = XContentFactory.jsonBuilder();
                builder.startObject();
                {
                    builder.startObject("properties");
                    {
                        builder.startObject("timestamp");
                        {
                            builder.field("type", "date");
                        }
                        builder.endObject();

                        builder.startObject("localidadId");
                        {
                            builder.field("type", "keyword");
                        }
                        builder.endObject();

                        builder.startObject("localidad");
                        {
                            builder.field("type", "keyword");
                        }
                        builder.endObject();

                        builder.startObject("provinciaId");
                        {
                            builder.field("type", "keyword");
                        }
                        builder.endObject();

                        builder.startObject("provincia");
                        {
                            builder.field("type", "keyword");
                        }
                        builder.endObject();

                        builder.startObject("paisId");
                        {
                            builder.field("type", "keyword");
                        }
                        builder.endObject();

                        builder.startObject("pais");
                        {
                            builder.field("type", "keyword");
                        }
                        builder.endObject();
                    }
                    builder.endObject();
                }
                builder.endObject();
                putMappingRequest.source(builder);
                client.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);
            }
        }
    }

    @PreDestroy
    public void destroy() throws IOException {
        client.close();
    }

    public Alert processEvent(Event event) throws IOException, InterruptedException {
        // meter evento en elastic
        insert(event);

        // cuando se inserta un evento atrás de otro, la respuesta de _count es inválida
        Thread.sleep(100);

        // consultar a elastic el ranking por país, provincia y localidad
        return query(event.getLocalidadId());
    }

    private Alert query(int localidadId) throws IOException {

        Localidad localidad = Localidad.findById(localidadId);

        // hay que pdirlo así porque la versión 6.4 de la api de elastic no tiene Count API
        // GET /provincia-*/_count?q=paisId:1
        HttpEntity paisResponse = client.getLowLevelClient().performRequest(new Request("GET", "/provincia-*/_count?q=paisId:" + localidad.getProvincia().getPais().getId())).getEntity();
        // GET /provincia-*/_count?q=provinciaId:24
        HttpEntity provinciaResponse = client.getLowLevelClient().performRequest(new Request("GET", "/provincia-*/_count?q=provinciaId:" + localidad.getProvincia().getId())).getEntity();
        // GET /provincia-*/_count?q=localidadId:1
        HttpEntity localidadResponse = client.getLowLevelClient().performRequest(new Request("GET", "/provincia-*/_count?q=localidadId:" + localidadId)).getEntity();

        return new Alert(
                localidad.getName(),
                (Integer) objectMapper.readValue(EntityUtils.toString(localidadResponse), Map.class).get("count"),
                localidad.getProvincia().getName(),
                (Integer) objectMapper.readValue(EntityUtils.toString(provinciaResponse), Map.class).get("count"),
                localidad.getProvincia().getPais().getName(),
                (Integer) objectMapper.readValue(EntityUtils.toString(paisResponse), Map.class).get("count"),
                false
        );
    }

    private void insert(Event event) throws IOException {
        //log.info("insertar: " + event.toString());

        String index = "provincia-" + event.getProvinciaId();

        IndexRequest request = new IndexRequest(index)
                .source(objectMapper.writeValueAsString(event), XContentType.JSON)
                ;//.type("_doc");
        try {
            client.index(request, RequestOptions.DEFAULT);
            //log.info("inserted " + event);
        } catch (ElasticsearchException e) {
            log.error("failed insert on " + index, e);
            throw e;
        }
    }
}
