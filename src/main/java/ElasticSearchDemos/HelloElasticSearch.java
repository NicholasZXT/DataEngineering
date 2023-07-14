package ElasticSearchDemos;

import co.elastic.clients.elasticsearch.cat.nodes.NodesRecord;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cat.ElasticsearchCatClient;
import co.elastic.clients.elasticsearch.cluster.ElasticsearchClusterClient;
import co.elastic.clients.elasticsearch.cat.NodesResponse;

import java.io.IOException;
import java.util.List;

public class HelloElasticSearch {

    public static void main(String[] args) throws IOException {
        String host = "10.7.1.93";
        int port = 9200;
        HelloElasticSearch es = new HelloElasticSearch();
        es.init_client(host, port);
        //ElasticsearchClusterClient clusterClient = es.client.cluster();
        //System.out.println(clusterClient.toString());
        ElasticsearchCatClient esCat = es.getCatClient();
        NodesResponse nodesResponse = esCat.nodes();
        List<NodesRecord> nodes = nodesResponse.valueBody();
        for(NodesRecord node: nodes){
            System.out.println(node.toString());
        }

    }

    private ElasticsearchClient client;

    public void init_client(String host, int port){
        // Create the low-level client
        RestClient restClient = RestClient.builder(new HttpHost(host, port)).build();

        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // And create the API client
        this.client = new ElasticsearchClient(transport);
    }

    public ElasticsearchCatClient getCatClient(){
        return this.client.cat();
    }
}
