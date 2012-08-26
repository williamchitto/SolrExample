package br.com.solr;

import java.io.File;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest.ACTION;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.SolrInputDocument;

public class indexacaoSimples {

  public void indexa() throws SolrServerException, IOException {

    SolrServer solr = new HttpSolrServer("http://localhost:8983/solr/");
    SolrInputDocument doc = new SolrInputDocument();
    doc.setField("id", "df000001.pdf");
    doc.setField("title", "O que é software livre");
    doc.setField("author", "Augusto Campos");
    doc.setField("content", "Este artigo responde...");

    solr.add(doc);
    solr.commit();

  }

  public static void main(String[] args) throws SolrServerException,
      IOException
  {
    new indexacaoSimples().indexaPdf();
  }

  public void indexaPdf() throws SolrServerException, IOException {

    SolrServer solr = new HttpSolrServer("http://localhost:8983/solr");
    ContentStreamUpdateRequest up = new ContentStreamUpdateRequest(
        "/update/extract");

    //Pasta com documentos pdf  
    String inicio = "D:\\solr\\pdf";

    File root = new File(inicio);
    for (File file : root.listFiles()) {

      up.setParam("literal.id", file.getName());
      up.addFile(file);
      up.setParam("fmap.content", "content");
      up.setAction(ACTION.COMMIT, true, true);
      solr.request(up);
    }
  }

}
