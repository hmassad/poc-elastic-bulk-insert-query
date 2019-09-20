# poc-elastic-bulk-insert-query

<em>objetivo</em>: meter evento de 1 localidad en elastic y consultar la cantidad de eventos de localidad, provincia y país 

## Implementación

1. al iniciar el servicio, verifica si están creados los íncies y los crea (con mapping) si no están
1. cuando se mete un documento, se consulta por la cantidad de documentos de la localidad, provincia y país

### REST API

dirección default: [http://localhost:8080](http://localhost:8080)

#### GET /status

devuelve siempre true

#### POST /events

recibe

```js
{
    timestamp: Instant,
    localidad: String
}
```

devuelve

```js
{
    localidad: String,
    casosLocalidad: int,
    provincia: String,
    casosProvincia: int,
    pais: String,
    casosPais: int,
    threatThresholdReached: boolean
}
```

#### POST /bulk

no espera cuerpo, inserta 100 eventos por cada localidad

devuelve

```js
{
    localidad: String,
    casosLocalidad: int,
    provincia: String,
    casosProvincia: int,
    pais: String,
    casosPais: int,
    threatThresholdReached: boolean
}
```

## Prerrequisitos

### vm.max_map_count

En Linux, asegurarse que el host que corre docker, no el contenedor, tenga seteado `vm.max_map_count` en un valor recomendado por elasticsearch

Para cambiarlo ejecutar

```bash
sudo sysctl -w vm.max_map_count=262144
```

Para más info, consultar https://www.elastic.co/guide/en/elasticsearch/reference/5.0/vm-max-map-count.html#vm-max-map-count

## Running

La imagen de docker de nuestra app se llama `backend`

Correr con docker elastic+kibana y debugear java 

```bash
sudo docker-compose -f docker-compose-elastic-kibana.yml up
```

Correr con docker todo junto (elastic+kibana+backend)

```bash
./gradlew build -x test
sudo docker-compose up
```

Recompilar la app y meterla en docker

```bash
sudo docker-compose stop backend
sudo docker-compose rm backend
./gradlew build -x test
sudo docker-compose up
```

## Qué Aprendí

1. No se puede meter un evento y consultar al toque, el índice tarda en crearse y count devuelve info vieja. Hay que separar escritura de lectura.
1. En elastic hay que crear los índices con mapping
1. En elastic, expresar los índices como `provincia-*` funciona genial
1. En elastic, para contar la cantidad de documentos con paísid igual a 1 se consulta con `GET /provincia-*/_count?q=paisId:1` o bien con su equivalente
    ```
    GET /provincia-*/_count
    {
        "query": {
            "query_string": {
                "query": "paisId:1"
            }
        }
    }
    ```
1. la versión más actual de elasticsearch high level api que hay en maven es la 6.4.3 al día 2019-08-17
1. borrar todos los índices `DELETE /provincia-*`
1. traer todos los documentos `GET /provincia-*/_search`
 