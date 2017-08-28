package es.pys.web.propertyEditors;

import java.beans.PropertyEditorSupport;

import es.pys.dao.ICategoriaDao;
import es.pys.dao.IPaisDao;

public class CategoriaEditor extends PropertyEditorSupport {

	private ICategoriaDao categoriaDao;
	
	public CategoriaEditor(ICategoriaDao categoriaDao) {
		this.categoriaDao = categoriaDao;
	}

    @Override
    public void setAsText(String id) {
        this.setValue(this.categoriaDao.findCategoria(Long.valueOf(id)));
    }

}