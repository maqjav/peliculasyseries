package es.pys.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.pys.model.Sesion;
import es.pys.storage.IStorage;
import es.pys.storage.exceptions.StorageException;
import es.pys.storage.factory.StorageFactory;

@RequestMapping("/storage")
@Controller
public class StorageController {

	private static Logger log = LogManager.getRootLogger();

	@Autowired
	private Sesion sesion;

	@Autowired
	private StorageFactory storageFactory;

	@RequestMapping(value = "/{img:.+}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public @ResponseBody
	byte[] seekImage(@PathVariable("img") String img) {
		log.info("Obteniendo la imagen: " + img);

		String folder = StringUtils.substringBefore(img, "~");
		String file = StringUtils.substringAfter(img, "~");
		IStorage storageService = storageFactory.getStorage();
		try {
			if (sesion != null && sesion.getId() != null)
				log.info("Se ha detectado que el identificador dentro del Storage vale: " + sesion.getId());
			else
				log.info("Se ha detectado que el identificador dentro del Storage vale NULL");

			return storageService.loadFile(file, folder);
		}
		catch (StorageException e) {
			log.error("Se ha producido un error al intentar cargar una imagen de un Storage.", e);
			return null;
		}
	}

	@RequestMapping(value = "/del/{img:.+}", method = RequestMethod.GET)
	public void delete(@PathVariable("img") String img, Model uiModel) {
		log.info("Se procede a suprimir la imagen: " + img);

		String folder = StringUtils.substringBefore(img, "~");
		String file = StringUtils.substringAfter(img, "~");
		IStorage storageService = storageFactory.getStorage();
		storageService.deleteFile(file, folder);
	}
}
