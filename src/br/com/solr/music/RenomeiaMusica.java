package br.com.solr.music;

import java.io.File;
import java.io.IOException;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.ID3Tag;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v1.ID3V1_0Tag;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

public class RenomeiaMusica {

	public static void main(String[] args) throws Exception {

		String inicio = "D:\\Musicas\\";

		File root = new File(inicio);

		new RenomeiaMusica().listaRecursiva(root);
		System.out.println("FIM");
	}

	public void listaRecursiva(File root) throws IOException, ID3Exception {
		for (File file : root.listFiles()) {
			if (file.isDirectory()) {
				listaRecursiva(file);
			}
			if (file.isFile()) {
				try{
				if (file.getName().substring(file.getName().lastIndexOf(".")).equals(".mp3")) {
					String titulo = getNomeMusica(file.getPath(), file.getName());
					if (titulo !=null && !titulo.equals("")) {
						file.renameTo(new File(file.getPath().replace(file.getName(), "") + titulo + ".mp3"));
					}
					
				/*	if(file.getName().contains("'"))
					{
						System.out.println(file.getPath());
						file.renameTo(new File( new File(file.getPath().replace(file.getName(), ""))+file.getName().replace("'", " ")));
					}*/
				}
				}catch (java.lang.StringIndexOutOfBoundsException e) {
				//	System.out.println(file.getPath());
					file.renameTo(new File(file.getPath()+".mp3"));
					throw e;
				}

			}
		}
	}

	private String getNomeMusica(String filePath, String fileName) {

		String titulo = "";

		File oSourceFile = new File(filePath);
		MediaFile oMediaFile = new MP3File(oSourceFile);

		ID3Tag[] aoID3Tag = null;
		try {

			aoID3Tag = oMediaFile.getTags();

		} catch (ID3Exception e) {
			// error getting year.. if one wasn't set
			// System.out.println("Could get read year from tag: " +
			// e.toString());
		}
		if (aoID3Tag == null)
			return "";
		for (int i = 0; i < aoID3Tag.length; i++) {

			if (aoID3Tag[i] instanceof ID3V1_0Tag) {

				ID3V1_0Tag oID3V1_0Tag = (ID3V1_0Tag) aoID3Tag[i];

				if (oID3V1_0Tag.getTitle() != null) {
					// System.out.println("Title = " + oID3V1_0Tag.getTitle());
					titulo = oID3V1_0Tag.getTitle();

				}

			} else if (aoID3Tag[i] instanceof ID3V2_3_0Tag) {
				ID3V2_3_0Tag oID3V2_3_0Tag = (ID3V2_3_0Tag) aoID3Tag[i];

				if (oID3V2_3_0Tag.getTIT2TextInformationFrame() != null) {
					titulo = oID3V2_3_0Tag.getTIT2TextInformationFrame().getTitle();
				} else {
					titulo = oID3V2_3_0Tag.getTitle();
				}

			}
		}
		return titulo;
	}
}
