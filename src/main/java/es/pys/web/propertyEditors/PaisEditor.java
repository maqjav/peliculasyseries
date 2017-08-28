package es.pys.web.propertyEditors;

import java.beans.PropertyEditorSupport;

import es.pys.dao.IPaisDao;

public class PaisEditor extends PropertyEditorSupport {
	
	private IPaisDao paisDao;
	
	public PaisEditor(IPaisDao paisDao) {
		this.paisDao = paisDao;
	}

    @Override
    public void setAsText(String iso) {
        this.setValue(this.paisDao.findPais(iso));
    }

}