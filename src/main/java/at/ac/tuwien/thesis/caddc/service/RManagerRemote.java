package at.ac.tuwien.thesis.caddc.service;

import javax.ejb.Remote;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RserveException;

/**
 * Remote interface to invoke calls to R
 */
@Remote
public interface RManagerRemote {

	public String testR() throws RserveException, REXPMismatchException;
}
