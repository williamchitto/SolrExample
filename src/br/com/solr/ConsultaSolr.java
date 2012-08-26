package br.com.solr;

import java.util.Iterator;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

public class ConsultaSolr {

  @SuppressWarnings("rawtypes")
  public void consultarSolr() throws SolrServerException {

    SolrServer solr = new HttpSolrServer("http://localhost:8983/solr/");
    SolrQuery query = new SolrQuery("*:*");
    QueryResponse rsp = solr.query(query);
    for (Iterator iterator = rsp.getResults().iterator(); iterator.hasNext();) {
      {
        SolrDocument solrDoc = (SolrDocument) iterator.next();
        System.out.println(solrDoc.getFieldValue("id"));
        System.out.println(solrDoc.getFieldValue("title"));
        System.out.println(solrDoc.getFieldValue("author"));
        
      }

    }
  }
  
  public static void main(String[] args) throws SolrServerException {
    new ConsultaSolr().consultarSolr();
  }
}
