package ngat.ems;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @author eng
 *
 */
public interface SkyModelArchive extends Remote {
	
	/** Request for archived status information.
	 * @param t1 Start time for archive search.
	 * @param t2 End time for archive search.
	 * @return Archived status information between search time limits.
	 * @throws RemoteException
	 */
	public List<SkyModelUpdate> getSkyModelHistory(long t1, long t2) throws RemoteException;
	
}