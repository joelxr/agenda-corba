package server;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class ServerStartup {

	public static void main(String args[]) {
		try {
			ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object objPoa = orb.resolve_initial_references("RootPOA");
			POA rootPOA = POAHelper.narrow(objPoa);
			org.omg.CORBA.Object obj = orb.resolve_initial_references("NameService");
			NamingContext naming = NamingContextHelper.narrow(obj);

			AgendaImpl agenda = new AgendaImpl(args[0], obj);
			
			org.omg.CORBA.Object objRef = rootPOA.servant_to_reference(agenda);
			NameComponent[] name = { new NameComponent(args[0], "Agenda") };
			
			naming.rebind(name, objRef);
			rootPOA.the_POAManager().activate();

			agenda.start();
			
			orb.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
