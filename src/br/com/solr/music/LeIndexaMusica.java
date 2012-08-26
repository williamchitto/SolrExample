package br.com.solr.music;

import java.io.File;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.ID3Tag;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v1.ID3V1_0Tag;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

public class LeIndexaMusica {

	public static void main(String[] args) throws Exception {

		String inicio = "C:\\Musicas";

		File root = new File(inicio);

		SolrServer solr = new HttpSolrServer("http://localhost:8181/solr");

		new LeIndexaMusica().listaRecursiva(root, solr);
		System.out.println("fim");

	}

	public void listaRecursiva(File root, SolrServer solr) throws IOException, ID3Exception {
		for (File file : root.listFiles()) {
			if (file.isDirectory()) {
				listaRecursiva(file, solr);
				
			}
			if (file.isFile()) {
				if (file.getName().substring(file.getName().lastIndexOf(".")).equals(".mp3") ) {

					MusicasVo music = getMusicaVo(file.getPath(), file.getName());
					indexa(music, solr);

				}

			}
		}
		
	}

	private void indexa(MusicasVo musica, SolrServer solr) {

		try {

			SolrInputDocument doc = new SolrInputDocument();
			doc.setField("nome", musica.getNome());
			if (musica.getTitulo() != null)
				doc.setField("titulo", musica.getTitulo());
			if (musica.getAlbum() != null)
				doc.setField("album", musica.getAlbum());
			if (musica.getArtista() != null)
				doc.setField("artista", musica.getArtista());
			if (musica.getAno() != null)
				doc.setField("ano", musica.getAno().toString());
			doc.setField("path", musica.getPath());

			solr.add(doc);

			solr.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private MusicasVo getMusicaVo(String filePath, String fileName) {

		MusicasVo musicasVo = new MusicasVo();

		File oSourceFile = new File(filePath);
		MediaFile oMediaFile = new MP3File(oSourceFile);

		musicasVo.setNome(fileName);
		musicasVo.setPath(filePath);
		musicasVo.setTitulo(oSourceFile.getName().replace(".mp3", ""));
		musicasVo.setArtista(oSourceFile.getParentFile().getName());
		ID3Tag[] aoID3Tag = null;

		try {

			aoID3Tag = oMediaFile.getTags();

		} catch (ID3Exception e) {
			// error getting year.. if one wasn't set
			// System.out.println("Could get read year from tag: " +
			// e.toString());
		}
		if (aoID3Tag == null){
		
			return musicasVo;
			
		}
		for (int i = 0; i < aoID3Tag.length; i++) {

			if (aoID3Tag[i] instanceof ID3V1_0Tag) {

				ID3V1_0Tag oID3V1_0Tag = (ID3V1_0Tag) aoID3Tag[i];

				if (oID3V1_0Tag.getTitle() != null) {
					// System.out.println("Title = " + oID3V1_0Tag.getTitle());
					musicasVo.setTitulo(oID3V1_0Tag.getTitle());
					musicasVo.setAlbum(oID3V1_0Tag.getAlbum());
					if (oID3V1_0Tag.getArtist() != null && !oID3V1_0Tag.getArtist().trim().equals("")) {
						musicasVo.setArtista(oID3V1_0Tag.getArtist().trim());
					} else {
						musicasVo.setArtista(oSourceFile.getParentFile().getName());
					}

					if (!oID3V1_0Tag.getYear().trim().equals("")) {
						try {
							musicasVo.setAno(Integer.valueOf(oID3V1_0Tag.getYear()));
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}

			} else if (aoID3Tag[i] instanceof ID3V2_3_0Tag) {
				ID3V2_3_0Tag oID3V2_3_0Tag = (ID3V2_3_0Tag) aoID3Tag[i];

				if (oID3V2_3_0Tag.getTIT2TextInformationFrame() != null) {
					musicasVo.setTitulo(oID3V2_3_0Tag.getTIT2TextInformationFrame().getTitle());
				} else {
					musicasVo.setTitulo(oID3V2_3_0Tag.getTitle());
				}
				try {
					musicasVo.setAlbum(oID3V2_3_0Tag.getAlbum());
					
					if (oID3V2_3_0Tag.getArtist() != null && !oID3V2_3_0Tag.getArtist().trim().equals("")) {
						musicasVo.setArtista(oID3V2_3_0Tag.getArtist().trim());
					} else {
						musicasVo.setArtista(oSourceFile.getParentFile().getName());
					}

		

					musicasVo.setAno(oID3V2_3_0Tag.getYear());

				} catch (ID3Exception e) {
					// error getting year.. if one wasn't set
					// System.out.println("Could get read year from tag: " +
					// e.toString());
				}

			}
		}
		return musicasVo;
	}
}
