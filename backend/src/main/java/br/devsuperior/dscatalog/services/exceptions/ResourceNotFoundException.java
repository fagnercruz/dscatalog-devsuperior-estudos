package br.devsuperior.dscatalog.services.exceptions;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6563888947114371405L;
	
	public ResourceNotFoundException(String msg) {
		super(msg);
	}

}
