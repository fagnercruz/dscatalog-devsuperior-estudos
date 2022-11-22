package br.devsuperior.dscatalog.services.exceptions;

public class EntityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6563888947114371405L;
	
	public EntityNotFoundException(String msg) {
		super(msg);
	}

}
